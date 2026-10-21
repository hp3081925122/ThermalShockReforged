package com.hp.thermalshockreforged.entity;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class GalvaniteEntity extends Monster implements GeoEntity {
    private static final ResourceLocation SQUALL = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "squall");
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(GalvaniteEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private boolean initialized;
    private boolean deathCommitted;
    private boolean experiencePending;
    private UUID experienceAttacker;

    public GalvaniteEntity(EntityType<? extends GalvaniteEntity> type, Level level) { super(type, level); xpReward = 10; }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 12).add(Attributes.ARMOR, 6).add(Attributes.ATTACK_DAMAGE, 7)
                .add(Attributes.MOVEMENT_SPEED, 0.15).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.ATTACK_KNOCKBACK, 0.8).add(Attributes.STEP_HEIGHT, 1.6);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "galvanitenoglow"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    public void setAnimation(String animation) {
        if (!"empty".equals(animation) && !"undefined".equals(animation)) triggerAnim("procedure", animation);
    }
    @Override protected EntityDimensions getDefaultDimensions(Pose pose) { return super.getDefaultDimensions(pose).withEyeHeight(0.8F); }
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) {
                return isTimeToAttack() && distanceToSqr(target) <= getBbWidth() * getBbWidth() + target.getBbWidth();
            }
        });
        targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        goalSelector.addGoal(3, new RandomStrollGoal(this, 1));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        goalSelector.addGoal(6, new AvoidEntityGoal<>(this, Monster.class, 32, 1, 1.2,
                entity -> SQUALL.equals(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()))) {
            @Override public boolean canUse() { return BuiltInRegistries.ENTITY_TYPE.getOptional(SQUALL).isPresent() && super.canUse(); }
        });
    }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.CHAIN_PLACE, 0.15F, 1); }
    private SoundEvent sound(String path) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path)); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return sound("galvanitehurt"); }
    @Override protected SoundEvent getDeathSound() { return sound("galvanitedeath"); }
    @Override public boolean hurt(DamageSource source, float amount) { return !source.is(DamageTypes.LIGHTNING_BOLT) && super.hurt(source, amount); }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        if (!initialized && level instanceof ServerLevel server) {
            initialized = true;
            server.sendParticles(ModEntities.TELEPORT_FLASH.get(), getX(), getEyeY(), getZ(), 1, 0.001, 0.001, 0.001, 1);
            server.playSound(null, blockPosition(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.HOSTILE, 1, 1);
        }
        return result;
    }
    @Override public void die(DamageSource source) {
        super.die(source);
        if (!dead || deathCommitted || !(level() instanceof ServerLevel server)) return;
        deathCommitted = true;
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
        super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); tag.putBoolean("GalvaniteInitialized", initialized);
        tag.putBoolean("GalvaniteDeathCommitted", deathCommitted); tag.putBoolean("GalvaniteExperiencePending", experiencePending);
        if (experienceAttacker != null) tag.putUUID("GalvaniteExperienceAttacker", experienceAttacker);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag); if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        initialized = tag.getBoolean("GalvaniteInitialized"); deathCommitted = tag.getBoolean("GalvaniteDeathCommitted");
        experiencePending = tag.getBoolean("GalvaniteExperiencePending");
        experienceAttacker = tag.hasUUID("GalvaniteExperienceAttacker") ? tag.getUUID("GalvaniteExperienceAttacker") : null;
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP)
                .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"))
                .triggerableAnim("walk", RawAnimation.begin().thenPlay("walk"))
                .triggerableAnim("run", RawAnimation.begin().thenPlay("run"));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            boolean moving = state.isMoving();
            if ((moving || state.getLimbSwingAmount() <= -0.15F || state.getLimbSwingAmount() >= 0.15F) && !isAggressive()) return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            if (isAggressive() && moving) return state.setAndContinue(RawAnimation.begin().thenLoop("run"));
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
        controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
