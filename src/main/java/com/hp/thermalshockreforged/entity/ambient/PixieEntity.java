package com.hp.thermalshockreforged.entity.ambient;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class PixieEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(PixieEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);

    public PixieEntity(EntityType<? extends PixieEntity> type, Level level) {
        super(type, level);
        xpReward = 0;
        moveControl = new net.minecraft.world.entity.ai.control.FlyingMoveControl(this, 10, true);
    }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 1).add(Attributes.MAX_HEALTH, 2).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0.6) .add(Attributes.FLYING_SPEED, 1); }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "texturepixie"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String value) { entityData.set(TEXTURE, value); }
    public void setAnimation(String value) { triggerAnim("procedure", value); }
    @Override
    protected void registerGoals() { goalSelector.addGoal(1, new RandomStrollGoal(this, 1, 20) { @Override protected Vec3 getPosition() { return position().add((random.nextFloat() * 2 - 1) * 16, (random.nextFloat() * 2 - 1) * 16, (random.nextFloat() * 2 - 1) * 16); } }); goalSelector.addGoal(2, new RandomLookAroundGoal(this)); goalSelector.addGoal(3, new FloatGoal(this)); goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.5F)); }

    @Override
    protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) { return new net.minecraft.world.entity.ai.navigation.FlyingPathNavigation(this, level); }
    @Override
    public void setNoGravity(boolean ignored) { super.setNoGravity(true); }
    @Override
    public void aiStep() { super.aiStep(); setNoGravity(true); }
    @Override
    public boolean causeFallDamage(double distance, float multiplier, DamageSource source) { return false; }
    @Override
    protected void checkFallDamage(double distance, boolean ground, net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos) { }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof net.minecraft.world.entity.projectile.arrow.AbstractArrow || source.getDirectEntity() instanceof net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud) return false;
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.LIGHTNING_BOLT) || source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.TRIDENT) || source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.DRAGON_BREATH) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) return false;
        return super.hurtServer(level, source, amount);
    }
    @Override
    public net.minecraft.world.InteractionResult mobInteract(Player player, net.minecraft.world.InteractionHand hand) {
        var parent = super.mobInteract(player, hand);
        if (parent.consumesAction()) return parent;
        var stack = player.getItemInHand(hand);
        if (!stack.is(net.minecraft.world.item.Items.GLASS_BOTTLE)) return parent;
        if (!level().isClientSide()) {
            stack.shrink(1);
            var bottled = new net.minecraft.world.item.ItemStack(AmbientContent.BOTTLED_PIXIE.get());
            if (stack.isEmpty()) player.setItemInHand(hand, bottled);
            else if (!player.getInventory().add(bottled)) player.drop(bottled, false);
            discard();
        }
        return net.minecraft.world.InteractionResult.SUCCESS;
    }
    @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(DamageSource source) { return net.minecraft.sounds.SoundEvents.ALLAY_HURT; }
    @Override
    protected net.minecraft.sounds.SoundEvent getDeathSound() { return net.minecraft.sounds.SoundEvents.ALLAY_DEATH; }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putString("Texture", texture());  }
    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); if (input.read("Texture", com.mojang.serialization.Codec.PASSTHROUGH).isPresent()) setTexture(input.getStringOr("Texture", ""));  }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP) .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
        controllers.add(procedure);
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
