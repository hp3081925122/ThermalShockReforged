package com.hp.thermalshock.entity.infestation;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class InfestophageSeedEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(InfestophageSeedEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);

    private boolean experiencePending;
    private java.util.UUID experienceSource;

    public InfestophageSeedEntity(EntityType<? extends InfestophageSeedEntity> type, Level level) {
        super(type, level);
        xpReward = 2;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.MAX_HEALTH, 2).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0.6);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE, "texturespringy");
    }

    public String texture() { return entityData.get(TEXTURE); }

    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }

    public void setAnimation(String animation) { triggerAnim("procedure", animation); }

    @Override
    protected void registerGoals() {
        
    }

    @Override
    public SoundEvent getAmbientSound() { return SoundEvents.DRIPSTONE_BLOCK_STEP; }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.SLIME_JUMP, 0.15F, 1); }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SLIME_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.SLIME_DEATH; }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        var direct = source.getDirectEntity();
        if (direct instanceof net.minecraft.world.entity.projectile.AbstractArrow || direct instanceof Player
                || direct instanceof net.minecraft.world.entity.projectile.ThrownPotion || direct instanceof AreaEffectCloud) return false;
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS)
                || source.is(DamageTypes.DROWN) || source.is(DamageTypes.LIGHTNING_BOLT) || source.is(DamageTypes.EXPLOSION)
                || source.is(DamageTypes.TRIDENT) || source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.DRAGON_BREATH)
                || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) return false;
        return super.hurt(source, amount);
    }

    @Override
    public boolean isPushable() { return false; }

    @Override
    protected void doPush(Entity entity) { }

    @Override
    protected void pushEntities() { }

    @Override
    public void baseTick() {
        super.baseTick();
        if (!(level() instanceof net.minecraft.server.level.ServerLevel server) || isRemoved()) return;
        var outer = net.minecraft.world.phys.AABB.ofSize(position(), 18, 18, 18);
        var inner = net.minecraft.world.phys.AABB.ofSize(position(), 10, 10, 10);
        var players = server.getEntitiesOfClass(Player.class, outer, entity -> true);
        if (!players.isEmpty() && random.nextDouble() < 0.05) {
            server.levelEvent(2001, blockPosition(), net.minecraft.world.level.block.Block.getId(server.getBlockState(blockPosition().below())));
        }
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (var player : players) {
            if (!player.getBoundingBox().intersects(inner)) continue;
            double distance = distanceToSqr(player);
            if (distance < nearestDistance) {
                nearest = player;
                nearestDistance = distance;
            }
        }
        if (!(nearest instanceof net.minecraft.server.level.ServerPlayer player)
                || player.gameMode.getGameModeForPlayer() != net.minecraft.world.level.GameType.SURVIVAL) return;
        discard();
        var spawned = InfestationEntities.INFESTOPHAGE.get().spawn(server, blockPosition(), MobSpawnType.MOB_SUMMONED);
        if (spawned != null) spawned.setDeltaMovement(0, 0.3, 0);
        server.playSound(null, blockPosition(), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.NEUTRAL, 0.5F, 0);
        server.sendParticles(net.minecraft.core.particles.ParticleTypes.POOF, getX(), getY(), getZ(), 30, 0.8, 1, 0.8, 0.2);
    }

    @Override
    protected void dropExperience(Entity source) {
        experiencePending = true;
        experienceSource = source == null ? null : source.getUUID();
    }

    @Override
    protected void tickDeath() {
        deathTime++;
        if (deathTime >= 40 && level() instanceof net.minecraft.server.level.ServerLevel server && !isRemoved()) {
            if (experiencePending) {
                experiencePending = false;
                super.dropExperience(experienceSource == null ? null : server.getEntity(experienceSource));
            }
            server.broadcastEntityEvent(this, (byte) 60);
            remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        updateSwingTime();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Texture", texture());
        tag.putBoolean("ExperiencePending", experiencePending);
        if (experienceSource != null) tag.putUUID("ExperienceSource", experienceSource);
        tag.putInt("SeedDeathTime", deathTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        experiencePending = tag.getBoolean("ExperiencePending");
        experienceSource = tag.hasUUID("ExperienceSource") ? tag.getUUID("ExperienceSource") : null;
        deathTime = Math.max(0, Math.min(40, tag.getInt("SeedDeathTime")));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP)
                .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"))
                .triggerableAnim("walk", RawAnimation.begin().thenPlay("walk"));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation()
                ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
        controllers.add(procedure);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
