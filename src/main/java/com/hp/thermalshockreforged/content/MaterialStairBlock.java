package com.hp.thermalshockreforged.content;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class MaterialStairBlock extends StairBlock implements com.hp.thermalshockreforged.content.BlockTooltips.Provider {
    private final java.util.List<String> descriptions;
    private final float blastResistance;
    private final boolean randomTicks;
    private final int blockedLight;
    private final int flammability;
    private final int fireSpread;

    public MaterialStairBlock(Properties properties, float blastResistance, boolean randomTicks, int blockedLight, int flammability, int fireSpread, String... descriptions) {
        super(Blocks.AIR.defaultBlockState(), properties);
        this.descriptions = java.util.List.of(descriptions);
        this.blastResistance = blastResistance;
        this.randomTicks = randomTicks;
        this.blockedLight = blockedLight;
        this.flammability = flammability;
        this.fireSpread = fireSpread;
    }

    @Override
    public float getExplosionResistance() {
        return blastResistance;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return randomTicks;
    }

    @Override
    protected int getLightDampening(BlockState state) {
        return blockedLight < 0 ? super.getLightDampening(state) : blockedLight;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
        return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos position, Direction face) {
        return fireSpread < 0 ? super.getFireSpreadSpeed(state, level, position, face) : fireSpread;
    }
    @Override
    public void appendHoverText(net.minecraft.world.item.ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        descriptions.forEach(key -> tooltipAdder.accept(net.minecraft.network.chat.Component.translatable(key)));
    }

}
