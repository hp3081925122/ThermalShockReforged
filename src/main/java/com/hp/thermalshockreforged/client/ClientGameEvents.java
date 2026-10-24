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
    public static void effectTick(net.neoforged.neoforge.event.tick.EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof net.minecraft.world.entity.LivingEntity living) || !living.level().isClientSide()) return;
        for (var effect : java.util.List.copyOf(living.getActiveEffects())) {
            if (!(effect.getEffect().value() instanceof com.hp.thermalshockreforged.content.status.ClientTickedEffect ticked) || !(effect.isInfiniteDuration() || effect.getDuration() > 0)) continue;
            int tickCount = effect.isInfiniteDuration() ? living.tickCount : effect.getDuration();
            if (effect.getEffect().value().shouldApplyEffectTickThisTick(tickCount, effect.getAmplifier())) ticked.clientEffectTick(living, effect.getAmplifier());
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
