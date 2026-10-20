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
    private static final String SPEAR_REMAINING = "thermal_shock_reforged:spear_no_melee_remaining";
    private static final String SPEAR_CREATED = "thermal_shock_reforged:spear_no_melee_created";
    private static final ResourceLocation MODIFIER = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "held_no_melee");
    private static final String REACH_REMAINING = "thermal_shock_reforged:held_reach_remaining";
    private static final String REACH_CREATED = "thermal_shock_reforged:held_reach_created";
    private static final String REACH_CURRENT = "thermal_shock_reforged:held_reach_current";
    private static final String REACH_HIDDEN = "thermal_shock_reforged:held_reach_hidden";
    private static final String REACH_HIDDEN_REMAINING = "thermal_shock_reforged:held_reach_hidden_remaining";
    private static final ResourceLocation REACH_MODIFIER = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "held_reach");
    private static final String ZOOM_REMAINING = "thermal_shock_reforged:magnetic_zoom_remaining";
    private static final String ZOOM_CREATED = "thermal_shock_reforged:magnetic_zoom_created";
    private static final ResourceLocation ZOOM_MODIFIER = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "magnetic_zoom");

    private HeldWeaponState() { }

    public static void refreshMagneticZoom(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        var speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;
        if (!speed.hasModifier(ZOOM_MODIFIER)) speed.addTransientModifier(new AttributeModifier(ZOOM_MODIFIER, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        entity.getPersistentData().putInt(ZOOM_REMAINING, 1);
        entity.getPersistentData().putLong(ZOOM_CREATED, entity.level().getGameTime());
    }

    public static void refreshReach(LivingEntity entity, int amount) {
        if (entity.level().isClientSide) return;
        var range = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (range == null) return;
        var data = entity.getPersistentData();
        int current = data.getInt(REACH_CURRENT);
        int remaining = data.getInt(REACH_REMAINING);
        if (current <= 0) {
            current = amount;
            remaining = 3;
        } else if (amount > current) {
            data.putInt(REACH_HIDDEN, current);
            data.putInt(REACH_HIDDEN_REMAINING, remaining);
            current = amount;
            remaining = 3;
        } else if (amount == current) {
            remaining = 3;
        } else if (remaining < 3) {
            data.putInt(REACH_HIDDEN, amount);
            data.putInt(REACH_HIDDEN_REMAINING, 3);
        }
        var modifier = range.getModifier(REACH_MODIFIER);
        if (modifier == null || modifier.amount() != current) {
            range.removeModifier(REACH_MODIFIER);
            range.addTransientModifier(new AttributeModifier(REACH_MODIFIER, current, AttributeModifier.Operation.ADD_VALUE));
        }
        data.putInt(REACH_CURRENT, current);
        data.putInt(REACH_REMAINING, remaining);
        entity.getPersistentData().putLong(REACH_CREATED, entity.level().getGameTime());
    }

    public static void refreshNoMelee(LivingEntity entity) {
        refreshNoMelee(entity, 1, 40);
    }

    public static void refreshNoMelee(LivingEntity entity, int duration, int reduction) {
        if (entity.level().isClientSide) return;
        if (entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE) == null) return;
        var data = entity.getPersistentData();
        String remaining = reduction == 20 ? SPEAR_REMAINING : REMAINING;
        String created = reduction == 20 ? SPEAR_CREATED : CREATED;
        data.putInt(remaining, Math.max(data.getInt(remaining), Math.clamp(duration, 1, 3)));
        data.putLong(created, entity.level().getGameTime());
        updateNoMeleeModifier(entity);
    }

    private static void updateNoMeleeModifier(LivingEntity entity) {
        var range = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (range == null) return;
        var data = entity.getPersistentData();
        int reduction = data.getInt(REMAINING) > 0 ? 40 : data.getInt(SPEAR_REMAINING) > 0 ? 20 : 0;
        var modifier = range.getModifier(MODIFIER);
        if (reduction == 0) {
            range.removeModifier(MODIFIER);
        } else if (modifier == null || modifier.amount() != -reduction) {
            range.removeModifier(MODIFIER);
            range.addTransientModifier(new AttributeModifier(MODIFIER, -reduction, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    public static void tick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide) return;
        var data = entity.getPersistentData();
        if (data.contains(ZOOM_REMAINING) && (!entity.isAlive() || data.getLong(ZOOM_CREATED) != entity.level().getGameTime())) {
            var speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) speed.removeModifier(ZOOM_MODIFIER);
            data.remove(ZOOM_REMAINING); data.remove(ZOOM_CREATED);
        }
        if (data.contains(REACH_REMAINING) && (!entity.isAlive() || data.getLong(REACH_CREATED) != entity.level().getGameTime())) {
            var reachRange = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
            int hiddenRemaining = data.getInt(REACH_HIDDEN_REMAINING);
            if (hiddenRemaining > 0 && entity.isAlive()) data.putInt(REACH_HIDDEN_REMAINING, hiddenRemaining - 1);
            int remaining = entity.isAlive() ? data.getInt(REACH_REMAINING) - 1 : 0;
            if (remaining > 0) {
                data.putInt(REACH_REMAINING, remaining);
            } else if (data.getInt(REACH_HIDDEN_REMAINING) > 0) {
                int hidden = data.getInt(REACH_HIDDEN);
                hiddenRemaining = data.getInt(REACH_HIDDEN_REMAINING);
                if (hiddenRemaining > 0 && entity.isAlive()) {
                    data.putInt(REACH_CURRENT, hidden);
                    data.putInt(REACH_REMAINING, hiddenRemaining);
                    data.remove(REACH_HIDDEN);
                    data.remove(REACH_HIDDEN_REMAINING);
                    if (reachRange != null) {
                        reachRange.removeModifier(REACH_MODIFIER);
                        reachRange.addTransientModifier(new AttributeModifier(REACH_MODIFIER, hidden, AttributeModifier.Operation.ADD_VALUE));
                    }
                } else {
                    data.remove(REACH_REMAINING);
                    data.remove(REACH_CURRENT);
                    data.remove(REACH_HIDDEN);
                    data.remove(REACH_HIDDEN_REMAINING);
                    if (reachRange != null) reachRange.removeModifier(REACH_MODIFIER);
                }
            } else {
                if (reachRange != null) reachRange.removeModifier(REACH_MODIFIER);
                data.remove(REACH_REMAINING);
                data.remove(REACH_CURRENT);
            }
            data.remove(REACH_CREATED);
        }
        if (!data.contains(REMAINING) && !data.contains(SPEAR_REMAINING)) return;
        if (data.contains(REMAINING) && (!entity.isAlive() || data.getLong(CREATED) != entity.level().getGameTime())) {
            int remaining = entity.isAlive() ? data.getInt(REMAINING) - 1 : 0;
            if (remaining > 0) data.putInt(REMAINING, remaining);
            else {
                data.remove(REMAINING);
                data.remove(CREATED);
            }
        }
        if (data.contains(SPEAR_REMAINING) && (!entity.isAlive() || data.getLong(SPEAR_CREATED) != entity.level().getGameTime())) {
            int remaining = entity.isAlive() ? data.getInt(SPEAR_REMAINING) - 1 : 0;
            if (remaining > 0) data.putInt(SPEAR_REMAINING, remaining);
            else {
                data.remove(SPEAR_REMAINING);
                data.remove(SPEAR_CREATED);
            }
        }
        updateNoMeleeModifier(entity);
    }

    public static void join(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || event.getLevel().isClientSide || !entity.isAlive()) return;
        var data = entity.getPersistentData();
        if (data.getInt(ZOOM_REMAINING) > 0) {
            var speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null && !speed.hasModifier(ZOOM_MODIFIER)) speed.addTransientModifier(new AttributeModifier(ZOOM_MODIFIER, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            data.putInt(ZOOM_REMAINING, 1); data.putLong(ZOOM_CREATED, Long.MIN_VALUE);
        }
        if (data.getInt(REACH_REMAINING) > 0) {
            var range = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
            int amount = Math.clamp(data.getInt(REACH_CURRENT), 0, 5);
            if (range != null && !range.hasModifier(REACH_MODIFIER)) range.addTransientModifier(new AttributeModifier(REACH_MODIFIER, amount, AttributeModifier.Operation.ADD_VALUE));
            data.putInt(REACH_REMAINING, Math.clamp(data.getInt(REACH_REMAINING), 1, 3));
            data.putLong(REACH_CREATED, Long.MIN_VALUE);
        }
        if (data.getInt(REMAINING) > 0) {
            data.putInt(REMAINING, Math.clamp(data.getInt(REMAINING), 1, 3));
            data.putLong(CREATED, Long.MIN_VALUE);
        }
        if (data.getInt(SPEAR_REMAINING) > 0) {
            data.putInt(SPEAR_REMAINING, Math.clamp(data.getInt(SPEAR_REMAINING), 1, 3));
            data.putLong(SPEAR_CREATED, Long.MIN_VALUE);
        }
        updateNoMeleeModifier(entity);
    }
}
