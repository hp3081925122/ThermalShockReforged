package com.hp.thermalshockreforged.content.reactor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.InteractionResult;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.icesquad.IceSquadContent;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public final class StratusReactorCoreBlock extends Block implements EntityBlock {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
    public StratusReactorCoreBlock() {
        super(Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).strength(2, 30).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(BLOCKSTATE) == 1 ? 0 : 15));
        registerDefaultState(defaultBlockState().setValue(BLOCKSTATE, 0));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(BLOCKSTATE); }
    @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 15; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ReactorCoreBlockEntity(pos, state); }
    @Override public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) { return state.getValue(BLOCKSTATE) != 1 && super.canHarvestBlock(state, level, pos, player); }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        for (int i = 0; i < 5; i++) tooltipAdder.accept(Component.translatable(getDescriptionId() + ".description_" + i));
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand == InteractionHand.MAIN_HAND && level instanceof ServerLevel server) activate(server, pos, player);
        return InteractionResult.SUCCESS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level instanceof ServerLevel server) activate(server, pos, player);
        return InteractionResult.SUCCESS;
    }
    private boolean pattern(ServerLevel level, BlockPos pos, Player player) {
        boolean valid = true;
        for (int x = -1; x <= 1; x++) for (int z = -1; z <= 1; z++) {
            BlockPos lower = pos.offset(x, -1, z);
            BlockPos upper = pos.offset(x, 1, z);
            boolean corner = x != 0 && z != 0;
            String material = corner ? "bordered_hazard_stripes" : "steelstem_block";
            if (!level.hasChunkAt(lower)) {
                valid = false;
                reportMismatch(player, lower, MaterialContent.blocks().get(material).get().defaultBlockState(), Blocks.AIR.defaultBlockState());
                continue;
            }
            BlockState lowerExpected = MaterialContent.blocks().get(material).get().defaultBlockState();
            if (!checkBlock(level, lower, lowerExpected, player)) valid = false;
            BlockPos frostrock = corner ? pos.offset(x, 0, z) : upper;
            BlockState frostrockExpected = MaterialContent.blocks().get("dark_frostrock_block").get().defaultBlockState();
            if (!checkBlock(level, frostrock, frostrockExpected, player)) valid = false;
        }
        return valid;
    }
    private boolean checkBlock(ServerLevel level, BlockPos pos, BlockState expected, Player player) {
        BlockState actual = level.getBlockState(pos);
        if (actual.is(expected.getBlock())) return true;
        reportMismatch(player, pos, expected, actual);
        return false;
    }
    private void reportMismatch(Player player, BlockPos pos, BlockState expected, BlockState actual) {
        if (player == null) return;
        player.displayClientMessage(Component.translatable("message.thermal_shock_reforged.stratus_reactor.invalid_block", pos.getX(), pos.getY(), pos.getZ(), expected.getBlock().getName(), actual.getBlock().getName()), false);
    }
    private void activate(ServerLevel level, BlockPos pos, Player player) {
        BlockState current = level.getBlockState(pos);
        if (!current.is(this) || current.getValue(BLOCKSTATE) == 1 || !pattern(level, pos, player) || !(level.getBlockEntity(pos) instanceof ReactorCoreBlockEntity core)) return;
        ReactorSequenceEntity task = ReactorContent.SEQUENCE.get().create(level);
        if (task == null) return;
        task.prepare(level, pos);
        BlockState activated = current.setValue(BLOCKSTATE, 1);
        if (!level.setBlock(pos, activated, 3)) return;
        core.assign(task.getUUID());
        if (!level.addFreshEntity(task) || task.isRemoved() || level.getEntity(task.getUUID()) != task) {
            task.discard();
            if (level.getBlockEntity(pos) == core && core.owns(task.getUUID()) && level.getBlockState(pos).equals(activated)) { core.assign(null); level.setBlock(pos, current, 3); }
            ThermalShockReforged.LOGGER.debug("Reactor sequence insertion rejected at {}", pos);
            return;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            var advancement = level.getServer().getAdvancements().get(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "stratus_reactor_get"));
            if (advancement != null) {
                var progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
                if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) serverPlayer.getAdvancements().award(advancement, criterion);
            }
        }
        level.playSound(null, pos, SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 1, 5);
        level.sendParticles(IceSquadContent.FOG_CLOUD.get(), pos.getX() + .5, pos.getY(), pos.getZ() + .5, 10, 2, 2, 2, 0);
        task.firstPulse(level);
        ThermalShockReforged.LOGGER.debug("Reactor sequence {} activated at {} in {}", task.getUUID(), pos, level.dimension().identifier());
    }
}
