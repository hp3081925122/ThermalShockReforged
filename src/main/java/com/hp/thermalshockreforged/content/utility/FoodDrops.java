package com.hp.thermalshockreforged.content.utility;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

final class FoodDrops {
    static void death(LivingDropsEvent event) {
        var entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level) || !level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) return;
        var type = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (!type.getNamespace().equals(ThermalShockReforged.MOD_ID)) return;
        Item item;
        double chance;
        switch (type.getPath()) {
            case "effigy" -> { item = UtilityContent.SNOWDAE.get(); chance = 0.007; }
            case "magnetite" -> { item = UtilityContent.LOQUESICLE.get(); chance = 0.01; }
            case "beetle_thrasher" -> { item = UtilityContent.BLOOD_POP.get(); chance = 0.01; }
            case "lens" -> { item = UtilityContent.CHALK_CANDY.get(); chance = 0.01; }
            case "bonerahna" -> { item = UtilityContent.MUCK_PIE.get(); chance = 0.004; }
            case "spectre" -> { item = UtilityContent.LUCK_TICKET.get(); chance = 0.004; }
            default -> { return; }
        }
        String committed = ThermalShockReforged.MOD_ID + ":food_drop_committed";
        if (entity.getPersistentData().getBoolean(committed)) return;
        entity.getPersistentData().putBoolean(committed, true);
        if (entity.getRandom().nextDouble() < chance) {
            ItemEntity drop = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(item));
            drop.setDefaultPickUpDelay();
            event.getDrops().add(drop);
        }
    }
}
