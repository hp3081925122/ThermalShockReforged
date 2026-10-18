package com.hp.thermalshockreforged.entity.icesquad;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.LegacyEffectParticles;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.ModGameRules;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Comparator;

public final class BastionEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BastionEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> COOLDOWN_VISIBLE = SynchedEntityData.defineId(BastionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntitySelector NEAREST_PLAYER = nearestSelector();
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossInfo;
    private int cooldown;
    private int attackAge = -1;
    private Vec3 attackOrigin = Vec3.ZERO;
    private String combatDimension = "";
    private long startedAt = Long.MIN_VALUE;
    private boolean attributesApplied;
    private boolean loadedFromSave;
    private boolean restoreAnimation;
    private boolean deathCommitted;
    private boolean experiencePending;
    private UUID experienceAttacker;

    public BastionEntity(EntityType<? extends BastionEntity> type, Level level) {
        super(type, level); xpReward = 400; setPersistenceRequired(); setTexture(burner() ? "icidrobastionred" : "icidrobastionblu");
        bossInfo = new ServerBossEvent(getDisplayName(), burner() ? BossEvent.BossBarColor.RED : BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);
        applyConfiguredAttributes();
    }
    public boolean burner() { return BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath().equals("icidroburner"); }
    private int kind() { return burner() ? 1 : 0; }
    public static AttributeSupplier.Builder createAttributes(int k) {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, BastionCombatConfig.SPEED[k].getDefault()).add(Attributes.MAX_HEALTH, BastionCombatConfig.HEALTH[k].getDefault())
                .add(Attributes.ARMOR, BastionCombatConfig.ARMOR[k].getDefault()).add(Attributes.ATTACK_DAMAGE, BastionCombatConfig.DAMAGE[k].getDefault()).add(Attributes.FOLLOW_RANGE, 48)
                .add(Attributes.KNOCKBACK_RESISTANCE, BastionCombatConfig.RESISTANCE[k].getDefault()).add(Attributes.ATTACK_KNOCKBACK, BastionCombatConfig.KNOCKBACK[k].getDefault()).add(Attributes.STEP_HEIGHT, 1);
    }
    void applyConfiguredAttributes() {
        if (level().isClientSide || attributesApplied || !BastionCombatConfig.SPEC.isLoaded()) return;
        int k = kind(); float health = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(BastionCombatConfig.HEALTH[k].get()); getAttribute(Attributes.ARMOR).setBaseValue(BastionCombatConfig.ARMOR[k].get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BastionCombatConfig.DAMAGE[k].get()); getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BastionCombatConfig.SPEED[k].get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(BastionCombatConfig.RESISTANCE[k].get()); getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(BastionCombatConfig.KNOCKBACK[k].get());
        setHealth(loadedFromSave ? Math.min(health, getMaxHealth()) : getMaxHealth()); attributesApplied = true;
        ThermalShockReforged.LOGGER.debug("Bastion attributes applied: uuid={}, burner={}, loadedFromSave={}, health={}, maxHealth={}", getUUID(), burner(), loadedFromSave, getHealth(), getMaxHealth());
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "icidrobastionblu"); builder.define(COOLDOWN_VISIBLE, false); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    public void setAnimation(String animation) { if (!"empty".equals(animation) && !"undefined".equals(animation)) triggerAnim("procedure", animation); }
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && distanceToSqr(target) <= (burner() ? 144 : 400); }
        });
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1)); goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
    }
    private static EntitySelector nearestSelector() {
        try { return new EntitySelectorParser(new StringReader("@p"), true).parse(); }
        catch (CommandSyntaxException failure) { throw new ExceptionInInitializerError(failure); }
    }
    private void faceNearest() {
        try {
            var players = NEAREST_PLAYER.findPlayers(createCommandSourceStack().withPermission(2));
            if (!players.isEmpty()) lookAt(EntityAnchorArgument.Anchor.FEET, players.getFirst().position());
        } catch (CommandSyntaxException failure) { ThermalShockReforged.LOGGER.debug("Bastion nearest-player query failed: uuid={}", getUUID(), failure); }
    }
    boolean attackAttempt(LivingEntity target, Entity direct) {
        if (!(target instanceof Player) || direct == null) return false;
        if (direct.getType() == (burner() ? IceSquadContent.BURNER_FLAME : IceSquadContent.ALT_CRYONIC_PROJECTILE).get()) return false;
        if (!(level() instanceof ServerLevel) || !isAlive() || target == this || cooldown > 0 || attackAge >= 0) return true;
        int k = kind(); startedAt = level().getGameTime(); attackOrigin = target.position(); combatDimension = level().dimension().location().toString();
        boolean dash = burner() ? distanceTo(target) >= 7 : distanceTo(target) <= 10;
        if (dash) {
            faceNearest(); setDeltaMovement(getLookAngle().scale(BastionCombatConfig.DASH_SPEED[k].get() * (burner() ? 1 : -1))); hasImpulse = true;
            sound(customSound("propulsionbootsactivate"), 1, -2); cooldown = BastionCombatConfig.DASH_COOLDOWN[k].get();
        } else {
            cooldown = BastionCombatConfig.SALVO_COOLDOWN[k].get(); attackAge = 0;
            if (!burner()) addEffect(new MobEffectInstance(StatusContent.STUNNED, 60, 1));
            setAnimation(burner() ? "aimflamethrower" : "aimchaingun");
        }
        entityData.set(COOLDOWN_VISIBLE, true);
        return true;
    }
    private static SoundEvent customSound(String name) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, name)); }
    private void sound(SoundEvent sound, float volume, float pitch) { level().playSound(null, BlockPos.containing(attackOrigin), sound, SoundSource.HOSTILE, volume, pitch); }
    private void advanceAttack(ServerLevel server) {
        if (attackAge < 0) return;
        int age = ++attackAge;
        if (age == 10) { setAnimation(burner() ? "fireflamethrower2" : "firechaingun2"); sound(SoundEvents.IRON_GOLEM_HURT, burner() ? 1 : 2, 5); }
        if (age >= 10 && age <= 55 && (age - 10) % 5 == 0) sound(SoundEvents.IRON_GOLEM_HURT, 0.5F, 2);
        if (age >= 10 && age <= 42 && (age - 10) % 2 == 0) {
            sound(burner() ? SoundEvents.FIRECHARGE_USE : customSound("hailfire"), 0.9F, burner() ? 2 : -1);
            faceNearest();
            var projectile = (burner() ? IceSquadContent.BURNER_FLAME : IceSquadContent.GUNNER_BULLET).get().create(server);
            if (projectile != null) {
                projectile.configure(this, false, BastionCombatConfig.MULTIPLIER[kind()].get(), 0); projectile.setCritArrow(burner());
                projectile.setPos(getX(), getY() + 1.5, getZ()); Vec3 look = getLookAngle(); projectile.shoot(look.x, look.y, look.z, burner() ? 1 : 2, burner() ? 8 : 3); server.addFreshEntity(projectile);
            }
        }
        if (age >= 55) attackAge = -1;
    }
    private void clearCombat() { attackAge = -1; cooldown = 0; entityData.set(COOLDOWN_VISIBLE, false); }
    @Override public void tick() {
        if (!level().isClientSide) {
            applyConfiguredAttributes(); String dimension = level().dimension().location().toString();
            if (!isAlive() || !combatDimension.isEmpty() && !combatDimension.equals(dimension)) clearCombat();
            combatDimension = dimension;
        }
        super.tick();
        if (!(level() instanceof ServerLevel server) || !isAlive()) return;
        if (restoreAnimation) { if (attackAge >= 0) setAnimation(attackAge < 10 ? burner() ? "aimflamethrower" : "aimchaingun" : burner() ? "fireflamethrower2" : "firechaingun2"); restoreAnimation = false; }
        if (server.getGameTime() == startedAt) return;
        if (cooldown > 0 && --cooldown == 0) entityData.set(COOLDOWN_VISIBLE, false);
        advanceAttack(server);
    }
    @Override protected void tickEffects() {
        if (!level().isClientSide || !entityData.get(COOLDOWN_VISIBLE)) { super.tickEffects(); return; }
        for (var effect : new ArrayList<>(getActiveEffects())) {
            boolean active = effect.tick(this, () -> onEffectUpdated(effect, true, null));
            if (active && effect.getDuration() % 600 == 0) onEffectUpdated(effect, false, null);
        }
        boolean spawn = isInvisible() ? random.nextInt(15) == 0 : random.nextBoolean();
        if (spawn) LegacyEffectParticles.emit(this, 0xFFFFFF, 2);
    }
    @Override public boolean hurt(DamageSource source, float amount) { return !source.is(DamageTypes.DROWN) && !source.is(DamageTypes.LIGHTNING_BOLT) && super.hurt(source, amount); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.IRON_GOLEM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.IRON_GOLEM_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.IRON_GOLEM_STEP, 0.15F, 1); }
    @Override public void startSeenByPlayer(ServerPlayer player) { super.startSeenByPlayer(player); bossInfo.addPlayer(player); }
    @Override public void stopSeenByPlayer(ServerPlayer player) { super.stopSeenByPlayer(player); bossInfo.removePlayer(player); }
    @Override protected void customServerAiStep() { super.customServerAiStep(); bossInfo.setProgress(getHealth() / getMaxHealth()); }
    @Override public void die(DamageSource source) {
        super.die(source);
        if (!dead || deathCommitted || !(level() instanceof ServerLevel server)) return;
        deathCommitted = true; clearCombat();
        if (server.getGameRules().getBoolean(ModGameRules.LOQENDIA_DROP_HEART_CRYSTALS)) { var crystal = new ItemEntity(server, getX(), getY(), getZ(), new ItemStack(MaterialContent.items().get("heart_crystal").get())); crystal.setPickUpDelay(10); server.addFreshEntity(crystal); }
        Vec3 center = position();
        for (var other : server.getEntitiesOfClass(BastionEntity.class, new AABB(center, center).inflate(21), entity -> entity.burner() != burner()).stream().sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(center))).toList()) other.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 9999, 1, true, false));
    }
    @Override protected void dropExperience(Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() {
        if (++deathTime >= 20 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) { experiencePending = false; super.dropExperience(experienceAttacker == null ? null : server.getEntity(experienceAttacker)); }
        }
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); tag.putInt("BastionCooldown", cooldown); tag.putInt("BastionAttackAge", attackAge);
        tag.putDouble("BastionOriginX", attackOrigin.x); tag.putDouble("BastionOriginY", attackOrigin.y); tag.putDouble("BastionOriginZ", attackOrigin.z); tag.putString("BastionDimension", combatDimension); tag.putBoolean("BastionDeathCommitted", deathCommitted);
        tag.putBoolean("BastionExperiencePending", experiencePending); if (experienceAttacker != null) tag.putUUID("BastionExperienceAttacker", experienceAttacker);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        loadedFromSave = true; super.readAdditionalSaveData(tag); attributesApplied = false; applyConfiguredAttributes(); setPersistenceRequired();
        if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        cooldown = Math.clamp(tag.getInt("BastionCooldown"), 0, 72000); attackAge = tag.contains("BastionAttackAge") ? Math.clamp(tag.getInt("BastionAttackAge"), -1, 54) : -1;
        attackOrigin = new Vec3(tag.getDouble("BastionOriginX"), tag.getDouble("BastionOriginY"), tag.getDouble("BastionOriginZ"));
        if (!Double.isFinite(attackOrigin.x) || !Double.isFinite(attackOrigin.y) || !Double.isFinite(attackOrigin.z)) { attackOrigin = position(); attackAge = -1; }
        combatDimension = tag.getString("BastionDimension"); deathCommitted = tag.getBoolean("BastionDeathCommitted");
        experiencePending = tag.getBoolean("BastionExperiencePending"); experienceAttacker = tag.hasUUID("BastionExperienceAttacker") ? tag.getUUID("BastionExperienceAttacker") : null;
        entityData.set(COOLDOWN_VISIBLE, cooldown > 0); restoreAnimation = attackAge >= 0; startedAt = Long.MIN_VALUE; bossInfo.setName(getDisplayName());
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP);
        for (String name : new String[]{"idle", "walk", "aimchaingun", "firechaingun", "firechaingun2", "aimflamethrower", "fireflamethrower", "fireflamethrower2"}) procedure.triggerableAnim(name, RawAnimation.begin().thenPlay(name));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop(state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F ? "walk" : "idle")))); controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
