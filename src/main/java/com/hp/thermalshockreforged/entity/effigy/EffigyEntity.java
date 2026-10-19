package com.hp.thermalshockreforged.entity.effigy;

import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class EffigyEntity extends AnimatedMonsterEntity {
    private static final ResourceLocation MOVEMENT_LOCK = ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "effigy_movement_lock");
    private int cooldown;
    private int movementLock;
    private boolean submitting;
    private final Set<UUID> activeStrikes = new LinkedHashSet<>();
    public EffigyEntity(EntityType<? extends EffigyEntity> type, Level level) { super(type, level, "effigy", 5); }
    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 40).add(Attributes.ARMOR, 4).add(Attributes.ATTACK_DAMAGE, 3)
            .add(Attributes.MOVEMENT_SPEED, .2).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.STEP_HEIGHT, .6);
    }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) { @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); } });
        targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        goalSelector.addGoal(3, new RandomStrollGoal(this, .8));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 9; }
    @Override protected List<String> procedureAnimations() { return List.of("0", "idle", "walk", "attack1", "attack2"); }
    public void beginAttack(LivingEntity victim) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || cooldown > 0 || submitting || victim.level() != server || victim == this) return;
        submitting = true;
        try {
            if (!EffigyStrikeEntity.submit(server, this, victim)) { ThermalShockReforged.LOGGER.debug("Effigy strike submission rejected: caster={}, target={}", getUUID(), victim.getUUID()); return; }
            cooldown = 40; movementLock = 25; synchronizeMovement();
            triggerProcedure("attack1");
            server.playSound(null, victim.blockPosition(), SoundEvents.GRINDSTONE_USE, SoundSource.HOSTILE, 1, 1);
        } finally { submitting = false; }
    }
    @Override public void baseTick() { super.baseTick(); if (!level().isClientSide) { if (cooldown > 0) --cooldown; if (movementLock > 0) --movementLock; synchronizeMovement(); } }
    private void synchronizeMovement() {
        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementLock > 0) { if (!speed.hasModifier(MOVEMENT_LOCK)) speed.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)); }
        else speed.removeModifier(MOVEMENT_LOCK);
    }
    void trackStrike(UUID task) { activeStrikes.add(task); }
    void forgetStrike(UUID task) { activeStrikes.remove(task); }
    @Override public boolean removeAllEffects() { boolean result = super.removeAllEffects(); if (!level().isClientSide && (cooldown > 0 || movementLock > 0)) { cooldown = 0; movementLock = 0; synchronizeMovement(); return true; } return result; }
    @Override public void remove(RemovalReason reason) { if (level() instanceof ServerLevel server) for (UUID id : activeStrikes) if (server.getEntity(id) instanceof EffigyStrikeEntity task) task.release(this, reason); super.remove(reason); }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.COPPER_STEP, .15F, 1); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.CHAIN_BREAK; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.CHAIN_BREAK; }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putInt("EffigyCooldown", cooldown); tag.putInt("EffigyMovementLock", movementLock); var strikes = new CompoundTag(); for (UUID id : activeStrikes) strikes.putUUID(id.toString(), id); tag.put("EffigyActiveStrikes", strikes); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); cooldown = Math.clamp(tag.getInt("EffigyCooldown"), 0, 40); movementLock = Math.clamp(tag.getInt("EffigyMovementLock"), 0, 25); activeStrikes.clear(); var strikes = tag.getCompound("EffigyActiveStrikes"); for (String key : strikes.getAllKeys()) if (strikes.hasUUID(key)) activeStrikes.add(strikes.getUUID(key)); synchronizeMovement(); }
}
