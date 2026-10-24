package com.hp.thermalshockreforged.content.machines;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlock;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.recipe.ExtractionRecipe;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public final class ExtractorBlock extends GeoDecorBlock.Waterlogged {
    public static final IntegerProperty PROCESS_ANIMATION = IntegerProperty.create("animation", 0, 2);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public ExtractorBlock() { super(GeoDecorKind.EXTRACTOR); registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)); }
    @Override protected IntegerProperty animationProperty() { return PROCESS_ANIMATION; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()); }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }

    private boolean process(ItemStack input, Level level, BlockPos pos, Player player) {
        var recipe = ExtractionRecipe.find(level, input);
        if (recipe == null) return false;
        if (!(level instanceof ServerLevel server)) return true;
        AdvancementHolder advancement = null;
        if (recipe.advancement().isPresent()) {
            advancement = server.getServer().getAdvancements().get(recipe.advancement().get());
            if (advancement == null) {
                ThermalShockReforged.LOGGER.debug("Extractor advancement unavailable; keeping input: player={}, position={}, advancement={}", player.getUUID(), pos, recipe.advancement().get());
                return true;
            }
        }
        var inputId = BuiltInRegistries.ITEM.getKey(input.getItem());
        input.shrink(1);
        for (var output : recipe.outputs()) {
            if (!output.guaranteed()) continue;
            int repeat = output.repeat().sample(server.getRandom());
            for (int count = 0; count < repeat; count++) {
                if (!MachineContent.drop(server, pos.getX(), pos.getY(), pos.getZ(), output.item().create())) {
                    input.grow(1);
                    ThermalShockReforged.LOGGER.debug("Extractor output rejected; input restored: player={}, position={}, input={}", player.getUUID(), pos, inputId);
                    return true;
                }
            }
        }
        if (recipe.experience().isPresent()) {
            var experience = recipe.experience().get();
            int orbs = experience.orbs().sample(server.getRandom());
            for (int count = 0; count < orbs; count++) server.addFreshEntity(new ExperienceOrb(server, pos.getX(), pos.getY(), pos.getZ(), experience.value()));
        }
        if (recipe.levels() > 0) player.giveExperienceLevels(recipe.levels());
        for (var output : recipe.outputs()) {
            if (output.guaranteed() || server.getRandom().nextFloat() >= output.chance()) continue;
            int repeat = output.repeat().sample(server.getRandom());
            for (int count = 0; count < repeat; count++) MachineContent.drop(server, pos.getX(), pos.getY(), pos.getZ(), output.item().create());
        }
        for (var sound : recipe.sounds()) sound.play(server, pos, SoundSource.NEUTRAL);
        if (recipe.particleBlock().isPresent()) server.levelEvent(2001, pos, Block.getId(recipe.particleBlock().get().value().defaultBlockState()));
        if (advancement != null && player instanceof ServerPlayer owner) {
            var progress = owner.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) owner.getAdvancements().award(advancement, criterion);
        }
        ThermalShockReforged.LOGGER.debug("Extraction completed: player={}, position={}, input={}", player.getUUID(), pos, inputId);
        return true;
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (process(stack, level, pos, player)) return InteractionResult.SUCCESS;
        if (hand == InteractionHand.MAIN_HAND && ExtractionRecipe.find(level, player.getOffhandItem()) != null) return InteractionResult.PASS;
        return InteractionResult.SUCCESS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return InteractionResult.SUCCESS; }
}
