package com.hp.thermalshockreforged.entity;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;

import com.hp.thermalshockreforged.config.CombatConfig;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import java.util.UUID;

public final class ForecastEntity extends Monster implements GeoEntity, ServerEndTickTask {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ForecastEntity.class, EntityDataSerializers.STRING);
    private static final Identifier DRIFT_SPEED = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "forecast_drift_speed");
    private static final EntitySelector NEAREST_PLAYER = nearestSelector();
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private int driftCooldown;
    private boolean driftMovePending;
    private boolean driftSlowPending;
    private int attackCooldown;
    private boolean spawnInitialized;
    private boolean experiencePending;
    private UUID experienceAttacker;

    public ForecastEntity(EntityType<? extends ForecastEntity> type, Level level) {
        super(type, level);
        xpReward = 8;
        setNoGravity(true);
        applyConfiguredAttributes(true);
    }

    private void applyConfiguredAttributes(boolean initial) {
        if (!(level() instanceof ServerLevel) || !CombatConfig.SPEC.isLoaded()) return;
        float health = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(CombatConfig.FORECAST_HEALTH.get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(CombatConfig.FORECAST_DAMAGE.get());
        setHealth(initial ? getMaxHealth() : Math.min(health, getMaxHealth()));
        ThermalShockReforged.LOGGER.debug("Configured Forecast entity={} initial={} health={} maxHealth={} attackDamage={}",
                getUUID(), initial, getHealth(), getMaxHealth(), getAttributeValue(Attributes.ATTACK_DAMAGE));
    }

    public static void join(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ForecastEntity forecast) forecast.applyConfiguredAttributes(false);
    }

    public static void attack(AttackAttemptEvent event) {
        if (event.getSource().getEntity() instanceof ForecastEntity forecast) {
            event.setCanceled(true);
            forecast.doHurtTarget(event.getEntity());
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 36).add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ARMOR, 6).add(Attributes.ATTACK_DAMAGE, 10).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, 0.6);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE, "forecast");
    }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    public void setAnimation(String animation) {
        if (!"empty".equals(animation) && !"undefined".equals(animation)) triggerAnim("procedure", animation);
    }

    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
            @Override protected boolean canPerformAttack(net.minecraft.world.entity.LivingEntity target) {
                return isTimeToAttack() && distanceToSqr(target) <= 100;
            }
        });
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, true));
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity target) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || target == this || attackCooldown > 0 || !target.isAlive() || target.level() != level()) return false;
        attackCooldown = CombatConfig.FORECAST_COOLDOWN.get();
        setAnimation("attack2");
        server.playSound(null, target.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1, 3);
        ZapWarningEntity zap = ModEntities.ZAP.get().spawn(server, target.blockPosition(), EntitySpawnReason.MOB_SUMMONED);
        if (zap != null) {
            zap.arm(this, CombatConfig.FORECAST_ZAP_MULTIPLIER.get());
            zap.setYRot(random.nextFloat() * 360);
        } else {
            ThermalShockReforged.LOGGER.debug("Forecast zap spawn rejected: caster={}, target={}", getUUID(), target.getUUID());
        }
        return true;
    }

    @Override public void baseTick() {
        super.baseTick();
        if (!level().isClientSide()) {
            if (attackCooldown > 0) attackCooldown--;
            if (driftCooldown > 0) driftCooldown--;
            if (isAlive() && driftCooldown == 0 && !hasEffect(MobEffects.SLOWNESS)) driftMovePending = true;
            updateDriftSpeed();
            if ((driftMovePending || driftSlowPending) && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
        }
    }

    @Override public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server) || !isAlive()) return;
        if (!server.getEntitiesOfClass(Player.class, AABB.ofSize(position(), 10, 10, 10), player -> true).isEmpty()) {
            try {
                var players = NEAREST_PLAYER.findPlayers(createCommandSourceStack().withPermission(2));
                if (!players.isEmpty()) {
                    lookAt(EntityAnchorArgument.Anchor.FEET, players.getFirst().position());
                    setYBodyRot(getYRot());
                }
            } catch (CommandSyntaxException failure) {
                ThermalShockReforged.LOGGER.debug("Forecast nearest-player query failed: uuid={}", getUUID(), failure);
            }
        }
    }

    @Override public void onAddedToLevel() {
        super.onAddedToLevel();
        if ((driftMovePending || driftSlowPending) && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }

    @Override public void endTick(ServerLevel server) {
        if (isRemoved() || !isAlive()) { driftMovePending = false; driftSlowPending = false; return; }
        if (driftSlowPending) {
            driftCooldown = 40;
            driftSlowPending = false;
            updateDriftSpeed();
        }
        if (driftMovePending) {
            driftMovePending = false;
            setDeltaMovement(new Vec3(random.nextDouble() * 2 - 1, random.nextDouble() * 0.4 - 0.2, random.nextDouble() * 2 - 1));
            hasImpulse = true;
            driftSlowPending = true;
            ThermalShockReforged.LOGGER.debug("Forecast drift pulse: entity={}, slowPending={}", getUUID(), driftSlowPending);
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }

    private static EntitySelector nearestSelector() {
        try { return new EntitySelectorParser(new StringReader("@p"), true).parse(); }
        catch (CommandSyntaxException failure) { throw new ExceptionInInitializerError(failure); }
    }

    private void updateDriftSpeed() {
        var attribute = getAttribute(Attributes.MOVEMENT_SPEED);
        var actual = getEffect(MobEffects.SLOWNESS);
        double factor = actual == null ? 1 : 1 - (double) 0.15F * (actual.getAmplifier() + 1);
        double amount = driftCooldown > 0 && factor > 1 - (double) 0.15F * 3 ? (1 - (double) 0.15F * 3) / factor - 1 : 0;
        var previous = attribute.getModifier(DRIFT_SPEED);
        if (previous != null && previous.amount() == amount) return;
        if (previous != null) attribute.removeModifier(DRIFT_SPEED);
        if (amount != 0) attribute.addTransientModifier(new AttributeModifier(DRIFT_SPEED, amount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
        return !source.is(DamageTypes.FALL) && !source.is(DamageTypes.LIGHTNING_BOLT) && super.hurtServer(level, source, amount);
    }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        if (!spawnInitialized) { spawnInitialized = true; setNoGravity(true); setPos(getX(), getY() + 1, getZ()); }
        return result;
    }

    @Override protected void dropExperience(net.minecraft.server.level.ServerLevel level, Entity attacker) {
        experiencePending = true;
        experienceAttacker = attacker == null ? null : attacker.getUUID();
    }
    @Override protected void tickDeath() {
        if (++deathTime >= 20 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) {
                experiencePending = false;
                super.dropExperience(server, experienceAttacker == null ? null : server.getEntity(experienceAttacker));
            }
        }
    }

    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("Texture", texture()); output.putInt("ZapCooldown", attackCooldown); output.putInt("DriftCooldown", driftCooldown);
        output.putBoolean("DriftMovePending", driftMovePending); output.putBoolean("DriftSlowPending", driftSlowPending); output.putBoolean("ForecastSpawnInitialized", spawnInitialized);
        output.putBoolean("ForecastExperiencePending", experiencePending);
        if (experienceAttacker != null) output.putString("ForecastExperienceAttacker", experienceAttacker.toString());
    }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        if (input.getString("Texture").isPresent()) setTexture(input.getStringOr("Texture", ""));
        attackCooldown = Math.clamp(input.getIntOr("ZapCooldown", 0), 0, 72000);
        driftCooldown = Math.clamp(input.getIntOr("DriftCooldown", 0), 0, 40);
        driftMovePending = input.getBooleanOr("DriftMovePending", false); driftSlowPending = input.getBooleanOr("DriftSlowPending", false); spawnInitialized = input.getBooleanOr("ForecastSpawnInitialized", false);
        experiencePending = input.getBooleanOr("ForecastExperiencePending", false);
        experienceAttacker = input.getString("ForecastExperienceAttacker").map(java.util.UUID::fromString).orElse(null);
        applyConfiguredAttributes(false);
        if (!level().isClientSide()) updateDriftSpeed();
    }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP)
                .triggerableAnim("idle", RawAnimation.begin().thenPlay("idle"))
                .triggerableAnim("attack", RawAnimation.begin().thenPlay("attack"))
                .triggerableAnim("attack2", RawAnimation.begin().thenPlay("attack2"));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation()
                ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
        controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
