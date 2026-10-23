package com.hp.thermalshockreforged.entity.cavebloomer;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.summon.SummonLedger;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class BloomlingEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BloomlingEntity.class, EntityDataSerializers.STRING);
    private static final Identifier MOVEMENT_LOCK = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "bloomling_movement_lock");
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private UUID summonOwner;
    private long summonExpiresAt = -1;
    private boolean initialized, deathCommitted, experiencePending;
    private UUID experienceAttacker, attackTarget;
    private int cooldown, movementLock, burstKind, burstRemaining;
    private UUID burstTask;
    private int burstRetry;
    private Vec3 soundOrigin = Vec3.ZERO;
    private double targetEyeHeight, burstDamage;

    public BloomlingEntity(EntityType<? extends BloomlingEntity> type, Level level) {
        super(type, level); xpReward = dandy() ? 0 : 10;
        setTexture(modelStem()); refreshDimensions();
    }
    public boolean dandy() { return getType() == CavebloomerContent.DANDY.get(); }
    public String modelStem() { return dandy() ? "dandybloomer" : "budding_bloomer"; }
    public static AttributeSupplier.Builder createAttributes(boolean dandy) {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, dandy ? 10 : 20).add(Attributes.ARMOR, dandy ? 0 : 4)
                .add(Attributes.ATTACK_DAMAGE, 6).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.ATTACK_KNOCKBACK, 0.6).add(Attributes.STEP_HEIGHT, 1);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "budding_bloomer"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    public void setAnimation(String animation) {
        if (!"empty".equals(animation) && !"undefined".equals(animation)) triggerAnim("procedure", animation);
    }
    @Override protected EntityDimensions getDefaultDimensions(Pose pose) { return super.getDefaultDimensions(pose).scale(dandy() ? 0.8F : 1); }
    @Override protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) {
                return isTimeToAttack() && distanceToSqr(target) <= getBbWidth() * getBbWidth() + target.getBbWidth();
            }
        });
        goalSelector.addGoal(3, new PanicGoal(this, 1.5));
        goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
        targetSelector.addGoal(5, new HurtByTargetGoal(this));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        goalSelector.addGoal(7, new FloatGoal(this));
    }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    public boolean canChangeDimensions(Level from, Level to) { return burstKind == 0; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.SPIDER_STEP; }
    private SoundEvent sound(String name) { return BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, name)); }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(sound("budbloomer_ambient"), 0.15F, 1); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return sound("budbloomer_hurt"); }
    @Override protected SoundEvent getDeathSound() { return sound(dandy() ? "budbloomer_hurt" : "budbloomer_death"); }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !source.is(DamageTypes.FALL) && super.hurtServer(level, source, amount); }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData data) {
        var result = super.finalizeSpawn(world, difficulty, reason, data);
        if (!initialized && world instanceof ServerLevel server) {
            initialized = true;
            server.sendParticles(CavebloomerContent.MAGNETIC_BOOM.get(), getX(), getEyeY(), getZ(), 1, 0.001, 0.001, 0.001, 0);
            server.playSound(null, blockPosition(), SoundEvents.SHULKER_TELEPORT, SoundSource.HOSTILE, 1, 1);
        }
        return result;
    }
    @Override public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity target) {
        if (!dandy()) return super.doHurtTarget(level, target);
        beginVolley(target); return false;
    }
    public void beginVolley(Entity target) {
        if (!(level() instanceof ServerLevel server) || !dandy() || !isAlive() || target == this || !target.isAlive() || target.level() != level() || cooldown > 0 || burstKind != 0) return;
        if (expireIfDue(server)) return;
        if (summonOwner != null && summonExpiresAt < SummonLedger.clock(server) + 20) {
            summonExpiresAt = SummonLedger.clock(server) + 20;
            SummonLedger.get(server).replace(summonOwner, getUUID(), getUUID(), summonExpiresAt);
        }
        cooldown = 120; movementLock = 75; updateMovementLock(); setAnimation("volley");
        armBurst(1, 20, target.position(), target.getEyeHeight(), target.getUUID(), 1);
        server.playSound(null, BlockPos.containing(soundOrigin), SoundEvents.CREEPER_PRIMED, SoundSource.HOSTILE, 1, 3);
    }
    private void armBurst(int kind, int remaining, Vec3 sound, double eyeHeight, UUID target, double multiplier) {
        burstKind = kind; burstRemaining = remaining;
        soundOrigin = sound; targetEyeHeight = eyeHeight; attackTarget = target;
        double value = getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier;
        burstDamage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
        submitBurst();
    }
    private void submitBurst() {
        if (!(level() instanceof ServerLevel server) || burstKind == 0 || burstTask != null || burstRetry > 0 || isRemoved()) return;
        var task = BloomlingBurstEntity.submit(server, this, burstKind, burstRemaining, soundOrigin, attackTarget, targetEyeHeight, burstDamage, summonOwner == null ? -1 : summonExpiresAt);
        if (task != null) { burstTask = task.getUUID(); burstRemaining = 0; }
        else { burstRetry = 20; ThermalShockReforged.LOGGER.debug("Bloomling burst insertion failed: caster={}, kind={}", getUUID(), burstKind); }
    }
    void completeBurst(UUID task) { if (task.equals(burstTask)) { burstKind = 0; burstTask = null; burstRemaining = 0; } }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); submitBurst(); }
    @Override public void die(DamageSource source) {
        super.die(source);
        if (!dead || deathCommitted || !(level() instanceof ServerLevel)) return;
        deathCommitted = true;
        if (!dandy() && source.getEntity() != null) armBurst(2, 40, position(), 0, null, 10.0 / 6.0);
    }
    public void bindSummon(UUID owner, long expiresAt) { summonOwner = owner; summonExpiresAt = expiresAt; }
    public boolean expireIfDue(ServerLevel server) {
        if (summonOwner == null || summonExpiresAt < 0 || SummonLedger.clock(server) < summonExpiresAt) return false;
        discard(); return true;
    }
    @Override public void remove(RemovalReason reason) {
        if (burstTask != null && level() instanceof ServerLevel server && server.getEntity(burstTask) instanceof BloomlingBurstEntity task) {
            task.captureCaster(this);
            if (reason.shouldDestroy() && reason != RemovalReason.KILLED) task.cancel();
        }
        if (reason.shouldDestroy() && summonOwner != null && level() instanceof ServerLevel server) SummonLedger.get(server).release(summonOwner, getUUID());
        super.remove(reason);
    }
    @Override public void tick() {
        if (level() instanceof ServerLevel server) {
            if (expireIfDue(server)) return;
            if (cooldown > 0) cooldown--;
            if (movementLock > 0 && --movementLock == 0) updateMovementLock();
            if (burstRetry > 0) burstRetry--;
            submitBurst();
        }
        if (!isRemoved()) super.tick();
    }
    private void updateMovementLock() {
        var attribute = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementLock > 0 && !attribute.hasModifier(MOVEMENT_LOCK)) attribute.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        else if (movementLock == 0) attribute.removeModifier(MOVEMENT_LOCK);
    }
    @Override protected void dropExperience(net.minecraft.server.level.ServerLevel level, Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() {
        if (++deathTime >= (dandy() ? 20 : 40) && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) {
                experiencePending = false;
                super.dropExperience(server, experienceAttacker == null ? null : server.getEntity(experienceAttacker));
            }
        }
    }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output); if (summonOwner != null) output.putString("BloomSummonOwner", summonOwner.toString()); output.putLong("BloomSummonExpiresAt", summonExpiresAt); output.putString("Texture", texture()); output.putBoolean("BloomInitialized", initialized);
        output.putBoolean("BloomDeathCommitted", deathCommitted); output.putBoolean("BloomExperiencePending", experiencePending);
        if (experienceAttacker != null) output.putString("BloomExperienceAttacker", experienceAttacker.toString());
        if (attackTarget != null) output.putString("BloomAttackTarget", attackTarget.toString());
        output.putInt("BloomCooldown", cooldown); output.putInt("BloomMovementLock", movementLock);
        output.putInt("BloomBurstKind", burstKind); output.putInt("BloomBurstRemaining", burstRemaining); output.putBoolean("BloomBurstUsesEndTicks", true); output.putInt("BloomBurstRetry", burstRetry);
        if (burstTask != null) output.putString("BloomBurstTask", burstTask.toString());
        output.putDouble("BloomSoundX", soundOrigin.x); output.putDouble("BloomSoundY", soundOrigin.y); output.putDouble("BloomSoundZ", soundOrigin.z);
        output.putDouble("BloomTargetEyeHeight", targetEyeHeight); output.putDouble("BloomBurstDamage", burstDamage);
    }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input); summonOwner = input.getString("BloomSummonOwner").map(java.util.UUID::fromString).orElse(null); summonExpiresAt = input.getString("BloomSummonExpiresAt").isPresent() ? input.getLongOr("BloomSummonExpiresAt", 0L) : -1; if (input.getString("Texture").isPresent()) setTexture(input.getStringOr("Texture", ""));
        initialized = input.getBooleanOr("BloomInitialized", false); deathCommitted = input.getBooleanOr("BloomDeathCommitted", false); experiencePending = input.getBooleanOr("BloomExperiencePending", false);
        experienceAttacker = input.getString("BloomExperienceAttacker").map(java.util.UUID::fromString).orElse(null);
        attackTarget = input.getString("BloomAttackTarget").map(java.util.UUID::fromString).orElse(null);
        cooldown = Math.clamp(input.getIntOr("BloomCooldown", 0), 0, 120); movementLock = Math.clamp(input.getIntOr("BloomMovementLock", 0), 0, 75);
        burstKind = input.getIntOr("BloomBurstKind", 0); burstTask = input.getString("BloomBurstTask").map(java.util.UUID::fromString).orElse(null);
        burstRemaining = Math.clamp(input.getIntOr("BloomBurstRemaining", 0), 0, dandy() ? 20 : 40);
        if (burstTask == null && burstKind != 0) burstRemaining = Math.max(1, burstRemaining);
        burstRetry = Math.clamp(input.getIntOr("BloomBurstRetry", 0), 0, 20);
        if (burstKind != (dandy() ? 1 : 2)) burstKind = 0;
        double x = input.getDoubleOr("BloomSoundX", 0.0), y = input.getDoubleOr("BloomSoundY", 0.0), z = input.getDoubleOr("BloomSoundZ", 0.0);
        if (Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)) soundOrigin = new Vec3(x, y, z); else { soundOrigin = position(); burstKind = 0; }
        double value = input.getDoubleOr("BloomTargetEyeHeight", 0.0); targetEyeHeight = Double.isFinite(value) ? Math.clamp(value, 0, 100) : 0;
        value = input.getDoubleOr("BloomBurstDamage", 0.0); burstDamage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
        updateMovementLock();
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP);
        for (String clip : new String[]{"Idle", "walk", "volley"}) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            if (state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F) return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            if (!dandy() && isDeadOrDying()) return state.setAndContinue(RawAnimation.begin().thenPlay("volley"));
            return state.setAndContinue(RawAnimation.begin().thenLoop("Idle"));
        }));
        controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
