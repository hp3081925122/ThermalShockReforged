package com.hp.thermalshockreforged.content;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public final class ModGameRules {
    public static final GameRule<Boolean> LOQENDIA_GENERATE_SPECIAL_STRUCTURES = GameRules.registerBoolean(
            "loqendiaGenerateSpecialStructures", GameRuleCategory.UPDATES, true);

    public static final GameRule<Boolean> LOQENDIA_ENFORCE_PROGRESSION = GameRules.registerBoolean(
            "loqendiaEnforceProgression", GameRuleCategory.UPDATES, true);
    private ModGameRules() { }
    public static void initialize() {
        ThermalShockReforged.LOGGER.debug("Thermal Shock Reforged gamerules initialized: loqendiaGenerateSpecialStructures, loqendiaEnforceProgression");
    }
}
