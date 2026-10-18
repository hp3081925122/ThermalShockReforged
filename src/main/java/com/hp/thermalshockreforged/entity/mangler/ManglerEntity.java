package com.hp.thermalshockreforged.entity.mangler;

import com.hp.thermalshockreforged.content.status.aura.AuraContent;
import net.minecraft.core.particles.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ManglerEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ManglerEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> COOLDOWN_VISIBLE = SynchedEntityData.defineId(ManglerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> COOLDOWN_AMBIENT = SynchedEntityData.defineId(ManglerEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int attackCooldown;
    private int stunRemaining;
    private boolean resumeStun;
    private String combatDimension = "";
    public ManglerEntity(EntityType<? extends ManglerEntity> type, Level level) { super(type, level); xpReward = this instanceof SummonedManglerEntity ? 0 : 6; moveControl = new FlyingMoveControl(this, 10, true); }
    public static AttributeSupplier.Builder createAttributes(boolean summoned) { return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.MAX_HEALTH, summoned ? 7 : 9).add(Attributes.ARMOR, 1.5).add(Attributes.ATTACK_DAMAGE, 1).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.FLYING_SPEED, 0.3).add(Attributes.STEP_HEIGHT, 0.6); }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, this instanceof SummonedManglerEntity ? "summoned_mangler" : "mangler25"); builder.define(COOLDOWN_VISIBLE, false); builder.define(COOLDOWN_AMBIENT, false); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String value) { entityData.set(TEXTURE, value); }
    public void setAnimation(String value) { if (value.equals("empty")) stopTriggeredAnim("procedure", null); else triggerAnim("procedure", value); }
    @Override
    protected PathNavigation createNavigation(Level level) { return new FlyingPathNavigation(this, level); }
    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) { @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); } });
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, true));
        goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8, 20) { @Override protected Vec3 getPosition() { return position().add((random.nextFloat() * 2 - 1) * 16, (random.nextFloat() * 2 - 1) * 16, (random.nextFloat() * 2 - 1) * 16); } });
        goalSelector.addGoal(4, new RandomLookAroundGoal(this)); targetSelector.addGoal(5, new HurtByTargetGoal(this));
    }
    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 100; }
    @Override
    public boolean doHurtTarget(Entity target) { if (target instanceof LivingEntity living) boost(living); return false; }
    public void boost(LivingEntity target) {
        if (level().isClientSide || !isAlive() || attackCooldown > 0 || !target.isAlive() || target.level() != level()) return;
        combatDimension = level().dimension().location().toString();
        attackCooldown = ManglerCombatConfig.BOOST_COOLDOWN.get(); entityData.set(COOLDOWN_VISIBLE, true); entityData.set(COOLDOWN_AMBIENT, false); setAnimation("boost");
        Player nearest = null; double distance = Double.MAX_VALUE;
        if (getServer() != null) for (var player : getServer().getPlayerList().getPlayers()) { double candidate = player.distanceToSqr(position()); if (candidate < distance) { distance = candidate; nearest = player; } }
        if (nearest != null) { lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.FEET, nearest.position()); yBodyRot = getYRot(); yHeadRot = getYRot(); }
        var look = getLookAngle(); setDeltaMovement(look.x * 2, -0.5, look.z * 2); hasImpulse = true;
        int damage = (int) Math.min(255, Math.max(0, Math.round(getAttributeValue(Attributes.ATTACK_DAMAGE) * (this instanceof SummonedManglerEntity ? 2 : 3))));
        addEffect(new MobEffectInstance(AuraContent.DAMAGING_AURA, 20, damage, false, true));
    }
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && isAlive()) {
            combatDimension = level().dimension().location().toString(); attackCooldown = 60; stunRemaining = 60;
            entityData.set(COOLDOWN_VISIBLE, true); entityData.set(COOLDOWN_AMBIENT, true); setAnimation("stunned");
            addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1, true, true));
            if (!(this instanceof SummonedManglerEntity)) { var look = getLookAngle(); double recoil = 0.6 * (1 - getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)); setDeltaMovement(-look.x * recoil, 0, -look.z * recoil); hasImpulse = true; }
        }
        return (this instanceof SummonedManglerEntity || !source.is(DamageTypes.IN_FIRE)) && super.hurt(source, amount);
    }
    @Override
    public void tick() {
        if (!level().isClientSide) {
            if (resumeStun) { if (stunRemaining > 0) setAnimation("stunned"); resumeStun = false; }
            if (!combatDimension.isEmpty() && !combatDimension.equals(level().dimension().location().toString())) { attackCooldown = 0; stunRemaining = 0; setAnimation("empty"); }
            if (attackCooldown > 0 && --attackCooldown == 0) entityData.set(COOLDOWN_VISIBLE, false);
            if (stunRemaining > 0 && --stunRemaining == 0) setAnimation("empty");
        }
        super.tick();
    }
    @Override
    protected void tickEffects() {
        if (!level().isClientSide || !entityData.get(COOLDOWN_VISIBLE)) { super.tickEffects(); return; }
        var particles = new java.util.ArrayList<ParticleOptions>(); boolean ambient = true;
        for (var effect : new java.util.ArrayList<>(getActiveEffects())) {
            effect.tick(this, () -> onEffectUpdated(effect, true, null));
            if (effect.getDuration() % 600 == 0) onEffectUpdated(effect, false, null);
            if (effect.isVisible()) { particles.add(effect.getParticleOptions()); if (!effect.isAmbient()) ambient = false; }
        }
        if (entityData.get(COOLDOWN_VISIBLE)) { particles.add(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, -1)); if (!entityData.get(COOLDOWN_AMBIENT)) ambient = false; }
        int frequency = (isInvisible() ? 15 : 4) * (ambient ? 5 : 1);
        if (!particles.isEmpty() && random.nextInt(frequency) == 0) level().addParticle(particles.get(random.nextInt(particles.size())), getRandomX(0.5), getRandomY(), getRandomZ(0.5), 1, 1, 1);
    }
    @Override
    public SoundEvent getAmbientSound() { return SoundEvents.VEX_AMBIENT; }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.VEX_HURT; }
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.VEX_DEATH; }
    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) { return false; }
    @Override
    protected void checkFallDamage(double distance, boolean ground, net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos) { }
    @Override
    public void setNoGravity(boolean ignored) { super.setNoGravity(true); }
    @Override
    public void aiStep() { super.aiStep(); setNoGravity(true); }
    @Override
    public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); tag.putInt("AttackCooldown", attackCooldown); tag.putInt("StunRemaining", stunRemaining); tag.putString("CombatDimension", combatDimension); tag.putBoolean("CooldownVisible", entityData.get(COOLDOWN_VISIBLE)); tag.putBoolean("CooldownAmbient", entityData.get(COOLDOWN_AMBIENT)); }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); if (tag.contains("Texture")) setTexture(tag.getString("Texture")); attackCooldown = Math.max(0, tag.getInt("AttackCooldown")); stunRemaining = Math.max(0, Math.min(60, tag.getInt("StunRemaining"))); combatDimension = tag.getString("CombatDimension"); entityData.set(COOLDOWN_VISIBLE, attackCooldown > 0 && tag.getBoolean("CooldownVisible")); entityData.set(COOLDOWN_AMBIENT, tag.getBoolean("CooldownAmbient")); resumeStun = stunRemaining > 0; }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("boost", RawAnimation.begin().thenPlay("boost")).triggerableAnim("stunned", RawAnimation.begin().thenPlay("stunned")).triggerableAnim("chase", RawAnimation.begin().thenPlay("chase")).triggerableAnim("chaseloop", RawAnimation.begin().thenPlay("chaseloop"));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop(isAggressive() && state.isMoving() ? "chaseloop" : "idle")))); controllers.add(procedure);
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
