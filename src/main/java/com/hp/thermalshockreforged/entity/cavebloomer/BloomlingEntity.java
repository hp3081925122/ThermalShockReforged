package com.hp.thermalshockreforged.entity.cavebloomer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.Comparator;
import com.hp.thermalshockreforged.entity.summon.SummonLedger;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class BloomlingEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BloomlingEntity.class, EntityDataSerializers.STRING);
    private static final ResourceLocation MOVEMENT_LOCK = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "bloomling_movement_lock");
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private UUID summonOwner;
    private long summonExpiresAt = -1;
    private boolean initialized, deathCommitted, experiencePending;
    private UUID experienceAttacker, attackTarget;
    private int cooldown, movementLock, burstKind, burstRemaining;
    private long burstStartedAt;
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
    @Override public boolean canChangeDimensions(Level from, Level to) { return burstKind == 0 && super.canChangeDimensions(from, to); }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.SPIDER_STEP; }
    private SoundEvent sound(String name) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, name)); }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(sound("budbloomer_ambient"), 0.15F, 1); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return sound("budbloomer_hurt"); }
    @Override protected SoundEvent getDeathSound() { return sound(dandy() ? "budbloomer_hurt" : "budbloomer_death"); }
    @Override public boolean hurt(DamageSource source, float amount) { return !source.is(DamageTypes.FALL) && super.hurt(source, amount); }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data) {
        var result = super.finalizeSpawn(world, difficulty, reason, data);
        if (!initialized && world instanceof ServerLevel server) {
            initialized = true;
            server.sendParticles(CavebloomerContent.MAGNETIC_BOOM.get(), getX(), getEyeY(), getZ(), 1, 0.001, 0.001, 0.001, 0);
            server.playSound(null, blockPosition(), SoundEvents.SHULKER_TELEPORT, SoundSource.HOSTILE, 1, 1);
        }
        return result;
    }
    @Override public boolean doHurtTarget(Entity target) {
        if (!dandy()) return super.doHurtTarget(target);
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
        armBurst(1, 19, target.position(), target.getEyeHeight(), target.getUUID(), 1);
        server.playSound(null, BlockPos.containing(soundOrigin), SoundEvents.CREEPER_PRIMED, SoundSource.HOSTILE, 1, 3);
    }
    private void armBurst(int kind, int remaining, Vec3 sound, double eyeHeight, UUID target, double multiplier) {
        burstKind = kind; burstRemaining = remaining; burstStartedAt = level().getGameTime();
        soundOrigin = sound; targetEyeHeight = eyeHeight; attackTarget = target;
        double value = getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier;
        burstDamage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
    }
    @Override public void die(DamageSource source) {
        super.die(source);
        if (!dead || deathCommitted || !(level() instanceof ServerLevel)) return;
        deathCommitted = true;
        if (!dandy() && source.getEntity() != null) armBurst(2, 39, position(), 0, null, 10.0 / 6.0);
    }
    public void bindSummon(UUID owner, long expiresAt) { summonOwner = owner; summonExpiresAt = expiresAt; }
    public boolean expireIfDue(ServerLevel server) {
        if (summonOwner == null || summonExpiresAt < 0 || SummonLedger.clock(server) < summonExpiresAt) return false;
        discard(); return true;
    }
    @Override public void remove(RemovalReason reason) {
        if (reason.shouldDestroy() && summonOwner != null && level() instanceof ServerLevel server) SummonLedger.get(server).release(summonOwner, getUUID());
        super.remove(reason);
    }
    @Override public void tick() {
        if (level() instanceof ServerLevel server) {
            if (expireIfDue(server)) return;
            if (cooldown > 0) cooldown--;
            if (movementLock > 0 && --movementLock == 0) updateMovementLock();
            if (burstKind != 0 && level().getGameTime() != burstStartedAt && --burstRemaining <= 0) {
                int kind = burstKind; burstKind = 0; burstRemaining = 0;
                if (kind == 1) explodeDandy(server); else explodeBudding(server);
            }
        }
        if (!isRemoved()) super.tick();
    }
    private void updateMovementLock() {
        var attribute = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementLock > 0 && !attribute.hasModifier(MOVEMENT_LOCK)) attribute.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        else if (movementLock == 0) attribute.removeModifier(MOVEMENT_LOCK);
    }
    private void explodeDandy(ServerLevel server) {
        server.playSound(null, BlockPos.containing(soundOrigin), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.HOSTILE, 1, 1);
        Entity target = attackTarget == null ? null : server.getEntity(attackTarget);
        double eye = target == null ? targetEyeHeight : target.getEyeHeight();
        server.sendParticles(ParticleTypes.EXPLOSION, soundOrigin.x, soundOrigin.y + eye, soundOrigin.z, 4, 0.7, 0.7, 0.7, 0);
        var center = position();
        var victims = server.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(3), entity -> entity != this && !(entity instanceof ItemEntity)
                && !(entity instanceof BloomlingEntity bloom && bloom.dandy()) && !CavebloomerContent.isBoss(entity));
        victims.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)));
        DamageSource damage = damageSources().source(DamageTypes.EXPLOSION);
        for (var victim : victims) {
            if (victim instanceof LivingEntity living && living.isBlocking()) CavebloomerCombat.damageBlockingItem(living, 1);
            else {
                victim.hurt(damage, (float)burstDamage);
                if (victim instanceof LivingEntity living) living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 220, 1));
            }
        }
        discard();
    }
    private void explodeBudding(ServerLevel server) {
        server.playSound(null, BlockPos.containing(soundOrigin), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1, 1);
        server.playSound(null, BlockPos.containing(soundOrigin), SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.HOSTILE, 1, 1);
        server.sendParticles(CavebloomerContent.RED_EXPLODE.get(), getX(), getEyeY(), getZ(), 3, 1, 1, 1, 0);
        server.sendParticles(CavebloomerContent.MAGNETIC_BOOM.get(), getX(), getEyeY(), getZ(), 3, 1, 1, 1, 0);
        var center = position();
        var victims = server.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(4), entity -> !(entity instanceof BloomlingEntity bloom && !bloom.dandy()));
        victims.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)));
        DamageSource damage = damageSources().source(DamageTypes.EXPLOSION);
        var look = getLookAngle();
        for (var victim : victims) {
            if (victim instanceof LivingEntity living && living.isBlocking()) CavebloomerCombat.damageBlockingItem(living, 10);
            else {
                victim.hurt(damage, (float)burstDamage);
                CavebloomerCombat.setResistedVelocity(victim, new Vec3(look.x * 0.1, 0.3, look.z * 0.1));
            }
        }
    }
    @Override protected void dropExperience(Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() {
        if (++deathTime >= (dandy() ? 20 : 40) && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) {
                experiencePending = false;
                super.dropExperience(experienceAttacker == null ? null : server.getEntity(experienceAttacker));
            }
        }
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); if (summonOwner != null) tag.putUUID("BloomSummonOwner", summonOwner); tag.putLong("BloomSummonExpiresAt", summonExpiresAt); tag.putString("Texture", texture()); tag.putBoolean("BloomInitialized", initialized);
        tag.putBoolean("BloomDeathCommitted", deathCommitted); tag.putBoolean("BloomExperiencePending", experiencePending);
        if (experienceAttacker != null) tag.putUUID("BloomExperienceAttacker", experienceAttacker);
        if (attackTarget != null) tag.putUUID("BloomAttackTarget", attackTarget);
        tag.putInt("BloomCooldown", cooldown); tag.putInt("BloomMovementLock", movementLock);
        tag.putInt("BloomBurstKind", burstKind); tag.putInt("BloomBurstRemaining", burstRemaining); tag.putLong("BloomBurstStartedAt", burstStartedAt);
        tag.putDouble("BloomSoundX", soundOrigin.x); tag.putDouble("BloomSoundY", soundOrigin.y); tag.putDouble("BloomSoundZ", soundOrigin.z);
        tag.putDouble("BloomTargetEyeHeight", targetEyeHeight); tag.putDouble("BloomBurstDamage", burstDamage);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag); summonOwner = tag.hasUUID("BloomSummonOwner") ? tag.getUUID("BloomSummonOwner") : null; summonExpiresAt = tag.contains("BloomSummonExpiresAt") ? tag.getLong("BloomSummonExpiresAt") : -1; if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        initialized = tag.getBoolean("BloomInitialized"); deathCommitted = tag.getBoolean("BloomDeathCommitted"); experiencePending = tag.getBoolean("BloomExperiencePending");
        experienceAttacker = tag.hasUUID("BloomExperienceAttacker") ? tag.getUUID("BloomExperienceAttacker") : null;
        attackTarget = tag.hasUUID("BloomAttackTarget") ? tag.getUUID("BloomAttackTarget") : null;
        cooldown = Math.clamp(tag.getInt("BloomCooldown"), 0, 120); movementLock = Math.clamp(tag.getInt("BloomMovementLock"), 0, 75);
        burstKind = tag.getInt("BloomBurstKind"); burstRemaining = Math.clamp(tag.getInt("BloomBurstRemaining"), 0, 39); burstStartedAt = tag.getLong("BloomBurstStartedAt");
        if (burstKind != (dandy() ? 1 : 2)) burstKind = 0;
        double x = tag.getDouble("BloomSoundX"), y = tag.getDouble("BloomSoundY"), z = tag.getDouble("BloomSoundZ");
        if (Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)) soundOrigin = new Vec3(x, y, z); else { soundOrigin = position(); burstKind = 0; }
        double value = tag.getDouble("BloomTargetEyeHeight"); targetEyeHeight = Double.isFinite(value) ? Math.clamp(value, 0, 100) : 0;
        value = tag.getDouble("BloomBurstDamage"); burstDamage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
        updateMovementLock();
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP);
        for (String clip : new String[]{"Idle", "walk", "volley"}) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            if (state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F) return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            if (!dandy() && isDeadOrDying()) return state.setAndContinue(RawAnimation.begin().thenPlay("volley"));
            return state.setAndContinue(RawAnimation.begin().thenLoop("Idle"));
        }));
        controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
