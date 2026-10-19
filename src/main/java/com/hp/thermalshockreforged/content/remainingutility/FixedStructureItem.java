package com.hp.thermalshockreforged.content.remainingutility;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.WorldProgressData;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

public final class FixedStructureItem extends Item {
    private static final BlockPos ORIGIN = new BlockPos(316, 53, 299);
    private final ResourceLocation templateId;
    private final boolean marksShalespring;

    FixedStructureItem(String template, boolean marksShalespring) {
        super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
        templateId = RemainingUtilityContent.id(template);
        this.marksShalespring = marksShalespring;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable(getDescriptionId() + ".description_0"));
        lines.add(Component.translatable(getDescriptionId() + ".description_1"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel) {
            var template = serverLevel.getStructureManager().get(templateId);
            if (template.isEmpty()) {
                ThermalShockReforged.LOGGER.error("Fixed structure template is missing: template={}, dimension={}", templateId, level.dimension().location());
                player.displayClientMessage(Component.translatable("message.thermal_shock_reforged.fixed_structure_failed"), true);
                return InteractionResultHolder.fail(stack);
            }
            player.displayClientMessage(Component.translatable("message.thermal_shock_reforged.fixed_structure_start"), true);
            ThermalShockReforged.LOGGER.debug("Placing full fixed structure: template={}, position={}, dimension={}", templateId, ORIGIN, level.dimension().location());
            boolean placed = template.get().placeInWorld(serverLevel, ORIGIN, ORIGIN,
                    new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false), serverLevel.random, 3);
            if (!placed) {
                ThermalShockReforged.LOGGER.error("Fixed structure placement failed: template={}, position={}, dimension={}", templateId, ORIGIN, level.dimension().location());
                player.displayClientMessage(Component.translatable("message.thermal_shock_reforged.fixed_structure_failed"), true);
                return InteractionResultHolder.fail(stack);
            }
            stack.shrink(1);
            if (marksShalespring) WorldProgressData.get(serverLevel).markShalespringExists();
            ThermalShockReforged.LOGGER.debug("Fixed structure placement completed: template={}, position={}, dimension={}", templateId, ORIGIN, level.dimension().location());
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
