package com.hp.thermalshockreforged.content.labworldgen;
import net.minecraft.world.InteractionResult;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public final class DissolverGemlockBlock extends Block {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
    public DissolverGemlockBlock() { super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(-1, 3600000).requiresCorrectToolForDrops().pushReaction(PushReaction.BLOCK).lightLevel(state -> state.getValue(BLOCKSTATE) == 1 ? 15 : 0)); registerDefaultState(defaultBlockState().setValue(BLOCKSTATE, 0)); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(BLOCKSTATE); }
    @Override protected int getLightDampening(BlockState state) { return 15; }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand == InteractionHand.MAIN_HAND && level instanceof ServerLevel server) activate(server, pos, player);
        return InteractionResult.SUCCESS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return InteractionResult.SUCCESS; }
    private void activate(ServerLevel level, BlockPos pos, Player player) {
        BlockState current = level.getBlockState(pos);
        ItemStack held = player.getMainHandItem();
        if (!current.is(this) || current.getValue(BLOCKSTATE) != 0 || !held.is(MaterialContent.items().get("phobotite").get()) || held.isEmpty()) return;
        var task = LabWorldgenContent.DISSOLVER_CYCLE.get().create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (task == null) { ThermalShockReforged.LOGGER.debug("Dissolver task creation failed at {}", pos); return; }
        task.prepare(level, pos);
        BlockState activated = current.setValue(BLOCKSTATE, 1);
        if (!level.setBlock(pos, activated, 3)) return;
        if (!level.addFreshEntity(task)) {
            if (level.getBlockState(pos).equals(activated)) level.setBlock(pos, current, 3);
            ThermalShockReforged.LOGGER.debug("Dissolver task insertion failed at {}", pos);
            return;
        }
        if (player.getMainHandItem() != held || held.isEmpty() || !held.is(MaterialContent.items().get("phobotite").get())) {
            task.discard();
            if (level.getBlockState(pos).equals(activated)) level.setBlock(pos, current, 3);
            return;
        }
        held.shrink(1);
        player.getInventory().setChanged();
        level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
        ThermalShockReforged.LOGGER.debug("Dissolver activated at {} in {} by {}", pos, level.dimension().identifier(), player.getUUID());
    }
}
