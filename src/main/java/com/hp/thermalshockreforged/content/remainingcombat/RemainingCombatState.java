package com.hp.thermalshockreforged.content.remainingcombat;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

public final class RemainingCombatState {
    private static final String CRYO = "thermal_shock_reforged:ice_bomb_remaining";
    private static final String CREATED = "thermal_shock_reforged:ice_bomb_created";
    private static final String REMOVE_PENDING = "thermal_shock_reforged:ice_bomb_remove_pending";
    private static final String KNOCKBACK_CREATED = "thermal_shock_reforged:claw_no_knockback_created";
    private static final ResourceLocation KNOCKBACK = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "claw_no_knockback");
    private RemainingCombatState() { }
    public static void applyCryoCooldown(LivingEntity entity) {
        entity.getPersistentData().remove(REMOVE_PENDING);
        entity.getPersistentData().putInt(CRYO, Math.max(120, entity.getPersistentData().getInt(CRYO)));
        entity.getPersistentData().putLong(CREATED, entity.level().getGameTime());
    }
    public static boolean blocksCryoAttack(LivingEntity entity) { finishRemoval(entity); return entity.getPersistentData().getInt(CRYO) > 0; }
    public static void removed(MobEffectEvent.Remove event) {
        if (!event.getEntity().level().isClientSide && event.getEffect().equals(RemainingCombatContent.CRYO_VISUAL) && event.getEffectInstance() != null)
            event.getEntity().getPersistentData().putBoolean(REMOVE_PENDING, true);
    }
    private static void finishRemoval(LivingEntity entity) {
        var data = entity.getPersistentData();
        if (!data.getBoolean(REMOVE_PENDING)) return;
        if (!entity.hasEffect(RemainingCombatContent.CRYO_VISUAL)) { data.remove(CRYO); data.remove(CREATED); }
        data.remove(REMOVE_PENDING);
    }
    public static void refreshNoKnockback(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        var attribute = entity.getAttribute(Attributes.ATTACK_KNOCKBACK);
        if (attribute != null && !attribute.hasModifier(KNOCKBACK)) attribute.addTransientModifier(new AttributeModifier(KNOCKBACK, -1020, AttributeModifier.Operation.ADD_VALUE));
        entity.getPersistentData().putLong(KNOCKBACK_CREATED, entity.level().getGameTime());
    }
    public static void tick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide) return;
        var data = entity.getPersistentData();
        finishRemoval(entity);
        if (data.contains(CRYO) && data.getLong(CREATED) != entity.level().getGameTime()) {
            int remaining = entity.isAlive() ? Math.max(0, data.getInt(CRYO) - 1) : 0;
            if (remaining > 0) data.putInt(CRYO, remaining);
            else { data.remove(CRYO); data.remove(CREATED); }
        }
        if (data.contains(KNOCKBACK_CREATED) && data.getLong(KNOCKBACK_CREATED) != entity.level().getGameTime()) {
            var attribute = entity.getAttribute(Attributes.ATTACK_KNOCKBACK);
            if (attribute != null) attribute.removeModifier(KNOCKBACK);
            data.remove(KNOCKBACK_CREATED);
        }
    }
    public static void join(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || event.getLevel().isClientSide) return;
        var data = entity.getPersistentData();
        if (data.getInt(CRYO) > 0) { data.putInt(CRYO, Math.clamp(data.getInt(CRYO), 1, 120)); data.putLong(CREATED, Long.MIN_VALUE); }
        if (data.contains(KNOCKBACK_CREATED)) { data.remove(KNOCKBACK_CREATED); var attribute = entity.getAttribute(Attributes.ATTACK_KNOCKBACK); if (attribute != null) attribute.removeModifier(KNOCKBACK); }
    }
    public static void serverTick(ServerTickEvent.Post event) { RemainingCombatContent.pruneAnimations(); }
    public static void stopped(ServerStoppedEvent event) { RemainingCombatContent.clearAnimations(); }
}
