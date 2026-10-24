package com.hp.thermalshockreforged.entity.hunters;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class FrostilyteEntity extends Monster implements GeoEntity, ServerEndTickTask {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(FrostilyteEntity.class, EntityDataSerializers.STRING);
    private static final Identifier MOVEMENT_LOCK = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "frostilyte_movement_lock");
    private static final ResourceKey<DamageType> DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "errant_damage"));
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int cooldown;
    private int movementLock;
    private int attackRemaining = -1;
    private long startedAt = Long.MIN_VALUE;
    private UUID pendingTarget;
    private Vec3 attackOrigin = Vec3.ZERO;
    private String combatDimension = "";

    public FrostilyteEntity(EntityType<? extends FrostilyteEntity> type, Level level) {
        super(type, level);
        xpReward = 8;
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.2).add(Attributes.MAX_HEALTH, 15).add(Attributes.ARMOR, 1.2)
                .add(Attributes.ATTACK_DAMAGE, 4).add(Attributes.FOLLOW_RANGE, 8).add(Attributes.STEP_HEIGHT, 0.6);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "frostilyte_a"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String value) { entityData.set(TEXTURE, value); }
    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && distanceToSqr(target) <= 2.25 && getSensing().hasLineOfSight(target); }
        });
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 12));
        targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers());
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        goalSelector.addGoal(7, new OpenDoorGoal(this, true));
    }

    void startAttack(LivingEntity target) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || target == this || cooldown > 0) return;
        combatDimension = level().dimension().identifier().toString();
        pendingTarget = target.getUUID();
        attackOrigin = target.position();
        attackRemaining = 11;
        cooldown = 12;
        movementLock = 20;
        startedAt = level().getGameTime();
        syncMovementLock();
        triggerAnim("procedure", "attack");
        level().playSound(null, BlockPos.containing(attackOrigin), SoundEvents.STRAY_AMBIENT, SoundSource.HOSTILE, 1, 3);
        ServerEndTickTasks.schedule(server, getUUID());
    }

    private void syncMovementLock() {
        var attribute = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementLock > 0 && !attribute.hasModifier(MOVEMENT_LOCK)) attribute.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        if (movementLock <= 0) attribute.removeModifier(MOVEMENT_LOCK);
    }

    private void clearCombat() { cooldown = 0; movementLock = 0; attackRemaining = -1; pendingTarget = null; syncMovementLock(); }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (attackRemaining >= 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }

    @Override
    public void tick() {
        if (!level().isClientSide()) {
            String dimension = level().dimension().identifier().toString();
            if (!combatDimension.isEmpty() && !combatDimension.equals(dimension) || !isAlive()) clearCombat();
            combatDimension = dimension;
            syncMovementLock();
        }
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;
        if (!isAlive()) { clearCombat(); return; }
        if (attackRemaining >= 0) ServerEndTickTasks.schedule(server, getUUID());
        if (level().getGameTime() == startedAt) return;
        if (cooldown > 0) cooldown--;
        if (movementLock > 0) { movementLock--; syncMovementLock(); }
    }

    @Override
    public void endTick(ServerLevel server) {
        if (attackRemaining < 0 || isRemoved()) return;
        if (!isAlive() || !combatDimension.equals(server.dimension().identifier().toString())) { clearCombat(); return; }
        if (--attackRemaining > 0) { ServerEndTickTasks.schedule(server, getUUID()); return; }
        UUID targetId = pendingTarget;
        attackRemaining = -1;
        pendingTarget = null;
        ThermalShockReforged.LOGGER.debug("Frostilyte strike at server end tick: entity={}, target={}, origin={}", getUUID(), targetId, attackOrigin);
        server.playSound(null, BlockPos.containing(attackOrigin), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1, 2);
        if (targetId == null || !(server.getEntity(targetId) instanceof LivingEntity target) || !target.isAlive() || target == this || distanceTo(target) > 1.5F) return;
        double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5 - target.getArmorValue() * 0.6;
        if (!Double.isFinite(damage)) return;
        target.hurt(new DamageSource(server.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DAMAGE)), (float)Math.max(0, damage));
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !source.is(DamageTypes.DROWN) && super.hurtServer(level, source, amount); }
    @Override
    public void die(DamageSource source) { super.die(source); if (dead) clearCombat(); }
    @Override
    public boolean removeWhenFarAway(double distance) { return false; }
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData group) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, group);
        setTexture("frostilyte_" + (char)('a' + random.nextInt(3)));
        return result;
    }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.STRAY_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.STRAY_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.STRAY_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.STRAY_STEP, 0.15F, 1); }
    @Override public void aiStep() { super.aiStep(); updateSwingTime(); }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("Texture", texture());
        output.putInt("AttackCooldown", cooldown);
        output.putInt("MovementLock", movementLock);
        output.putInt("AttackRemaining", attackRemaining);
        output.putString("CombatDimension", combatDimension);
        if (pendingTarget != null) output.putString("PendingTarget", pendingTarget.toString());
        output.putDouble("AttackX", attackOrigin.x); output.putDouble("AttackY", attackOrigin.y); output.putDouble("AttackZ", attackOrigin.z);
    }
    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        if (input.read("Texture", com.mojang.serialization.Codec.PASSTHROUGH).isPresent()) setTexture(input.getStringOr("Texture", ""));
        cooldown = Math.max(0, Math.min(12, input.getIntOr("AttackCooldown", 0)));
        movementLock = Math.max(0, Math.min(20, input.getIntOr("MovementLock", 0)));
        pendingTarget = input.getString("PendingTarget").map(java.util.UUID::fromString).orElse(null);
        attackRemaining = pendingTarget != null && input.read("AttackRemaining", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? Math.max(-1, Math.min(11, input.getIntOr("AttackRemaining", 0))) : -1;
        attackOrigin = new Vec3(input.getDoubleOr("AttackX", 0.0), input.getDoubleOr("AttackY", 0.0), input.getDoubleOr("AttackZ", 0.0));
        if (!Double.isFinite(attackOrigin.x) || !Double.isFinite(attackOrigin.y) || !Double.isFinite(attackOrigin.z)) { pendingTarget = null; attackRemaining = -1; }
        combatDimension = input.getStringOr("CombatDimension", "");
        startedAt = Long.MIN_VALUE;
        if (!level().isClientSide()) syncMovementLock();
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("attack", RawAnimation.begin().thenPlay("attack"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP
                : state.setAndContinue(RawAnimation.begin().thenLoop(state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F ? "walk" : "idle"))));
        controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
