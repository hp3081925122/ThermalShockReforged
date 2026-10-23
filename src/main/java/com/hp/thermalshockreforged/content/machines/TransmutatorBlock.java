package com.hp.thermalshockreforged.content.machines;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import com.hp.thermalshockreforged.content.recipe.TransmutationRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public final class TransmutatorBlock extends Block implements com.hp.thermalshockreforged.content.BlockTooltips.Provider {
    public TransmutatorBlock() { super(Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(2, 20).lightLevel(state -> 10)); }
    private boolean process(ItemStack input, Level level, BlockPos pos, Player player) {
        var recipe = TransmutationRecipe.find(level, input);
        if (recipe == null) return false;
        var output = recipe.result().copy();
        if (!(level instanceof ServerLevel server)) return true;
        input.shrink(1);
        if (!MachineContent.drop(server, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5, output)) {
            input.grow(1);
            ThermalShockReforged.LOGGER.debug("Transmutation output rejected; input restored: player={}, position={}, output={}", player.getUUID(), pos, output);
            return true;
        }
        server.playSound(null, pos, SoundEvents.COPPER_PLACE, SoundSource.BLOCKS, 1, 3);
        ThermalShockReforged.LOGGER.debug("Transmutation completed: player={}, position={}, output={}", player.getUUID(), pos, output);
        return true;
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return process(stack, level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return InteractionResult.PASS; }
    @Override protected int getLightDampening(BlockState state) { return 15; }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextDouble() < .1) level.addParticle(ParticleTypes.END_ROD, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5, 0, .2, 0);
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("block.thermal_shock_reforged.transmutator.description_0"));
    }
}
