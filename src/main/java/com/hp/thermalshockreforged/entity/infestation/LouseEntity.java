package com.hp.thermalshockreforged.entity.infestation;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

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
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class LouseEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(LouseEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);

    public LouseEntity(EntityType<? extends LouseEntity> type, Level level) {
        super(type, level);
        xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.MAX_HEALTH, 1).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 1);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE, "ash_louse");
    }

    public String texture() { return entityData.get(TEXTURE); }

    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }

    public void setAnimation(String animation) { triggerAnim("procedure", animation); }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new RandomStrollGoal(this, 1));
        goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        goalSelector.addGoal(3, new FloatGoal(this));
        goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class, 6, 1, 1.2));
        goalSelector.addGoal(5, new AvoidEntityGoal<>(this, com.hp.thermalshockreforged.entity.ProboskerEntity.class, 6, 1, 1));
        goalSelector.addGoal(6, new AvoidEntityGoal<>(this, LivingEntity.class, 6, 1, 1, entity -> InfestationEntities.hasType(entity, "razorwing")));
        goalSelector.addGoal(7, new AvoidEntityGoal<>(this, LivingEntity.class, 6, 1, 1.2, entity -> InfestationEntities.hasType(entity, "monolith")));
        goalSelector.addGoal(8, new AvoidEntityGoal<>(this, LivingEntity.class, 12, 1, 1.2, entity -> InfestationEntities.hasType(entity, "squall")));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !source.is(DamageTypes.FALL) && super.hurtServer(level, source, amount); }

    @Override
    public SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData group) {
        var result = super.finalizeSpawn(level, difficulty, reason, group);
        if (random.nextDouble() < 0.6) setTexture(random.nextDouble() < 0.6 ? "cobalt_louse" : "ruby_louse");
        return result;
    }

    @Override
    public net.minecraft.world.InteractionResult mobInteract(Player player, net.minecraft.world.InteractionHand hand) {
        var result = super.mobInteract(player, hand);
        if (result.consumesAction()) return result;
        if (!level().isClientSide()) {
            discard();
            level().playSound(null, blockPosition(), SoundEvents.GENERIC_EAT.value(), SoundSource.PLAYERS, 1, 1);
            player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() + 1);
        }
        return net.minecraft.world.InteractionResult.SUCCESS;
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("Texture", texture());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        if (input.read("Texture", com.mojang.serialization.Codec.PASSTHROUGH).isPresent()) setTexture(input.getStringOr("Texture", ""));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 0, state -> PlayState.STOP)
                .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"))
                .triggerableAnim("walk", RawAnimation.begin().thenPlay("walk"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 0, state -> procedure.isPlayingTriggeredAnimation()
                ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop(state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F ? "walk" : "idle"))));
        controllers.add(procedure);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
