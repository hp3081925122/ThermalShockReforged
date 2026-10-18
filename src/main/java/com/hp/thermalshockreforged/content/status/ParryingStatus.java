package com.hp.thermalshockreforged.content.status;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.Comparator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;

public final class ParryingStatus {
    private static final ResourceLocation KNIGHTS_EDGE = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "knights_edge");
    private ParryingStatus() { }

    public static void beforeArmor(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel level) || !entity.hasEffect(StatusContent.PARRYING)) return;
        var window = entity.getEffect(StatusContent.PARRYING);
        ThermalShockReforged.LOGGER.debug("Parry window consumed before armor: entity={}, dimension={}, remaining={}, amplifier={}", entity.getUUID(), level.dimension().location(), window.getDuration(), window.getAmplifier());
        entity.removeEffect(StatusContent.PARRYING);
        level.playSound(null, entity.blockPosition(), SoundEvents.ANVIL_PLACE, SoundSource.NEUTRAL, 1, 5);
        level.sendParticles(ParticleTypes.EXPLOSION, entity.getX(), entity.getY(), entity.getZ(), 1, 0, 0, 0, 0);
        var center = entity.position();
        var nearby = level.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(2), other -> other != entity);
        nearby.sort(Comparator.comparingDouble(other -> other.distanceToSqr(center)));
        for (var other : nearby) other.addEffect(new MobEffectInstance(StatusContent.STUNNED, 20, 9));
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 9, true, false));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1, true, false));
        double recoil = 0.6 * (1 - Mth.clamp(entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0, 1));
        entity.setDeltaMovement(entity.getDeltaMovement().add(entity.getLookAngle().scale(-recoil)));
        entity.hasImpulse = true;
        entity.hurtMarked = true;
        if (KNIGHTS_EDGE.equals(BuiltInRegistries.ITEM.getKey(entity.getMainHandItem().getItem()))) {
            entity.addEffect(new MobEffectInstance(StatusContent.KNIGHTS_EDGE_CHARGE, 40, 0));
        }
    }
}
