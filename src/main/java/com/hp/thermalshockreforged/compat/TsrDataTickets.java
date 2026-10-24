package com.hp.thermalshockreforged.compat;

import com.geckolib.constant.dataticket.DataTicket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class TsrDataTickets {
    public static final DataTicket<ItemStack> ITEMSTACK = DataTicket.create("thermal_shock_reforged:itemstack", ItemStack.class);
    public static final DataTicket<LivingEntity> ENTITY = DataTicket.create("thermal_shock_reforged:entity", LivingEntity.class);
    public static final DataTicket<EquipmentSlot> EQUIPMENT_SLOT = DataTicket.create("thermal_shock_reforged:equipment_slot", EquipmentSlot.class);

    private TsrDataTickets() { }
}
