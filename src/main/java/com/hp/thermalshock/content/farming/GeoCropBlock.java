package com.hp.thermalshock.content.farming;

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
                .strength(kind == CropKind.ANCIENT_BARBUBBLE ? .7f : .5f, 0).noCollission().noOcclusion().pushReaction(PushReaction.DESTROY)
                .isRedstoneConductor((s, l, p) -> false).noLootTable();
        if (kind.luminous) props.lightLevel(s -> 5).hasPostProcess((s, l, p) -> true).emissiveRendering((s, l, p) -> true);
        return props;
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(ANIMATION); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new GeoCropBlockEntity(kind, pos, state); }
    @Override protected net.minecraft.world.MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof GeoCropBlockEntity entity ? entity : null;
    }
    @Override protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        return level.getBlockEntity(pos) instanceof GeoCropBlockEntity entity && entity.triggerEvent(id, param);
    }
    @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return kind == CropKind.FROST_LOTUS ? box(.1, .1, .1, 15.9, 15.9, 15.9) : super.getShape(state, level, pos, context);
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        if (level.isClientSide) return;
        String soil = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock()).toString();
        if (!java.util.Set.of("thermal_shock:purified_grassy_shale", "thermal_shock:grassy_shale", "thermal_shock:snowy_shale", "thermal_shock:planter_pot", "thermal_shock:planter_box").contains(soil)) {
            Block.dropResources(state, level, pos, null);
            level.destroyBlock(pos, false);
        }
    }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (kind == CropKind.HEARTHFLY_BRANCH) level.addParticle(FarmingContent.HEARTHFLY.get(),
                pos.getX() + random.nextDouble(), pos.getY() + 1.9, pos.getZ() + random.nextDouble(),
                random.nextDouble() * .2 - .1, random.nextDouble() * .2 - .1, random.nextDouble() * .2 - .1);
    }
    @Override public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean harvest, FluidState fluid) {
        boolean result = super.onDestroyedByPlayer(state, level, pos, player, harvest, fluid);
        SickleHarvesting.harvest(level, pos, kind, player);
        return result;
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!state.is(replacement.getBlock())) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof FarmInventoryBlockEntity entity) {
                Containers.dropContents(level, pos, entity);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, replacement, moving);
        }
    }
    public static final class Directional extends GeoCropBlock {
        public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
        public Directional(CropKind kind) { super(kind); registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)); }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(FACING); }
        @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
        @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
        @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    }
}
