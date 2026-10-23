package com.hp.thermalshockreforged.entity.cavebloomer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public final class CavebloomerCombat {
    private CavebloomerCombat() { }
    public static void damageBlockingItem(LivingEntity victim, int damage) {
        if (!victim.isBlocking()) return;
        var slot = victim.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        victim.getUseItem().hurtAndBreak(damage, victim, slot);
    }
    public static void setResistedVelocity(Entity victim, Vec3 velocity) {
        double resistance = victim instanceof LivingEntity living ? Math.clamp(1 - living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0, 1) : 1;
        if (resistance <= 0) return;
        victim.setDeltaMovement(velocity.scale(resistance)); victim.hurtMarked = true; victim.hurtMarked = true;
    }
}
