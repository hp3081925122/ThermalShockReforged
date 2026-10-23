package com.hp.thermalshockreforged.content.weapons;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.state.BlockState;

public final class SimpleElementalWeaponItem extends Item {
    private final String id;

    public SimpleElementalWeaponItem(String id) {
        super(com.hp.thermalshockreforged.ModRegistration.item().durability(100).attributes(ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 4.5, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build()));
        this.id = id;
    }

    @Override public float getDestroySpeed(ItemStack stack, BlockState state) { return 1.6F; }

    @Override public void hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("item." + ThermalShockReforged.MOD_ID + "." + id + ".description_0"));
    }
}
