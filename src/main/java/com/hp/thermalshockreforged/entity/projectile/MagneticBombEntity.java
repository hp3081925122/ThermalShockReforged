package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class MagneticBombEntity extends PathfinderMob implements GeoEntity, ServerEndTickTask {
    private static final EntityDataAccessor<Boolean> DETONATING = SynchedEntityData.defineId(MagneticBombEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(MagneticBombEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int fuse = -1;
    public MagneticBombEntity(EntityType<? extends MagneticBombEntity> type, Level level) { super(type, level); xpReward = 0; }
    public boolean epsilon() { return getType() == ProjectileContent.EPSILON_BOMB.get(); }
    public boolean detonating() { return entityData.get(DETONATING); }
    public String texture() { return entityData.get(TEXTURE); }
    public static AttributeSupplier.Builder attributes(boolean epsilon) {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, epsilon ? 0 : 10).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.STEP_HEIGHT, 0);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder); builder.define(DETONATING, false); builder.define(TEXTURE, "magnetic_bomb_base");
    }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        setNoGravity(true); fuse = 40;
        if (level instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
        return result;
    }
    @Override public void baseTick() {
        super.baseTick();
        if (!epsilon() && level().isClientSide) level().addParticle(ProjectileContent.GREEN_PARTICLE.get(), getX(), getY(), getZ(), 0, 0, 0);
    }
    @Override public void tick() {
        super.tick();
        if (!isRemoved() && !detonating() && fuse > 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override public void endTick(ServerLevel level) { if (!isRemoved() && !detonating() && fuse > 0 && --fuse == 0) prime(level); }
    private void prime(ServerLevel level) {
        if (detonating()) return;
        if (!MagneticDetonationEntity.submit(level, this)) {
            fuse = 1;
            ThermalShockReforged.LOGGER.warn("Unable to submit magnetic detonation for {}", getUUID());
            return;
        }
        entityData.set(DETONATING, true); fuse = 0;
        ThermalShockReforged.LOGGER.debug("Magnetic detonation committed once for {} (epsilon={})", getUUID(), epsilon());
    }
    @Override public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS)
                || source.is(DamageTypes.DROWN) || source.is(DamageTypes.LIGHTNING_BOLT) || source.is(DamageTypes.FALLING_ANVIL)
                || source.is(DamageTypes.DRAGON_BREATH) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)
                || source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud) return false;
        return super.hurt(source, amount);
    }
    @Override public void die(DamageSource source) { super.die(source); if (level() instanceof ServerLevel server) prime(server); }
    @Override protected void tickDeath() { if (++deathTime == 10) { remove(Entity.RemovalReason.KILLED); dropExperience(getLastDamageSource() == null ? null : getLastDamageSource().getEntity()); } }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); tag.putInt("MagneticFuse", fuse); tag.putBoolean("MagneticDetonating", detonating()); tag.putString("Texture", texture());
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag); fuse = tag.contains("MagneticFuse") ? Math.clamp(tag.getInt("MagneticFuse"), -1, 40) : -1;
        entityData.set(DETONATING, tag.getBoolean("MagneticDetonating"));
        if (tag.contains("Texture") && tag.getString("Texture").matches("[a-z0-9_]+")) entityData.set(TEXTURE, tag.getString("Texture"));
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 4, state -> detonating() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
        controllers.add(new AnimationController<>(this, "procedure", 4, state -> detonating() ? state.setAndContinue(RawAnimation.begin().thenPlay("1")) : PlayState.STOP));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
