package com.hp.thermalshockreforged.compat.ali;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.loot.LootContent;
import com.yanny.aci.tooltip.TooltipBuilder;
import com.yanny.ali.api.AliEntrypoint;
import com.yanny.ali.api.IPlugin;
import com.yanny.ali.api.IServerRegistry;

@AliEntrypoint
public final class ThermalShockAliPlugin implements IPlugin {
    private static final String KEY = "ali." + ThermalShockReforged.MOD_ID + ".";

    @Override public String getModId() { return ThermalShockReforged.MOD_ID; }

    @Override public void registerServer(IServerRegistry registry) {
        registry.registerConditionTooltip(LootContent.WorldCrux.class, (utils, condition) -> TooltipBuilder.keyValue(KEY + "condition.world_crux", condition.min()));
        registry.registerConditionTooltip(LootContent.GameRule.class, (utils, condition) -> TooltipBuilder.keyValue(KEY + "condition.game_rule", TooltipBuilder.translate("gamerule." + condition.rule())));
        registry.registerFunctionTooltip(LootContent.UnlimitedLifetime.class, (utils, function) -> TooltipBuilder.keyOnly(KEY + "function.unlimited_lifetime"));
        ThermalShockReforged.LOGGER.debug("Advanced Loot Info tooltips registered for custom loot types");
    }
}
