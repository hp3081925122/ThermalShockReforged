package com.hp.thermalshockreforged.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

public final class WaterloggingFixProcessor extends StructureProcessor {
    public static final WaterloggingFixProcessor INSTANCE = new WaterloggingFixProcessor();
    public static final MapCodec<WaterloggingFixProcessor> CODEC = MapCodec.unit(() -> INSTANCE);
    private WaterloggingFixProcessor() { }
    @Override public StructureTemplate.StructureBlockInfo process(LevelReader level, BlockPos seed, BlockPos relative, StructureTemplate.StructureBlockInfo original, StructureTemplate.StructureBlockInfo current, StructurePlaceSettings settings, StructureTemplate template) {
        if (current.state().getFluidState().isEmpty() || !(level instanceof LevelAccessor mutable)) return current;
        if (level instanceof WorldGenRegion region && !region.getCenter().equals(ChunkPos.containing(current.pos()))) return current;
        var chunk = level.getChunk(current.pos());
        int y = current.pos().getY();
        if (y >= chunk.getMinY() && y <= (chunk.getMaxY() + 1)) mutable.scheduleTick(current.pos(), current.state().getBlock(), 0);
        return current;
    }
    @Override protected StructureProcessorType<?> getType() { return LoqendiaContent.WATERLOGGING_FIX.get(); }
}
