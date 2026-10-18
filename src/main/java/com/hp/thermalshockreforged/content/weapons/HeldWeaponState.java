package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

public final class HeldWeaponState {
    private static final String REMAINING = "thermal_shock_reforged:no_melee_remaining";
    private static final String CREATED = "thermal_shock_reforged:no_melee_created";
    private static final ResourceLocation MODIFIER = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "held_no_melee");

    private HeldWeaponState() { }

    public static void refreshNoMelee(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        var range = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (range == null) return;
        if (!range.hasModifier(MODIFIER)) range.addTransientModifier(new AttributeModifier(MODIFIER, -40, AttributeModifier.Operation.ADD_VALUE));
        entity.getPersistentData().putInt(REMAINING, 1);
        entity.getPersistentData().putLong(CREATED, entity.level().getGameTime());
    }

    public static void tick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide) return;
        var data = entity.getPersistentData();
        if (!data.contains(REMAINING)) return;
        if (entity.isAlive() && data.getLong(CREATED) == entity.level().getGameTime()) return;
        var range = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (range != null) range.removeModifier(MODIFIER);
        data.remove(REMAINING);
        data.remove(CREATED);
    }

    public static void join(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || event.getLevel().isClientSide || !entity.isAlive()) return;
        var data = entity.getPersistentData();
        if (data.getInt(REMAINING) <= 0) return;
        var range = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (range != null && !range.hasModifier(MODIFIER)) range.addTransientModifier(new AttributeModifier(MODIFIER, -40, AttributeModifier.Operation.ADD_VALUE));
        data.putLong(CREATED, Long.MIN_VALUE);
    }
}
