package com.hp.thermalshockreforged.content.growth;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

public final class VisiBulbBlock extends Block implements EntityBlock {
    public static final IntegerProperty BLOCKSTATE=IntegerProperty.create("blockstate",0,1);
    public VisiBulbBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.GRASS).strength(.5f,0).lightLevel(s->s.getValue(BLOCKSTATE)==1?4:0).noCollission().noOcclusion().hasPostProcess((s,l,p)->true).emissiveRendering((s,l,p)->true).isRedstoneConductor((s,l,p)->false));
        registerDefaultState(defaultBlockState().setValue(BLOCKSTATE,0));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(BLOCKSTATE); }
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos) { return true; }
    @Override protected VoxelShape getVisualShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) { return Shapes.empty(); }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) { return new GrowthInventoryBlockEntity(pos,state); }
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState previous,boolean moving) { super.onPlace(state,level,pos,previous,moving); GrowthSoil.checkMature(level,pos); }
    @Override public boolean onDestroyedByPlayer(BlockState state,Level level,BlockPos pos,Player player,boolean harvest,FluidState fluid) { boolean result=super.onDestroyedByPlayer(state,level,pos,player,harvest,fluid); GrowthHarvest.harvest(level,pos,GrowthKind.VISI_BULB,player); return result; }
    private void use(Level level,BlockPos pos,Player player) {
        if (!(level instanceof ServerLevel server)||level.getBlockState(pos).getValue(BLOCKSTATE)!=0) return;
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,1300,0));
        level.setBlock(pos,level.getBlockState(pos).setValue(BLOCKSTATE,1),3);
        level.playSound(null,pos,SoundEvents.HONEY_BLOCK_BREAK,SoundSource.NEUTRAL,1,1);
        level.playSound(null,pos,SoundEvents.BUBBLE_COLUMN_BUBBLE_POP,SoundSource.NEUTRAL,1,1);
        server.sendParticles(ParticleTypes.SPLASH,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,5,.4,.4,.4,.1);
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit) { use(level,pos,player); return ItemInteractionResult.SUCCESS; }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) { use(level,pos,player); return InteractionResult.SUCCESS; }
    @Override protected MenuProvider getMenuProvider(BlockState state,Level level,BlockPos pos) { return level.getBlockEntity(pos) instanceof GrowthInventoryBlockEntity entity?entity:null; }
    @Override protected boolean triggerEvent(BlockState state,Level level,BlockPos pos,int id,int param) { super.triggerEvent(state,level,pos,id,param); return level.getBlockEntity(pos) instanceof GrowthInventoryBlockEntity entity&&entity.triggerEvent(id,param); }
    @Override protected boolean hasAnalogOutputSignal(BlockState state) { return true; }
    @Override protected int getAnalogOutputSignal(BlockState state,Level level,BlockPos pos) { return level.getBlockEntity(pos) instanceof GrowthInventoryBlockEntity entity?AbstractContainerMenu.getRedstoneSignalFromContainer(entity):0; }
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving) {
        if (!state.is(replacement.getBlock())) {
            if (!level.isClientSide&&level.getBlockEntity(pos) instanceof GrowthInventoryBlockEntity entity) { Containers.dropContents(level,pos,entity); level.updateNeighbourForOutputSignal(pos,this); }
            super.onRemove(state,level,pos,replacement,moving);
        }
    }
}
