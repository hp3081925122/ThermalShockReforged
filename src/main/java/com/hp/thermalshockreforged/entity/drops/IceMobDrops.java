package com.hp.thermalshockreforged.entity.drops;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.hunters.HuntersContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public final class IceMobDrops {
    private static final TagKey<EntityType<?>> ICE = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "ice"));

    private IceMobDrops() { }

    public static void death(LivingDeathEvent event) {
        var entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level) || !level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) return;
        if (entity.getType() == HuntersContent.FROSTILYTE.get() && entity.getRandom().nextDouble() < 0.1) {
            var chip = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(MaterialContent.items().get("neural_chip").get()));
            chip.setPickUpDelay(10);
            level.addFreshEntity(chip);
        }
        if (entity.getType().is(ICE) && entity.getRandom().nextDouble() < 0.7) {
            var phobotite = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(MaterialContent.items().get("phobotite").get()));
            phobotite.setPickUpDelay(10);
            level.addFreshEntity(phobotite);
        }
    }
}
