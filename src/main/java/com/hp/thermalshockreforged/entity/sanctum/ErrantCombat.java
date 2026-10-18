package com.hp.thermalshockreforged.entity.sanctum;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;

final class ErrantCombat {
    private static final ResourceLocation MOVEMENT_LOCK = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "errant_movement_lock");
    private static final ResourceKey<DamageType> DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "errant_damage"));
    private final ErrantEntity owner;
    private int cooldown;
    private int movementLock;
    private int strikeRemaining;
    private UUID target;
    private BlockPos soundOrigin = BlockPos.ZERO;
    private String dimension = "";
    ErrantCombat(ErrantEntity owner) { this.owner = owner; }
    void begin(LivingEntity victim) {
        if (!(owner.level() instanceof ServerLevel level) || !owner.isAlive() || victim == owner || !victim.isAlive() || victim.level() != level || cooldown > 0) return;
        dimension = level.dimension().location().toString();
        soundOrigin = victim.blockPosition();
        if (owner.getRandom().nextDouble() < .7) {
            if (owner.getRandom().nextDouble() < .6) {
                cooldown = 45;
                movementLock = 30;
                syncMovementLock();
                owner.setAnimation("hold");
                level.playSound(null, soundOrigin, SoundEvents.SHIELD_BLOCK, SoundSource.NEUTRAL, 1, 4);
                owner.addEffect(new MobEffectInstance(StatusContent.PARRYING, 20, 1, false, false));
            } else {
                cooldown = 10;
                owner.setAnimation("dodge");
                level.playSound(null, soundOrigin, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "propulsionbootsactivate")), SoundSource.NEUTRAL, 1, 3);
                owner.setDeltaMovement(owner.getLookAngle().scale(-2));
                owner.hasImpulse = true;
            }
        } else {
            cooldown = 50;
            movementLock = 30;
            strikeRemaining = 12;
            target = victim.getUUID();
            syncMovementLock();
            owner.setAnimation("attack");
            level.playSound(null, soundOrigin, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.NEUTRAL, 1, 1);
            ServerEndTickTasks.schedule(level, owner.getUUID());
        }
    }
    void baseTick() {
        if (owner.level().isClientSide) return;
        if (!dimension.isEmpty() && !dimension.equals(owner.level().dimension().location().toString())) { cooldown = 0; movementLock = 0; strikeRemaining = 0; target = null; dimension = ""; }
        if (cooldown > 0) cooldown--;
        if (movementLock > 0) movementLock--;
        syncMovementLock();
    }
    private void syncMovementLock() {
        var speed = owner.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementLock > 0 && !speed.hasModifier(MOVEMENT_LOCK)) speed.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        if (movementLock <= 0) speed.removeModifier(MOVEMENT_LOCK);
    }
    void schedule(ServerLevel level) {
        if (strikeRemaining > 0 && dimension.equals(level.dimension().location().toString())) ServerEndTickTasks.schedule(level, owner.getUUID());
    }
    void endTick(ServerLevel level) {
        if (owner.isRemoved() || strikeRemaining <= 0 || !dimension.equals(level.dimension().location().toString())) return;
        if (--strikeRemaining > 0) { schedule(level); return; }
        UUID targetId = target;
        target = null;
        level.playSound(null, soundOrigin, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, 1, 4);
        if (targetId == null || !(level.getEntity(targetId) instanceof LivingEntity victim) || victim == owner || !victim.isAlive() || owner.distanceTo(victim) > 3F) return;
        var values = owner.sanctum() ? SanctumConfig.SANCTUM_ERRANT : SanctumConfig.ERRANT;
        double damage = owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * values.strikeMultiplier().get() - victim.getArmorValue() * values.armorFactor().get();
        if (damage < 0) ThermalShockReforged.LOGGER.debug("Clamped negative errant strike damage: entity={}, target={}, rawDamage={}, armor={}", owner.getUUID(), victim.getUUID(), damage, victim.getArmorValue());
        if (Double.isFinite(damage)) victim.hurt(new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAMAGE)), (float)Math.clamp(damage, 0, Float.MAX_VALUE));
        double strength = values.strikeKnockback().get() * (1 - Mth.clamp(victim.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0, 1));
        if (Double.isFinite(strength) && strength > 0) {
            victim.setDeltaMovement(owner.getLookAngle().scale(strength));
            victim.hasImpulse = true;
            victim.hurtMarked = true;
        }
    }
    void save(CompoundTag tag) {
        tag.putInt("ErrantCooldown", cooldown);
        tag.putInt("ErrantMovementLock", movementLock);
        tag.putInt("ErrantStrikeRemaining", strikeRemaining);
        tag.putString("ErrantCombatDimension", dimension);
        tag.putLong("ErrantSoundOrigin", soundOrigin.asLong());
        if (target != null) tag.putUUID("ErrantStrikeTarget", target);
    }
    void load(CompoundTag tag) {
        cooldown = Math.clamp(tag.getInt("ErrantCooldown"), 0, 50);
        movementLock = Math.clamp(tag.getInt("ErrantMovementLock"), 0, 30);
        strikeRemaining = Math.clamp(tag.getInt("ErrantStrikeRemaining"), 0, 12);
        dimension = tag.getString("ErrantCombatDimension");
        soundOrigin = BlockPos.of(tag.getLong("ErrantSoundOrigin"));
        target = tag.hasUUID("ErrantStrikeTarget") ? tag.getUUID("ErrantStrikeTarget") : null;
        if (ResourceLocation.tryParse(dimension) == null) { cooldown = 0; movementLock = 0; strikeRemaining = 0; dimension = ""; }
        if (target == null) strikeRemaining = 0;
        if (!owner.level().isClientSide) syncMovementLock();
    }
}
