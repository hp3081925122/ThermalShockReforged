package com.hp.thermalshockreforged.entity.hunters;

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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class FrostilyteEntity extends Monster implements GeoEntity, ServerEndTickTask {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(FrostilyteEntity.class, EntityDataSerializers.STRING);
    private static final ResourceLocation MOVEMENT_LOCK = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "frostilyte_movement_lock");
    private static final ResourceKey<DamageType> DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "errant_damage"));
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
        combatDimension = level().dimension().location().toString();
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
        if (!level().isClientSide) {
            String dimension = level().dimension().location().toString();
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
        if (!isAlive() || !combatDimension.equals(server.dimension().location().toString())) { clearCombat(); return; }
        if (--attackRemaining > 0) { ServerEndTickTasks.schedule(server, getUUID()); return; }
        UUID targetId = pendingTarget;
        attackRemaining = -1;
        pendingTarget = null;
        ThermalShockReforged.LOGGER.debug("Frostilyte strike at server end tick: entity={}, target={}, origin={}", getUUID(), targetId, attackOrigin);
        server.playSound(null, BlockPos.containing(attackOrigin), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1, 2);
        if (targetId == null || !(server.getEntity(targetId) instanceof LivingEntity target) || !target.isAlive() || target == this || distanceTo(target) > 1.5F) return;
        double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5 - target.getArmorValue() * 0.6;
        if (!Double.isFinite(damage)) return;
        target.hurt(new DamageSource(server.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAMAGE)), (float)Math.max(0, damage));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) { return !source.is(DamageTypes.DROWN) && super.hurt(source, amount); }
    @Override
    public void die(DamageSource source) { super.die(source); if (dead) clearCombat(); }
    @Override
    public boolean removeWhenFarAway(double distance) { return false; }
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData group) {
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
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Texture", texture());
        tag.putInt("AttackCooldown", cooldown);
        tag.putInt("MovementLock", movementLock);
        tag.putInt("AttackRemaining", attackRemaining);
        tag.putString("CombatDimension", combatDimension);
        if (pendingTarget != null) tag.putUUID("PendingTarget", pendingTarget);
        tag.putDouble("AttackX", attackOrigin.x); tag.putDouble("AttackY", attackOrigin.y); tag.putDouble("AttackZ", attackOrigin.z);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        cooldown = Math.max(0, Math.min(12, tag.getInt("AttackCooldown")));
        movementLock = Math.max(0, Math.min(20, tag.getInt("MovementLock")));
        pendingTarget = tag.hasUUID("PendingTarget") ? tag.getUUID("PendingTarget") : null;
        attackRemaining = pendingTarget != null && tag.contains("AttackRemaining") ? Math.max(-1, Math.min(11, tag.getInt("AttackRemaining"))) : -1;
        attackOrigin = new Vec3(tag.getDouble("AttackX"), tag.getDouble("AttackY"), tag.getDouble("AttackZ"));
        if (!Double.isFinite(attackOrigin.x) || !Double.isFinite(attackOrigin.y) || !Double.isFinite(attackOrigin.z)) { pendingTarget = null; attackRemaining = -1; }
        combatDimension = tag.getString("CombatDimension");
        startedAt = Long.MIN_VALUE;
        if (!level().isClientSide) syncMovementLock();
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("attack", RawAnimation.begin().thenPlay("attack"));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP
                : state.setAndContinue(RawAnimation.begin().thenLoop(state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F ? "walk" : "idle"))));
        controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
