package com.hp.thermalshockreforged.entity.sanctum;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.ResourceLocation;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class KilolyteEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(KilolyteEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(KilolyteEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Long> ANIMATION_START = SynchedEntityData.defineId(KilolyteEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Integer> ANIMATION_SEQUENCE = SynchedEntityData.defineId(KilolyteEntity.class, EntityDataSerializers.INT);
    private int animationRemaining;
    private double clientAnimationOffset;
    private String clientClip = "empty";
    private int birthProtection;
    private boolean initialized;
    private boolean attributesApplied;
    private boolean loadedFromSave;
    private int cooldown;
    private int movementLock;
    private boolean experiencePending;
    private UUID experienceAttacker;

    public KilolyteEntity(EntityType<? extends KilolyteEntity> type, Level level) { super(type, level); xpReward = 100; setPersistenceRequired(); refreshDimensions(); applyConfiguredAttributes(); }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 100).add(Attributes.ARMOR, 10).add(Attributes.ATTACK_DAMAGE, 13)
                .add(Attributes.MOVEMENT_SPEED, .17).add(Attributes.FOLLOW_RANGE, 12).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, .6);
    }
    private void applyConfiguredAttributes() {
        if (level().isClientSide || attributesApplied || !SanctumConfig.SPEC.isLoaded()) return;
        float health = getHealth(); var config = SanctumConfig.KILOLYTE;
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(config.health().get());
        getAttribute(Attributes.ARMOR).setBaseValue(config.armor().get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(config.damage().get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(config.speed().get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(config.resistance().get());
        setHealth(loadedFromSave ? Math.min(health, getMaxHealth()) : getMaxHealth());
        attributesApplied = true;
        ThermalShockReforged.LOGGER.debug("Kilolyte attributes applied: entity={}, loaded={}, health={}, attackDamage={}", getUUID(), loadedFromSave, getHealth(), getAttributeValue(Attributes.ATTACK_DAMAGE));
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); applyConfiguredAttributes(); }
    @Override public SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        if (!initialized && level instanceof ServerLevel server) {
            initialized = true; birthProtection = 70; setAnimation("spawn");
            server.sendParticles(com.hp.thermalshockreforged.entity.icesquad.IceSquadContent.FOG_CLOUD.get(), getX(), getY() - .5, getZ(), 1, .3, 0, .3, 0);
        }
        return result;
    }
    private static final ResourceLocation MOVEMENT_LOCK = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "kilolyte_movement_lock");
    public void beginAttack(LivingEntity victim) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || victim == this || cooldown > 0 || victim.level() != level()) return;
        boolean ring = random.nextDouble() < SanctumConfig.KILOLYTE.ringChance().get();
        if (!KilolyteAttackEntity.submit(server, this, victim, ring)) {
            ThermalShockReforged.LOGGER.warn("Kilolyte attack could not be scheduled: caster={}, target={}", getUUID(), victim.getUUID());
            return;
        }
        cooldown = ring ? 35 : 27;
        movementLock = ring ? 45 : 30;
        syncMovementLock();
        setAnimation(ring ? "attack" : "attack2");
        server.playSound(null, victim.blockPosition(), SoundEvents.WITHER_HURT, net.minecraft.sounds.SoundSource.HOSTILE, 1, ring ? -3 : 3);
    }
    @Override public void baseTick() {
        super.baseTick();
        if (level().isClientSide) return;
        if (birthProtection > 0) birthProtection--;
        if (animationRemaining > 0 && --animationRemaining == 0) {
            entityData.set(ANIMATION, "empty"); entityData.set(ANIMATION_SEQUENCE, entityData.get(ANIMATION_SEQUENCE) + 1);
        }
        if (cooldown > 0) cooldown--;
        if (movementLock > 0) movementLock--;
        syncMovementLock();
    }
    private void syncMovementLock() {
        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementLock > 0 && !speed.hasModifier(MOVEMENT_LOCK)) speed.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        if (movementLock <= 0) speed.removeModifier(MOVEMENT_LOCK);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder); builder.define(TEXTURE, "kilolyte"); builder.define(ANIMATION, "empty");
        builder.define(ANIMATION_START, 0L); builder.define(ANIMATION_SEQUENCE, 0);
    }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    public void setAnimation(String animation) {
        int duration = duration(animation);
        if (duration <= 0 || level().isClientSide) return;
        animationRemaining = duration; entityData.set(ANIMATION, animation); entityData.set(ANIMATION_START, level().getGameTime());
        entityData.set(ANIMATION_SEQUENCE, entityData.get(ANIMATION_SEQUENCE) + 1);
    }
    private static int duration(String clip) {
        return switch (clip) { case "spawn" -> 74; case "attack" -> 31; case "attack2" -> 29; case "idle" -> 44; case "walk" -> 30; case "death" -> 6; default -> 0; };
    }
    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (!level().isClientSide || !ANIMATION_SEQUENCE.equals(accessor)) return;
        if (!"empty".equals(clientClip)) stopTriggeredAnim("procedure", clientClip);
        clientClip = entityData.get(ANIMATION);
        clientAnimationOffset = Math.max(0, level().getGameTime() - entityData.get(ANIMATION_START));
        if (!"empty".equals(clientClip)) triggerAnim("procedure", clientClip);
    }
    @Override protected EntityDimensions getDefaultDimensions(Pose pose) { return super.getDefaultDimensions(pose).scale(2); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && distanceToSqr(target) <= 4.84; }
        });
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 12));
        targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers());
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.IRON_GOLEM_STEP, .15F, 1); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.WITHER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.WITHER_DEATH; }
    @Override public boolean hurt(DamageSource source, float amount) { return !source.is(DamageTypes.DROWN) && super.hurt(source, amount); }
    @Override protected float getDamageAfterMagicAbsorb(DamageSource source, float amount) {
        if (birthProtection > 0 && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_EFFECTS) && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_RESISTANCE)) {
            if (Float.isFinite(amount) && amount > 0) {
                damageContainers.peek().setReduction(net.neoforged.neoforge.common.damagesource.DamageContainer.Reduction.MOB_EFFECTS, amount);
                if (amount < 3.4028235E37F && source.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) player.awardStat(net.minecraft.stats.Stats.DAMAGE_DEALT_RESISTED, Math.round(amount * 10));
            } else damageContainers.peek().setNewDamage(0);
            return 0;
        }
        return super.getDamageAfterMagicAbsorb(source, amount);
    }
    @Override protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        spawnAtLocation(new net.minecraft.world.item.ItemStack(com.hp.thermalshockreforged.content.weapons.WeaponContent.ZAP_GAUNTLET.get()));
    }
    @Override protected void dropExperience(Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() {
        if (++deathTime >= 20 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) {
                experiencePending = false;
                super.dropExperience(experienceAttacker == null ? null : server.getEntity(experienceAttacker));
            }
        }
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("KilolyteInitialized", initialized); tag.putInt("KilolyteBirthProtection", birthProtection);
        tag.putString("KilolyteAnimation", entityData.get(ANIMATION)); tag.putInt("KilolyteAnimationRemaining", animationRemaining); tag.putString("Texture", texture()); tag.putInt("KilolyteCooldown", cooldown); tag.putInt("KilolyteMovementLock", movementLock);
        tag.putBoolean("KilolyteExperiencePending", experiencePending);
        if (experienceAttacker != null) tag.putUUID("KilolyteExperienceAttacker", experienceAttacker);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        loadedFromSave = true; attributesApplied = false;
        super.readAdditionalSaveData(tag);
        initialized = tag.getBoolean("KilolyteInitialized"); birthProtection = Math.clamp(tag.getInt("KilolyteBirthProtection"), 0, 70);
        String clip = tag.getString("KilolyteAnimation"); animationRemaining = Math.clamp(tag.getInt("KilolyteAnimationRemaining"), 0, duration(clip));
        if (animationRemaining > 0) {
            entityData.set(ANIMATION, clip); entityData.set(ANIMATION_START, level().getGameTime() - duration(clip) + animationRemaining);
            entityData.set(ANIMATION_SEQUENCE, entityData.get(ANIMATION_SEQUENCE) + 1);
        }
        applyConfiguredAttributes();
        if (!tag.contains("NeoForgeData", 10) && tag.contains("ForgeData", 10)) getPersistentData().merge(tag.getCompound("ForgeData"));
        if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        cooldown = Math.clamp(tag.getInt("KilolyteCooldown"), 0, 35);
        movementLock = Math.clamp(tag.getInt("KilolyteMovementLock"), 0, 45);
        if (!level().isClientSide) syncMovementLock();
        experiencePending = tag.getBoolean("KilolyteExperiencePending");
        experienceAttacker = tag.hasUUID("KilolyteExperienceAttacker") ? tag.getUUID("KilolyteExperienceAttacker") : null;
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new PhaseController();
        for (String clip : java.util.List.of("idle", "walk", "attack", "attack2", "spawn", "death")) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            if ((state.isMoving() || state.getLimbSwingAmount() <= -.15F || state.getLimbSwingAmount() >= .15F)) return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
        controllers.add(procedure);
    }
    private final class PhaseController extends AnimationController<KilolyteEntity> {
        private PhaseController() { super(KilolyteEntity.this, "procedure", 4, state -> PlayState.STOP); }
        @Override protected double adjustTick(double tick) {
            double offset = getAnimationState() == State.TRANSITIONING ? clientAnimationOffset : Math.max(0, clientAnimationOffset - transitionLength);
            return super.adjustTick(tick) + offset;
        }
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
