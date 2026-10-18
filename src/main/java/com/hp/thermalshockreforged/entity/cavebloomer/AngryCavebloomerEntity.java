package com.hp.thermalshockreforged.entity.cavebloomer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.aura.AuraContent;
import com.hp.thermalshockreforged.entity.boss.BossDeathSequence;
import com.hp.thermalshockreforged.world.WorldProgressData;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class AngryCavebloomerEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(AngryCavebloomerEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private final CavebloomerAttacks attacks = new CavebloomerAttacks(this);
    private final BossDeathSequence deathSequence = new BossDeathSequence();
    private final ServerBossEvent bossBar = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.NOTCHED_6);
    private boolean deathCommitted, experiencePending;
    private UUID experienceAttacker;
    public AngryCavebloomerEntity(EntityType<? extends AngryCavebloomerEntity> type, Level level) {
        super(type, level); xpReward = 1000; setPersistenceRequired(); applyConfiguredAttributes(true);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 325).add(Attributes.ARMOR, 10).add(Attributes.ATTACK_DAMAGE, 7)
                .add(Attributes.MOVEMENT_SPEED, 0.2).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, 0.2)
                .add(Attributes.ATTACK_KNOCKBACK, 0.6).add(Attributes.STEP_HEIGHT, 0.6);
    }
    void applyConfiguredAttributes(boolean initial) {
        if (!(level() instanceof ServerLevel) || !CavebloomerConfig.SPEC.isLoaded()) return;
        float health = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(CavebloomerConfig.HEALTH.get()); getAttribute(Attributes.ARMOR).setBaseValue(CavebloomerConfig.ARMOR.get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(CavebloomerConfig.DAMAGE.get()); getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(CavebloomerConfig.SPEED.get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(CavebloomerConfig.RESISTANCE.get()); getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(CavebloomerConfig.KNOCKBACK.get());
        setHealth(initial ? getMaxHealth() : Math.min(health, getMaxHealth()));
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(TEXTURE, "angry_cavebloomer"); }
    public String texture() { return entityData.get(TEXTURE); }
    public void setTexture(String texture) { entityData.set(TEXTURE, texture); }
    public void setAnimation(String animation) {
        if (!"empty".equals(animation) && !"undefined".equals(animation)) triggerAnim("procedure", animation);
    }
    @Override protected void registerGoals() {
        super.registerGoals(); targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && distanceToSqr(target) <= 36; }
        });
        goalSelector.addGoal(3, new RandomStrollGoal(this, 1)); targetSelector.addGoal(4, new HurtByTargetGoal(this));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this)); goalSelector.addGoal(6, new FloatGoal(this));
    }
    @Override public boolean doHurtTarget(Entity target) { attacks.begin(target); return false; }
    void beginAttack(Entity target) { attacks.begin(target); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    public void sound(SoundEvent sound, float volume, float pitch) { level().playSound(null, blockPosition(), sound, SoundSource.HOSTILE, volume, pitch); }
    public void sound(String name, float volume, float pitch) { sound(soundEvent(name), volume, pitch); }
    private SoundEvent soundEvent(String name) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, name)); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return soundEvent("cavebloomer_hurt"); }
    @Override protected SoundEvent getDeathSound() { return soundEvent("cavebloomer_death"); }
    @Override public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() != null && source.getDirectEntity() instanceof CavebloomerBombEntity bomb) {
            if (!level().isClientSide) bomb.discard(); return false;
        }
        return !source.is(DamageTypes.FALL) && super.hurt(source, amount);
    }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            attacks.tick(server); deathSequence.tick(server);
            if (isAlive() && getHealth() < getMaxHealth() * CavebloomerConfig.AURA_HEALTH_FRACTION.get()) addEffect(new MobEffectInstance(AuraContent.DAMAGING_AURA, 20, 2));
            bossBar.setProgress(Math.clamp(getHealth() / getMaxHealth(), 0, 1));
        }
    }
    @Override protected void dropCustomDeathLoot(ServerLevel server, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(server, source, recentlyHit); spawnAtLocation(new ItemStack(CavebloomerContent.BOSS_BOX.get()));
    }
    @Override public void die(DamageSource source) {
        super.die(source);
        if (!dead || deathCommitted || !(level() instanceof ServerLevel server)) return;
        deathCommitted = true; attacks.cancel(server); deathSequence.start(server, position(), getEyeHeight());
        server.playSound(null, blockPosition(), soundEvent("cavebloomer_death"), SoundSource.NEUTRAL, 1, 1);
        var center = position(); var players = server.getEntitiesOfClass(ServerPlayer.class, new AABB(center, center).inflate(24), player -> true);
        players.sort(Comparator.comparingDouble(player -> player.distanceToSqr(center)));
        var advancement = server.getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "cavebloomer_kill"));
        if (advancement == null) ThermalShockReforged.LOGGER.warn("Cavebloomer advancement chain is not available: cavebloomer_kill");
        else for (var player : players) {
            var progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
        }
        server.getServer().getPlayerList().broadcastSystemMessage(Component.literal("§dAngry Cavebloomer has been defeated!"), false);
        WorldProgressData.get(server).advanceCrux(1);
    }
    @Override protected void dropExperience(Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() {
        if (++deathTime >= 60 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) {
                experiencePending = false; super.dropExperience(experienceAttacker == null ? null : server.getEntity(experienceAttacker));
            }
        }
    }
    @Override public void remove(RemovalReason reason) {
        if (reason.shouldDestroy() && level() instanceof ServerLevel server && attacks != null) attacks.cancel(server);
        super.remove(reason);
    }
    @Override public void startSeenByPlayer(ServerPlayer player) { super.startSeenByPlayer(player); bossBar.addPlayer(player); }
    @Override public void stopSeenByPlayer(ServerPlayer player) { super.stopSeenByPlayer(player); bossBar.removePlayer(player); }
    @Override public void setCustomName(Component name) { super.setCustomName(name); if (bossBar != null) bossBar.setName(getDisplayName()); }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); tag.putString("Texture", texture()); attacks.save(tag); deathSequence.save(tag);
        tag.putBoolean("CavebloomerDeathCommitted", deathCommitted); tag.putBoolean("CavebloomerExperiencePending", experiencePending);
        if (experienceAttacker != null) tag.putUUID("CavebloomerExperienceAttacker", experienceAttacker);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag); applyConfiguredAttributes(false); if (tag.contains("Texture")) setTexture(tag.getString("Texture"));
        attacks.load(tag); deathSequence.load(tag); deathCommitted = tag.getBoolean("CavebloomerDeathCommitted"); experiencePending = tag.getBoolean("CavebloomerExperiencePending");
        experienceAttacker = tag.hasUUID("CavebloomerExperienceAttacker") ? tag.getUUID("CavebloomerExperienceAttacker") : null; bossBar.setName(getDisplayName());
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP);
        for (String clip : new String[]{"0", "1", "2", "Idle", "walk", "prime", "prime2", "prime3", "prime4"}) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            return state.setAndContinue(RawAnimation.begin().thenLoop(state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F ? "walk" : "Idle"));
        })); controllers.add(procedure);
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
