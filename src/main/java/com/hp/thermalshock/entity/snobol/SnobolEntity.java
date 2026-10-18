package com.hp.thermalshock.entity.snobol;

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

public final class SnobolEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(SnobolEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);

    public SnobolEntity(EntityType<? extends SnobolEntity> type, Level level) {
        super(type, level);
        xpReward = 3;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.MAX_HEALTH, 4).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0.6);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE, "texturesnobol");
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
    public boolean hurt(DamageSource source, float amount) { return !source.is(DamageTypes.FALL) && super.hurt(source, amount); }

    @Override
    public net.minecraft.world.InteractionResult mobInteract(Player player, net.minecraft.world.InteractionHand hand) {
        var parent = super.mobInteract(player, hand);
        if (parent.consumesAction() || player.isShiftKeyDown()) return parent;
        var held = player.getItemInHand(hand);
        boolean explosive = held.is(net.minecraft.world.item.Items.TNT);
        boolean toxic = held.is(SnobolContent.ANTITOXIN.get());
        if (!explosive && !toxic) return parent;
        if (level() instanceof net.minecraft.server.level.ServerLevel server) {
            held.shrink(1);
            if (toxic) SnobolContent.give(player, new net.minecraft.world.item.ItemStack(com.hp.thermalshock.content.MaterialContent.items().get("corrial_flask").get()));
            server.playSound(null, blockPosition(), SoundEvents.RABBIT_AMBIENT, SoundSource.NEUTRAL, 1, 1);
            discard();
            var type = explosive ? SnobolContent.EXPLOSIVE_SNOBOL.get() : SnobolContent.SIIBOL.get();
            var spawned = type.spawn(server, blockPosition(), MobSpawnType.MOB_SUMMONED);
            if (spawned != null) spawned.setDeltaMovement(0, 0, 0);
            server.sendParticles(net.minecraft.core.particles.ParticleTypes.POOF, getX(), getY(), getZ(), 3, 0.6, 0.6, 0.6, 0.2);
        }
        return net.minecraft.world.InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Texture", texture());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
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
