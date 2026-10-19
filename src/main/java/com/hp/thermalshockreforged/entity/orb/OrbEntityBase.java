package com.hp.thermalshockreforged.entity.orb;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Explosion;

public abstract class OrbEntityBase extends Monster {
    private int graceTicks = 100;

    protected OrbEntityBase(EntityType<? extends OrbEntityBase> type, Level level, String name) {
        super(type, level);
        xpReward = 0;
        setNoAi(false);
        setCustomName(Component.literal(name));
        setCustomNameVisible(true);
        moveControl = new FlyingMoveControl(this, 10, true);
        setNoGravity(true);
    }

    @Override protected PathNavigation createNavigation(Level level) { return new FlyingPathNavigation(this, level); }
    @Override protected void registerGoals() { super.registerGoals(); }
    @Override public boolean causeFallDamage(float distance, float multiplier, DamageSource source) { return false; }
    @Override public boolean ignoreExplosion(Explosion explosion) { return true; }
    @Override public boolean fireImmune() { return true; }
    @Override public boolean isPushable() { return false; }
    @Override protected void doPush(Entity entity) { }
    @Override protected void pushEntities() { }
    @Override protected void checkFallDamage(double distance, boolean onGround, BlockState state, BlockPos pos) { }
    @Override public void setNoGravity(boolean ignored) { super.setNoGravity(true); }
    @Override public void aiStep() { super.aiStep(); setNoGravity(true); }

    @Override public boolean hurt(DamageSource source, float amount) {
        Entity direct = source.getDirectEntity();
        if (source.is(DamageTypes.IN_FIRE) || direct instanceof AbstractArrow || direct instanceof Player
                || direct instanceof ThrownPotion || direct instanceof AreaEffectCloud || source.is(DamageTypes.FALL)
                || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.LIGHTNING_BOLT)
                || source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION) || source.is(DamageTypes.TRIDENT)
                || source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.DRAGON_BREATH) || source.is(DamageTypes.WITHER)
                || source.is(DamageTypes.WITHER_SKULL)) return false;
        return super.hurt(source, amount);
    }

    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty, net.minecraft.world.entity.MobSpawnType reason, SpawnGroupData group) {
        graceTicks = 100;
        return super.finalizeSpawn(level, difficulty, reason, group);
    }

    @Override public void baseTick() {
        super.baseTick();
        applyPlayerEffect();
        setDeltaMovement(getLookAngle().scale(.5));
        if (!level().isClientSide && --graceTicks <= 0) discard();
    }

    private void applyPlayerEffect() {
        for (Entity entity : level().getEntities(this, getBoundingBox().inflate(.5), entity -> entity instanceof Player player && canAffect(player))) {
            if (entity instanceof Player player) {
                player.hurt(new DamageSource(level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.INDIRECT_MAGIC)), 5.0F);
                if (level() instanceof net.minecraft.server.level.ServerLevel server) spawnImpactParticles(server);
            }
        }
    }

    protected abstract boolean canAffect(Player player);
    protected abstract void spawnImpactParticles(net.minecraft.server.level.ServerLevel level);

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("OrbGraceTicks", Math.max(0, Math.min(100, graceTicks)));
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        graceTicks = Math.max(0, Math.min(100, tag.getInt("OrbGraceTicks")));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.FLYING_SPEED, .3);
    }
}
