package com.hp.thermalshockreforged.entity.ambient;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.server.level.ServerLevel;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class PillbudEntity extends net.minecraft.world.entity.monster.Monster implements GeoEntity, ServerEndTickTask {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(PillbudEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int cooldown;
    private boolean resumeAnimation;
    private int skillTick = -1;
    private java.util.UUID skillTarget;
    private String skillDimension = "";
    private Vec3 origin = Vec3.ZERO;

    public PillbudEntity(EntityType<? extends PillbudEntity> type, Level level) {
        super(type, level);
        xpReward = 3;

    }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.MAX_HEALTH, 1).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 1).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 1) ; }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "pillbud"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String value) { entityData.set(TEXTURE, value); }
    public void setAnimation(String value) { triggerAnim("procedure", value); }
    @Override
    protected void registerGoals() { goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) { @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); } }); targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers()); goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8)); goalSelector.addGoal(4, new RandomLookAroundGoal(this)); targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, false, false)); }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 49; }
    @Override
    public boolean doHurtTarget(Entity target) { if (target instanceof LivingEntity living) beginSkill(living); return false; }
    public void beginSkill(LivingEntity target) {
        if (level().isClientSide || !isAlive() || cooldown > 0 || skillTick >= 0 || !target.isAlive() || target.level() != level() || !(target instanceof Player)) return;
        cooldown = 60;
        skillTick = 0;
        skillTarget = target.getUUID();
        skillDimension = level().dimension().location().toString();
        origin = target.position();
        face(target);
        setAnimation("attack");
        getNavigation().stop(); soundAtOrigin(net.minecraft.sounds.SoundEvents.CREEPER_PRIMED);
        if (level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }
    private void face(LivingEntity target) { lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, target.getEyePosition()); yBodyRot = getYRot(); yHeadRot = getYRot(); }
    private LivingEntity skillTarget() {
        if (!(level() instanceof net.minecraft.server.level.ServerLevel server) || skillTarget == null || !skillDimension.equals(level().dimension().location().toString())) return null;
        var found = server.getEntity(skillTarget);
        return found instanceof LivingEntity living && living.isAlive() && living.level() == level() ? living : null;
    }
    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (skillTick >= 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override
    public void tick() {
        if (!level().isClientSide && cooldown > 0) cooldown--;
        super.tick();
        if (level().isClientSide) return;
        if (!isAlive()) { skillTick = -1; skillTarget = null; return; }
        if (skillTick >= 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override
    public void endTick(ServerLevel server) {
        if (skillTick < 0 || isRemoved()) return;
        if (!isAlive() || !skillDimension.equals(server.dimension().location().toString())) { skillTick = -1; skillTarget = null; return; }
        var target = skillTarget();
        if (target == null) { skillTick = -1; skillTarget = null; return; }
        if (resumeAnimation) { setAnimation("attack"); resumeAnimation = false; }
        skillTick++;
        if (skillTick < 10) getNavigation().stop();
        if (skillTick == 10) {
            ThermalShockReforged.LOGGER.debug("Pillbud launch at server end tick: entity={}, age={}, origin={}", getUUID(), skillTick, origin);
            face(target);
            var look = getLookAngle();
            setDeltaMovement(look.x * 2, 0.4, look.z * 2);
            hasImpulse = true;
            soundAtOrigin(net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "propulsionbootsactivate")));
            soundAtOrigin(net.minecraft.sounds.SoundEvents.FIREWORK_ROCKET_LAUNCH);
        }
        if (skillTick == 22) {
            ThermalShockReforged.LOGGER.debug("Pillbud detonation at server end tick: entity={}, age={}, origin={}", getUUID(), skillTick, origin);
            var position = position();
            var source = new DamageSource(server.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.EXPLOSION));
            float damage = (float) (getAttributeValue(Attributes.ATTACK_DAMAGE) * 8);
            discard();
            for (var hit : server.getEntitiesOfClass(Entity.class, new net.minecraft.world.phys.AABB(position, position).inflate(2), e -> true)) hit.hurt(source, damage);
            soundAtOrigin(net.minecraft.sounds.SoundEvents.FIREWORK_ROCKET_BLAST);
            soundAtOrigin(net.minecraft.sounds.SoundEvents.FIREWORK_ROCKET_TWINKLE);
            server.sendParticles(AmbientContent.PRISMATIC_BOOM.get(), position.x, position.y, position.z, 1, 0.001, 0.001, 0.001, 0);
        }
        else ServerEndTickTasks.schedule(server, getUUID());
    }

    @Override
    protected boolean isImmobile() { return skillTick >= 0 && skillTick < 10 || super.isImmobile(); }
    private void soundAtOrigin(net.minecraft.sounds.SoundEvent sound) { level().playSound(null, net.minecraft.core.BlockPos.containing(origin), sound, net.minecraft.sounds.SoundSource.HOSTILE, 1, 1); }
    @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(DamageSource source) { return net.minecraft.sounds.SoundEvents.GENERIC_HURT; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); tag.putInt("SkillCooldown", cooldown); tag.putInt("SkillTick", skillTick); if (skillTarget != null) tag.putUUID("SkillTarget", skillTarget); tag.putString("SkillDimension", skillDimension); tag.putDouble("OriginX", origin.x); tag.putDouble("OriginY", origin.y); tag.putDouble("OriginZ", origin.z); }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); if (tag.contains("Texture")) setTexture(tag.getString("Texture")); cooldown = Math.max(0, tag.getInt("SkillCooldown")); skillTick = tag.contains("SkillTick") ? tag.getInt("SkillTick") : -1; skillTarget = tag.hasUUID("SkillTarget") ? tag.getUUID("SkillTarget") : null; skillDimension = tag.getString("SkillDimension"); origin = new Vec3(tag.getDouble("OriginX"), tag.getDouble("OriginY"), tag.getDouble("OriginZ")); resumeAnimation = skillTick >= 0; }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP) .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle")).triggerableAnim("walk", RawAnimation.begin().thenPlay("walk")).triggerableAnim("attack", RawAnimation.begin().thenPlay("attack"));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop(state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F ? "walk" : "idle"))));
        controllers.add(procedure);
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
