package com.hp.thermalshockreforged.content.npc;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public record NpcTrade(String item, int count, int levels, String spice, int spiceCount) {
    public boolean buy(ServerPlayer player, BlockPos anchor) {
        Item reward = BuiltInRegistries.ITEM.get(id(item));
        Item currency = spiceCount == 0 ? Items.AIR : BuiltInRegistries.ITEM.get(id(spice));
        if (reward == Items.AIR || spiceCount > 0 && currency == Items.AIR) {
            ThermalShockReforged.LOGGER.warn("NPC trade unavailable: reward={}, currency={}", item, spice); return false;
        }
        if (player.experienceLevel < levels) return false;
        var inventory = player.getInventory();
        if (spiceCount > 0) {
            if (inventory.clearOrCountMatchingItems(stack -> stack.is(currency), 0, player.inventoryMenu.getCraftSlots()) < spiceCount) return false;
            int paid = inventory.clearOrCountMatchingItems(stack -> stack.is(currency), spiceCount, player.inventoryMenu.getCraftSlots());
            if (paid != spiceCount) {
                if (paid > 0) ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(currency, paid));
                ThermalShockReforged.LOGGER.error("NPC payment mismatch: player={}, currency={}, required={}, paid={}", player.getUUID(), spice, spiceCount, paid);
                return false;
            }
        }
        player.giveExperienceLevels(-levels);
        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(reward, count));
        inventory.setChanged(); player.containerMenu.broadcastChanges();
        sound(player, anchor, BuiltInRegistries.SOUND_EVENT.get(id("npcbuy")), 1, 1);
        if (spiceCount > 0) sound(player, anchor, SoundEvents.DISPENSER_FAIL, .5F, 5);
        sound(player, anchor, SoundEvents.EXPERIENCE_ORB_PICKUP, .5F, 5);
        ThermalShockReforged.LOGGER.debug("NPC trade committed: player={}, item={}, count={}, levels={}, spice={}, spiceCount={}", player.getUUID(), item, count, levels, spice, spiceCount);
        return true;
    }
    private static ResourceLocation id(String path) { return path.indexOf(':') >= 0 ? ResourceLocation.parse(path) : ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static void sound(ServerPlayer player, BlockPos anchor, SoundEvent sound, float volume, float pitch) { player.connection.send(new ClientboundSoundPacket(Holder.direct(sound), SoundSource.NEUTRAL, anchor.getX(), anchor.getY(), anchor.getZ(), volume, pitch, player.getRandom().nextLong())); }
}
