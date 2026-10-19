package com.hp.thermalshockreforged.content.remainingutility;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import com.hp.thermalshockreforged.content.terrain.TerrainContent;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class SnowMelterItem extends Item {
    SnowMelterItem() { super(new Properties().stacksTo(1)); }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable(getDescriptionId() + ".description_0"));
        lines.add(Component.translatable(getDescriptionId() + ".description_1"));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getPlayer() instanceof ServerPlayer player) || player.getCooldowns().isOnCooldown(this)) return InteractionResult.SUCCESS;
        ServerLevel level = player.serverLevel();
        var pos = context.getClickedPos();
        player.getCooldowns().addCooldown(this, 5);
        var advancement = player.server.getAdvancements().get(RemainingUtilityContent.id("hpsm_get"));
        if (advancement != null) {
            var progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
        }
        level.playSound(null, pos, SoundEvents.PLAYER_BREATH, SoundSource.NEUTRAL, 1, 2);
        level.sendParticles(ParticleTypes.POOF, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 5, .25, 0, .25, 0);
        BlockState state = level.getBlockState(pos);
        Block replacement = null;
        if (state.is(MaterialContent.blocks().get("shale").get()) || state.is(TerrainContent.GRASSY_SHALE.get())) replacement = TerrainContent.SNOWY_SHALE.get();
        else if (state.is(TerrainContent.SNOWY_SHALE.get())) replacement = MaterialContent.blocks().get("shale").get();
        else if (state.is(MaterialContent.blocks().get("permafrost").get())) replacement = NativeDecorContent.SNOWY_PERMAFROST.get();
        else if (state.is(NativeDecorContent.SNOWY_PERMAFROST.get())) replacement = MaterialContent.blocks().get("permafrost").get();
        else if (state.is(Blocks.SNOW)) replacement = Blocks.AIR;
        if (replacement != null) level.setBlock(pos, replacement.withPropertiesOf(state), 3);
        return InteractionResult.SUCCESS;
    }
}
