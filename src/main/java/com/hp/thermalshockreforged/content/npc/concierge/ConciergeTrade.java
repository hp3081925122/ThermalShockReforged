package com.hp.thermalshockreforged.content.npc.concierge;

import com.hp.thermalshockreforged.content.recipe.NpcTradeRecipe;
import net.minecraft.server.level.ServerPlayer;

public final class ConciergeTrade {
    private ConciergeTrade() { }

    public static boolean buy(ServerPlayer player, String page) {
        var recipe = NpcTradeRecipe.get(player.level(), page);
        if (recipe == null) return false;
        boolean quest = recipe.requires().isPresent() && recipe.requires().get().equals(ConciergeContent.id("blasphemy_4")) && ConciergeQuest.completed(player);
        if (!quest && !recipe.allowed(player)) return false;
        return recipe.trade(player);
    }
}
