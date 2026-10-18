package com.hp.thermalshockreforged.entity.ice;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.LegacyEffectParticles;
import com.hp.thermalshockreforged.content.status.StatusContent;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class IceCreatureEntity extends Monster implements GeoEntity, RangedAttackMob {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(IceCreatureEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> COOLDOWN_VISIBLE = SynchedEntityData.defineId(IceCreatureEntity.class, EntityDataSerializers.BOOLEAN);
    private static final ResourceLocation MOVEMENT_LOCK = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "ice_movement_lock");
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int cooldown;
    private int movementLock;
    private int attackAge = -1;
    private UUID pendingTarget;
    private Vec3 attackOrigin = Vec3.ZERO;
    private boolean alternate;
    private String combatDimension = "";
    private long startedAt = Long.MIN_VALUE;
    private int driftPhase = -1;
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
        if (level().isClientSide || attributesApplied || !IceCombatConfig.SPEC.isLoaded()) return;
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
            cooldown = IceCombatConfig.SOLDIER_COOLDOWN.get(); movementLock = 29; startedAt = level().getGameTime(); combatDimension = level().dimension().location().toString(); attackOrigin = target.position(); animate("attack"); sound(SoundEvents.STRAY_AMBIENT, 1, 3); syncMovementLock();
            return true;
        }
        pendingTarget = target.getUUID(); attackOrigin = target.position(); attackAge = 0; startedAt = level().getGameTime(); combatDimension = level().dimension().location().toString();
        if (k == 2) {
            cooldown = IceCombatConfig.DRONE_COOLDOWN.get(); animate("ready"); setTexture("icidrone_charged"); faceEyes(target); sound(customSound("galvanitehurt"), 0.8F, 3);
        } else {
            cooldown = IceCombatConfig.ZERK_COOLDOWN.get(); alternate = random.nextDouble() < IceCombatConfig.ZERK_ALT_CHANCE.get(); entityData.set(COOLDOWN_VISIBLE, true);
            if (alternate) { movementLock = 4; target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 5, true, true)); }
            else { animate("attack"); sound(SoundEvents.SHIELD_BREAK, 0.8F, -1); attackAge = -1; pendingTarget = null; }
        }
        syncMovementLock();
        return k != 3;
    }
    private void faceEyes(LivingEntity target) { lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition()); }
    private void sound(SoundEvent sound, float volume, float pitch) { level().playSound(null, BlockPos.containing(attackOrigin), sound, SoundSource.HOSTILE, volume, pitch); }
    private static SoundEvent customSound(String path) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path)); }
    private void syncMovementLock() {
        var attribute = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementLock > 0 && !attribute.hasModifier(MOVEMENT_LOCK)) attribute.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        if (movementLock <= 0) attribute.removeModifier(MOVEMENT_LOCK);
    }
    private void clearCombat() {
        attackAge = -1; pendingTarget = null; cooldown = 0; movementLock = 0; driftPhase = -1; driftCooldown = 0; entityData.set(COOLDOWN_VISIBLE, false); syncMovementLock();
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
            for (int i = 0; i < 3; i++) { var bullet = IceContent.ICE_BULLET.get().create(server); if (bullet == null) continue; bullet.configure(this, false, 5.0 / 3, 0); bullet.setPos(getX(), getEyeY(), getZ()); var look = getLookAngle(); bullet.shoot(look.x, look.y, look.z, 1.1F, 2); server.addFreshEntity(bullet); }
        } else if (kind() == 3 && attackAge == 10) {
            attackAge = -1; pendingTarget = null;
            if (target == null || !target.isAlive() || target == this) return;
            double strength = IceCombatConfig.ZERK_LAUNCH.get() * Math.max(0, 1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            if (strength > 0) target.setDeltaMovement(0, strength, 0);
            double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * IceCombatConfig.ZERK_ALT_MULTIPLIER.get();
            if (Double.isFinite(damage)) target.hurt(new DamageSource(registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC_KILL)), (float)Math.max(0, damage));
            sound(SoundEvents.IRON_GOLEM_ATTACK, 1, 1); sound(SoundEvents.ANVIL_PLACE, 1, -1); animate("attack2");
        }
    }
    @Override public void performRangedAttack(LivingEntity target, float power) {
        if (kind() != 1 || !(level() instanceof ServerLevel server) || !isAlive()) return;
        var bullet = IceContent.ICE_BULLET.get().create(server); if (bullet == null) return;
        bullet.configure(this, true, 2.5, IceCombatConfig.BULLET_KNOCKBACK.get()); bullet.setPos(getX(), getEyeY() - 0.1F, getZ());
        double dx = target.getX() - getX(), dz = target.getZ() - getZ();
        bullet.shoot(dx, target.getY() + target.getEyeHeight() - 1.1 - bullet.getY() + Math.hypot(dx, dz) * 0.2F, dz, 0.2F, 12);
        server.addFreshEntity(bullet);
    }
    private void droneTick(ServerLevel server) {
        if (!isAlive()) { server.playSound(null, blockPosition(), SoundEvents.BAMBOO_PLACE, SoundSource.HOSTILE, 0.1F, 3); return; }
        if (driftCooldown > 0) driftCooldown--;
        if (driftPhase == 1) { setDeltaMovement(random.nextDouble() * 2 - 1, random.nextDouble() * 0.4 - 0.2, random.nextDouble() * 2 - 1); driftPhase = 0; }
        else if (driftPhase == 0) { addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2, true, false)); driftCooldown = 40; driftPhase = -1; }
        else if (driftCooldown == 0 && !hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) driftPhase = 1;
        var center = position();
        if (!server.getEntitiesOfClass(Player.class, AABB.ofSize(center, 10, 10, 10), player -> true).isEmpty()) {
            var nearest = server.getServer().getPlayerList().getPlayers().stream().min(Comparator.comparingDouble(player -> player.distanceToSqr(center))).orElse(null);
            if (nearest != null) lookAt(EntityAnchorArgument.Anchor.FEET, nearest.position());
        }
        server.playSound(null, blockPosition(), SoundEvents.BAMBOO_PLACE, SoundSource.HOSTILE, 0.1F, 3);
    }
    @Override public void baseTick() {
        super.baseTick();
        if (level() instanceof ServerLevel server && kind() == 2) droneTick(server);
        if (!level().isClientSide && kind() == 3 && isInWater()) { addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 2, 3, true, false)); addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 3, true, false)); }
    }
    @Override public void tick() {
        if (!level().isClientSide) {
            applyConfiguredAttributes();
            if (kind() == 0 && attackAge >= 0 && pendingTarget != null && level() instanceof ServerLevel server
                    && (combatDimension.isEmpty() || combatDimension.equals(server.dimension().location().toString()))
                    && SoldierComboEntity.submit(server, this, pendingTarget, attackOrigin, attackAge)) { attackAge = -1; pendingTarget = null; }
            String dimension = level().dimension().location().toString();
            if (!combatDimension.isEmpty() && !combatDimension.equals(dimension) || !isAlive()) clearCombat();
            combatDimension = dimension; syncMovementLock();
        }
        super.tick();
        if (!(level() instanceof ServerLevel server) || !isAlive()) return;
        if (restoreAnimation) { if (attackAge >= 0 && kind() != 3) animate(kind() == 0 ? "attack" : "ready"); restoreAnimation = false; }
        if (server.getGameTime() == startedAt) return;
        if (cooldown > 0 && --cooldown == 0) entityData.set(COOLDOWN_VISIBLE, false);
        if (movementLock > 0) { movementLock--; syncMovementLock(); }
        advanceAttack(server);
    }
    @Override public void die(DamageSource source) {
        super.die(source);
        if (!dead) return;
        clearCombat();
        if (kind() == 1 && !deathSpawned && level() instanceof ServerLevel server) {
            deathSpawned = true;
            var drone = IceContent.ICIDRONE.get().spawn(server, BlockPos.containing(getX() + 0.5, getY() + 0.5, getZ() + 0.5), MobSpawnType.MOB_SUMMONED);
            if (drone != null) drone.setDeltaMovement(0, 0.3, 0);
            ThermalShockReforged.LOGGER.debug("Cryomancer death spawn committed: source={}, drone={}", getUUID(), drone == null ? null : drone.getUUID());
        }
    }
    @Override protected void dropExperience(Entity attacker) {
        if (kind() != 0) { super.dropExperience(attacker); return; }
        soldierExperiencePending = true; soldierExperienceAttacker = attacker == null ? null : attacker.getUUID();
    }
    @Override protected void tickDeath() {
        if (kind() != 0) { super.tickDeath(); return; }
        if (++deathTime >= 20 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (soldierExperiencePending && level() instanceof ServerLevel server) {
                soldierExperiencePending = false; super.dropExperience(soldierExperienceAttacker == null ? null : server.getEntity(soldierExperienceAttacker));
            }
        }
    }
    @Override public boolean hurt(DamageSource source, float amount) { return !(kind() == 2 ? source.is(DamageTypes.FALL) : source.is(DamageTypes.DROWN)) && super.hurt(source, amount); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override protected EntityDimensions getDefaultDimensions(Pose pose) { return kind() == 0 ? super.getDefaultDimensions(pose).scale(1.1F) : super.getDefaultDimensions(pose); }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData group) { var result = super.finalizeSpawn(level, difficulty, reason, group); if (kind() == 2) { setNoGravity(true); teleportTo(getX(), getY() + 1, getZ()); } return result; }
    @Override protected SoundEvent getAmbientSound() { return kind() == 3 ? null : kind() == 2 ? customSound("galvaniteambient") : SoundEvents.STRAY_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return kind() == 3 ? SoundEvents.IRON_GOLEM_HURT : kind() == 2 ? customSound("galvanitehurt") : SoundEvents.STRAY_HURT; }
    @Override protected SoundEvent getDeathSound() { return kind() == 3 ? SoundEvents.IRON_GOLEM_DEATH : kind() == 2 ? customSound("galvanitedeath") : SoundEvents.STRAY_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { if (kind() != 2) playSound(kind() == 3 ? SoundEvents.IRON_GOLEM_STEP : SoundEvents.STRAY_STEP, 0.15F, 1); }
    @Override public void aiStep() { super.aiStep(); updateSwingTime(); }
    @Override protected void tickEffects() {
        if (!level().isClientSide || !entityData.get(COOLDOWN_VISIBLE)) { super.tickEffects(); return; }
        for (var effect : new ArrayList<>(getActiveEffects())) {
            boolean active = effect.tick(this, () -> onEffectUpdated(effect, true, null));
            if (active && effect.getDuration() % 600 == 0) onEffectUpdated(effect, false, null);
        }
        boolean spawn = isInvisible() ? random.nextInt(15) == 0 : random.nextBoolean();
        if (spawn) LegacyEffectParticles.emit(this, 0xFFFFFF, 2);
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); tag.putBoolean("SoldierExperiencePending", soldierExperiencePending);
        if (soldierExperienceAttacker != null) tag.putUUID("SoldierExperienceAttacker", soldierExperienceAttacker);
        tag.putString("Texture", texture()); tag.putInt("IceCooldown", cooldown); tag.putInt("IceMovementLock", movementLock); tag.putInt("IceAttackAge", attackAge);
        if (pendingTarget != null) tag.putUUID("IceTarget", pendingTarget); tag.putDouble("IceOriginX", attackOrigin.x); tag.putDouble("IceOriginY", attackOrigin.y); tag.putDouble("IceOriginZ", attackOrigin.z);
        tag.putString("IceDimension", combatDimension); tag.putBoolean("IceAlternate", alternate); tag.putInt("IceDriftPhase", driftPhase); tag.putInt("IceDriftCooldown", driftCooldown); tag.putBoolean("IceDeathSpawned", deathSpawned);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        loadedFromSave = true; super.readAdditionalSaveData(tag); attributesApplied = false; applyConfiguredAttributes();
        if (!tag.contains("NeoForgeData", 10) && tag.contains("ForgeData", 10)) getPersistentData().merge(tag.getCompound("ForgeData"));
        soldierExperiencePending = tag.getBoolean("SoldierExperiencePending"); soldierExperienceAttacker = tag.hasUUID("SoldierExperienceAttacker") ? tag.getUUID("SoldierExperienceAttacker") : null;
        if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        cooldown = Math.max(0, Math.min(72000, tag.getInt("IceCooldown"))); movementLock = Math.max(0, Math.min(29, tag.getInt("IceMovementLock")));
        pendingTarget = tag.hasUUID("IceTarget") ? tag.getUUID("IceTarget") : null; attackAge = pendingTarget != null && tag.contains("IceAttackAge") ? Math.max(-1, Math.min(22, tag.getInt("IceAttackAge"))) : -1;
        attackOrigin = new Vec3(tag.getDouble("IceOriginX"), tag.getDouble("IceOriginY"), tag.getDouble("IceOriginZ"));
        if (!Double.isFinite(attackOrigin.x) || !Double.isFinite(attackOrigin.y) || !Double.isFinite(attackOrigin.z)) { pendingTarget = null; attackAge = -1; attackOrigin = position(); }
        combatDimension = tag.getString("IceDimension"); alternate = tag.getBoolean("IceAlternate"); driftPhase = tag.contains("IceDriftPhase") ? Math.max(-1, Math.min(1, tag.getInt("IceDriftPhase"))) : -1; driftCooldown = Math.max(0, Math.min(40, tag.getInt("IceDriftCooldown"))); deathSpawned = tag.getBoolean("IceDeathSpawned");
        entityData.set(COOLDOWN_VISIBLE, kind() == 3 && cooldown > 0); if (!level().isClientSide) syncMovementLock(); restoreAnimation = attackAge >= 0; startedAt = Long.MIN_VALUE;
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("attack", RawAnimation.begin().thenPlay("attack")).triggerableAnim("attack2", RawAnimation.begin().thenPlay("attack2")).triggerableAnim("ready", RawAnimation.begin().thenPlay("ready"));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            boolean moving = state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F;
            if (kind() == 2) return state.setAndContinue(RawAnimation.begin().thenLoop("0"));
            if (kind() == 1 && !moving && isDeadOrDying()) return state.setAndContinue(RawAnimation.begin().thenPlay("hurt"));
            String animation = kind() == 3 ? isSprinting() ? "walk2" : moving && !isAggressive() || isAggressive() && state.isMoving() ? "walk" : "idle" : moving ? "walk" : "idle";
            return state.setAndContinue(RawAnimation.begin().thenLoop(animation));
        })); controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
