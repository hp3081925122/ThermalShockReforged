package com.hp.thermalshockreforged.client;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.client.armor.ArmorClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class ClientGameEvents {
    @SubscribeEvent
    public static void effectTick(PlayerTickEvent.Pre event) {
        if (!event.getEntity().level().isClientSide() || event.getEntity() != net.minecraft.client.Minecraft.getInstance().player) return;
        for (var effect : java.util.List.copyOf(event.getEntity().getActiveEffects())) {
            if (effect.getEffect().value() instanceof com.hp.thermalshockreforged.content.status.ClientTickedEffect ticked && (effect.isInfiniteDuration() || effect.getDuration() > 0)
                    && effect.getEffect().value().shouldApplyEffectTickThisTick(effect.getDuration(), effect.getAmplifier())) ticked.clientEffectTick(event.getEntity(), effect.getAmplifier());
        }
    }

    @SubscribeEvent
    public static void recipesReceived(net.neoforged.neoforge.client.event.RecipesReceivedEvent event) {
        com.hp.thermalshockreforged.content.recipe.RecipeContent.acceptClientRecipes(event.getRecipeMap());
    }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        ArmorClient.playerTick(event);
    }
}
