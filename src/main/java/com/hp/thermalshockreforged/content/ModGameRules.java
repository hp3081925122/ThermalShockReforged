package com.hp.thermalshockreforged.content;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.world.level.GameRules;

public final class ModGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> LOQENDIA_DROP_HEART_CRYSTALS = GameRules.register(
            "loqendiaDropHeartCrystals", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));

    public static final GameRules.Key<GameRules.BooleanValue> LOQENDIA_GENERATE_SPECIAL_STRUCTURES = GameRules.register(
            "loqendiaGenerateSpecialStructures", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));

    public static final GameRules.Key<GameRules.BooleanValue> LOQENDIA_ENFORCE_PROGRESSION = GameRules.register(
            "loqendiaEnforceProgression", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.BooleanValue> LOQENDIA_PYLON_CORES_EXPLODE = GameRules.register(
            "loqendiaPylonCoresExplode", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.BooleanValue> LOQENDIA_FIRE_EXTINGUISHES = GameRules.register(
            "loqendiaFireExtinguishes", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.BooleanValue> LOQENDIA_MAGMA_FREEZES_UNDERWATER = GameRules.register(
            "loqendiaMagmaFreezesUnderwater", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));

    private ModGameRules() { }
    public static void initialize() {
        ThermalShockReforged.LOGGER.debug("Thermal Shock Reforged gamerules initialized: loqendiaDropHeartCrystals, loqendiaGenerateSpecialStructures, loqendiaEnforceProgression, loqendiaPylonCoresExplode, loqendiaFireExtinguishes, loqendiaMagmaFreezesUnderwater");
    }
}
