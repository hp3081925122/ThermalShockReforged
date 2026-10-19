package com.hp.thermalshockreforged.content.machines;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlock;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public final class ExtractorBlock extends GeoDecorBlock.Waterlogged {
    private enum Processing { CORE, KEY, SMALL, MEDIUM, PESTICIDE, CHIP }
    public static final IntegerProperty PROCESS_ANIMATION = IntegerProperty.create("animation", 0, 2);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public ExtractorBlock() { super(GeoDecorKind.EXTRACTOR); registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)); }
    @Override protected IntegerProperty animationProperty() { return PROCESS_ANIMATION; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()); }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }

    private static Processing processing(ItemStack input) {
        var id = BuiltInRegistries.ITEM.getKey(input.getItem());
        if (input.isEmpty() || !id.getNamespace().equals(ThermalShockReforged.MOD_ID)) return null;
        return switch (id.getPath()) {
            case "infected_core_sample" -> Processing.CORE;
            case "herald_key" -> Processing.KEY;
            case "infected_seeds", "blood_vessel", "ashen_bone" -> Processing.SMALL;
            case "beetle_claw", "infected_flesh", "tainted_energy" -> Processing.MEDIUM;
            case "pesticide" -> Processing.PESTICIDE;
            case "neural_chip" -> Processing.CHIP;
            default -> null;
        };
    }
    private boolean process(ItemStack input, Level level, BlockPos pos, Player player) {
        var recipe = processing(input);
        if (recipe == null) return false;
        if (!(level instanceof ServerLevel server)) return true;
        var advancement = recipe == Processing.CORE ? server.getServer().getAdvancements().get(MachineContent.id("blasphemy_3")) : null;
        if (recipe == Processing.CORE && advancement == null) {
            ThermalShockReforged.LOGGER.debug("Extractor advancement unavailable; keeping core sample: player={}, position={}", player.getUUID(), pos);
            return true;
        }
        input.shrink(1);
        if (recipe == Processing.CORE) {
            if (!MachineContent.drop(server, pos.getX(), pos.getY(), pos.getZ(), "primordial_blood")) {
                input.grow(1);
                ThermalShockReforged.LOGGER.debug("Extractor blood output rejected; core sample restored: player={}, position={}", player.getUUID(), pos);
                return true;
            }
            effect(server, pos, recipe);
            if (player instanceof ServerPlayer owner) {
                var progress = owner.getAdvancements().getOrStartProgress(advancement);
                if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) owner.getAdvancements().award(advancement, criterion);
            }
        } else {
            if (recipe == Processing.KEY) {
                for (int count = 0; count < server.random.nextIntBetweenInclusive(3, 8); count++) server.addFreshEntity(new ExperienceOrb(server, pos.getX(), pos.getY(), pos.getZ(), 5));
                player.giveExperienceLevels(10);
                if (server.random.nextDouble() < .3) for (int count = 0; count < server.random.nextIntBetweenInclusive(1, 3); count++) MachineContent.drop(server, pos.getX(), pos.getY(), pos.getZ(), "helion_slag");
            } else {
                int xp = switch (recipe) { case SMALL -> 2; case MEDIUM -> 8; case PESTICIDE -> 3; case CHIP -> 20; default -> 0; };
                server.addFreshEntity(new ExperienceOrb(server, pos.getX(), pos.getY(), pos.getZ(), xp));
            }
            effect(server, pos, recipe);
            if (recipe == Processing.PESTICIDE && server.random.nextDouble() < .1) MachineContent.drop(server, pos.getX(), pos.getY(), pos.getZ(), "ferrous_nugget");
            if (recipe == Processing.CHIP && server.random.nextDouble() < .3) for (int count = 0; count < server.random.nextIntBetweenInclusive(1, 6); count++) MachineContent.drop(server, pos.getX(), pos.getY(), pos.getZ(), "phobotite");
        }
        ThermalShockReforged.LOGGER.debug("Extraction completed: player={}, position={}, recipe={}", player.getUUID(), pos, recipe);
        return true;
    }
    private static void effect(ServerLevel level, BlockPos pos, Processing recipe) {
        boolean metallic = recipe == Processing.PESTICIDE || recipe == Processing.CHIP;
        level.playSound(null, pos, metallic ? SoundEvents.BASALT_BREAK : SoundEvents.SCULK_CATALYST_BREAK, SoundSource.NEUTRAL, 1, metallic ? 3 : 1);
        if (recipe == Processing.CORE) level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
        String block = recipe == Processing.PESTICIDE ? "ferrum_plating" : recipe == Processing.CHIP ? "phobotite_block" : "vermilli_leaves";
        level.levelEvent(2001, pos, Block.getId(BuiltInRegistries.BLOCK.get(MachineContent.id(block)).defaultBlockState()));
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (process(stack, level, pos, player)) return ItemInteractionResult.SUCCESS;
        if (hand == InteractionHand.MAIN_HAND && processing(player.getOffhandItem()) != null) return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        return ItemInteractionResult.SUCCESS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return InteractionResult.SUCCESS; }
}
