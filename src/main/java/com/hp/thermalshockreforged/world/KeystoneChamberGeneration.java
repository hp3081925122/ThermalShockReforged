package com.hp.thermalshockreforged.world;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.server.level.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class KeystoneChamberGeneration {
    private static final ResourceLocation ARRIVAL = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "loqendia_get");
    private static final ResourceLocation TEMPLATE = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "keystonerubicon");
    private static final ResourceLocation WINDCHIME = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "windchime");
    private static final BlockPos ORIGIN = new BlockPos(1333, 133, 1333);
    private KeystoneChamberGeneration() { }
    public static void register() {
        NeoForge.EVENT_BUS.addListener(KeystoneChamberGeneration::wakeUp);
        NeoForge.EVENT_BUS.addListener(KeystoneChamberGeneration::endTick);
    }
    private static void wakeUp(PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)
                || !level.getGameRules().getBoolean(ModGameRules.LOQENDIA_GENERATE_SPECIAL_STRUCTURES)) return;
        var advancement = player.server.getAdvancements().get(ARRIVAL);
        if (advancement == null) {
            ThermalShockReforged.LOGGER.warn("Keystone chamber arrival advancement is missing: {}", ARRIVAL);
            return;
        }
        if (player.getAdvancements().getOrStartProgress(advancement).isDone()) return;
        WorldProgressData progress = WorldProgressData.get(level);
        if (!progress.beginKeystonePlacement()) return;
        BlockPos noticeAnchor = player.blockPosition().immutable();
        boolean placed = false;
        try {
            var template = level.getStructureManager().get(TEMPLATE);
            if (template.isEmpty()) {
                ThermalShockReforged.LOGGER.error("Keystone chamber template is missing: template={}, dimension={}", TEMPLATE, level.dimension().location());
                return;
            }
            ThermalShockReforged.LOGGER.debug("Placing full Keystone chamber: position={}, dimension={}", ORIGIN, level.dimension().location());
            placed = template.get().placeInWorld(level, ORIGIN, ORIGIN,
                    new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false), level.random, 3);
            if (placed) ThermalShockReforged.LOGGER.debug("Keystone chamber placement completed: position={}, dimension={}", ORIGIN, level.dimension().location());
            else ThermalShockReforged.LOGGER.error("Keystone chamber placement failed: position={}, dimension={}", ORIGIN, level.dimension().location());
        } finally {
            progress.finishKeystonePlacement(placed, level.dimension().location(), noticeAnchor);
        }
    }
    private static void endTick(ServerTickEvent.Post event) {
        var server = event.getServer();
        WorldProgressData progress = WorldProgressData.forGeneration(server.overworld());
        int elapsed = progress.advanceKeystoneNotice();
        if (elapsed == 100) {
            server.getPlayerList().broadcastSystemMessage(Component.literal("§c<§kSopel§r§c> I have a gift for you :^)"), false);
            var dimension = progress.keystoneNoticeDimension();
            ServerLevel level = dimension == null ? null : server.getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
            if (level != null) {
                var sound = BuiltInRegistries.SOUND_EVENT.get(WINDCHIME);
                int repetitions = level.players().size();
                for (int i = 0; i < repetitions; i++) level.playSound(null, progress.keystoneNoticeAnchor(), sound, SoundSource.NEUTRAL, 1, 1);
            }
        } else if (elapsed == 145) {
            server.getPlayerList().broadcastSystemMessage(Component.literal("§c<§kSomeplace§r§c> Come to X1333 Z1333. See you soon..."), false);
        } else if (elapsed == 200) {
            server.getPlayerList().broadcastSystemMessage(Component.literal("§aNOTE: Thermal Shock depends on Music Triggers to play it's OST in-game. The MT config file for Thermal Shock can be downloaded FOR FREE at https://www.patreon.com/cw/ThermaDev. Thanks, and enjoy the mod!"), false);
        }
    }
}
