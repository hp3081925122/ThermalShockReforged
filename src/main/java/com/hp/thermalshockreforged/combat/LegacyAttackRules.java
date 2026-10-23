package com.hp.thermalshockreforged.combat;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.StatusContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.NeoForge;

public final class LegacyAttackRules {
    private static final TagKey<EntityType<?>> CHARACTERS = entityTag(ThermalShockReforged.MOD_ID, "character_mobs");
    private static final TagKey<EntityType<?>> COLD_REFLECTORS = entityTag("mod", "ice");
    private static final TagKey<EntityType<?>> CRYO_RESIST = entityTag(ThermalShockReforged.MOD_ID, "cryo_resist");
    private static final TagKey<Biome> COLD_BIOMES = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "ignition_-1"));

    private LegacyAttackRules() { }

    private static TagKey<EntityType<?>> entityTag(String namespace, String path) {
        return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(namespace, path));
    }

    public static void register() { NeoForge.EVENT_BUS.addListener(LegacyAttackRules::attack); }

    private static void attack(AttackAttemptEvent event) {
        var victim = event.getEntity();
        if (!(victim.level() instanceof ServerLevel level)) return;
        if (victim.getType().is(CHARACTERS)) {
            event.setCanceled(true);
            ThermalShockReforged.LOGGER.debug("Character attack canceled: victim={}, source={}", victim.getUUID(), event.getSource().getMsgId());
            return;
        }
        var attacker = event.getSource().getEntity();
        if (attacker == null) return;
        if (victim instanceof Player && attacker.getType().is(COLD_REFLECTORS) && victim.getRandom().nextDouble() < .2) {
            attacker.hurt(level.damageSources().source(DamageTypes.PLAYER_ATTACK), 2);
            level.playSound(null, victim.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, .3F, 1);
            ThermalShockReforged.LOGGER.debug("Cold attack reflected: victim={}, attacker={}", victim.getUUID(), attacker.getUUID());
        }
        if (level.getBiome(victim.blockPosition()).is(COLD_BIOMES) && attacker.getType().is(CRYO_RESIST) && victim.getRandom().nextDouble() < .7) {
            victim.addEffect(new MobEffectInstance(StatusContent.FROSTBITE, 120, 0));
            ThermalShockReforged.LOGGER.debug("Cold biome frostbite applied: victim={}, attacker={}", victim.getUUID(), attacker.getUUID());
        }
    }
}
