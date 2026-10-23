package com.hp.thermalshockreforged.entity.icesquad;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import java.util.List;

public final class StriderConstructorBlock extends Block implements com.hp.thermalshockreforged.content.BlockTooltips.Provider {
    public StriderConstructorBlock() { super(Properties.of().mapColor(MapColor.METAL).sound(SoundType.GLASS).strength(1, 10)); }
    @Override protected int getLightDampening(BlockState state) { return 15; }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("block.thermal_shock_reforged.icidrostrider_constructor.description_0"));
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (previous.is(this) || !(level instanceof ServerLevel server) || !level.getBlockState(pos).is(this) || !server.removeBlock(pos, false)) return;
        IceSquadContent.STRIDER.get().spawn(server, pos, EntitySpawnReason.MOB_SUMMONED);
        server.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.5, 0.5, 0.5, 0);
        server.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1, 2);
        server.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1, 2);
    }
}
