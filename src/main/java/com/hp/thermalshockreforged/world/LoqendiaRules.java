package com.hp.thermalshockreforged.world;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.config.WorldRulesConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

final class LoqendiaRules {
    private static final TagKey<Block> SUSTAIN_FIRE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "sustain_fire"));

    private LoqendiaRules() { }

    static void register() {
        NeoForge.EVENT_BUS.addListener(LoqendiaRules::join);
        NeoForge.EVENT_BUS.addListener(LoqendiaRules::place);
    }

    private static void join(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Pig && event.getLevel().dimension().equals(LoqendiaContent.DIMENSION)) {
            event.setCanceled(true);
            ThermalShockReforged.LOGGER.debug("Loqendia pig entry rejected: entity={}, loaded={}", event.getEntity().getUUID(), event.loadedFromDisk());
        }
    }

    private static void place(BlockEvent.EntityPlaceEvent event) {
        var actor = event.getEntity();
        if (actor == null || !actor.level().dimension().equals(LoqendiaContent.DIMENSION)
                || !(event.getLevel() instanceof Level level) || level.isClientSide) return;
        var pos = event.getPos();
        var state = level.getBlockState(pos);
        if (state.is(Blocks.FIRE) && WorldRulesConfig.FIRE_EXTINGUISHES.get()
                && !level.getBlockState(pos.below()).is(SUSTAIN_FIRE)) {
            if (level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)) {
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1, 1);
                ThermalShockReforged.LOGGER.debug("Loqendia placed fire extinguished: position={}, actor={}", pos, actor.getUUID());
            }
        } else if (state.is(Blocks.MAGMA_BLOCK) && WorldRulesConfig.MAGMA_FREEZES_UNDERWATER.get()
                && level.getBlockState(pos.above()).is(Blocks.WATER)) {
            if (level.setBlock(pos, Blocks.NETHERRACK.defaultBlockState(), 3)) {
                level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.NEUTRAL, 1, 1);
                ThermalShockReforged.LOGGER.debug("Loqendia submerged magma cooled: position={}, actor={}", pos, actor.getUUID());
            }
        }
    }
}
