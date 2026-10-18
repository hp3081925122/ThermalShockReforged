package com.hp.thermalshockreforged.content.growth;

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

public final class GrowthHarvest {
    public static void harvest(Level world, BlockPos pos, GrowthKind kind, Player player) {
        if (!(world instanceof ServerLevel level)||player==null) return;
        String tool=BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).toString();
        int tier=switch(tool) {
            case "thermal_shock_reforged:ruby_sickle","thermal_shock_reforged:bloodstone_sickle","thermal_shock_reforged:carborundum_sickle" -> 1;
            case "thermal_shock_reforged:claw_sickle" -> 2;
            case "thermal_shock_reforged:stratosilver_sickle" -> 3;
            default -> 0;
        };
        if (tier==0) {
            if (kind==GrowthKind.VISI_BULB) drop(level,pos,"thermal_shock_reforged:visi_bulb",false);
            return;
        }
        if (tier>=2&&level.random.nextDouble()<.3) ancientSeedLoot(level,pos);
        switch(kind) {
            case SPARKLER -> {
                repeat(level,pos,"thermal_shock_reforged:steelstem",tier==3?3:2,tier==3?3:2,false);
                repeat(level,pos,"thermal_shock_reforged:sparkler_seed",1,tier==3?3:2,true);
            }
            case GEL_BULB -> {
                repeat(level,pos,"thermal_shock_reforged:springelatin_ball",tier==3?2:1,tier==3?5:2,false);
                repeat(level,pos,"thermal_shock_reforged:gel_bulb_seed",1,tier==3?3:2,true);
            }
            case VISI_BULB -> repeat(level,pos,"thermal_shock_reforged:visi_bulb_seed",tier==3?2:1,tier==1?2:3,true);
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
}
