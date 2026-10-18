package com.hp.thermalshock.entity.fauna;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

final class FaunaEvents {
    private FaunaEvents() { }

    static void incomingDamage(LivingIncomingDamageEvent event) {
        if (!event.getEntity().level().isClientSide && event.getSource().getEntity() instanceof FlareEntity) event.getEntity().igniteForSeconds(4);
    }

    static void death(LivingDeathEvent event) {
        var entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level) || !level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) return;
        if (entity instanceof BonerahnaEntity && entity.getRandom().nextDouble() < 0.004) drop(level, entity, "muck_pie", 1);
        if (entity instanceof FlareEntity) {
            if (entity.getRandom().nextDouble() < 0.7) drop(level, entity, "infected_seeds", (int) Math.round(1 + entity.getRandom().nextDouble() * 4));
            if (entity.getRandom().nextDouble() < 0.3) drop(level, entity, "infected_flesh", 1);
        }
    }

    private static void drop(ServerLevel level, net.minecraft.world.entity.LivingEntity entity, String id, int count) {
        for (int i = 0; i < count; i++) {
            var drop = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(FaunaEntities.item(id)));
            drop.setDefaultPickUpDelay();
            level.addFreshEntity(drop);
        }
    }
}
