package com.hp.thermalshockreforged.content.npc;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.recipe.NpcTradeRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;

public final class NpcTrade {
    private NpcTrade() { }

    public static boolean buy(ServerPlayer player, BlockPos anchor, String page) {
        var recipe = NpcTradeRecipe.get(player.level(), page);
        if (recipe == null || !recipe.allowed(player) || !recipe.trade(player)) return false;
        sound(player, anchor, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "npcbuy")), 1, 1);
        if (!recipe.costs().isEmpty()) sound(player, anchor, SoundEvents.DISPENSER_FAIL, .5F, 5);
        sound(player, anchor, SoundEvents.EXPERIENCE_ORB_PICKUP, .5F, 5);
        return true;
    }

    private static void sound(ServerPlayer player, BlockPos anchor, SoundEvent sound, float volume, float pitch) { player.connection.send(new ClientboundSoundPacket(Holder.direct(sound), SoundSource.NEUTRAL, anchor.getX(), anchor.getY(), anchor.getZ(), volume, pitch, player.getRandom().nextLong())); }
}
