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
    private static final String REACH_REMAINING = "thermal_shock_reforged:held_reach_remaining";
    private static final String REACH_CREATED = "thermal_shock_reforged:held_reach_created";
    private static final ResourceLocation REACH_MODIFIER = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "held_reach");

    private HeldWeaponState() { }

    public static void refreshReach(LivingEntity entity, int amount) {
        if (entity.level().isClientSide) return;
        var range = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (range == null) return;
        var modifier = range.getModifier(REACH_MODIFIER);
        if (modifier == null || modifier.amount() != amount) {
            range.removeModifier(REACH_MODIFIER);
            range.addTransientModifier(new AttributeModifier(REACH_MODIFIER, amount, AttributeModifier.Operation.ADD_VALUE));
        }
        entity.getPersistentData().putInt("thermal_shock_reforged:held_reach_amount", amount);
        entity.getPersistentData().putInt(REACH_REMAINING, 3);
        entity.getPersistentData().putLong(REACH_CREATED, entity.level().getGameTime());
    }

    public static void refreshNoMelee(LivingEntity entity) {
        refreshNoMelee(entity, 1);
    }

    public static void refreshNoMelee(LivingEntity entity, int duration) {
        if (entity.level().isClientSide) return;
        var range = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (range == null) return;
        if (!range.hasModifier(MODIFIER)) range.addTransientModifier(new AttributeModifier(MODIFIER, -40, AttributeModifier.Operation.ADD_VALUE));
        entity.getPersistentData().putInt(REMAINING, duration);
        entity.getPersistentData().putLong(CREATED, entity.level().getGameTime());
    }

    public static void tick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide) return;
        var data = entity.getPersistentData();
        if (data.contains(REACH_REMAINING) && (!entity.isAlive() || data.getLong(REACH_CREATED) != entity.level().getGameTime())) {
            int remaining = entity.isAlive() ? data.getInt(REACH_REMAINING) - 1 : 0;
            if (remaining > 0) data.putInt(REACH_REMAINING, remaining);
            else {
                var range = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
                if (range != null) range.removeModifier(REACH_MODIFIER);
                data.remove(REACH_REMAINING);
                data.remove(REACH_CREATED);
                data.remove("thermal_shock_reforged:held_reach_amount");
            }
        }
        if (!data.contains(REMAINING)) return;
        if (entity.isAlive() && data.getLong(CREATED) == entity.level().getGameTime()) return;
        int remaining = entity.isAlive() ? data.getInt(REMAINING) - 1 : 0;
        if (remaining > 0) { data.putInt(REMAINING, remaining); return; }
        var range = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (range != null) range.removeModifier(MODIFIER);
        data.remove(REMAINING);
        data.remove(CREATED);
    }

    public static void join(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || event.getLevel().isClientSide || !entity.isAlive()) return;
        var data = entity.getPersistentData();
        if (data.getInt(REACH_REMAINING) > 0) {
            var range = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
            int amount = Math.clamp(data.getInt("thermal_shock_reforged:held_reach_amount"), 0, 5);
            if (range != null && !range.hasModifier(REACH_MODIFIER)) range.addTransientModifier(new AttributeModifier(REACH_MODIFIER, amount, AttributeModifier.Operation.ADD_VALUE));
            data.putInt(REACH_REMAINING, Math.clamp(data.getInt(REACH_REMAINING), 1, 3));
            data.putLong(REACH_CREATED, Long.MIN_VALUE);
        }
        if (data.getInt(REMAINING) <= 0) return;
        var range = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (range != null && !range.hasModifier(MODIFIER)) range.addTransientModifier(new AttributeModifier(MODIFIER, -40, AttributeModifier.Operation.ADD_VALUE));
        data.putLong(CREATED, Long.MIN_VALUE);
    }
}
