package com.hp.thermalshockreforged.entity.snobol;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.server.level.ServerLevel;

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

public final class ExplosiveSnobolEntity extends Monster implements GeoEntity, ServerEndTickTask {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ExplosiveSnobolEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);

    private int fuseRemaining;
    private net.minecraft.world.phys.Vec3 fuseOrigin;
    private double fuseDamage;
    private String fuseDimension = "";

    public ExplosiveSnobolEntity(EntityType<? extends ExplosiveSnobolEntity> type, Level level) {
        super(type, level);
        xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.MAX_HEALTH, 4).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 1).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0.6);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE, "boomsnobol");
    }

    public String texture() { return entityData.get(TEXTURE); }

    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }

    public void setAnimation(String animation) { triggerAnim("procedure", animation); }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new RandomStrollGoal(this, 1));
        goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        goalSelector.addGoal(3, new FloatGoal(this));
        goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.5F));
        goalSelector.addGoal(5, new AvoidEntityGoal<>(this, LivingEntity.class, 6, 1, 1.2, entity -> SnobolContent.hasType(entity, "monolith")));
        goalSelector.addGoal(6, new AvoidEntityGoal<>(this, Player.class, 6, 1, 1.2));
    }

    @Override
    public SoundEvent getAmbientSound() { return SoundEvents.RABBIT_AMBIENT; }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.RABBIT_JUMP, 0.15F, 1); }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.RABBIT_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.RABBIT_DEATH; }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level() instanceof net.minecraft.server.level.ServerLevel server && isAlive() && fuseRemaining == 0
                && amount > 0 && !source.is(DamageTypes.FALL) && !isInvulnerableTo(source)) {
            fuseRemaining = 20;
            fuseDimension = server.dimension().location().toString();
            fuseOrigin = position();
            fuseDamage = getAttributeValue(Attributes.ATTACK_DAMAGE);
            setAnimation("bounce");
            server.playSound(null, blockPosition(), SoundEvents.TNT_PRIMED, SoundSource.NEUTRAL, 1, 3);
            ServerEndTickTasks.schedule(server, getUUID());
            ThermalShockReforged.LOGGER.debug("Explosive Snobol fuse armed: entity={}, remainingEndTicks={}, origin={}", getUUID(), fuseRemaining, fuseOrigin);
        }
        return !source.is(DamageTypes.FALL) && super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        if (fuseRemaining > 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (fuseRemaining > 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }

    @Override
    public void endTick(ServerLevel server) {
        if (isRemoved() || fuseRemaining <= 0) return;
        if (!isAlive() || !fuseDimension.equals(server.dimension().location().toString())) {
            fuseRemaining = 0;
            ThermalShockReforged.LOGGER.debug("Explosive Snobol fuse cancelled: entity={}, alive={}, dimension={}", getUUID(), isAlive(), server.dimension().location());
            return;
        }
        if (--fuseRemaining == 0) {
            var origin = fuseOrigin == null ? position() : fuseOrigin;
            discard();
            SnobolBlastEntity.detonate(server, origin, fuseDamage);
        } else ServerEndTickTasks.schedule(server, getUUID());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Texture", texture());
        tag.putInt("FuseRemaining", fuseRemaining);
        if (fuseOrigin != null) {
            tag.putDouble("FuseX", fuseOrigin.x);
            tag.putDouble("FuseY", fuseOrigin.y);
            tag.putDouble("FuseZ", fuseOrigin.z);
        }
        tag.putDouble("FuseDamage", fuseDamage);
        tag.putString("FuseDimension", fuseDimension);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        fuseRemaining = tag.getString("FuseDimension").equals(level().dimension().location().toString())
                ? Math.max(0, Math.min(20, tag.getInt("FuseRemaining"))) : 0;
        fuseOrigin = tag.contains("FuseX") ? new net.minecraft.world.phys.Vec3(tag.getDouble("FuseX"), tag.getDouble("FuseY"), tag.getDouble("FuseZ")) : position();
        fuseDamage = Math.max(0, tag.getDouble("FuseDamage"));
        if (!Double.isFinite(fuseDamage)) fuseDamage = 0;
        fuseDimension = tag.getString("FuseDimension");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP)
                .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"))
                .triggerableAnim("walk", RawAnimation.begin().thenPlay("walk"))
                .triggerableAnim("bounce", RawAnimation.begin().thenPlay("bounce"));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation()
                ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop(state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F ? "walk" : "idle"))));
        controllers.add(procedure);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
