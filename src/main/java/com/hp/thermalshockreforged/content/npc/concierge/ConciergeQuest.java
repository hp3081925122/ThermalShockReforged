package com.hp.thermalshockreforged.content.npc.concierge;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public final class ConciergeQuest {
    static final String INTRO = "thermal_shock_reforged_concierge_trade_introduced";
    private static final String COMPLETE = "thermal_shock_reforged_concierge_blasphemy_paid";
    private static final String INFO = "thermal_shock_reforged_npc_info_vilebath";
    static boolean has(ServerPlayer player, String id) {
        var advancement = player.getServer().getAdvancements().get(ConciergeContent.id(id));
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }
    static boolean award(ServerPlayer player, String id) {
        var advancement = player.getServer().getAdvancements().get(ConciergeContent.id(id));
        if (advancement == null) { ThermalShockReforged.LOGGER.warn("Concierge advancement missing: id={}", id); return false; }
        var progress = player.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
        return true;
    }
    static boolean completed(ServerPlayer player) { return player.getPersistentData().getBoolean(COMPLETE) || has(player, "blasphemy_4"); }
    static boolean vilebath(ServerPlayer player) { return player.getPersistentData().getBoolean(INFO) || has(player, "blasphemy_4"); }
    static boolean finish(ServerPlayer player, BlockPos anchor) {
        if (completed(player) || !has(player, "blasphemy_3")) return false;
        var advancement = player.getServer().getAdvancements().get(ConciergeContent.id("blasphemy_4"));
        var reward = BuiltInRegistries.ITEM.get(ConciergeContent.id("panacea"));
        var payment = BuiltInRegistries.ITEM.get(ConciergeContent.id("primordial_blood"));
        if (advancement == null || reward == Items.AIR || payment == Items.AIR) {
            ThermalShockReforged.LOGGER.warn("Concierge quest settlement unavailable; keeping payment: player={}", player.getUUID());
            return false;
        }
        if (!com.hp.thermalshockreforged.content.recipe.NpcTradeRecipe.pay(player, payment, 1)) return false;
        player.getPersistentData().putBoolean(COMPLETE, true);
        player.getPersistentData().putBoolean(INFO, true);
        award(player, "blasphemy_4");
        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(reward));
        player.giveExperienceLevels(25);
        ((net.minecraft.server.level.ServerLevel) player.level()).addFreshEntity(new ExperienceOrb(((net.minecraft.server.level.ServerLevel) player.level()), anchor.getX(), anchor.getY(), anchor.getZ(), 60));
        player.getInventory().setChanged(); player.containerMenu.broadcastChanges();
        ThermalShockReforged.LOGGER.debug("Concierge quest settled: player={}, blood=1, panacea=1, levels=25, orb=60", player.getUUID());
        return true;
    }
    static void advancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (event.getAdvancement().id().equals(ConciergeContent.id("blasphemy_4"))) event.getEntity().getPersistentData().putBoolean(INFO, true);
    }
    static void clonePlayer(PlayerEvent.Clone event) {
        for (String key : new String[]{INTRO, COMPLETE, INFO}) {
            if (event.getOriginal().getPersistentData().getBoolean(key)) event.getEntity().getPersistentData().putBoolean(key, true);
        }
    }
}
