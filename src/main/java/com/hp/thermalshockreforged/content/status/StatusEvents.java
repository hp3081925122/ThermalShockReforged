package com.hp.thermalshockreforged.content.status;

import com.hp.thermalshockreforged.combat.PreArmorDamageEvent;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayDeque;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.*;

public final class StatusEvents {
    private static final ThreadLocal<Propagation> PROPAGATION = new ThreadLocal<>();
    private StatusEvents() { }

    public static void onHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.hasEffect(StatusContent.FROSTBITE)) return;
        if (entity.level() instanceof ServerLevel level) {
            level.playSound(null, entity.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("thermal_shock_reforged:frostbitten")), SoundSource.AMBIENT, 1.0F, 1.0F);
        }
        event.setCanceled(true);
    }

    public static void playElectrifiedVisual(LivingEntity entity) {
        if (entity.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, entity.getX(), entity.getY(), entity.getZ(), 24, 1.0, 2.0, 1.0, 0.1);
            level.playSound(null, entity.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("thermal_shock_reforged:sparkhum")), SoundSource.NEUTRAL, 0.5F, 1.0F);
        }
    }

    public static void onDamage(PreArmorDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide() || !entity.hasEffect(StatusContent.ELECTRIFIED)) return;
        float incomingDamage = event.getAmount();
        if (!(incomingDamage > 0) || !Float.isFinite(incomingDamage)) return;
        Propagation active = PROPAGATION.get();
        if (active != null) {
            active.visited.add(entity);
            if (active.queued.add(entity)) active.pending.addLast(new Pulse(entity, event.getSource(), incomingDamage));
            return;
        }
        Propagation chain = new Propagation();
        chain.visited.add(entity);
        chain.queued.add(entity);
        chain.pending.addLast(new Pulse(entity, event.getSource(), incomingDamage));
        PROPAGATION.set(chain);
        try {
            while (!chain.pending.isEmpty()) {
                Pulse pulse = chain.pending.removeFirst();
                LivingEntity origin = pulse.entity;
                for (LivingEntity target : origin.level().getEntitiesOfClass(LivingEntity.class,
                        new net.minecraft.world.phys.AABB(origin.position(), origin.position()).inflate(4.0),
                        candidate -> candidate != origin && !chain.visited.contains(candidate) && candidate.hasEffect(StatusContent.ELECTRIFIED))
                        .stream().sorted(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(origin.position()))).toList()) {
                    if (chain.visited.add(target)) target.hurt(pulse.source, pulse.damage / 2.0F);
                }
            }
        } finally {
            PROPAGATION.remove();
        }
    }

    private static final class Propagation {
        private final Set<LivingEntity> visited = new HashSet<>();
        private final Set<LivingEntity> queued = new HashSet<>();
        private final ArrayDeque<Pulse> pending = new ArrayDeque<>();
    }

    private record Pulse(LivingEntity entity, DamageSource source, float damage) { }
}
