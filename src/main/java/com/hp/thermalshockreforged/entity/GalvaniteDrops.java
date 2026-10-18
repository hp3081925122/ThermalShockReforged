package com.hp.thermalshockreforged.entity;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.world.WorldProgressData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;

public final class GalvaniteDrops {
    private GalvaniteDrops() { }
    public static void drop(LivingEntity entity, ServerLevel level) {
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS) || WorldProgressData.get(level).worldCrux() < 1) return;
        for (int i = 0; i < 2; i++) {
            if (entity.getRandom().nextDouble() >= .2) continue;
            var cell = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(MaterialContent.items().get("bioelectric_cell").get()));
            cell.setPickUpDelay(10);
            level.addFreshEntity(cell);
        }
    }
}
