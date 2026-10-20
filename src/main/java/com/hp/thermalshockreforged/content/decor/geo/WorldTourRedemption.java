package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

final class WorldTourRedemption {
    private static final String CLAIMED = "HasFschtTrophy";

    private static boolean claimed(Player player) {
        return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getCompound(ThermalShockReforged.MOD_ID).getBoolean(CLAIMED);
    }
    private static void claim(Player player) {
        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        CompoundTag mod = persisted.getCompound(ThermalShockReforged.MOD_ID);
        mod.putBoolean(CLAIMED, true);
        persisted.put(ThermalShockReforged.MOD_ID, mod);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
    }
    static void clonePlayer(PlayerEvent.Clone event) {
        if (claimed(event.getOriginal())) claim(event.getEntity());
    }
    static void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("TSredeem").executes(context -> {
            var source = context.getSource();
            String sound = "thermoid_cries";
            float pitch = -1;
            if (source.getEntity() instanceof Player player) {
                String name = player.getDisplayName().getString();
                if ((name.equals("Dev") || name.equals("fshct")) && !claimed(player)) {
                    ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(GeoDecorContent.item(GeoDecorKind.WORLD_TOUR_TROPHY)));
                    player.displayClientMessage(Component.translatable("message.thermal_shock_reforged.world_tour.congratulations"), false);
                    claim(player);
                    sound = "thermalshockjingle";
                    pitch = 2;
                }
            }
            source.getLevel().playSound(null, BlockPos.containing(source.getPosition()), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, sound)), SoundSource.NEUTRAL, 1, pitch);
            return 0;
        }));
    }
}
