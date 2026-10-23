package com.hp.thermalshockreforged.content;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModGameRules {
    private static final DeferredRegister<GameRule<?>> RULES = DeferredRegister.create(Registries.GAME_RULE, ThermalShockReforged.MOD_ID);

    public static final DeferredHolder<GameRule<?>, GameRule<Boolean>> LOQENDIA_GENERATE_SPECIAL_STRUCTURES = bool("loqendia_generate_special_structures", GameRuleCategory.UPDATES, true);
    public static final DeferredHolder<GameRule<?>, GameRule<Boolean>> LOQENDIA_ENFORCE_PROGRESSION = bool("loqendia_enforce_progression", GameRuleCategory.UPDATES, true);

    private ModGameRules() { }

    private static DeferredHolder<GameRule<?>, GameRule<Boolean>> bool(String id, GameRuleCategory category, boolean defaultValue) {
        return RULES.register(id, () -> new GameRule<>(category, GameRuleType.BOOL, BoolArgumentType.bool(), GameRuleTypeVisitor::visitBoolean, Codec.BOOL, value -> value ? 1 : 0, defaultValue, FeatureFlagSet.of()));
    }

    public static boolean get(Level level, DeferredHolder<GameRule<?>, GameRule<Boolean>> rule) {
        if (level instanceof ServerLevel server) return server.getGameRules().get(rule.get());
        return rule.get().defaultValue();
    }

    public static void register(IEventBus bus) {
        RULES.register(bus);
        ThermalShockReforged.LOGGER.debug("Thermal Shock Reforged gamerules registered: loqendia_generate_special_structures, loqendia_enforce_progression");
    }
}
