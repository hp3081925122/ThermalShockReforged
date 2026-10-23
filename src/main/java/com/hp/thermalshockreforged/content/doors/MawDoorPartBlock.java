package com.hp.thermalshockreforged.content.doors;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.ArrayList;
import java.util.List;

public final class MawDoorPartBlock extends Block {
    public static final IntegerProperty X = IntegerProperty.create("x", 0, 2);
    public static final IntegerProperty Y = IntegerProperty.create("y", 0, 4);
    public static final IntegerProperty Z = IntegerProperty.create("z", 0, 2);
    private static boolean cleaning;

    public MawDoorPartBlock() {
        super(com.hp.thermalshockreforged.ModRegistration.block().strength(-1, 3600000).noOcclusion().noLootTable().isRedstoneConductor((state, level, pos) -> false));
        registerDefaultState(defaultBlockState().setValue(X, 1).setValue(Y, 0).setValue(Z, 1));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(X, Y, Z); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override protected int getLightDampening(BlockState state) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state) { return true; }
    @Override protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }

    public BlockState state(BlockPos offset) {
        return defaultBlockState().setValue(X, offset.getX() + 1).setValue(Y, offset.getY()).setValue(Z, offset.getZ() + 1);
    }

    public static BlockPos offset(BlockState state) {
        return new BlockPos(state.getValue(X) - 1, state.getValue(Y), state.getValue(Z) - 1);
    }

    public static List<BlockPos> offsets(Direction facing) {
        var offsets = new ArrayList<BlockPos>(15);
        boolean northSouth = facing == Direction.NORTH || facing == Direction.SOUTH;
        for (int y = 0; y < 5; y++) {
            if (northSouth) for (int x = -1; x <= 1; x++) offsets.add(new BlockPos(x, y, 0));
            else for (int z = -1; z <= 1; z++) offsets.add(new BlockPos(0, y, z));
        }
        return List.copyOf(offsets);
    }

    public static BlockPos main(BlockGetter level, BlockPos pos, BlockState state) {
        if (!state.is(SpecialDoorsContent.mawPart())) return null;
        BlockPos main = pos.subtract(offset(state));
        if (level instanceof LevelReader reader && !reader.hasChunkAt(main)) return null;
        return level.getBlockState(main).is(SpecialDoorsContent.block(DoorKind.MAW)) ? main : null;
    }

    private static BlockState mainState(BlockGetter level, BlockPos pos, BlockState state) {
        BlockPos main = main(level, pos, state);
        return main == null ? null : level.getBlockState(main);
    }

    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockState main = mainState(level, pos, state);
        return main == null ? Shapes.empty() : DoorKind.MAW.cellShape(main.getValue(SpecialDoorBlock.FACING), main.getValue(SpecialDoorBlock.BLOCKSTATE) == 1, offset(state));
    }

    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return getShape(state, level, pos, context); }

    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (!level.isClientSide()) level.scheduleTick(pos, this, 2);
    }

    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (main(level, pos, state) == null) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockPos main = main(level, pos, state);
        return main == null ? InteractionResult.PASS : level.getBlockState(main).useItemOn(stack, level, player, hand, hit.withPosition(main));
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockPos main = main(level, pos, state);
        return main == null ? InteractionResult.PASS : level.getBlockState(main).useWithoutItem(level, player, hit.withPosition(main));
    }

    @Override protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        BlockPos main = main(level, pos, state);
        if (main != null) level.getBlockState(main).attack(level, main, player);
    }

    @Override protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        BlockPos main = main(level, pos, state);
        return main == null ? 1 : level.getBlockState(main).getDestroyProgress(player, level, main);
    }

    @Override public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, net.minecraft.world.level.material.FluidState fluid) {
        BlockPos main = main(level, pos, state);
        if (main == null || level.isClientSide()) return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
        BlockState target = level.getBlockState(main);
        if (willHarvest && !player.isCreative()) Block.dropResources(target, level, main, level.getBlockEntity(main), player, player.getMainHandItem());
        boolean destroyed = level.destroyBlock(main, false, player);
        if (level.getBlockState(pos).is(this)) clean(() -> level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3));
        return destroyed;
    }

    @Override protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean moving) {
        if (!cleaning) {
            BlockPos main = main(level, pos, state);
            if (main != null) level.destroyBlock(main, true);
        }
        super.affectNeighborsAfterRemoval(state, level, pos, moving);
    }

    static void clean(Runnable action) {
        boolean previous = cleaning;
        cleaning = true;
        try { action.run(); } finally { cleaning = previous; }
    }

    @Override public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        BlockPos main = main(level, pos, state);
        return main == null ? 0 : level.getBlockState(main).getExplosionResistance(level, main, explosion);
    }

    @Override public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
        BlockPos main = main(level, pos, state);
        return main == null ? super.getSoundType(state, level, pos, entity) : level.getBlockState(main).getSoundType(level, main, entity);
    }

    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        BlockPos main = main(level, pos, state);
        return main == null ? ItemStack.EMPTY : level.getBlockState(main).getCloneItemStack(main, level, includeData, player);
    }
}
