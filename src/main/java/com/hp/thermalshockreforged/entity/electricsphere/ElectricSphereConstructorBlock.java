package com.hp.thermalshockreforged.entity.electricsphere;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.MapColor;

public final class ElectricSphereConstructorBlock extends Block implements com.hp.thermalshockreforged.content.BlockTooltips.Provider {
    public ElectricSphereConstructorBlock() { super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.BAMBOO).strength(1, 99)); }
    @Override public int getLightDampening(BlockState state) { return 15; }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("block.thermal_shock_reforged.electric_sphere_constructor.description_0"));
        tooltipAdder.accept(Component.translatable("block.thermal_shock_reforged.electric_sphere_constructor.description_1"));
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        if (!(level instanceof ServerLevel server) || oldState.is(this) || !level.getBlockState(pos).is(this)) return;
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        var sphere = ElectricSphereContent.ELECTRIC_SPHERE.get().spawn(server, pos, EntitySpawnReason.MOB_SUMMONED);
        if (sphere != null && server.getEntity(sphere.getUUID()) == sphere) sphere.setYRot(server.getRandom().nextFloat() * 360);
        else { if (sphere != null) sphere.discard(); Block.dropResources(state, server, pos); }
    }
}
