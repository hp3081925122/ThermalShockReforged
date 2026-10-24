package com.hp.thermalshockreforged.entity.ribbonblade;

import com.hp.thermalshockreforged.content.status.*;
import com.hp.thermalshockreforged.entity.ModEntities;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import com.hp.thermalshockreforged.entity.squall.SquallEntity;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.*;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class RibbonbladeGalvaniteEntity extends AnimatedMonsterEntity implements ServerEndTickTask {
    private static final EntityDataAccessor<Boolean> COOLDOWN_VISIBLE = SynchedEntityData.defineId(RibbonbladeGalvaniteEntity.class, EntityDataSerializers.BOOLEAN);
    private int cooldown;
    private int tauntWindow;
    private int chargeDelay;
    private boolean deathDropsDone;
    private boolean attributesApplied;
    private boolean loaded;
    private boolean submitting;
    private UUID activeStrike;
    public RibbonbladeGalvaniteEntity(EntityType<? extends RibbonbladeGalvaniteEntity> type, Level level) { super(type, level, "ribbonblade", 8); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(COOLDOWN_VISIBLE, false); }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); }
        });
        targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        goalSelector.addGoal(4, new AvoidEntityGoal<>(this, SquallEntity.class, 32, 1, 1.2));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 144; }
    @Override protected List<String> procedureAnimations() { return List.of("idle", "attack", "taunt", "block"); }
    @Override protected String movementAnimation(boolean moving) { return "idle"; }
    public void applyConfiguredAttributes() {
        if (attributesApplied || !RibbonbladeConfig.SPEC.isLoaded()) return;
        attributesApplied = true;
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(RibbonbladeConfig.HEALTH.get());
        getAttribute(Attributes.ARMOR).setBaseValue(RibbonbladeConfig.ARMOR.get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(RibbonbladeConfig.DAMAGE.get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(RibbonbladeConfig.SPEED.get());
        setHealth(loaded ? Math.min(getHealth(), getMaxHealth()) : getMaxHealth());
    }
    public void beginAttack(LivingEntity target) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || target == this || target.level() != level() || cooldown > 0 || submitting) return;
        if (tauntWindow > 0 || hasEffect(MobEffects.SLOWNESS)) {
            cooldown = RibbonbladeConfig.TAUNT_COOLDOWN.get(); entityData.set(COOLDOWN_VISIBLE, false);
            triggerProcedure("taunt"); sound(server, "ribbonblade_taunting", 1, 1); return;
        }
        Vec3 facing = new Vec3(target.getLookAngle().x, 0, target.getLookAngle().z);
        if (facing.lengthSqr() < 1.0E-8) facing = new Vec3(0, 0, 1); else facing = facing.normalize();
        Vec3 destination = target.position().add(facing.scale(2));
        var box = getBoundingBox().move(destination.subtract(position()));
        if (!server.hasChunksAt(BlockPos.containing(box.minX, box.minY, box.minZ), BlockPos.containing(box.maxX, box.maxY, box.maxZ)) || !server.noCollision(this, box)) return;
        submitting = true;
        RibbonbladeActionEntity task;
        try { task = RibbonbladeActionEntity.submitStrike(server, this, target, destination); } finally { submitting = false; }
        if (task == null) return;
        activeStrike = task.getUUID();
        cooldown = RibbonbladeConfig.STRIKE_COOLDOWN.get(); entityData.set(COOLDOWN_VISIBLE, false);
        if (random.nextBoolean()) tauntWindow = 40;
        teleportTo(destination.x, destination.y, destination.z);
        server.playSound(null, target.blockPosition(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.HOSTILE, 1, 3);
        server.sendParticles(ModEntities.TELEPORT_FLASH.get(), getX(), getY() + target.getEyeHeight(), getZ(), 1, .001, .001, .001, 1);
        triggerProcedure("attack");
    }
    public void reactToHit() {
        if (!(level() instanceof ServerLevel server) || !isAlive()) return;
        if (!hasEffect(StatusContent.ELECTRIFIED)) {
            if (random.nextDouble() < .3) sound(server, "ribbonblade_hurts", .65F, 1);
            if (chargeDelay == 0) { chargeDelay = 5; ServerEndTickTasks.schedule(server, getUUID()); }
        } else {
            addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 5, false, true));
            cooldown = RibbonbladeConfig.BLOCK_COOLDOWN.get(); entityData.set(COOLDOWN_VISIBLE, true);
            triggerProcedure("block");
            server.playSound(null, blockPosition(), SoundEvents.ANVIL_PLACE, SoundSource.HOSTILE, .5F, 3);
            sound(server, "ribbonblade_block", 1, 1);
        }
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (chargeDelay > 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void baseTick() {
        super.baseTick();
        if (level() instanceof ServerLevel server) {
            if (cooldown > 0 && --cooldown == 0) entityData.set(COOLDOWN_VISIBLE, false);
            if (tauntWindow > 0) tauntWindow--;
            if (chargeDelay > 0) ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) { if (!isRemoved() && chargeDelay > 0 && --chargeDelay == 0) addEffect(new MobEffectInstance(StatusContent.ELECTRIFIED, 30, 0, false, true)); if (chargeDelay > 0) ServerEndTickTasks.schedule(level, getUUID()); }
    @Override protected void tickEffects() {
        if (!level().isClientSide() || !entityData.get(COOLDOWN_VISIBLE)) { super.tickEffects(); return; }
        for (var effect : new ArrayList<>(getActiveEffects())) effect.tickClient();
        if (isInvisible() ? random.nextInt(15) == 0 : random.nextBoolean()) LegacyEffectParticles.emit(this, -1, 1);
    }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) { return Float.isFinite(amount) && amount > 0 && !source.is(DamageTypes.LIGHTNING_BOLT) && super.hurtServer(level, source, amount); }
    @Override public void die(DamageSource source) { super.die(source); if (dead && !deathDropsDone && level() instanceof ServerLevel server) { deathDropsDone = true; } }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData data) {
        var result = super.finalizeSpawn(level, difficulty, reason, data);
        applyConfiguredAttributes();
        if (level instanceof ServerLevel server) {
            server.sendParticles(ModEntities.TELEPORT_FLASH.get(), getX(), getEyeY(), getZ(), 1, .001, .001, .001, 1);
            server.playSound(null, blockPosition(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.HOSTILE, 1, 1);
            RibbonbladeActionEntity.submitSpawn(server, this);
        }
        return result;
    }
    @Override public void awardKillScore(Entity target, DamageSource source) { super.awardKillScore(target, source); if (level() instanceof ServerLevel server) sound(server, "ribbonblade_win", 1, 1); }
    @Override public void remove(RemovalReason reason) { if (activeStrike != null && level() instanceof ServerLevel server && server.getEntity(activeStrike) instanceof RibbonbladeActionEntity task) task.capture(this); super.remove(reason); }
    private void sound(ServerLevel server, String name, float volume, float pitch) { server.playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(RibbonbladeContent.id(name)), SoundSource.HOSTILE, volume, pitch); }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.CHAIN_PLACE, .15F, 1); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return BuiltInRegistries.SOUND_EVENT.getValue(RibbonbladeContent.id("galvanitehurt")); }
    @Override protected SoundEvent getDeathSound() { return BuiltInRegistries.SOUND_EVENT.getValue(RibbonbladeContent.id("galvanitedeath")); }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putInt("RibbonCooldown", cooldown); output.putInt("RibbonTaunt", tauntWindow); output.putInt("RibbonCharge", chargeDelay); output.putBoolean("RibbonCooldownVisible", entityData.get(COOLDOWN_VISIBLE)); output.putBoolean("RibbonDropsDone", deathDropsDone); if (activeStrike != null) output.putString("RibbonActiveStrike", activeStrike.toString()); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); loaded = true; cooldown = Math.clamp(input.getIntOr("RibbonCooldown", 0), 0, 72000); tauntWindow = Math.clamp(input.getIntOr("RibbonTaunt", 0), 0, 40); chargeDelay = Math.clamp(input.getIntOr("RibbonCharge", 0), 0, 5); entityData.set(COOLDOWN_VISIBLE, cooldown > 0 && input.getBooleanOr("RibbonCooldownVisible", false)); deathDropsDone = input.getBooleanOr("RibbonDropsDone", false); activeStrike = input.getString("RibbonActiveStrike").map(java.util.UUID::fromString).orElse(null); }
    public static AttributeSupplier.Builder attributes() { return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 40).add(Attributes.ARMOR, 10).add(Attributes.ATTACK_DAMAGE, 7).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.ATTACK_KNOCKBACK, .8).add(Attributes.STEP_HEIGHT, 1.6); }
}
