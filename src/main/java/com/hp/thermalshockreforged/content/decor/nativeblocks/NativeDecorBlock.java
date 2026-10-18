package com.hp.thermalshockreforged.content.decor.nativeblocks;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;

class NativeDecorBlock extends Block {
    record Traits(int opacity, boolean skylight, boolean emptyVisual, boolean overlay,
                  float enchantPower, int flammability, boolean soil, boolean cullSame) { }
    private final Traits traits;
    private final VoxelShape shape;
    private final List<String> descriptions;

    NativeDecorBlock(Properties properties, Traits traits, VoxelShape shape, String... descriptions) {
        super(properties);
        this.traits = traits;
        this.shape = shape;
        this.descriptions = List.of(descriptions);
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return traits.opacity();
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return traits.skylight() || super.propagatesSkylightDown(state, level, pos);
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return traits.emptyVisual() ? Shapes.empty() : super.getVisualShape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape == null ? super.getShape(state, level, pos, context) : shape;
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacent, Direction side) {
        return traits.cullSame() && adjacent.is(this) || super.skipRendering(state, adjacent, side);
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluid) {
        return traits.overlay() || super.shouldDisplayFluidOverlay(state, level, pos, fluid);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return traits.flammability() == 0 ? super.getFlammability(state, level, pos, direction) : traits.flammability();
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, BlockState plant) {
        return traits.soil() ? TriState.TRUE : super.canSustainPlant(state, level, pos, facing, plant);
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
        return traits.enchantPower() == 0 ? super.getEnchantPowerBonus(state, level, pos) : traits.enchantPower();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        descriptions.forEach(key -> lines.add(Component.translatable(key)));
    }
}
