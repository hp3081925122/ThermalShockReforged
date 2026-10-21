package com.hp.thermalshockreforged.entity.mangler;

import java.util.UUID;
import com.hp.thermalshockreforged.entity.summon.SummonLedger;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class HeraldEntity extends Monster implements GeoEntity, ServerEndTickTask {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(HeraldEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private Vec3 anchor;
    private String anchorDimension = "";
    private long summonReadyAt;
    private int killCount;
    private int activationRemaining = -1;
    private Vec3 activationOrigin = Vec3.ZERO;
    private boolean active;
    private boolean keyDropped;
    private boolean deathVisualStarted;
    private boolean configuredAttributes;
    private boolean loadedFromSave;
    public HeraldEntity(EntityType<? extends HeraldEntity> type, Level level) { super(type, level); xpReward = 500; setPersistenceRequired(); applyConfiguredAttributes(); }
    void applyConfiguredAttributes() {
        if (level().isClientSide || configuredAttributes || !ManglerCombatConfig.SPEC.isLoaded()) return;
        float savedHealth = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(ManglerCombatConfig.HERALD_HEALTH.get());
        getAttribute(Attributes.ARMOR).setBaseValue(ManglerCombatConfig.HERALD_ARMOR.get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(ManglerCombatConfig.HERALD_RESISTANCE.get());
        getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(ManglerCombatConfig.HERALD_KNOCKBACK.get());
        setHealth(loadedFromSave ? Math.min(savedHealth, getMaxHealth()) : getMaxHealth());
        configuredAttributes = true;
        ThermalShockReforged.LOGGER.debug("Herald configured attributes applied: uuid={}, loadedFromSave={}, health={}, maxHealth={}, armor={}, knockback={}, resistance={}", getUUID(), loadedFromSave, getHealth(), getMaxHealth(), getAttributeValue(Attributes.ARMOR), getAttributeValue(Attributes.ATTACK_KNOCKBACK), getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
    }
    public static AttributeSupplier.Builder createAttributes() { return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.MAX_HEALTH, ManglerCombatConfig.HERALD_HEALTH.getDefault()).add(Attributes.ARMOR, ManglerCombatConfig.HERALD_ARMOR.getDefault()).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, ManglerCombatConfig.HERALD_RESISTANCE.getDefault()).add(Attributes.ATTACK_KNOCKBACK, ManglerCombatConfig.HERALD_KNOCKBACK.getDefault()).add(Attributes.STEP_HEIGHT, 0); }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "herald"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String value) { entityData.set(TEXTURE, value); }
    public int killCount() { return killCount; }
    public void setAnimation(String value) { triggerAnim("procedure", value); }
    @Override
    protected void registerGoals() { goalSelector.addGoal(1, new RandomLookAroundGoal(this)); }
    @Override
    public boolean removeWhenFarAway(double distance) { return false; }
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!keyDropped) {
            ThermalShockReforged.LOGGER.debug("Herald damage blocked before key generation: entity={}, amount={}, source={}", getUUID(), amount, source.typeHolder().unwrapKey().map(key -> key.location()).orElse(null));
            return false;
        }
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) return false;
        return super.hurt(source, amount);
    }
    void acceptedDamage() {
        if (!(level() instanceof ServerLevel server) || !isAlive()) return;
        if (SummonLedger.clock(server) < summonReadyAt) return;
        summonReadyAt = SummonLedger.clock(server) + ManglerCombatConfig.SUMMON_COOLDOWN.get();
        long until = SummonLedger.clock(server) + ManglerCombatConfig.SUMMON_LIFETIME.get();
        var ledger = SummonLedger.get(server); var reservation = UUID.randomUUID();
        if (!ledger.reserve(getUUID(), reservation, until, ManglerCombatConfig.SUMMON_CAP.get())) return;
        if (random.nextDouble() >= ManglerCombatConfig.SUMMON_CHANCE.get()) { ledger.release(getUUID(), reservation); return; }
        var spawnPos = summonPosition();
        var child = ManglerContent.SUMMONED_MANGLER.get().spawn(server, spawnPos, MobSpawnType.MOB_SUMMONED);
        if (child == null || server.getEntity(child.getUUID()) != child) { ledger.release(getUUID(), reservation); return; }
        child.bind(getUUID(), until); child.setDeltaMovement(Vec3.ZERO);
        ledger.replace(getUUID(), reservation, child.getUUID(), until);
        ThermalShockReforged.LOGGER.debug("Herald summoned nearby creature: herald={}, child={}, position={}", getUUID(), child.getUUID(), spawnPos);
    }
    private BlockPos summonPosition() {
        double angle = random.nextDouble() * Math.PI * 2;
        double radius = 2.0 + random.nextDouble() * 2.0;
        return BlockPos.containing(position().add(Math.cos(angle) * radius, random.nextDouble() * 1.5 - 0.5, Math.sin(angle) * radius));
    }
    void siphon(LivingEntity victim) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || killCount >= 10 || isShiftKeyDown() || active || keyDropped) return;
        var origin = victim.position();
        server.sendParticles(StatusContent.BLOOD_SPIRAL.get(), origin.x, origin.y + victim.getEyeHeight(), origin.z, 1, 0.001, 0.001, 0.001, 0);
        server.sendParticles(StatusContent.BLOOD_ATTACK.get(), origin.x, origin.y + getEyeHeight() - 2, origin.z, 1, 0.001, 0.001, 0.001, 0);
        setAnimation(random.nextDouble() < 0.5 ? "siphon" : "siphon2");
        soundAt(origin, SoundEvents.WARDEN_EMERGE, 6); soundAt(origin, SoundEvents.BELL_BLOCK, -3);
        killCount++;
        if (killCount >= 10) { activationRemaining = 20; activationOrigin = origin; ServerEndTickTasks.schedule(server, getUUID()); }
    }
    private void soundAt(Vec3 origin, SoundEvent sound, float pitch) { level().playSound(null, BlockPos.containing(origin), sound, SoundSource.HOSTILE, 1, pitch); }
    @Override
    public void baseTick() {
        applyConfiguredAttributes();
        super.baseTick();
        if (level().isClientSide) return;
        String dimension = level().dimension().location().toString();
        if (anchor == null || !anchorDimension.equals(dimension)) {
            if (anchor != null && activationRemaining >= 0) { activationRemaining = -1; if (!active && !keyDropped && isAlive()) killCount = Math.min(killCount, 9); }
            anchor = position(); anchorDimension = dimension;
        }
        if (active) setShiftKeyDown(true);
        if (position().distanceToSqr(anchor) > 1.0E-10) teleportTo(anchor.x, anchor.y, anchor.z);
    }
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData group) {
        var result = super.finalizeSpawn(level, difficulty, reason, group);
        anchor = position(); anchorDimension = level.getLevel().dimension().location().toString();
        ThermalShockReforged.LOGGER.debug("Herald spawn anchor initialized: entity={}, position={}, dimension={}, reason={}", getUUID(), anchor, anchorDimension, reason);
        return result;
    }
    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (activationRemaining >= 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override
    public void tick() {
        super.tick();
        if (activationRemaining >= 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override
    public void endTick(ServerLevel server) {
        if (isRemoved() || activationRemaining < 0) return;
        if (!isAlive()) { activationRemaining = -1; return; }
        if (!anchorDimension.isEmpty() && !anchorDimension.equals(server.dimension().location().toString())) {
            activationRemaining = -1;
            if (!active && !keyDropped) killCount = Math.min(killCount, 9);
            return;
        }
        if (--activationRemaining > 0) { ServerEndTickTasks.schedule(server, getUUID()); return; }
        activationRemaining = -1; active = true; keyDropped = true; setShiftKeyDown(true);
        soundAt(activationOrigin, SoundEvents.BELL_RESONATE, 1); soundAt(activationOrigin, SoundEvents.BEACON_ACTIVATE, -2);
        for (var drop : com.hp.thermalshockreforged.content.loot.LootContent.roll(server, "gameplay/herald_key", this)) com.hp.thermalshockreforged.content.loot.LootContent.spawn(server, activationOrigin, drop, 10);
    }
    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (dead && !deathVisualStarted && level() instanceof ServerLevel server) { deathVisualStarted = true; activationRemaining = -1; com.hp.thermalshockreforged.entity.attack.BossDeathEffectEntity.submit(server, ManglerContent.HERALD_DEATH_EFFECT.get(), position(), getEyeHeight()); }
    }
    @Override
    public SoundEvent getAmbientSound() { return sound("monolithambient"); }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return sound("monolithhurt"); }
    @Override
    protected SoundEvent getDeathSound() { return sound("monolithdeath"); }
    static SoundEvent sound(String path) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", path)); }
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); tag.putLong("SummonReadyAt", summonReadyAt); tag.putInt("HeraldKillCount", killCount); tag.putBoolean("HeraldBloom", active); tag.putBoolean("KeyDropped", keyDropped); tag.putBoolean("DeathVisualStarted", deathVisualStarted); tag.putInt("ActivationRemaining", activationRemaining);
        tag.putDouble("ActivationX", activationOrigin.x); tag.putDouble("ActivationY", activationOrigin.y); tag.putDouble("ActivationZ", activationOrigin.z);
        if (anchor != null) { tag.putDouble("HeraldXpos", anchor.x); tag.putDouble("HeraldYpos", anchor.y); tag.putDouble("HeraldZpos", anchor.z); tag.putString("AnchorDimension", anchorDimension); }
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        loadedFromSave = true;
        super.readAdditionalSaveData(tag);
        configuredAttributes = false;
        applyConfiguredAttributes();
        if (tag.contains("Texture")) setTexture(tag.getString("Texture")); summonReadyAt = tag.getLong("SummonReadyAt");
        var legacy = getPersistentData(); killCount = Math.max(0, Math.min(10, tag.contains("HeraldKillCount") ? tag.getInt("HeraldKillCount") : (int) legacy.getDouble("HeraldKillCount"))); active = tag.getBoolean("HeraldBloom") || legacy.getString("HeraldActive").equals("Yup"); keyDropped = tag.getBoolean("KeyDropped") || active; deathVisualStarted = tag.getBoolean("DeathVisualStarted");
        activationRemaining = !keyDropped && tag.contains("ActivationRemaining") ? Math.clamp(tag.getInt("ActivationRemaining"), -1, 20) : -1; activationOrigin = new Vec3(tag.getDouble("ActivationX"), tag.getDouble("ActivationY"), tag.getDouble("ActivationZ"));
        if (!Double.isFinite(activationOrigin.x) || !Double.isFinite(activationOrigin.y) || !Double.isFinite(activationOrigin.z)) activationRemaining = -1;
        var anchorTag = tag.contains("HeraldXpos") ? tag : legacy;
        if (anchorTag.contains("HeraldXpos") && anchorTag.contains("HeraldYpos") && anchorTag.contains("HeraldZpos")) { anchor = new Vec3(anchorTag.getDouble("HeraldXpos"), anchorTag.getDouble("HeraldYpos"), anchorTag.getDouble("HeraldZpos")); anchorDimension = tag.contains("AnchorDimension") ? tag.getString("AnchorDimension") : level().dimension().location().toString(); }
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP).triggerableAnim("siphon", RawAnimation.begin().thenPlay("siphon")).triggerableAnim("siphon2", RawAnimation.begin().thenPlay("siphon2")).triggerableAnim("siphon3", RawAnimation.begin().thenPlay("siphon3"));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop(isShiftKeyDown() ? "idlebloom" : "idle")))); controllers.add(procedure);
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
