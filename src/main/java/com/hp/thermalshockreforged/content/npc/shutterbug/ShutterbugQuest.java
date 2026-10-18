package com.hp.thermalshockreforged.content.npc.shutterbug;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.camera.CameraContent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;

final class ShutterbugQuest {
    static boolean introduced(ServerPlayer player) { return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getCompound(ThermalShockReforged.MOD_ID).getBoolean("UnetteIntroduced"); }
    static void finishIntroduction(ServerPlayer player) {
        if (introduced(player)) return;
        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        CompoundTag mod = persisted.getCompound(ThermalShockReforged.MOD_ID);
        mod.putBoolean("UnetteIntroduced", true); persisted.put(ThermalShockReforged.MOD_ID, mod); player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(CameraContent.CAMERA.get()));
        ThermalShockReforged.LOGGER.debug("Unette introduction committed: player={}", player.getUUID());
    }
    static boolean done(ServerPlayer player, String id) {
        var advancement = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, id));
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }
    static boolean grantNew(ServerPlayer player, String id) {
        var advancement = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, id));
        if (advancement == null) { ThermalShockReforged.LOGGER.warn("Unette advancement unavailable: id={}, player={}", id, player.getUUID()); return false; }
        var progress = player.getAdvancements().getOrStartProgress(advancement);
        if (progress.isDone()) return false;
        for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
        return progress.isDone();
    }
    static void openErrand(ServerPlayer player, ShutterbugEntity npc, BlockPos anchor) {
        if (done(player, "leaf_it_to_me_3")) { ShutterbugContent.open(player, npc, anchor, ShutterbugPage.ERRAND_NULL); return; }
        boolean photo = done(player, "leaf_it_to_me_2");
        boolean killed = done(player, "cavebloomer_kill");
        if (killed && !photo) {
            ShutterbugContent.open(player, npc, anchor, ShutterbugPage.ERRAND_END_B);
            if (grantNew(player, "leaf_it_to_me_3")) ShutterbugContent.sound(player, anchor, "thermalshockjingle", SoundSource.NEUTRAL);
        } else if (photo) {
            ShutterbugContent.open(player, npc, anchor, ShutterbugPage.ERRAND_END_A);
            if (grantNew(player, "leaf_it_to_me_3")) {
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(CameraContent.BUNDLE.get()));
                ShutterbugContent.sound(player, anchor, "thermalshockjingle", SoundSource.NEUTRAL);
            }
        } else ShutterbugContent.open(player, npc, anchor, ShutterbugPage.ERRAND_A);
    }
}
