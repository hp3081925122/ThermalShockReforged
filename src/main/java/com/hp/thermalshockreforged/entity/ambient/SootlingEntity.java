package com.hp.thermalshockreforged.entity.ambient;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class SootlingEntity extends net.minecraft.world.entity.monster.Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(SootlingEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int cooldown;
    private boolean resumeAnimation;
    private int skillTick = -1;
    private java.util.UUID skillTarget;
    private String skillDimension = "";
    private Vec3 origin = Vec3.ZERO;

    public SootlingEntity(EntityType<? extends SootlingEntity> type, Level level) {
        super(type, level);
        xpReward = 10;
        setPersistenceRequired(); moveControl = new net.minecraft.world.entity.ai.control.FlyingMoveControl(this, 10, true);
    }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.2).add(Attributes.MAX_HEALTH, 26).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 3).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0.6) .add(Attributes.FLYING_SPEED, 0.2); }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "flare_wrangler"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String value) { entityData.set(TEXTURE, value); }
    public void setAnimation(String value) { triggerAnim("procedure", value); }
    @Override
    protected void registerGoals() { targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, false)); goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false) { @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); } }); goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8, 20) { @Override protected Vec3 getPosition() { return position().add((random.nextFloat() * 2 - 1) * 16, (random.nextFloat() * 2 - 1) * 16, (random.nextFloat() * 2 - 1) * 16); } }); targetSelector.addGoal(4, new HurtByTargetGoal(this)); goalSelector.addGoal(5, new RandomLookAroundGoal(this)); }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 16; }
    @Override
    public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity target) { if (target instanceof LivingEntity living) beginSkill(living); return false; }
    public void beginSkill(LivingEntity target) {
        if (level().isClientSide() || !isAlive() || cooldown > 0 || skillTick >= 0 || !target.isAlive() || target.level() != level() ) return;
        cooldown = 80;
        skillTick = 0;
        skillTarget = target.getUUID();
        skillDimension = level().dimension().identifier().toString();
        origin = target.position();
        face(target);
        setAnimation("attack");

    }
    private void face(LivingEntity target) { lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, target.getEyePosition()); yBodyRot = getYRot(); yHeadRot = getYRot(); }
    private LivingEntity skillTarget() {
        if (!(level() instanceof net.minecraft.server.level.ServerLevel server) || skillTarget == null || !skillDimension.equals(level().dimension().identifier().toString())) return null;
        var found = server.getEntity(skillTarget);
        return found instanceof LivingEntity living && living.isAlive() && living.level() == level() ? living : null;
    }
    @Override
    public void tick() {
        int previousSkillTick = skillTick;
        if (!level().isClientSide() && cooldown > 0) cooldown--;
        super.tick();
        if (level().isClientSide()) return;
        if (!isAlive()) { skillTick = -1; skillTarget = null; return; }
        if (skillTick < 0 || previousSkillTick < 0) return;
        var target = skillTarget();
        if (target == null) { skillTick = -1; skillTarget = null; return; }
        if (resumeAnimation) { setAnimation("attack"); resumeAnimation = false; }
        skillTick++;
        if (skillTick == 20) { face(target); var look = getLookAngle(); setDeltaMovement(look.x * 2, -0.5, look.z * 2); hurtMarked = true; }
        if (skillTick >= 20 && skillTick <= 38 && (skillTick - 20) % 2 == 0 && distanceTo(target) <= 2) {
            var source = new DamageSource(level().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE).getOrThrow(com.hp.thermalshockreforged.content.ModDamageTypes.THERM_IFRAME_BYPASS));
            target.hurt(source, (float) (getAttributeValue(Attributes.ATTACK_DAMAGE) / 3));
        }
        if (skillTick >= 38) { skillTick = -1; skillTarget = null; }
    }

    @Override
    protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) { return new net.minecraft.world.entity.ai.navigation.FlyingPathNavigation(this, level); }
    @Override
    public void setNoGravity(boolean ignored) { super.setNoGravity(true); }
    @Override
    public void aiStep() { super.aiStep(); setNoGravity(true); }
    @Override
    public boolean causeFallDamage(double distance, float multiplier, DamageSource source) { return false; }
    @Override
    protected void checkFallDamage(double distance, boolean ground, net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos) { }

    @Override
    public boolean removeWhenFarAway(double distance) { return false; }
    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !source.is(DamageTypes.IN_FIRE) && super.hurtServer(level, source, amount); }
    @Override
    public net.minecraft.sounds.SoundEvent getAmbientSound() { return net.minecraft.sounds.SoundEvents.BEE_LOOP_AGGRESSIVE; }
    @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(DamageSource source) { return net.minecraft.sounds.SoundEvents.GENERIC_HURT; }
    @Override
    protected net.minecraft.sounds.SoundEvent getDeathSound() { return net.minecraft.sounds.SoundEvents.GENERIC_DEATH; }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putString("Texture", texture()); output.putInt("SkillCooldown", cooldown); output.putInt("SkillTick", skillTick); if (skillTarget != null) output.putString("SkillTarget", skillTarget.toString()); output.putString("SkillDimension", skillDimension); output.putDouble("OriginX", origin.x); output.putDouble("OriginY", origin.y); output.putDouble("OriginZ", origin.z); }
    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); if (input.read("Texture", com.mojang.serialization.Codec.PASSTHROUGH).isPresent()) setTexture(input.getStringOr("Texture", "")); cooldown = Math.max(0, input.getIntOr("SkillCooldown", 0)); skillTick = input.read("SkillTick", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? input.getIntOr("SkillTick", 0) : -1; skillTarget = input.getString("SkillTarget").map(java.util.UUID::fromString).orElse(null); skillDimension = input.getStringOr("SkillDimension", ""); origin = new Vec3(input.getDoubleOr("OriginX", 0.0), input.getDoubleOr("OriginY", 0.0), input.getDoubleOr("OriginZ", 0.0)); resumeAnimation = skillTick >= 0; }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP) .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle")).triggerableAnim("attack", RawAnimation.begin().thenPlay("attack"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
        controllers.add(procedure);
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
