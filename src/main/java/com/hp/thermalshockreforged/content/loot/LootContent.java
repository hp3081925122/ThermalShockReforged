package com.hp.thermalshockreforged.content.loot;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.WorldProgressData;
import com.hp.thermalshockreforged.config.WorldRulesConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LootContent {
    private static final String UNLIMITED_KEY = ThermalShockReforged.MOD_ID + ":unlimited_lifetime";
    private static final DeferredRegister<MapCodec<? extends LootItemCondition>> CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<MapCodec<? extends LootItemFunction>> FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<WorldCrux>> WORLD_CRUX = CONDITIONS.register("world_crux", () -> WorldCrux.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<GameRule>> GAME_RULE = CONDITIONS.register("game_rule", () -> GameRule.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<UnlimitedLifetime>> UNLIMITED_LIFETIME = FUNCTIONS.register("unlimited_lifetime", () -> UnlimitedLifetime.CODEC);

    private LootContent() { }

    public static void register(IEventBus bus) {
        CONDITIONS.register(bus);
        FUNCTIONS.register(bus);
        NeoForge.EVENT_BUS.addListener(LootContent::itemJoined);
    }

    private static void itemJoined(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof ItemEntity item)) return;
        var stack = item.getItem();
        var data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null || !data.contains(UNLIMITED_KEY)) return;
        var copy = stack.copy();
        CustomData.update(DataComponents.CUSTOM_DATA, copy, tag -> tag.remove(UNLIMITED_KEY));
        item.setItem(copy);
        item.setUnlimitedLifetime();
    }

    public static ResourceKey<LootTable> table(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path));
    }

    public static List<ItemStack> roll(ServerLevel level, String path, Entity entity) {
        var params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, entity.position()).withParameter(LootContextParams.THIS_ENTITY, entity).create(LootContextParamSets.GIFT);
        return level.getServer().reloadableRegistries().getLootTable(table(path)).getRandomItems(params);
    }

    public static void dropBlockTable(ServerLevel level, String path, BlockState state, BlockPos pos, Vec3 at) {
        var params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.BLOCK_STATE, state).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).create(LootContextParamSets.BLOCK);
        for (var stack : level.getServer().reloadableRegistries().getLootTable(table(path)).getRandomItems(params)) spawn(level, at, stack, 10);
    }

    public static boolean spawn(ServerLevel level, Vec3 at, ItemStack stack, int pickUpDelay) {
        var item = new ItemEntity(level, at.x, at.y, at.z, stack);
        item.setPickUpDelay(pickUpDelay);
        return level.addFreshEntity(item);
    }

    public record WorldCrux(double min) implements LootItemCondition {
        static final MapCodec<WorldCrux> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.DOUBLE.fieldOf("min").forGetter(WorldCrux::min)).apply(instance, WorldCrux::new));
        @Override public MapCodec<WorldCrux> codec() { return CODEC; }
        @Override public boolean test(LootContext context) { return WorldProgressData.get(context.getLevel()).worldCrux() >= min; }
    }

    public record GameRule(String rule) implements LootItemCondition {
        static final MapCodec<GameRule> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.STRING.fieldOf("rule").forGetter(GameRule::rule)).apply(instance, GameRule::new));
        @Override public MapCodec<GameRule> codec() { return CODEC; }
        @Override public boolean test(LootContext context) {
            if (rule.equals("loqendiaDropHeartCrystals")) return WorldRulesConfig.DROP_HEART_CRYSTALS.get();
            var result = new boolean[] { false };
            var found = new boolean[] { false };
            GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
                @Override public void visitBoolean(GameRules.Key<GameRules.BooleanValue> key, GameRules.Type<GameRules.BooleanValue> type) {
                    if (!key.getId().equals(rule)) return;
                    found[0] = true;
                    result[0] = context.getLevel().getGameRules().getBoolean(key);
                }
            });
            if (!found[0]) ThermalShockReforged.LOGGER.warn("Loot condition references unknown boolean game rule: {}", rule);
            return result[0];
        }
    }

    public record UnlimitedLifetime() implements LootItemFunction {
        static final UnlimitedLifetime INSTANCE = new UnlimitedLifetime();
        static final MapCodec<UnlimitedLifetime> CODEC = MapCodec.unit(INSTANCE);
        @Override public MapCodec<UnlimitedLifetime> codec() { return CODEC; }
        @Override public ItemStack apply(ItemStack stack, LootContext context) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean(UNLIMITED_KEY, true));
            return stack;
        }
    }
}
