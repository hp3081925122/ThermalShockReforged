package com.hp.thermalshockreforged.entity;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class ProboskerEntity extends TamableAnimal implements GeoEntity {
    private static final EntityDataAccessor<Boolean> PURPLE = SynchedEntityData.defineId(ProboskerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final Identifier MOVEMENT_LOCK = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "probosker_movement_lock");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation SIT = RawAnimation.begin().thenLoop("sit");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);

    public ProboskerEntity(EntityType<? extends ProboskerEntity> type, Level level) {
        super(type, level);
        xpReward = 1;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.TEMPT_RANGE, 10).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.MAX_HEALTH, 32)
                .add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 6).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0.6);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PURPLE, false);
    }

    public String texture() {
        return entityData.get(PURPLE) ? "purple_probosker" : "blue_probosker";
    }

    public void setPurple(boolean purple) {
        entityData.set(PURPLE, purple);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(1, new RandomStrollGoal(this, 1));
        goalSelector.addGoal(2, new TemptGoal(this, 1.3, Ingredient.of(MaterialContent.items().get("raw_crystal_koi").get()), false));
        targetSelector.addGoal(3, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(4, new OwnerHurtTargetGoal(this));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        goalSelector.addGoal(6, new FloatGoal(this));
        targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false, (entity, targetLevel) -> hasType(entity, "infestophage_seed")));
        targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false, (entity, targetLevel) -> hasType(entity, "louse")));
        goalSelector.addGoal(9, new MeleeAttackGoal(this, 1.6, false));
        goalSelector.addGoal(10, new AvoidEntityGoal<>(this, LivingEntity.class, 6, 1, 1.2, entity -> hasType(entity, "monolith")));
    }

    private static boolean hasType(LivingEntity entity, String path) {
        var id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return id.getNamespace().equals(ThermalShockReforged.MOD_ID) && id.getPath().equals(path);
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity entity) {
        return distanceToSqr(entity) <= 1;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        if (stack.is(Items.COD) || stack.is(Items.SALMON)) return true;
        var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id.getNamespace().equals(ThermalShockReforged.MOD_ID) && switch (id.getPath()) {
            case "raw_sentrapede", "raw_crystal_koi", "raw_chromefish", "raw_pondler" -> true;
            default -> false;
        };
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof SpawnEggItem) return super.mobInteract(player, hand);
        if (level().isClientSide()) return isOwnedBy(player) || isFood(stack) ? InteractionResult.CONSUME : InteractionResult.PASS;
        if (!isTame() && isFood(stack)) {
            usePlayerItem(player, hand, stack);
            if (random.nextInt(3) == 0 && !EventHooks.onAnimalTame(this, player)) {
                tame(player);
                level().broadcastEntityEvent(this, (byte) 7);
            } else {
                level().broadcastEntityEvent(this, (byte) 6);
            }
            setPersistenceRequired();
            return InteractionResult.SUCCESS;
        }
        if (isTame() && !isOwnedBy(player)) return InteractionResult.PASS;
        if (isOwnedBy(player) && isFood(stack) && getHealth() < getMaxHealth()) {
            var food = stack.get(DataComponents.FOOD);
            float healing = food == null ? 4 : food.nutrition();
            usePlayerItem(player, hand, stack);
            heal(healing);
            return InteractionResult.SUCCESS;
        }
        InteractionResult result = super.mobInteract(player, hand);
        if (result.consumesAction()) {
            setPersistenceRequired();
            return result;
        }
        if (isOwnedBy(player) && !isFood(stack)) {
            setShiftKeyDown(!isShiftKeyDown());
            setOrderedToSit(!isOrderedToSit());
            setInSittingPose(isOrderedToSit());
            navigation.stop();
            setTarget(null);
            level().playSound(null, blockPosition(), isOrderedToSit() ? SoundEvents.PUFFER_FISH_BLOW_OUT : SoundEvents.PUFFER_FISH_BLOW_UP, SoundSource.NEUTRAL, 1, 1);
            return InteractionResult.SUCCESS;
        }
        return result;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData group) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, group);
        setPurple(random.nextDouble() < 0.4);
        return result;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        ProboskerEntity child = ModEntities.PROBOSKER.get().create(level, net.minecraft.world.entity.EntitySpawnReason.BREEDING);
        if (child != null) child.finalizeSpawn(level, level.getCurrentDifficultyAt(child.blockPosition()), EntitySpawnReason.BREEDING, null);
        return child;
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float damage) {
        return !source.is(DamageTypes.DROWN) && super.hurtServer(level, source, damage);
    }

    @Override
    public SoundEvent getAmbientSound() { return null; }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.PUFFER_FISH_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.PUFFER_FISH_DEATH; }

    @Override
    protected void playStepSound(BlockPos position, BlockState state) { playSound(SoundEvents.WOLF_STEP.value(), 0.15F, 1); }

    @Override
    public void aiStep() {
        super.aiStep();
        updateSwingTime();
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (!level().isClientSide()) {
            var speed = getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) {
                if (isShiftKeyDown()) {
                    if (!speed.hasModifier(MOVEMENT_LOCK)) {
                        speed.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                    }
                } else {
                    speed.removeModifier(MOVEMENT_LOCK);
                }
            }
        }
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("Texture", texture());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        setPurple(input.getStringOr("Texture", "").equals("purple_probosker"));
    }

    public void setAnimation(String animation) { triggerAnim("procedure", animation); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP);
        for (String animation : new String[]{"sit", "idle", "walk", "swim", "swim2", "swim3"}) {
            procedure.triggerableAnim(animation, RawAnimation.begin().thenPlay(animation));
        }
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation()
                ? PlayState.STOP : state.setAndContinue(state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F ? WALK : isInSittingPose() ? SIT : IDLE)));
        controllers.add(procedure);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
