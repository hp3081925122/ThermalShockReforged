package com.hp.thermalshockreforged.content.farming;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.*;

public class GeoCropBlock extends Block implements EntityBlock {
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
    public final CropKind kind;
    public GeoCropBlock(CropKind kind) {
        super(properties(kind));
        this.kind = kind;
        registerDefaultState(defaultBlockState().setValue(ANIMATION, 0));
    }
    private static Properties properties(CropKind kind) {
        var props = Properties.of().mapColor(MapColor.PLANT).sound(kind == CropKind.ANCIENT_BARBUBBLE ? SoundType.WOOD : SoundType.GRASS)
                .strength(kind == CropKind.ANCIENT_BARBUBBLE ? .7f : .5f, 0).noCollision().noOcclusion().pushReaction(PushReaction.DESTROY)
                .isRedstoneConductor((s, l, p) -> false);
        if (kind.luminous) props.lightLevel(s -> 5).emissiveRendering((s, l, p) -> true);
        return props;
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(ANIMATION); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new GeoCropBlockEntity(kind, pos, state); }
    @Override protected net.minecraft.world.MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof GeoCropBlockEntity entity ? entity : null;
    }
    @Override protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        return level.getBlockEntity(pos) instanceof GeoCropBlockEntity entity && entity.triggerEvent(id, param);
    }
    @Override protected int getLightDampening(BlockState state) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state) { return true; }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return kind == CropKind.FROST_LOTUS ? box(.1, .1, .1, 15.9, 15.9, 15.9) : super.getShape(state, level, pos, context);
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        if (level.isClientSide()) return;
        String soil = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock()).toString();
        if (!java.util.Set.of("thermal_shock_reforged:purified_grassy_shale", "thermal_shock_reforged:grassy_shale", "thermal_shock_reforged:snowy_shale", "thermal_shock_reforged:planter_pot", "thermal_shock_reforged:planter_box").contains(soil)) {
            if (level.getBlockState(pos).is(this) && level.destroyBlock(pos, false) && !level.getBlockState(pos).is(this)) Block.dropResources(state, level, pos, null);
        }
    }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (kind == CropKind.HEARTHFLY_BRANCH) level.addParticle(FarmingContent.HEARTHFLY.get(),
                pos.getX() + random.nextDouble(), pos.getY() + 1.9, pos.getZ() + random.nextDouble(),
                random.nextDouble() * .2 - .1, random.nextDouble() * .2 - .1, random.nextDouble() * .2 - .1);
    }
    @Override protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean moving) { Containers.updateNeighboursAfterDestroy(state, level, pos); }
    public static final class Directional extends GeoCropBlock {
        public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
        public Directional(CropKind kind) { super(kind); registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)); }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(FACING); }
        @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
        @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
        @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    }
}
