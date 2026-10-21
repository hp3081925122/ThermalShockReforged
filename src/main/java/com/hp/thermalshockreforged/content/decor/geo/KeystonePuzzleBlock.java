package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.content.atlas.AtlasContent;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.WorldProgressData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class KeystonePuzzleBlock extends GeoDecorBlock {
    private static final String CLAIMED = "KeystoneClaimed";
    private static final BlockPos[] SWITCHES = {new BlockPos(14, 0, 7), new BlockPos(0, 0, 7), new BlockPos(14, 0, -7), new BlockPos(0, 0, -7)};
    public KeystonePuzzleBlock(GeoDecorKind kind) { super(kind); }
    private boolean interact(Level level, BlockPos pos, Player player) {
        if (!(level.getBlockEntity(pos) instanceof GeoDecorBlockEntity entity) || entity.kind != kind) return kind != GeoDecorKind.KEYSTONE_PEDESTAL;
        if (!(level instanceof ServerLevel server)) return kind != GeoDecorKind.KEYSTONE_PEDESTAL || entity.getPersistentData().getBoolean("caninteract") && !entity.getPersistentData().getBoolean(CLAIMED);
        if (kind == GeoDecorKind.RINTHIUM_BUTTON) {
            if (!entity.getPersistentData().getBoolean("active")) {
                entity.getPersistentData().putBoolean("active", true);
                entity.setChanged();
                level.setBlock(pos, level.getBlockState(pos).setValue(animationProperty(), 1), 3);
                level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
                level.playSound(null, pos, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "mantle_lever")), SoundSource.NEUTRAL, 1, 1);
                maybePromptLever(server, pos, player);
            }
            return true;
        }
        if (kind == GeoDecorKind.KEYSTONE_PEDESTAL) {
            var data = entity.getPersistentData();
            if (!data.getBoolean("caninteract") || data.getBoolean(CLAIMED)) return false;
            data.putBoolean("caninteract", false);
            data.putBoolean(CLAIMED, true);
            entity.setChanged();
            level.setBlock(pos, level.getBlockState(pos).setValue(animationProperty(), 2), 3);
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(AtlasContent.KEYSTONE.get()));
            level.playSound(null, pos, SoundEvents.AMBIENT_CAVE.value(), SoundSource.NEUTRAL, 1, 1);
            server.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("message.thermal_shock_reforged.keystone.you_know"), false);
            return true;
        }
        BlockPos center = pos.offset(7, 2, 0);
        var oldCenter = level.getBlockEntity(center);
        boolean ready = oldCenter == null || !oldCenter.getPersistentData().getBoolean("caninteract") && !oldCenter.getPersistentData().getBoolean(CLAIMED);
        if (!allSwitchesActive(level, pos)) ready = false;
        if (!ready) {
            level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.NEUTRAL, 1, 1);
            return true;
        }
        var pedestal = GeoDecorContent.block(GeoDecorKind.KEYSTONE_PEDESTAL);
        level.setBlock(center, pedestal.defaultBlockState(), 3);
        if (!(level.getBlockEntity(center) instanceof GeoDecorBlockEntity awakened) || awakened.kind != GeoDecorKind.KEYSTONE_PEDESTAL) return true;
        awakened.getPersistentData().putBoolean("caninteract", true);
        awakened.setChanged();
        animate(level, pos);
        animate(level, center);
        animate(level, pos.offset(7, 0, 0));
        level.sendBlockUpdated(center, level.getBlockState(center), level.getBlockState(center), 3);
        level.playSound(null, pos, SoundEvents.MINECART_RIDING, SoundSource.NEUTRAL, 1, -3);
        level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, .5F, 1);
        return true;
    }
    private static void maybePromptLever(ServerLevel server, BlockPos buttonPos, Player player) {
        BlockPos lever = null;
        for (BlockPos offset : SWITCHES) {
            BlockPos candidate = buttonPos.subtract(offset);
            if (server.getBlockEntity(candidate) instanceof GeoDecorBlockEntity entity && entity.kind == GeoDecorKind.RINTHIUM_LEVER) {
                lever = candidate;
                break;
            }
        }
        if (lever == null || !allSwitchesActive(server, lever)) return;
        var progress = WorldProgressData.get(server);
        if (progress.keystoneNoticeComplete() && progress.markKeystoneLeverHintShown()) player.displayClientMessage(Component.translatable("message.thermal_shock_reforged.keystone.puzzle_lever"), false);
    }
    private static boolean allSwitchesActive(Level level, BlockPos leverPos) {
        for (BlockPos offset : SWITCHES) {
            var button = level.getBlockEntity(leverPos.offset(offset));
            if (button == null || !button.getPersistentData().getBoolean("active")) return false;
        }
        return true;
    }
    private static void animate(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty property && property.getPossibleValues().contains(1)) level.setBlock(pos, state.setValue(property, 1), 3);
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return interact(level, pos, player) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return interact(level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
    public static final class Pedestal extends KeystonePuzzleBlock {
        private static final IntegerProperty DISPLAY_ANIMATION = IntegerProperty.create("animation", 0, 2);
        public Pedestal() { super(GeoDecorKind.KEYSTONE_PEDESTAL); }
        @Override protected IntegerProperty animationProperty() { return DISPLAY_ANIMATION; }
    }
    public static final class Directional extends KeystonePuzzleBlock {
        private static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
        public Directional(GeoDecorKind kind) { super(kind); registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)); }
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(FACING); }
        @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
        @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
        @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    }
}
