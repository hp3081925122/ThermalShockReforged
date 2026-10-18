package com.hp.thermalshockreforged.content.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;
import net.minecraft.world.phys.Vec3;

public final class SickleHarvesting {
    public static void harvest(Level world, BlockPos pos, CropKind crop, Player player) {
        if (!(world instanceof ServerLevel level) || player == null) return;
        String tool = BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).toString();
        switch (tool) {
            case "thermal_shock_reforged:ruby_sickle", "thermal_shock_reforged:bloodstone_sickle", "thermal_shock_reforged:carborundum_sickle" -> base(level, pos, crop);
            case "thermal_shock_reforged:claw_sickle" -> {
                if (level.random.nextDouble() < .3) ancientSeedLoot(level, pos);
                claw(level, pos, crop);
            }
            case "thermal_shock_reforged:stratosilver_sickle" -> {
                if (level.random.nextDouble() < .3) ancientSeedLoot(level, pos);
                stratosilver(level, pos, crop);
            }
            default -> fallback(level, pos, crop);
        }
    }
    private static void ancientSeedLoot(ServerLevel level, BlockPos pos) {
        var table = level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "gameplay/ancient_seeds")));
        var params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.atLowerCornerOf(pos)).create(LootContextParamSets.CHEST);
        for (ItemStack stack : table.getRandomItems(params)) {
            var entity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack.copy());
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);
        }
    }
    private static void repeat(ServerLevel level, BlockPos pos, String item, double minimum, double maximum, boolean unlimited) {
        for (int index = 0; index < Math.round(minimum == maximum ? minimum : minimum + level.random.nextDouble() * (maximum - minimum)); index++) drop(level, pos, item, unlimited);
    }
    private static void drop(ServerLevel level, BlockPos pos, String id, boolean unlimited) {
        var item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(id)).orElseThrow(() -> new IllegalStateException("Missing farming harvest item: " + id));
        var entity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(item));
        entity.setPickUpDelay(10);
        if (unlimited) entity.setUnlimitedLifetime();
        level.addFreshEntity(entity);
    }
    private static void base(ServerLevel level, BlockPos pos, CropKind crop) {
        switch (crop) {
            case SKORIO -> {
                repeat(level, pos, "thermal_shock_reforged:skorio_petal", 2, 2, false);
                repeat(level, pos, "thermal_shock_reforged:skorio_seed", 1.0, 2.0, true);
            }
            case BARBUBBLE -> {
                repeat(level, pos, "thermal_shock_reforged:barbubble_melon", 2, 2, false);
                repeat(level, pos, "thermal_shock_reforged:barbubble_seed", 1.0, 2.0, true);
            }
            case ANCIENT_SKORIO -> {
                repeat(level, pos, "thermal_shock_reforged:skorio_petal", 5, 5, false);
                repeat(level, pos, "thermal_shock_reforged:ancient_skorio_seed", 1.0, 2.0, true);
            }
            case ANCIENT_BARBUBBLE -> {
                repeat(level, pos, "thermal_shock_reforged:barbubble_melon_slice", 5, 5, false);
                repeat(level, pos, "thermal_shock_reforged:ancient_barbubble_seed", 1.0, 2.0, true);
            }
            case DEWLEAF -> {
                repeat(level, pos, "thermal_shock_reforged:dew_bottle", 1.0, 2.0, false);
                repeat(level, pos, "thermal_shock_reforged:dewleaf_seed", 1.0, 2.0, true);
            }
            case NEOFLORM -> {
                repeat(level, pos, "thermal_shock_reforged:neoflorm_crystal", 2, 2, false);
                repeat(level, pos, "thermal_shock_reforged:neoflorm_seed", 1.0, 2.0, true);
            }
            case FROST_LOTUS -> {
                repeat(level, pos, "thermal_shock_reforged:frost_pearl", 2, 2, false);
                repeat(level, pos, "thermal_shock_reforged:frost_lotus_seed", 1.0, 2.0, true);
            }
            case HEARTHFLY_BRANCH -> {
                repeat(level, pos, "thermal_shock_reforged:hearth_honey", 2, 2, false);
                repeat(level, pos, "thermal_shock_reforged:hearthfly_branch_seed", 1.0, 2.0, true);
            }
            case VOLATINE -> {
                repeat(level, pos, "minecraft:gunpowder", 2, 2, false);
                repeat(level, pos, "thermal_shock_reforged:volatine_seed", 1.0, 2.0, true);
            }
            default -> {}
        }
    }

    private static void claw(ServerLevel level, BlockPos pos, CropKind crop) {
        switch (crop) {
            case SKORIO -> {
                repeat(level, pos, "thermal_shock_reforged:skorio_petal", 2, 2, false);
                repeat(level, pos, "thermal_shock_reforged:skorio_seed", 1.0, 2.0, true);
            }
            case BARBUBBLE -> {
                repeat(level, pos, "thermal_shock_reforged:barbubble_melon", 2, 2, false);
                repeat(level, pos, "thermal_shock_reforged:barbubble_seed", 1.0, 2.0, true);
            }
            case ANCIENT_SKORIO -> {
                repeat(level, pos, "thermal_shock_reforged:skorio_petal", 5, 5, false);
                repeat(level, pos, "thermal_shock_reforged:ancient_skorio_seed", 1.0, 2.0, true);
            }
            case ANCIENT_BARBUBBLE -> {
                repeat(level, pos, "thermal_shock_reforged:barbubble_melon_slice", 5, 5, false);
                repeat(level, pos, "thermal_shock_reforged:ancient_barbubble_seed", 1.0, 2.0, true);
            }
            case DEWLEAF -> {
                repeat(level, pos, "thermal_shock_reforged:dew_bottle", 1.0, 2.0, false);
                repeat(level, pos, "thermal_shock_reforged:dewleaf_seed", 1.0, 2.0, true);
            }
            case NEOFLORM -> {
                repeat(level, pos, "thermal_shock_reforged:neoflorm_crystal", 2.0, 3.0, false);
                repeat(level, pos, "thermal_shock_reforged:neoflorm_seed", 1.0, 2.0, true);
            }
            case FROST_LOTUS -> {
                repeat(level, pos, "thermal_shock_reforged:frost_pearl", 2, 2, false);
                repeat(level, pos, "thermal_shock_reforged:frost_lotus_seed", 1.0, 2.0, true);
            }
            case HEARTHFLY_BRANCH -> {
                repeat(level, pos, "thermal_shock_reforged:hearth_honey", 2, 2, false);
                repeat(level, pos, "thermal_shock_reforged:hearthfly_branch_seed", 1.0, 2.0, true);
            }
            case VOLATINE -> {
                repeat(level, pos, "minecraft:gunpowder", 2, 2, false);
                repeat(level, pos, "thermal_shock_reforged:volatine_seed", 1.0, 2.0, true);
            }
            default -> {}
        }
    }

    private static void stratosilver(ServerLevel level, BlockPos pos, CropKind crop) {
        switch (crop) {
            case SKORIO -> {
                repeat(level, pos, "thermal_shock_reforged:skorio_petal", 3, 3, false);
                repeat(level, pos, "thermal_shock_reforged:skorio_seed", 1.0, 3.0, true);
                if (level.random.nextDouble() < 0.6) drop(level, pos, "thermal_shock_reforged:ancient_skorio_seed", true);
            }
            case BARBUBBLE -> {
                repeat(level, pos, "thermal_shock_reforged:barbubble_melon", 3, 3, false);
                repeat(level, pos, "thermal_shock_reforged:barbubble_seed", 1.0, 3.0, true);
                if (level.random.nextDouble() < 0.6) drop(level, pos, "thermal_shock_reforged:ancient_barbubble_seed", true);
            }
            case ANCIENT_SKORIO -> {
                repeat(level, pos, "thermal_shock_reforged:skorio_petal", 7, 7, false);
                repeat(level, pos, "thermal_shock_reforged:ancient_skorio_seed", 1.0, 3.0, true);
            }
            case ANCIENT_BARBUBBLE -> {
                repeat(level, pos, "thermal_shock_reforged:barbubble_melon_slice", 8, 8, false);
                repeat(level, pos, "thermal_shock_reforged:ancient_barbubble_seed", 1.0, 3.0, true);
            }
            case DEWLEAF -> {
                repeat(level, pos, "thermal_shock_reforged:dew_bottle", 2.0, 4.0, false);
                repeat(level, pos, "thermal_shock_reforged:dewleaf_seed", 1.0, 3.0, true);
            }
            case NEOFLORM -> {
                repeat(level, pos, "thermal_shock_reforged:neoflorm_crystal", 2.0, 6.0, false);
                repeat(level, pos, "thermal_shock_reforged:neoflorm_seed", 1.0, 2.0, true);
                if (level.random.nextDouble() < 0.5) drop(level, pos, "thermal_shock_reforged:stellar_scrap", true);
            }
            case FROST_LOTUS -> {
                repeat(level, pos, "thermal_shock_reforged:frost_pearl", 3, 3, false);
                repeat(level, pos, "thermal_shock_reforged:frost_lotus_seed", 1.0, 3.0, true);
            }
            case HEARTHFLY_BRANCH -> {
                repeat(level, pos, "thermal_shock_reforged:hearth_honey", 2, 2, false);
                repeat(level, pos, "thermal_shock_reforged:hearthfly_branch_seed", 1.0, 3.0, true);
            }
            case VOLATINE -> {
                repeat(level, pos, "minecraft:gunpowder", 2, 2, false);
                repeat(level, pos, "thermal_shock_reforged:volatine_seed", 1.0, 2.0, true);
            }
            default -> {}
        }
    }

    private static void fallback(ServerLevel level, BlockPos pos, CropKind crop) {
        switch (crop) {
            case SKORIO -> {
                drop(level, pos, "thermal_shock_reforged:skorio", false);
            }
            case BARBUBBLE -> {
                drop(level, pos, "thermal_shock_reforged:barbubble", false);
            }
            case NEOFLORM -> {
                drop(level, pos, "thermal_shock_reforged:neoflorm", false);
            }
            case ANCIENT_SKORIO -> {
                drop(level, pos, "thermal_shock_reforged:ancient_skorio", false);
            }
            case ANCIENT_BARBUBBLE -> {
                drop(level, pos, "thermal_shock_reforged:ancient_barbubble", false);
            }
            case DEWLEAF -> {
                drop(level, pos, "thermal_shock_reforged:dewleaf", false);
            }
            case FROST_LOTUS -> {
                drop(level, pos, "thermal_shock_reforged:frost_lotus", false);
            }
            case HEARTHFLY_BRANCH -> {
                drop(level, pos, "thermal_shock_reforged:hearthfly_branch", false);
            }
            case VOLATINE -> {
                drop(level, pos, "thermal_shock_reforged:volatine", false);
            }
            default -> {}
        }
    }
}
