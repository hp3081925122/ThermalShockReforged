package com.hp.thermalshockreforged.entity.legacy;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.config.CombatConfig;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import com.hp.thermalshockreforged.entity.orb.WhistlerNoteEntity;
import java.util.List;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class SpectreEntity extends AnimatedMonsterEntity {
    private static final ResourceLocation MOVEMENT_LOCK = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "spectre_movement_lock");
    private int cooldown;
    private int movementLock;
    private boolean attributesApplied;
    private boolean loadedFromSave;

    public SpectreEntity(EntityType<? extends SpectreEntity> type, Level level) { super(type, level, "specter", 5); applyConfiguredAttributes(); }
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, 6, 1, 1.2));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); }
        });
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
        goalSelector.addGoal(4, new RandomStrollGoal(this, .8));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 64; }
    @Override protected List<String> procedureAnimations() { return List.of("idle", "attack"); }
    @Override protected String movementAnimation(boolean moving) { return "idle"; }

    public void beginAttack(LivingEntity victim) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || victim == this || victim.level() != level() || cooldown > 0) return;
        cooldown = CombatConfig.SPECTRE_COOLDOWN.get();
        movementLock = 20;
        syncMovementLock();
        server.playSound(null, victim.blockPosition(), SoundEvents.SHULKER_SHOOT, SoundSource.HOSTILE, 1, 2);
        var nearest = level().getNearestPlayer(this, -1);
        if (nearest != null) {
            lookAt(EntityAnchorArgument.Anchor.FEET, nearest.position());
            setYBodyRot(getYRot());
            yBodyRotO = getYRot(); yHeadRotO = getYRot();
        }
        triggerProcedure("attack");
        WhistlerNoteEntity.shootSpectre(this, getAttributeValue(Attributes.ATTACK_DAMAGE) * CombatConfig.SPECTRE_NOTE_MULTIPLIER.get());
        ThermalShockReforged.LOGGER.debug("Spectre shot: caster={}, victim={}, cooldown={}", getUUID(), victim.getUUID(), cooldown);
    }

    private void syncMovementLock() {
        if (level().isClientSide) return;
        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementLock > 0) {
            if (!speed.hasModifier(MOVEMENT_LOCK)) speed.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        } else speed.removeModifier(MOVEMENT_LOCK);
    }
    @Override public void baseTick() {
        super.baseTick();
        if (level().isClientSide) return;
        if (cooldown > 0) cooldown--;
        if (movementLock > 0 && --movementLock == 0) syncMovementLock();
    }
    @Override public boolean hurt(DamageSource source, float amount) {
        return !source.is(DamageTypes.DROWN) && !source.is(DamageTypes.WITHER) && !source.is(DamageTypes.WITHER_SKULL) && super.hurt(source, amount);
    }
    @Override protected SoundEvent getAmbientSound() { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "whistler_ambient")); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "whistler_hurt")); }
    @Override protected SoundEvent getDeathSound() { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "whistler_death")); }

    private void applyConfiguredAttributes() {
        if (level().isClientSide || attributesApplied || !CombatConfig.SPEC.isLoaded()) return;
        float health = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(CombatConfig.SPECTRE_HEALTH.get());
        getAttribute(Attributes.ARMOR).setBaseValue(CombatConfig.SPECTRE_ARMOR.get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(CombatConfig.SPECTRE_DAMAGE.get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(CombatConfig.SPECTRE_SPEED.get());
        getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(CombatConfig.SPECTRE_FOLLOW_RANGE.get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(CombatConfig.SPECTRE_RESISTANCE.get());
        getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(CombatConfig.SPECTRE_KNOCKBACK.get());
        setHealth(loadedFromSave ? Math.min(health, getMaxHealth()) : getMaxHealth());
        attributesApplied = true;
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); applyConfiguredAttributes(); syncMovementLock(); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putInt("SpectreCooldown", cooldown); tag.putInt("SpectreMovementLock", movementLock); }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        loadedFromSave = true;
        super.readAdditionalSaveData(tag);
        attributesApplied = false;
        applyConfiguredAttributes();
        cooldown = Math.clamp(tag.getInt("SpectreCooldown"), 0, 72000);
        movementLock = Math.clamp(tag.getInt("SpectreMovementLock"), 0, 20);
        syncMovementLock();
    }
    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes().add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.MAX_HEALTH, 25).add(Attributes.ARMOR, 4).add(Attributes.ATTACK_DAMAGE, 3).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, .1).add(Attributes.ATTACK_KNOCKBACK, .1).add(Attributes.STEP_HEIGHT, 1);
    }
}
