package com.hp.thermalshockreforged.content.growth;

import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.storage.loot.LootParams;

public final class GelBulbBlock extends Block implements EntityBlock {
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public GelBulbBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_MAGENTA).sound(SoundType.GRASS).strength(.5f,0).noCollission().noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((s,l,p)->false));
        registerDefaultState(defaultBlockState().setValue(ANIMATION,0).setValue(FACING,Direction.NORTH));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) { builder.add(ANIMATION,FACING); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) { return new GelBulbBlockEntity(pos,state); }
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos) { return true; }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING,context.getHorizontalDirection().getOpposite()); }
    @Override protected BlockState rotate(BlockState state,Rotation rotation) { return state.setValue(FACING,rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state,Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState previous,boolean moving) { super.onPlace(state,level,pos,previous,moving); GrowthSoil.checkMature(level,pos); }
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder builder) { var drops=super.getDrops(state,builder); return drops.isEmpty()?List.of(new ItemStack(this)):drops; }
    @Override public boolean onDestroyedByPlayer(BlockState state,Level level,BlockPos pos,Player player,boolean harvest,FluidState fluid) {
        boolean result=super.onDestroyedByPlayer(state,level,pos,player,harvest,fluid);
        if (result && !level.getBlockState(pos).is(this)) GrowthHarvest.harvest(level,pos,GrowthKind.GEL_BULB,player);
        return result;
    }
    @Override protected MenuProvider getMenuProvider(BlockState state,Level level,BlockPos pos) { return level.getBlockEntity(pos) instanceof GelBulbBlockEntity entity?entity:null; }
    @Override protected boolean triggerEvent(BlockState state,Level level,BlockPos pos,int id,int param) { super.triggerEvent(state,level,pos,id,param); return level.getBlockEntity(pos) instanceof GelBulbBlockEntity entity&&entity.triggerEvent(id,param); }
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving) {
        if (!state.is(replacement.getBlock())) {
            if (!level.isClientSide&&level.getBlockEntity(pos) instanceof GelBulbBlockEntity entity) { Containers.dropContents(level,pos,entity); level.updateNeighbourForOutputSignal(pos,this); }
            super.onRemove(state,level,pos,replacement,moving);
        }
    }
}
