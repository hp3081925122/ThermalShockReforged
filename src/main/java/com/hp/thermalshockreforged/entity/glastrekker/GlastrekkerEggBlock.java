package com.hp.thermalshockreforged.entity.glastrekker;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.InteractionResult;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public final class GlastrekkerEggBlock extends Block implements com.hp.thermalshockreforged.content.BlockTooltips.Provider {
    public GlastrekkerEggBlock() { super(com.hp.thermalshockreforged.ModRegistration.block().mapColor(MapColor.QUARTZ).sound(SoundType.CALCITE).strength(.8F, .2F).friction(.7F)); }
    @Override public int getLightDampening(BlockState state) { return 10; }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("block.thermal_shock_reforged.glastrekker_egg.description_0"));
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(Items.BONE_MEAL)) return InteractionResult.PASS;
        if (level instanceof ServerLevel server) {
            if (!player.getAbilities().instabuild) stack.shrink(1);
            server.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 12, .9, .9, .9, .05);
            server.playSound(null, pos, SoundEvents.SNIFFER_EGG_CRACK, SoundSource.BLOCKS, 1, 2);
            if (server.getRandom().nextDouble() < .1) {
                var newborn = GlastrekkerContent.GLASTREKKER.get().spawn(server, pos, EntitySpawnReason.MOB_SUMMONED);
                if (newborn != null && !newborn.isRemoved() && newborn.isAddedToLevel()) {
                    newborn.setYRot(server.getRandom().nextFloat() * 360);
                    server.destroyBlock(pos, false);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
