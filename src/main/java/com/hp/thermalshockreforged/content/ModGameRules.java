package com.hp.thermalshockreforged.content;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.world.level.GameRules;

public final class ModGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> LOQENDIA_DROP_HEART_CRYSTALS = GameRules.register(
            "loqendiaDropHeartCrystals", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));

    private ModGameRules() { }
    public static void initialize() {
        ThermalShockReforged.LOGGER.debug("Thermal Shock Reforged gamerule initialized: loqendiaDropHeartCrystals");
    }
}
