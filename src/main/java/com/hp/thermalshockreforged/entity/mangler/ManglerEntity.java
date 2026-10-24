package com.hp.thermalshockreforged.entity.mangler;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.aura.AuraContent;
import com.hp.thermalshockreforged.content.status.LegacyEffectParticles;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public class ManglerEntity extends Monster implements GeoEntity, ServerEndTickTask {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ManglerEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> COOLDOWN_VISIBLE = SynchedEntityData.defineId(ManglerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> COOLDOWN_AMBIENT = SynchedEntityData.defineId(ManglerEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int attackCooldown;
    private long stunSchedule;
    private boolean stunnedAnimation;
    private boolean resumeStun;
    private String combatDimension = "";
    public ManglerEntity(EntityType<? extends ManglerEntity> type, Level level) { super(type, level); xpReward = this instanceof SummonedManglerEntity ? 0 : 6; moveControl = new FlyingMoveControl(this, 10, true); }
    public static AttributeSupplier.Builder createAttributes(boolean summoned) { return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.MAX_HEALTH, summoned ? 7 : 9).add(Attributes.ARMOR, 1.5).add(Attributes.ATTACK_DAMAGE, 1).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.FLYING_SPEED, 0.3).add(Attributes.STEP_HEIGHT, 0.6); }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, this instanceof SummonedManglerEntity ? "summoned_mangler" : "mangler25"); builder.define(COOLDOWN_VISIBLE, false); builder.define(COOLDOWN_AMBIENT, false); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String value) { entityData.set(TEXTURE, value); }
    public void setAnimation(String value) { stunnedAnimation = value.equals("stunned"); if (value.equals("empty")) stopTriggeredAnim("procedure", null); else triggerAnim("procedure", value); }
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
    public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity target) { if (target instanceof LivingEntity living) boost(living); return false; }
    public void boost(LivingEntity target) {
        if (level().isClientSide() || !isAlive() || attackCooldown > 0 || !target.isAlive() || target.level() != level()) return;
        combatDimension = level().dimension().identifier().toString();
        attackCooldown = ManglerCombatConfig.BOOST_COOLDOWN.get(); entityData.set(COOLDOWN_VISIBLE, true); entityData.set(COOLDOWN_AMBIENT, false); setAnimation("boost");
        Player nearest = null; double distance = Double.MAX_VALUE;
        if (level().getServer() != null) for (var player : level().getServer().getPlayerList().getPlayers()) { double candidate = player.distanceToSqr(position()); if (candidate < distance) { distance = candidate; nearest = player; } }
        if (nearest != null) { lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.FEET, nearest.position()); yBodyRot = getYRot(); yHeadRot = getYRot(); }
        var look = getLookAngle(); setDeltaMovement(look.x * 2, -0.5, look.z * 2); hurtMarked = true;
        int damage = (int) Math.min(255, Math.max(0, Math.round(getAttributeValue(Attributes.ATTACK_DAMAGE) * (this instanceof SummonedManglerEntity ? 2 : 3))));
        addEffect(new MobEffectInstance(AuraContent.DAMAGING_AURA, 20, damage, false, true));
    }
    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
        if (!level().isClientSide() && !isRemoved()) {
            setAnimation("stunned");
            ThermalShockReforged.LOGGER.debug("Mangler hurt animation triggered: entity={}, summoned={}, alive={}", getUUID(), this instanceof SummonedManglerEntity, isAlive());
        }
        if (!level().isClientSide() && isAlive()) {
            combatDimension = level().dimension().identifier().toString(); attackCooldown = 60; stunSchedule |= 1L << 59;
            entityData.set(COOLDOWN_VISIBLE, true); entityData.set(COOLDOWN_AMBIENT, true);
            addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1, true, true));
            if (level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
            ThermalShockReforged.LOGGER.debug("Mangler stun armed: entity={}, summoned={}, pendingEndTickSlots={}", getUUID(), this instanceof SummonedManglerEntity, Long.bitCount(stunSchedule));
            if (!(this instanceof SummonedManglerEntity)) { var look = getLookAngle(); double recoil = 0.6 * (1 - getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)); setDeltaMovement(-look.x * recoil, 0, -look.z * recoil); hurtMarked = true; }
        }
        return (this instanceof SummonedManglerEntity || !source.is(DamageTypes.IN_FIRE)) && super.hurtServer(level, source, amount);
    }
    @Override
    public void tick() {
        if (!level().isClientSide()) {
            if (resumeStun) { if (stunnedAnimation) setAnimation("stunned"); resumeStun = false; }
            if (!combatDimension.isEmpty() && !combatDimension.equals(level().dimension().identifier().toString())) { attackCooldown = 0; stunSchedule = 0; entityData.set(COOLDOWN_VISIBLE, false); setAnimation("empty"); combatDimension = level().dimension().identifier().toString(); }
            if (attackCooldown > 0 && --attackCooldown == 0) entityData.set(COOLDOWN_VISIBLE, false);
            if (stunSchedule != 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
        }
        super.tick();
    }
    @Override public void onAddedToLevel() {
        super.onAddedToLevel();
        if (stunSchedule != 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override public void endTick(ServerLevel server) {
        if (isRemoved() || stunSchedule == 0) return;
        if (!combatDimension.isEmpty() && !combatDimension.equals(server.dimension().identifier().toString())) { attackCooldown = 0; stunSchedule = 0; entityData.set(COOLDOWN_VISIBLE, false); setAnimation("empty"); combatDimension = server.dimension().identifier().toString(); return; }
        boolean completed = (stunSchedule & 1L) != 0;
        stunSchedule >>>= 1;
        if (completed && stunSchedule == 0) setAnimation("empty");
        if (stunSchedule != 0) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override
    protected void tickEffects() {
        if (!level().isClientSide() || !entityData.get(COOLDOWN_VISIBLE)) { super.tickEffects(); return; }
        boolean ambient = entityData.get(COOLDOWN_AMBIENT);
        for (var effect : new java.util.ArrayList<>(getActiveEffects())) {
            effect.tickClient();
            if (effect.isVisible() && !effect.isAmbient()) ambient = false;
        }
        boolean emit = isInvisible() ? random.nextInt(15) == 0 : random.nextBoolean();
        if (ambient) emit &= random.nextInt(5) == 0;
        if (emit) LegacyEffectParticles.emit(this, -1, 2, null, ambient);
    }
    @Override
    public SoundEvent getAmbientSound() { return SoundEvents.VEX_AMBIENT; }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.VEX_HURT; }
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.VEX_DEATH; }
    @Override
    public boolean causeFallDamage(double distance, float multiplier, DamageSource source) { return false; }
    @Override
    protected void checkFallDamage(double distance, boolean ground, net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos) { }
    @Override
    public void setNoGravity(boolean ignored) { super.setNoGravity(true); }
    @Override
    public void aiStep() { super.aiStep(); setNoGravity(true); }
    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putString("Texture", texture()); output.putInt("AttackCooldown", attackCooldown); output.putLong("StunEndSchedule", stunSchedule); output.putBoolean("StunAnimation", stunnedAnimation); output.putString("CombatDimension", combatDimension); output.putBoolean("CooldownVisible", entityData.get(COOLDOWN_VISIBLE)); output.putBoolean("CooldownAmbient", entityData.get(COOLDOWN_AMBIENT)); }
    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input); if (input.read("Texture", com.mojang.serialization.Codec.PASSTHROUGH).isPresent()) setTexture(input.getStringOr("Texture", "")); attackCooldown = Math.max(0, input.getIntOr("AttackCooldown", 0));
        int legacyRemaining = Math.clamp(input.getIntOr("StunRemaining", 0), 0, 60);
        stunSchedule = input.read("StunEndSchedule", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? input.getLongOr("StunEndSchedule", 0L) & 0x0fffffffffffffffL : legacyRemaining > 0 ? 1L << (legacyRemaining - 1) : 0;
        stunnedAnimation = input.read("StunAnimation", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? input.getBooleanOr("StunAnimation", false) : stunSchedule != 0;
        combatDimension = input.getStringOr("CombatDimension", ""); entityData.set(COOLDOWN_VISIBLE, attackCooldown > 0 && input.getBooleanOr("CooldownVisible", false)); entityData.set(COOLDOWN_AMBIENT, input.getBooleanOr("CooldownAmbient", false)); resumeStun = stunnedAnimation;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("boost", RawAnimation.begin().thenPlay("boost")).triggerableAnim("stunned", RawAnimation.begin().thenPlay("stunned")).triggerableAnim("chase", RawAnimation.begin().thenPlay("chase")).triggerableAnim("chaseloop", RawAnimation.begin().thenPlay("chaseloop"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop(isAggressive() && state.isMoving() ? "chaseloop" : "idle")))); controllers.add(procedure);
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
