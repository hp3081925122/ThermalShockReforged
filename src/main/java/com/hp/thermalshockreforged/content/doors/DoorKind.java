package com.hp.thermalshockreforged.content.doors;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum DoorKind {
    MAW("maw_door", "mawdoor"),
    BUNKER("bunker_door", "bunker_door");

    public final String id;
    public final String asset;
    private final VoxelShape[] closed = new VoxelShape[4];
    private final VoxelShape[] open = new VoxelShape[4];

    DoorKind(String id, String asset) {
        this.id = id;
        this.asset = asset;
        if (id.equals("maw_door")) {
            VoxelShape northSouth = Block.box(-16, 0, 0, 32, 64, 16);
            VoxelShape eastWest = Block.box(0, 0, -16, 16, 64, 32);
            VoxelShape openNorthSouth = Shapes.or(Block.box(-16, 0, 0, 32, 1, 16),
                    Block.box(-16, 0, 0, -14, 64, 16), Block.box(30, 0, 0, 32, 64, 16));
            VoxelShape openEastWest = Shapes.or(Block.box(0, 0, -16, 16, 1, 32),
                    Block.box(0, 0, -16, 16, 64, -14), Block.box(0, 0, 30, 16, 64, 32));
            closed[0] = northSouth; closed[1] = eastWest; closed[2] = northSouth; closed[3] = eastWest;
            open[0] = openNorthSouth; open[1] = openEastWest; open[2] = openNorthSouth; open[3] = openEastWest;
        } else {
            closed[0] = Block.box(-48, 0, 0, 16, 48, 16);
            closed[1] = Block.box(0, 0, -48, 16, 48, 16);
            closed[2] = Block.box(0, 0, 0, 64, 48, 16);
            closed[3] = Block.box(0, 0, 0, 16, 48, 64);
            open[0] = Shapes.or(Block.box(-48, 0, 0, 16, 1, 16), Block.box(15, 0, 0, 16, 48, 16));
            open[1] = Shapes.or(Block.box(0, 0, -48, 16, 1, 16), Block.box(0, 0, 15, 16, 48, 16));
            open[2] = Shapes.or(Block.box(0, 0, 0, 64, 1, 16), Block.box(0, 0, 0, 1, 48, 16));
            open[3] = Shapes.or(Block.box(0, 0, 0, 16, 1, 64), Block.box(0, 0, 0, 16, 48, 1));
        }
    }

    VoxelShape shape(net.minecraft.core.Direction direction, boolean opened) {
        int index = switch (direction) { case EAST -> 1; case SOUTH -> 2; case WEST -> 3; default -> 0; };
        return opened ? open[index] : closed[index];
    }

    BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of().mapColor(this == MAW ? MapColor.TERRACOTTA_PINK : MapColor.DEEPSLATE)
                .sound(this == MAW ? SoundType.WOOD : SoundType.METAL).strength(-1, 3600000)
                .noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((state, level, pos) -> false);
    }
}
