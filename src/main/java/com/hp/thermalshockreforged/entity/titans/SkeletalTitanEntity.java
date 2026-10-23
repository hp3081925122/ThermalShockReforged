package com.hp.thermalshockreforged.entity.titans;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;

public final class SkeletalTitanEntity extends AnimatedMonsterEntity {
    public enum Kind {
        SKELETITAN("skeletitan", "skeletitan", .2), FROST("frost_titan", "frost_titan", .25), MANTLE("mantletitan", "mantletitannoglow", .2);
        private final String id;
        private final String texture;
        private final double speed;
        Kind(String id, String texture, double speed) { this.id = id; this.texture = texture; this.speed = speed; }
        public String id() { return id; }
        public double speed() { return speed; }
    }

    private static final Identifier MANTLESTONE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "mantlestone");
    private static final Identifier MOVEMENT_LOCK = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "titan_movement_lock");
    private final Kind kind;
    private boolean attributesApplied;
    private boolean loadedFromSave;
    private boolean spawnVariantChecked;
    private int cooldown;
    private int movementLock;

    public SkeletalTitanEntity(EntityType<? extends SkeletalTitanEntity> type, Level level, Kind kind) {
        super(type, level, kind.texture, 3);
        this.kind = kind;
        refreshDimensions();
        applyConfiguredAttributes();
    }

    public Kind kind() { return kind; }
    @Override protected EntityDimensions getDefaultDimensions(Pose pose) { return super.getDefaultDimensions(pose).scale(1.6F); }
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); }
        });
        targetSelector.addGoal(2, new HurtByTargetGoal(this));
        goalSelector.addGoal(3, new RandomStrollGoal(this, .8));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 16; }

    public void beginAttack(LivingEntity victim) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || !victim.isAlive() || victim == this || victim.level() != level() || cooldown > 0) return;
        if (!TitanSwingEntity.submit(server, this, victim)) {
            ThermalShockReforged.LOGGER.warn("Titan swing scheduling failed: caster={}, target={}", getUUID(), victim.getUUID());
            return;
        }
        cooldown = TitanCombatConfig.COOLDOWN[kind.ordinal()].get();
        movementLock = 20;
        syncMovementLock();
        triggerProcedure("bigswing1");
        server.playSound(null, victim.blockPosition(), SoundEvents.WITHER_SKELETON_AMBIENT, SoundSource.HOSTILE, 1, -3);
    }

    private void syncMovementLock() {
        if (level().isClientSide()) return;
        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementLock > 0) {
            if (!speed.hasModifier(MOVEMENT_LOCK)) speed.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        } else speed.removeModifier(MOVEMENT_LOCK);
    }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 0, state -> PlayState.STOP);
        for (String clip : new String[] {"idle", "bigswing1", "bigswing2", "hurt", "walk", "walk2"}) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(new LegacyAnimationController<>(this, "movement", 0, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            boolean moving = state.isMoving() || state.getLimbSwingAmount() <= -.15F || state.getLimbSwingAmount() >= .15F;
            if (moving && !isAggressive()) return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            if (isDeadOrDying()) return state.setAndContinue(RawAnimation.begin().thenPlay("death"));
            if (isAggressive() && state.isMoving()) return state.setAndContinue(RawAnimation.begin().thenLoop("walk2"));
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
        controllers.add(procedure);
    }
    @Override protected java.util.List<String> procedureAnimations() { return java.util.List.of("idle", "bigswing1", "bigswing2", "hurt", "walk", "walk2"); }

    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData data) {
        var result = super.finalizeSpawn(world, difficulty, reason, data);
        if (kind != Kind.SKELETITAN || spawnVariantChecked || !(world instanceof ServerLevel server)) return result;
        spawnVariantChecked = true;
        boolean mantle = BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(getX(), getY() - 1, getZ())).getBlock()).equals(MANTLESTONE);
        EntityType<SkeletalTitanEntity> variant = mantle ? TitanContent.MANTLETITAN.get() : random.nextDouble() < .12 ? TitanContent.FROST_TITAN.get() : null;
        if (variant == null) return result;
        var replacement = variant.create(server);
        if (replacement == null) return result;
        BlockPos pos = blockPosition();
        replacement.moveTo(pos.getX() + .5, pos.getY(), pos.getZ() + .5, server.getRandom().nextFloat() * 360, 0);
        replacement.yHeadRot = replacement.getYRot();
        replacement.yBodyRot = replacement.getYRot();
        EventHooks.finalizeMobSpawn(replacement, server, server.getCurrentDifficultyAt(pos), EntitySpawnReason.MOB_SUMMONED, null);
        if (replacement.isSpawnCancelled() || replacement.isRemoved()) {
            replacement.discard();
            ThermalShockReforged.LOGGER.debug("Skeletitan variant initialization rejected: source={}, type={}", getUUID(), BuiltInRegistries.ENTITY_TYPE.getKey(variant));
            return result;
        }
        replacement.playAmbientSound();
        replacement.setYRot(getYRot());
        replacement.setXRot(getXRot());
        replacement.setYBodyRot(getYRot());
        replacement.setYHeadRot(getYRot());
        replacement.setDeltaMovement(Vec3.ZERO);
        if (server.addFreshEntity(replacement)) {
            ThermalShockReforged.LOGGER.debug("Skeletitan variant committed: source={}, replacement={}, type={}", getUUID(), replacement.getUUID(), BuiltInRegistries.ENTITY_TYPE.getKey(variant));
            discard();
        } else {
            replacement.discard();
            ThermalShockReforged.LOGGER.debug("Skeletitan variant join rejected: source={}, type={}", getUUID(), BuiltInRegistries.ENTITY_TYPE.getKey(variant));
        }
        return result;
    }

    @Override public void baseTick() {
        super.baseTick();
        if (!level().isClientSide()) {
            if (cooldown > 0) cooldown--;
            if (movementLock > 0 && --movementLock == 0) syncMovementLock();
        }
        if (!level().isClientSide() && kind == Kind.FROST && isUnderWater()) {
            addEffect(new MobEffectInstance(MobEffects.SPEED, 10, 2, false, false));
            addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 10, 2, false, false));
        }
    }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !(kind == Kind.FROST && source.is(DamageTypes.DROWN)) && super.hurtServer(level, source, amount); }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.SKELETON_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "bone_hit")); }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SKELETON_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.SKELETON_STEP, .15F, 1); }

    private void applyConfiguredAttributes() {
        if (level().isClientSide() || attributesApplied || !TitanCombatConfig.SPEC.isLoaded()) return;
        int index = kind.ordinal();
        float previousHealth = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(TitanCombatConfig.HEALTH[index].get());
        getAttribute(Attributes.ARMOR).setBaseValue(TitanCombatConfig.ARMOR[index].get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(TitanCombatConfig.DAMAGE[index].get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(TitanCombatConfig.SPEED[index].get());
        getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(TitanCombatConfig.FOLLOW_RANGE[index].get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(TitanCombatConfig.RESISTANCE[index].get());
        getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(TitanCombatConfig.KNOCKBACK[index].get());
        setHealth(loadedFromSave ? Math.min(previousHealth, getMaxHealth()) : getMaxHealth());
        attributesApplied = true;
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); applyConfiguredAttributes(); syncMovementLock(); }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("TitanSpawnVariantChecked", spawnVariantChecked);
        output.putInt("TitanCooldown", cooldown);
        output.putInt("TitanMovementLock", movementLock);
    }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        loadedFromSave = true;
        super.readAdditionalSaveData(input);
        spawnVariantChecked = input.getBooleanOr("TitanSpawnVariantChecked", false);
        attributesApplied = false;
        applyConfiguredAttributes();
        cooldown = Math.clamp(input.getIntOr("TitanCooldown", 0), 0, 72000);
        movementLock = Math.clamp(input.getIntOr("TitanMovementLock", 0), 0, 20);
        syncMovementLock();
    }

    public static AttributeSupplier.Builder createAttributes(Kind kind) {
        return createMobAttributes().add(Attributes.MAX_HEALTH, 32).add(Attributes.ARMOR, 6).add(Attributes.ATTACK_DAMAGE, 6)
            .add(Attributes.MOVEMENT_SPEED, kind.speed()).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.KNOCKBACK_RESISTANCE, .2)
            .add(Attributes.ATTACK_KNOCKBACK, .5).add(Attributes.STEP_HEIGHT, .6);
    }
}
