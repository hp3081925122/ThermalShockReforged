package com.hp.thermalshock.content.status;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.*;

public final class StatusEvents {
    private static final ThreadLocal<Set<LivingEntity>> PROPAGATION_PATH = ThreadLocal.withInitial(HashSet::new);
    private StatusEvents() { }

    public static void onHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.hasEffect(StatusContent.FROSTBITE)) return;
        if (entity.level() instanceof ServerLevel level) {
            level.playSound(null, entity.blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("thermal_shock:frostbitten")), SoundSource.AMBIENT, 1.0F, 1.0F);
        }
        event.setCanceled(true);
    }

    public static void playElectrifiedVisual(LivingEntity entity) {
        if (entity.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, entity.getX(), entity.getY(), entity.getZ(), 24, 1.0, 2.0, 1.0, 0.1);
            level.playSound(null, entity.blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("thermal_shock:sparkhum")), SoundSource.NEUTRAL, 0.5F, 1.0F);
        }
    }

    public static void onDamage(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || !entity.hasEffect(StatusContent.ELECTRIFIED)) return;
        float incomingDamage = event.getNewDamage();
        incomingDamage += event.getContainer().getReduction(net.neoforged.neoforge.common.damagesource.DamageContainer.Reduction.ARMOR);
        incomingDamage += event.getContainer().getReduction(net.neoforged.neoforge.common.damagesource.DamageContainer.Reduction.ENCHANTMENTS);
        incomingDamage += event.getContainer().getReduction(net.neoforged.neoforge.common.damagesource.DamageContainer.Reduction.MOB_EFFECTS);
        Set<LivingEntity> path = PROPAGATION_PATH.get();
        if (!path.add(entity)) return;
        try {
            for (LivingEntity target : entity.level().getEntitiesOfClass(LivingEntity.class,
                    new net.minecraft.world.phys.AABB(entity.position(), entity.position()).inflate(4.0),
                    candidate -> candidate != entity && candidate.hasEffect(StatusContent.ELECTRIFIED))
                    .stream().sorted(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(entity.position()))).toList()) {
                if (!path.contains(target)) target.hurt(event.getSource(), incomingDamage / 2.0F);
            }
        } finally {
            path.remove(entity);
            if (path.isEmpty()) PROPAGATION_PATH.remove();
        }
    }
}



