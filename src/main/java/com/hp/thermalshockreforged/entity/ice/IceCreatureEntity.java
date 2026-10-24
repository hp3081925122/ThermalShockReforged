package com.hp.thermalshockreforged.entity.ice;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.LegacyEffectParticles;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.*;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class IceCreatureEntity extends Monster implements GeoEntity, RangedAttackMob, ServerEndTickTask {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(IceCreatureEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> COOLDOWN_VISIBLE = SynchedEntityData.defineId(IceCreatureEntity.class, EntityDataSerializers.BOOLEAN);
    private static final Identifier MOVEMENT_LOCK = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "ice_movement_lock");
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int cooldown;
    private int movementLock;
    private int attackAge = -1;
    private UUID pendingTarget;
    private Vec3 attackOrigin = Vec3.ZERO;
    private boolean alternate;
    private String combatDimension = "";
    private long startedAt = Long.MIN_VALUE;
    private boolean driftMovePending;
    private boolean driftSlowPending;
    private int driftCooldown;
    private boolean deathSpawned;
    private boolean attributesApplied;
    private boolean loadedFromSave;
    private boolean restoreAnimation;
    private boolean soldierExperiencePending;
    private UUID soldierExperienceAttacker;

    public IceCreatureEntity(EntityType<? extends IceCreatureEntity> type, Level level) {
        super(type, level);
        xpReward = switch (kind()) { case 0, 1 -> 10; case 2 -> 5; default -> 40; };
        setPersistenceRequired();
        setTexture(defaultTexture());
        if (kind() == 0) refreshDimensions();
        applyConfiguredAttributes();
    }
    public int kind() {
        return switch (BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath()) { case "frostilyte_soldier" -> 0; case "frostilyte_cryomancer" -> 1; case "icidrone" -> 2; default -> 3; };
    }
    private String defaultTexture() { return switch (kind()) { case 0 -> "soldier_a"; case 1 -> "frostilytestaff"; case 2 -> "icidrone_rubicon"; default -> "novazerk"; }; }
    public static AttributeSupplier.Builder createAttributes(int kind) {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, IceCombatConfig.SPEED[kind].getDefault()).add(Attributes.MAX_HEALTH, IceCombatConfig.HEALTH[kind].getDefault())
                .add(Attributes.ARMOR, IceCombatConfig.ARMOR[kind].getDefault()).add(Attributes.ATTACK_DAMAGE, IceCombatConfig.DAMAGE[kind].getDefault()).add(Attributes.FOLLOW_RANGE, kind == 0 ? 8 : kind == 1 ? 32 : 16)
                .add(Attributes.KNOCKBACK_RESISTANCE, IceCombatConfig.RESISTANCE[kind].getDefault()).add(Attributes.ATTACK_KNOCKBACK, IceCombatConfig.KNOCKBACK[kind].getDefault()).add(Attributes.STEP_HEIGHT, kind == 2 ? 0 : 0.6);
    }
    void applyConfiguredAttributes() {
        if (level().isClientSide() || attributesApplied || !IceCombatConfig.SPEC.isLoaded()) return;
        float health = getHealth(); int k = kind();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(IceCombatConfig.HEALTH[k].get());
        getAttribute(Attributes.ARMOR).setBaseValue(IceCombatConfig.ARMOR[k].get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(IceCombatConfig.DAMAGE[k].get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(IceCombatConfig.SPEED[k].get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(IceCombatConfig.RESISTANCE[k].get());
        getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(IceCombatConfig.KNOCKBACK[k].get());
        setHealth(loadedFromSave ? Math.min(health, getMaxHealth()) : getMaxHealth());
        attributesApplied = true;
        ThermalShockReforged.LOGGER.debug("Ice attributes applied: uuid={}, type={}, loadedFromSave={}, health={}, maxHealth={}, attackDamage={}", getUUID(), getType(), loadedFromSave, getHealth(), getMaxHealth(), getAttributeValue(Attributes.ATTACK_DAMAGE));
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "soldier_a"); builder.define(COOLDOWN_VISIBLE, false); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String value) { entityData.set(TEXTURE, value); }
    private void animate(String value) { triggerAnim("procedure", value); }
    @Override
    protected void registerGoals() {
        int k = kind();
        if (k == 1) {
            targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
            goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 70, 5) { @Override public boolean canContinueToUse() { return canUse(); } });
            goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 16));
            goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0));
            goalSelector.addGoal(4, new RandomLookAroundGoal(this));
            goalSelector.addGoal(5, new OpenDoorGoal(this, true));
            goalSelector.addGoal(6, new OpenDoorGoal(this, false));
            return;
        }
        goalSelector.addGoal(1, new MeleeAttackGoal(this, k == 3 ? 0.7 : 1.2, k == 0) {
            @Override protected boolean canPerformAttack(LivingEntity target) { double reach = kind() == 0 ? 6.25 : kind() == 2 ? 100 : 12.25; return isTimeToAttack() && distanceToSqr(target) <= reach && (kind() == 0 || getSensing().hasLineOfSight(target)); }
        });
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, k != 2, k != 3));
        if (k == 0) {
            goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 12));
            targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers());
            goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0));
            goalSelector.addGoal(6, new RandomLookAroundGoal(this));
            goalSelector.addGoal(7, new OpenDoorGoal(this, true));
        } else {
            targetSelector.addGoal(3, new HurtByTargetGoal(this));
            goalSelector.addGoal(4, new RandomLookAroundGoal(this));
            if (k == 3) goalSelector.addGoal(5, new RandomStrollGoal(this, 0));
        }
    }

    boolean attackAttempt(LivingEntity target) {
        int k = kind();
        if (k == 1) return false;
        if (!(level() instanceof ServerLevel) || !isAlive() || target == this) return true;
        if (cooldown > 0) return true;
        if (k == 0) {
            if (!SoldierComboEntity.submit((ServerLevel)level(), this, target.getUUID(), target.position(), 0)) {
                ThermalShockReforged.LOGGER.warn("Soldier combo could not be scheduled: caster={}, target={}", getUUID(), target.getUUID());
                return true;
            }
            cooldown = IceCombatConfig.SOLDIER_COOLDOWN.get(); movementLock = 29; startedAt = level().getGameTime(); combatDimension = level().dimension().identifier().toString(); attackOrigin = target.position(); animate("attack"); sound(SoundEvents.STRAY_AMBIENT, 1, 3); syncMovementLock();
            return true;
        }
        pendingTarget = target.getUUID(); attackOrigin = target.position(); attackAge = 0; startedAt = level().getGameTime(); combatDimension = level().dimension().identifier().toString();
        if (k == 2) {
            cooldown = IceCombatConfig.DRONE_COOLDOWN.get(); animate("ready"); setTexture("icidrone_charged"); faceEyes(target); sound(customSound("galvanitehurt"), 0.8F, 3);
        } else {
            cooldown = IceCombatConfig.ZERK_COOLDOWN.get(); alternate = random.nextDouble() < IceCombatConfig.ZERK_ALT_CHANCE.get(); entityData.set(COOLDOWN_VISIBLE, true);
            if (alternate) { movementLock = 4; target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 25, 5, true, true)); }
            else { animate("attack"); sound(SoundEvents.SHIELD_BREAK.value(), 0.8F, -1); attackAge = -1; pendingTarget = null; }
        }
        syncMovementLock();
        if (attackAge >= 0 && level() instanceof ServerLevel server) {
            ServerEndTickTasks.schedule(server, getUUID());
            ThermalShockReforged.LOGGER.debug("Ice attack armed: entity={}, kind={}, remainingEndTicks={}", getUUID(), k, k == 2 ? 20 : 10);
        }
        return k != 3;
    }
    private void faceEyes(LivingEntity target) { lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition()); }
    private void sound(SoundEvent sound, float volume, float pitch) { level().playSound(null, BlockPos.containing(attackOrigin), sound, SoundSource.HOSTILE, volume, pitch); }
    private static SoundEvent customSound(String path) { return BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path)); }
    private void syncMovementLock() {
        var attribute = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementLock > 0 && !attribute.hasModifier(MOVEMENT_LOCK)) attribute.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        if (movementLock <= 0) attribute.removeModifier(MOVEMENT_LOCK);
    }
    private void clearCombat() {
        attackAge = -1; pendingTarget = null; cooldown = 0; movementLock = 0; driftMovePending = false; driftSlowPending = false; driftCooldown = 0; entityData.set(COOLDOWN_VISIBLE, false); syncMovementLock();
        if (kind() == 2) setTexture(defaultTexture());
    }
    private void advanceAttack(ServerLevel server) {
        if (attackAge < 0) return;
        int limit = kind() == 2 ? 20 : 10;
        if (attackAge >= limit) { attackAge = -1; pendingTarget = null; return; }
        attackAge++;
        LivingEntity target = pendingTarget != null && server.getEntity(pendingTarget) instanceof LivingEntity living ? living : null;
        if (kind() == 2 && attackAge == 20) {
            attackAge = -1; pendingTarget = null; setTexture(defaultTexture());
            if (target == null || !target.isAlive() || target == this) return;
            faceEyes(target); animate("attack"); sound(customSound("hailfire"), 1, 1);
            for (int i = 0; i < 3; i++) { var bullet = IceContent.ICE_BULLET.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED); if (bullet == null) continue; bullet.configure(this, false, 5.0 / 3, 0); bullet.setPos(getX(), getEyeY(), getZ()); var look = getLookAngle(); bullet.shoot(look.x, look.y, look.z, 1.1F, 2); server.addFreshEntity(bullet); }
        } else if (kind() == 3 && attackAge == 10) {
            attackAge = -1; pendingTarget = null;
            if (target == null || !target.isAlive() || target == this) return;
            double strength = IceCombatConfig.ZERK_LAUNCH.get() * Math.max(0, 1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            if (strength > 0) target.setDeltaMovement(0, strength, 0);
            double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * IceCombatConfig.ZERK_ALT_MULTIPLIER.get();
            if (Double.isFinite(damage)) target.hurt(new DamageSource(registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.GENERIC_KILL)), (float)Math.max(0, damage));
            sound(SoundEvents.IRON_GOLEM_ATTACK, 1, 1); sound(SoundEvents.ANVIL_PLACE, 1, -1); animate("attack2");
        }
    }
    @Override public void performRangedAttack(LivingEntity target, float power) {
        if (kind() != 1 || !(level() instanceof ServerLevel server) || !isAlive()) return;
        var bullet = IceContent.ICE_BULLET.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED); if (bullet == null) return;
        bullet.configure(this, true, 2.5, IceCombatConfig.BULLET_KNOCKBACK.get()); bullet.setPos(getX(), getEyeY() - 0.1F, getZ());
        double dx = target.getX() - getX(), dz = target.getZ() - getZ();
        bullet.shoot(dx, target.getY() + target.getEyeHeight() - 1.1 - bullet.getY() + Math.hypot(dx, dz) * 0.2F, dz, 0.2F, 12);
        server.addFreshEntity(bullet);
    }
    private void droneTick(ServerLevel server) {
        if (!isAlive()) { server.playSound(null, blockPosition(), SoundEvents.BAMBOO_PLACE, SoundSource.HOSTILE, 0.1F, 3); return; }
        if (driftCooldown > 0) driftCooldown--;
        if (driftCooldown == 0 && !hasEffect(MobEffects.SLOWNESS)) driftMovePending = true;
        if (driftMovePending || driftSlowPending) ServerEndTickTasks.schedule(server, getUUID());
        var center = position();
        if (!server.getEntitiesOfClass(Player.class, AABB.ofSize(center, 10, 10, 10), player -> true).isEmpty()) {
            var nearest = server.getServer().getPlayerList().getPlayers().stream().min(Comparator.comparingDouble(player -> player.distanceToSqr(center))).orElse(null);
            if (nearest != null) lookAt(EntityAnchorArgument.Anchor.FEET, nearest.position());
        }
        server.playSound(null, blockPosition(), SoundEvents.BAMBOO_PLACE, SoundSource.HOSTILE, 0.1F, 3);
    }
    @Override public void onAddedToLevel() {
        super.onAddedToLevel();
        if ((driftMovePending || driftSlowPending || attackAge >= 0 && kind() >= 2) && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override public void endTick(ServerLevel server) {
        if (isRemoved() || !isAlive()) { driftMovePending = false; driftSlowPending = false; attackAge = -1; pendingTarget = null; return; }
        if (!combatDimension.isEmpty() && !combatDimension.equals(server.dimension().identifier().toString())) { clearCombat(); return; }
        if (kind() >= 2) advanceAttack(server);
        if (driftSlowPending) {
            driftSlowPending = false;
            addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 2, true, false));
            driftCooldown = 40;
        }
        if (driftMovePending) {
            driftMovePending = false;
            setDeltaMovement(random.nextDouble() * 2 - 1, random.nextDouble() * 0.4 - 0.2, random.nextDouble() * 2 - 1);
            hurtMarked = true;
            driftSlowPending = true;
            ThermalShockReforged.LOGGER.debug("Icidrone drift pulse: entity={}, slowPending={}", getUUID(), driftSlowPending);
        }
        if (driftSlowPending || attackAge >= 0 && kind() >= 2) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override public void baseTick() {
        super.baseTick();
        if (level() instanceof ServerLevel server && kind() == 2) droneTick(server);
        if (!level().isClientSide() && kind() == 3 && isInWater()) { addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 2, 3, true, false)); addEffect(new MobEffectInstance(MobEffects.SPEED, 2, 3, true, false)); }
    }
    @Override public void tick() {
        if (!level().isClientSide()) {
            applyConfiguredAttributes();
            if (kind() == 0 && attackAge >= 0 && pendingTarget != null && level() instanceof ServerLevel server
                    && (combatDimension.isEmpty() || combatDimension.equals(server.dimension().identifier().toString()))
                    && SoldierComboEntity.submit(server, this, pendingTarget, attackOrigin, attackAge)) { attackAge = -1; pendingTarget = null; }
            String dimension = level().dimension().identifier().toString();
            if (!combatDimension.isEmpty() && !combatDimension.equals(dimension) || !isAlive()) clearCombat();
            combatDimension = dimension; syncMovementLock();
        }
        super.tick();
        if (!(level() instanceof ServerLevel server) || !isAlive()) return;
        if (restoreAnimation) { if (attackAge >= 0 && kind() != 3) animate(kind() == 0 ? "attack" : "ready"); restoreAnimation = false; }
        if (attackAge >= 0 && kind() >= 2) ServerEndTickTasks.schedule(server, getUUID());
        if (server.getGameTime() == startedAt) return;
        if (cooldown > 0 && --cooldown == 0) entityData.set(COOLDOWN_VISIBLE, false);
        if (movementLock > 0) { movementLock--; syncMovementLock(); }
    }
    public boolean hasCommittedDeath() { return dead; }
    @Override public void die(DamageSource source) {
        super.die(source);
        if (!dead) return;
        clearCombat();
        if (kind() == 1 && !deathSpawned && level() instanceof ServerLevel server) {
            deathSpawned = true;
            var drone = IceContent.ICIDRONE.get().spawn(server, BlockPos.containing(getX() + 0.5, getY() + 0.5, getZ() + 0.5), EntitySpawnReason.MOB_SUMMONED);
            if (drone != null) drone.setDeltaMovement(0, 0.3, 0);
            ThermalShockReforged.LOGGER.debug("Cryomancer death spawn committed: source={}, drone={}", getUUID(), drone == null ? null : drone.getUUID());
        }
    }
    @Override protected void dropExperience(net.minecraft.server.level.ServerLevel level, Entity attacker) {
        if (kind() != 0) { super.dropExperience(level, attacker); return; }
        soldierExperiencePending = true; soldierExperienceAttacker = attacker == null ? null : attacker.getUUID();
    }
    @Override protected void tickDeath() {
        if (kind() != 0) { super.tickDeath(); return; }
        if (++deathTime >= 20 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (soldierExperiencePending && level() instanceof ServerLevel server) {
                soldierExperiencePending = false; super.dropExperience(server, soldierExperienceAttacker == null ? null : server.getEntity(soldierExperienceAttacker));
            }
        }
    }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return !(kind() == 2 ? source.is(DamageTypes.FALL) : source.is(DamageTypes.DROWN)) && super.hurtServer(level, source, amount); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override protected EntityDimensions getDefaultDimensions(Pose pose) { return kind() == 0 ? super.getDefaultDimensions(pose).scale(1.1F) : super.getDefaultDimensions(pose); }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData group) { var result = super.finalizeSpawn(level, difficulty, reason, group); if (kind() == 2) { setNoGravity(true); teleportTo(getX(), getY() + 1, getZ()); } return result; }
    @Override protected SoundEvent getAmbientSound() { return kind() == 3 ? null : kind() == 2 ? customSound("galvaniteambient") : SoundEvents.STRAY_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return kind() == 3 ? SoundEvents.IRON_GOLEM_HURT : kind() == 2 ? customSound("galvanitehurt") : SoundEvents.STRAY_HURT; }
    @Override protected SoundEvent getDeathSound() { return kind() == 3 ? SoundEvents.IRON_GOLEM_DEATH : kind() == 2 ? customSound("galvanitedeath") : SoundEvents.STRAY_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { if (kind() != 2) playSound(kind() == 3 ? SoundEvents.IRON_GOLEM_STEP : SoundEvents.STRAY_STEP, 0.15F, 1); }
    @Override public void aiStep() { super.aiStep(); updateSwingTime(); }
    @Override protected void tickEffects() {
        if (!level().isClientSide() || !entityData.get(COOLDOWN_VISIBLE)) { super.tickEffects(); return; }
        for (var effect : new ArrayList<>(getActiveEffects())) {
            onEffectUpdated(effect, true, null);
            if (effect.getDuration() % 600 == 0) onEffectUpdated(effect, false, null);
        }
        boolean spawn = isInvisible() ? random.nextInt(15) == 0 : random.nextBoolean();
        if (spawn) LegacyEffectParticles.emit(this, 0xFFFFFF, 2);
    }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output); output.putBoolean("SoldierExperiencePending", soldierExperiencePending);
        if (soldierExperienceAttacker != null) output.putString("SoldierExperienceAttacker", soldierExperienceAttacker.toString());
        output.putString("Texture", texture()); output.putInt("IceCooldown", cooldown); output.putInt("IceMovementLock", movementLock); output.putInt("IceAttackAge", attackAge);
        if (pendingTarget != null) output.putString("IceTarget", pendingTarget.toString()); output.putDouble("IceOriginX", attackOrigin.x); output.putDouble("IceOriginY", attackOrigin.y); output.putDouble("IceOriginZ", attackOrigin.z);
        output.putString("IceDimension", combatDimension); output.putBoolean("IceAlternate", alternate); output.putBoolean("IceDriftMovePending", driftMovePending); output.putBoolean("IceDriftSlowPending", driftSlowPending); output.putInt("IceDriftCooldown", driftCooldown); output.putBoolean("IceDeathSpawned", deathSpawned);
    }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        loadedFromSave = true; super.readAdditionalSaveData(input); attributesApplied = false; applyConfiguredAttributes();
        soldierExperiencePending = input.getBooleanOr("SoldierExperiencePending", false); soldierExperienceAttacker = input.getString("SoldierExperienceAttacker").map(java.util.UUID::fromString).orElse(null);
        if (input.read("Texture", com.mojang.serialization.Codec.PASSTHROUGH).isPresent()) setTexture(input.getStringOr("Texture", ""));
        cooldown = Math.max(0, Math.min(72000, input.getIntOr("IceCooldown", 0))); movementLock = Math.max(0, Math.min(29, input.getIntOr("IceMovementLock", 0)));
        pendingTarget = input.getString("IceTarget").map(java.util.UUID::fromString).orElse(null); attackAge = pendingTarget != null && input.read("IceAttackAge", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? Math.max(-1, Math.min(22, input.getIntOr("IceAttackAge", 0))) : -1;
        attackOrigin = new Vec3(input.getDoubleOr("IceOriginX", 0.0), input.getDoubleOr("IceOriginY", 0.0), input.getDoubleOr("IceOriginZ", 0.0));
        if (!Double.isFinite(attackOrigin.x) || !Double.isFinite(attackOrigin.y) || !Double.isFinite(attackOrigin.z)) { pendingTarget = null; attackAge = -1; attackOrigin = position(); }
        combatDimension = input.getStringOr("IceDimension", ""); alternate = input.getBooleanOr("IceAlternate", false);
        int legacyDriftPhase = input.read("IceDriftPhase", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? Math.clamp(input.getIntOr("IceDriftPhase", 0), -1, 1) : -1;
        driftMovePending = input.read("IceDriftMovePending", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? input.getBooleanOr("IceDriftMovePending", false) : legacyDriftPhase == 1;
        driftSlowPending = input.read("IceDriftSlowPending", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? input.getBooleanOr("IceDriftSlowPending", false) : legacyDriftPhase == 0;
        driftCooldown = Math.clamp(input.getIntOr("IceDriftCooldown", 0), 0, 40); deathSpawned = input.getBooleanOr("IceDeathSpawned", false);
        entityData.set(COOLDOWN_VISIBLE, kind() == 3 && cooldown > 0); if (!level().isClientSide()) syncMovementLock(); restoreAnimation = attackAge >= 0; startedAt = Long.MIN_VALUE;
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        int transitionTicks = kind() >= 2 ? 0 : 4;
        var procedure = new LegacyAnimationController<>(this, "procedure", transitionTicks, state -> PlayState.STOP).triggerableAnim("attack", RawAnimation.begin().thenPlay("attack")).triggerableAnim("attack2", RawAnimation.begin().thenPlay("attack2")).triggerableAnim("ready", RawAnimation.begin().thenPlay("ready"));
        controllers.add(new LegacyAnimationController<>(this, "movement", transitionTicks, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            boolean moving = state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F;
            if (kind() == 2) return state.setAndContinue(RawAnimation.begin().thenLoop("0"));
            if (kind() == 1 && !moving && isDeadOrDying()) return state.setAndContinue(RawAnimation.begin().thenPlay("hurt"));
            String animation = kind() == 3 ? isSprinting() ? "walk2" : moving && !isAggressive() || isAggressive() && state.isMoving() ? "walk" : "idle" : moving ? "walk" : "idle";
            return state.setAndContinue(RawAnimation.begin().thenLoop(animation));
        })); controllers.add(procedure);
        ThermalShockReforged.LOGGER.debug("Ice animation controllers registered: entity={}, kind={}, transitionTicks={}", getUUID(), kind(), transitionTicks);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
