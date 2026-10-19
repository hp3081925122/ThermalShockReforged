package com.hp.thermalshockreforged.entity.beetles;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.LegacyEffectParticles;
import java.util.*;
import net.minecraft.core.particles.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class BeetleCombatEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BeetleCombatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> EMERGING = SynchedEntityData.defineId(BeetleCombatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final net.minecraft.resources.ResourceLocation MOVEMENT_LOCK = BeetleContent.id("beetle_movement_lock");
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    protected int cooldown;
    protected int movementLock;
    private int sleepLock;
    private boolean attributesApplied;
    private boolean loadedFromSave;
    private boolean experiencePending;
    private boolean submittingAttack;
    private UUID experienceAttacker;
    private String pendingAnimation = "";

    protected BeetleCombatEntity(EntityType<? extends BeetleCombatEntity> type, Level level, String texture, int experience) { super(type, level); entityData.set(TEXTURE, texture); xpReward = experience; setPersistenceRequired(); }
    public final boolean thrasher() { return this instanceof BeetleThrasherEntity; }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String value) { entityData.set(TEXTURE, value); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, ""); builder.define(EMERGING, false); }
    public static AttributeSupplier.Builder createAttributes(boolean thrasher) { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, thrasher ? 45 : 82).add(Attributes.ARMOR, thrasher ? 8 : 10).add(Attributes.ATTACK_DAMAGE, thrasher ? 3 : 8).add(Attributes.MOVEMENT_SPEED, thrasher ? .2 : 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, thrasher ? 0 : 1).add(Attributes.STEP_HEIGHT, thrasher ? .6 : 1); }
    @Override protected void registerGoals() {
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        goalSelector.addGoal(thrasher() ? 3 : 2, new MeleeAttackGoal(this, 1.2, false) { @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); } });
        goalSelector.addGoal(thrasher() ? 4 : 3, new RandomStrollGoal(this, 1));
        targetSelector.addGoal(thrasher() ? 5 : 4, new HurtByTargetGoal(this));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= (thrasher() ? 36 : 121); }
    @Override public boolean doHurtTarget(Entity target) { if (target instanceof LivingEntity living) beginAttack(living); return false; }
    public void beginAttack(LivingEntity victim) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || cooldown > 0 || submittingAttack || victim == this || !victim.isAlive() || victim.level() != level()) return;
        boolean first = random.nextDouble() < (thrasher() ? BeetleConfig.THRASHER_TRIPLE_CHANCE.get() : BeetleConfig.CRASHER_HEAVY_CHANCE.get());
        int skill = thrasher() ? (first ? 2 : 3) : (first ? 0 : 1);
        boolean submitted;
        submittingAttack = true;
        try { submitted = BeetleAttackEntity.submit(server, this, victim, skill); }
        finally { submittingAttack = false; }
        if (!submitted) { ThermalShockReforged.LOGGER.debug("Beetle attack insertion failed: caster={}, target={}, skill={}", getUUID(), victim.getUUID(), skill); return; }
        cooldown = switch (skill) { case 0 -> BeetleConfig.CRASHER_STRIKE_COOLDOWN.get(); case 1 -> BeetleConfig.CRASHER_DASH_COOLDOWN.get(); case 2 -> BeetleConfig.THRASHER_TRIPLE_COOLDOWN.get(); default -> BeetleConfig.THRASHER_DODGE_COOLDOWN.get(); };
        movementLock = switch (skill) { case 0 -> 15; case 1 -> 25; case 2 -> 18; default -> 35; };
        entityData.set(EMERGING, false);
        synchronizeMovementLock();
        getNavigation().stop();
        if (!thrasher()) faceNearestPlayer(server);
        triggerAnim("procedure", skill == 0 || skill == 3 ? "attack2" : "attack");
        server.playSound(null, victim.blockPosition(), SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.NEUTRAL, 1, 1);
        server.playSound(null, victim.blockPosition(), SoundEvents.IRON_GOLEM_HURT, SoundSource.NEUTRAL, 1, thrasher() ? 5 : -2);
        ThermalShockReforged.LOGGER.debug("Beetle attack committed: caster={}, target={}, skill={}, cooldown={}", getUUID(), victim.getUUID(), skill, cooldown);
    }
    void faceNearestPlayer(ServerLevel server) { var player = server.players().stream().min(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(this))).orElse(null); if (player != null) { lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.FEET, player.position()); yBodyRot = getYRot(); yHeadRot = getYRot(); } }
    protected void startEmergence() { cooldown = Math.max(cooldown, 60); entityData.set(EMERGING, true); pendingAnimation = "unbury"; }
    private void synchronizeMovementLock() {
        if (level().isClientSide) return;
        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementLock > 0 || sleepLock > 0) { if (!speed.hasModifier(MOVEMENT_LOCK)) speed.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)); }
        else speed.removeModifier(MOVEMENT_LOCK);
    }
    @Override public void tick() {
        if (!level().isClientSide) {
            applyConfiguredAttributes();
            if (cooldown > 0 && --cooldown == 0) entityData.set(EMERGING, false);
            if (movementLock > 0) movementLock--;
            if (sleepLock > 0) sleepLock--;
            if (thrasher() && level().getNearestPlayer(getX(), getY(), getZ(), 8, true) == null) sleepLock = 5;
            synchronizeMovementLock();
        }
        super.tick();
        if (!level().isClientSide && !pendingAnimation.isEmpty()) { triggerAnim("procedure", pendingAnimation); pendingAnimation = ""; }
    }
    @Override protected void tickEffects() {
        if (!level().isClientSide || !entityData.get(EMERGING)) { super.tickEffects(); return; }
        for (var effect : new ArrayList<>(getActiveEffects())) { effect.tick(this, () -> onEffectUpdated(effect, true, null)); if (effect.getDuration() % 600 == 0) onEffectUpdated(effect, false, null); }
        boolean emit = isInvisible() ? random.nextInt(15) == 0 : random.nextBoolean();
        if (emit) LegacyEffectParticles.emit(this, -1, 2);
    }
    @Override public void aiStep() { super.aiStep(); updateSwingTime(); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean hurt(DamageSource source, float amount) { return !(source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) && super.hurt(source, amount); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }
    @Override protected void dropExperience(Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() { if (++deathTime >= (thrasher() ? 20 : 60) && !isRemoved()) { remove(RemovalReason.KILLED); if (experiencePending && level() instanceof ServerLevel server) { experiencePending = false; super.dropExperience(experienceAttacker == null ? null : server.getEntity(experienceAttacker)); } } }
    private void applyConfiguredAttributes() {
        if (level().isClientSide || attributesApplied || !BeetleConfig.SPEC.isLoaded()) return;
        int index = thrasher() ? 1 : 0;
        float previous = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(BeetleConfig.HEALTH[index].get());
        getAttribute(Attributes.ARMOR).setBaseValue(BeetleConfig.ARMOR[index].get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BeetleConfig.DAMAGE[index].get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BeetleConfig.SPEED[index].get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(BeetleConfig.RESISTANCE[index].get());
        setHealth(loadedFromSave ? Math.min(previous, getMaxHealth()) : getMaxHealth());
        attributesApplied = true;
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); applyConfiguredAttributes(); synchronizeMovementLock(); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); tag.putInt("BeetleCooldown", cooldown); tag.putInt("BeetleMovementLock", movementLock); tag.putInt("BeetleSleepLock", sleepLock); tag.putBoolean("BeetleEmerging", entityData.get(EMERGING)); tag.putBoolean("BeetleExperiencePending", experiencePending); if (experienceAttacker != null) tag.putUUID("BeetleExperienceAttacker", experienceAttacker); }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        loadedFromSave = true; super.readAdditionalSaveData(tag); attributesApplied = false;
        if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        cooldown = Math.clamp(tag.getInt("BeetleCooldown"), 0, 72000); movementLock = Math.clamp(tag.getInt("BeetleMovementLock"), 0, 35); sleepLock = Math.clamp(tag.getInt("BeetleSleepLock"), 0, 5); entityData.set(EMERGING, tag.getBoolean("BeetleEmerging") && cooldown > 0);
        var legacySleep = getEffect(MobEffects.MOVEMENT_SLOWDOWN);
        if (thrasher() && legacySleep != null && legacySleep.getAmplifier() == 255 && legacySleep.getDuration() <= 5 && legacySleep.isAmbient() && !legacySleep.isVisible()) { sleepLock = Math.max(sleepLock, Math.max(0, legacySleep.getDuration())); removeEffect(MobEffects.MOVEMENT_SLOWDOWN); }
        experiencePending = tag.getBoolean("BeetleExperiencePending"); experienceAttacker = tag.hasUUID("BeetleExperienceAttacker") ? tag.getUUID("BeetleExperienceAttacker") : null;
        applyConfiguredAttributes(); synchronizeMovementLock();
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("attack", RawAnimation.begin().thenPlay("attack")).triggerableAnim("attack2", RawAnimation.begin().thenPlay("attack2"));
        if (!thrasher()) procedure.triggerableAnim("unbury", RawAnimation.begin().thenPlay("unbury"));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop(thrasher() && (state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= .15F) ? "walk" : "idle")))); controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
