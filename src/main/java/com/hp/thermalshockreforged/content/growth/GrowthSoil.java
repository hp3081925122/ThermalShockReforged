package com.hp.thermalshockreforged.content.growth;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

final class GrowthSoil {
    private static final Set<String> MATURE = Set.of("purified_grassy_shale", "grassy_shale", "snowy_shale", "planter_pot", "planter_box");
    private static final Set<String> SPARKLER = Set.of("vermillion_sparkler_stem", "vermillion_sparkler_top_1", "vermillion_sparkler_top_2", "vermillion_sparkler_top_3", "grassy_shale", "snowy_shale", "planter_pot", "planter_box");
    static boolean accepts(Level level, BlockPos pos, boolean sparkler) {
        var id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock());
        return id.getNamespace().equals("thermal_shock_reforged") && (sparkler ? SPARKLER : MATURE).contains(id.getPath());
    }
    static void checkMature(Level level, BlockPos pos) {
        if (!level.isClientSide() && !accepts(level, pos, false)) {
            var current = level.getBlockState(pos);
            if (level.destroyBlock(pos, false) && !level.getBlockState(pos).is(current.getBlock())) Block.dropResources(current, level, pos, null);
        }
    }
}
