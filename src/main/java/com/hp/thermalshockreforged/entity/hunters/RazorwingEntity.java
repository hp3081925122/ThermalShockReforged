package com.hp.thermalshockreforged.entity.hunters;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import net.minecraft.world.InteractionResult;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class RazorwingEntity extends TamableAnimal implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(RazorwingEntity.class, EntityDataSerializers.STRING);
    private static final Set<String> FOODS = Set.of("raw_crystal_koi", "raw_sentrapede", "raw_chromefish", "raw_pondler");
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private boolean converting;

    public RazorwingEntity(EntityType<? extends RazorwingEntity> type, Level level) {
        super(type, level);
        xpReward = 3;
        setTexture(defaultTexture());
        if (armored()) setPersistenceRequired();
    }

    public boolean armored() { return getType() != HuntersContent.RAZORWING.get(); }
    public String defaultTexture() {
        if (getType() == HuntersContent.RUBY_ARMOURED_RAZORWING.get()) return "texturerazorwingrubyarmor";
        if (getType() == HuntersContent.BLOOD_ARMOURED_RAZORWING.get()) return "texturerazorwingbloodarmor";
        return "texturerazorwing";
    }
    public static AttributeSupplier.Builder createAttributes(boolean armored) {
        var builder = Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, armored ? 0.35 : 0.3).add(Attributes.MAX_HEALTH, 14)
                .add(Attributes.ARMOR, armored ? 10 : 4).add(Attributes.ATTACK_DAMAGE, 2).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 1);
        if (armored) builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.1);
        return builder;
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "texturerazorwing"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String value) { entityData.set(TEXTURE, value); }
    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new RandomStrollGoal(this, 1));
        goalSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        if (!armored()) goalSelector.addGoal(4, new AvoidEntityGoal<>(this, LivingEntity.class,
                other -> BuiltInRegistries.ENTITY_TYPE.getKey(other.getType()).toString().equals(ThermalShockReforged.MOD_ID + ":monolith"), 6, 1, 1.2, other -> true));
        goalSelector.addGoal(armored() ? 4 : 5, new RandomLookAroundGoal(this));
        goalSelector.addGoal(armored() ? 5 : 6, new FloatGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        if (armored()) return stack.is(Items.COD);
        var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return stack.is(Items.COD) || stack.is(Items.SALMON) || id.getNamespace().equals(ThermalShockReforged.MOD_ID) && FOODS.contains(id.getPath());
    }

    private InteractionResult animalInteraction(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof SpawnEggItem) return super.mobInteract(player, hand);
        if (level().isClientSide()) return isOwnedBy(player) || isFood(stack) ? InteractionResult.CONSUME : InteractionResult.PASS;
        if (isTame()) {
            if (!isOwnedBy(player)) return InteractionResult.SUCCESS;
            if (isFood(stack) && getHealth() < getMaxHealth()) {
                var food = stack.get(DataComponents.FOOD);
                usePlayerItem(player, hand, stack);
                heal(food == null ? 4 : food.nutrition());
                return InteractionResult.SUCCESS;
            }
            return super.mobInteract(player, hand);
        }
        if (isFood(stack)) {
            usePlayerItem(player, hand, stack);
            if (random.nextInt(3) == 0 && !EventHooks.onAnimalTame(this, player)) {
                tame(player);
                level().broadcastEntityEvent(this, (byte)7);
            } else level().broadcastEntityEvent(this, (byte)6);
            setPersistenceRequired();
            return InteractionResult.SUCCESS;
        }
        InteractionResult result = super.mobInteract(player, hand);
        if (result.consumesAction()) setPersistenceRequired();
        return result;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (converting || isRemoved()) return InteractionResult.SUCCESS;
        InteractionResult result = animalInteraction(player, hand);
        if (!armored()) {
            ItemStack armor = player.getMainHandItem();
            EntityType<RazorwingEntity> next = armor.is(MaterialContent.items().get("ruby_razorwing_armor").get()) ? HuntersContent.RUBY_ARMOURED_RAZORWING.get()
                    : armor.is(MaterialContent.items().get("bloodstone_razorwing_armor").get()) ? HuntersContent.BLOOD_ARMOURED_RAZORWING.get() : null;
            if (next != null && isOwnedBy(player) && isAlive()) {
                if (level().isClientSide()) return InteractionResult.SUCCESS;
                return convertArmor(player, armor, next) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            }
        } else if (!level().isClientSide()) player.startRiding(this);
        return result;
    }

    private boolean convertArmor(Player player, ItemStack armor, EntityType<RazorwingEntity> type) {
        if (!(level() instanceof ServerLevel server) || converting || armor.isEmpty()) return false;
        converting = true;
        RazorwingEntity replacement = null;
        try {
            replacement = type.create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
            if (replacement == null) { ThermalShockReforged.LOGGER.debug("Razorwing armor conversion failed to create replacement: source={}", getUUID()); return false; }
            replacement.finalizeSpawn(server, server.getCurrentDifficultyAt(blockPosition()), EntitySpawnReason.CONVERSION, null);
            CompoundTag state = saveWithoutId(new CompoundTag());
            state.remove("UUID"); state.remove("Passengers"); state.remove("Texture");
            replacement.load(state);
            replacement.setTexture(replacement.defaultTexture());
            replacement.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35);
            replacement.getAttribute(Attributes.ARMOR).setBaseValue(10);
            replacement.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.1);
            replacement.setHealth(Math.min(getHealth(), replacement.getMaxHealth()));
            replacement.setPersistenceRequired();
            replacement.setYRot(getYRot()); replacement.setXRot(getXRot()); replacement.setYBodyRot(getYRot()); replacement.setYHeadRot(getYRot());
            replacement.setDeltaMovement(Vec3.ZERO);
            if (!server.addFreshEntity(replacement)) { replacement.discard(); ThermalShockReforged.LOGGER.debug("Razorwing armor conversion join rejected: source={}, replacement={}", getUUID(), replacement.getUUID()); return false; }
            if (!player.getAbilities().instabuild) armor.shrink(1);
            for (Entity passenger : List.copyOf(getPassengers())) passenger.startRiding(replacement, true);
            ThermalShockReforged.LOGGER.debug("Razorwing armor conversion committed: source={}, replacement={}, owner={}, health={}, type={}", getUUID(), replacement.getUUID(), replacement.getOwnerUUID(), replacement.getHealth(), BuiltInRegistries.ENTITY_TYPE.getKey(type));
            discard();
            server.playSound(null, blockPosition(), SoundEvents.STRIDER_HAPPY, SoundSource.NEUTRAL, 1, 1);
            server.playSound(null, blockPosition(), SoundEvents.HORSE_ARMOR.value(), SoundSource.NEUTRAL, 1, 1);
            return true;
        } finally {
            if (!isRemoved()) converting = false;
        }
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return armored() && getFirstPassenger() instanceof LivingEntity passenger ? passenger : super.getControllingPassenger();
    }
    @Override
    protected void tickRidden(Player player, Vec3 movement) { super.tickRidden(player, movement); followPassenger(player); }
    private void followPassenger(LivingEntity passenger) {
        setYRot(passenger.getYRot()); yRotO = getYRot(); setXRot(passenger.getXRot() * 0.5F);
        setRot(getYRot(), getXRot()); yBodyRot = passenger.getYRot(); yHeadRot = passenger.getYRot();
    }
    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 movement) { return new Vec3(0, 0, player.zza); }
    @Override
    protected float getRiddenSpeed(Player player) { return (float)getAttributeValue(Attributes.MOVEMENT_SPEED); }
    @Override
    public void travel(Vec3 movement) {
        LivingEntity passenger = getControllingPassenger();
        if (armored() && passenger != null && !(passenger instanceof Player)) {
            followPassenger(passenger);
            setSpeed((float)getAttributeValue(Attributes.MOVEMENT_SPEED));
            super.travel(new Vec3(0, 0, passenger.zza));
            calculateEntityAnimation(true);
        } else super.travel(movement);
    }
    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float partialTick) {
        return new Vec3(0, dimensions.height() * .75 - .2, 0);
    }
    @Override
    public AgeableMob getBreedOffspring(ServerLevel server, AgeableMob other) {
        RazorwingEntity child = ((EntityType<RazorwingEntity>)getType()).create(server, net.minecraft.world.entity.EntitySpawnReason.BREEDING);
        if (child != null) child.finalizeSpawn(server, server.getCurrentDifficultyAt(child.blockPosition()), EntitySpawnReason.BREEDING, null);
        return child;
    }
    @Override public boolean removeWhenFarAway(double distance) { return armored() ? false : super.removeWhenFarAway(distance); }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !source.is(DamageTypes.FALL) && !source.is(DamageTypes.CACTUS) && super.hurtServer(level, source, amount); }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.STRIDER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.STRIDER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.STRIDER_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.STRIDER_STEP, 0.15F, 1); }
    @Override public void aiStep() { super.aiStep(); updateSwingTime(); }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putString("Texture", texture()); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); if (input.getString("Texture").isPresent()) setTexture(input.getStringOr("Texture", "")); }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> {
            boolean moving = state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F;
            String animation = moving && !isVehicle() && !isAggressive() ? "walk" : state.isMoving() && (isVehicle() || isAggressive()) ? "walk2" : "idle";
            return state.setAndContinue(RawAnimation.begin().thenLoop(animation));
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
