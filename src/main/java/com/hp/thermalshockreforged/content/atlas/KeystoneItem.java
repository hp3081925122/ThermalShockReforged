package com.hp.thermalshockreforged.content.atlas;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.*;
import com.hp.thermalshockreforged.world.LoqendiaContent;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

class KeystoneItem extends Item {
    KeystoneItem() { super(com.hp.thermalshockreforged.ModRegistration.item().stacksTo(1).rarity(Rarity.EPIC)); }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.keystone.description_0"));
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.keystone.description_1"));
    }
    @Override public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer(); var level = context.getLevel(); var pos = context.getClickedPos();
        if (player == null || !(level instanceof ServerLevel server) || player.getCooldowns().isOnCooldown(getDefaultInstance()) || (!level.dimension().equals(Level.OVERWORLD) && !level.dimension().equals(LoqendiaContent.DIMENSION))) return InteractionResult.SUCCESS;
        for (int x = -1; x <= 1; x++) for (int z = -1; z <= 1; z++) if (!level.getBlockState(pos.offset(x, 0, z)).is(Blocks.OBSIDIAN)) return InteractionResult.SUCCESS;
        if (!level.mayInteract(player, pos.above()) || !player.mayUseItemAt(pos.above(), context.getClickedFace(), context.getItemInHand())) return InteractionResult.SUCCESS;
        if (server.getStructureManager().get(AtlasContent.BASE_TEMPLATE).isEmpty()) { ThermalShockReforged.LOGGER.warn("Atlas activation template is missing: {}", AtlasContent.BASE_TEMPLATE); return InteractionResult.SUCCESS; }
        var task = AtlasContent.ACTIVATION.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
        if (task == null) return InteractionResult.SUCCESS;
        task.prepare(server, pos, context.getClickedFace());
        if (!server.addFreshEntity(task)) return InteractionResult.SUCCESS;
        var atlas = GeoDecorContent.block(GeoDecorKind.ATLAS);
        boolean changed = server.setBlock(pos.above(), atlas.withPropertiesOf(server.getBlockState(pos.above())), 3);
        if (!changed && !server.getBlockState(pos.above()).is(atlas)) { task.discard(); return InteractionResult.SUCCESS; }
        player.getCooldowns().addCooldown(getDefaultInstance(), 100);
        server.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.NEUTRAL, 1, 3);
        return InteractionResult.SUCCESS;
    }
}
