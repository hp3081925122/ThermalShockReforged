package com.hp.thermalshockreforged.content.growth;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.*;

public final class SpireTipBlock extends Block {
    public SpireTipBlock() { super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.COLOR_PURPLE).sound(SoundType.SLIME_BLOCK).instabreak().lightLevel(s->7).noCollision().noOcclusion().isRedstoneConductor((s,l,p)->false)); }
    @Override protected int getLightDampening(BlockState state) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state) { return true; }
    @Override protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, net.minecraft.world.level.redstone.Orientation from, boolean moving) {
        super.neighborChanged(state, level, pos, block, from, moving);
        if (level instanceof ServerLevel server && level.isEmptyBlock(pos.below()) && level.getBlockState(pos).is(this)
                && level.destroyBlock(pos, false) && !level.getBlockState(pos).is(this)) {
            com.hp.thermalshockreforged.content.loot.LootContent.dropBlockTable(server, "gameplay/spire_bloom_collapse", state, pos, net.minecraft.world.phys.Vec3.atLowerCornerOf(pos));
        }
    }
}
