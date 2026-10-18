package com.hp.thermalshockreforged.entity.infestation;

import com.hp.thermalshockreforged.content.MaterialContent;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

final class GraveyardDrops {
    private static final TagKey<EntityType<?>> GRAVEYARD = tag("graveyard_mobs");
    private static final TagKey<EntityType<?>> SKELETITAN = tag("skeletitan");
    private static final TagKey<EntityType<?>> UNDEAD = tag("undead_mobs");
    private static final java.util.Set<String> FLESH = java.util.Set.of("razoraptor", "mangler", "flare");

    private GraveyardDrops() { }

    private static TagKey<EntityType<?>> tag(String path) { return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", path)); }

    static void death(LivingDeathEvent event) {
        var entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level) || !level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) return;
        var random = entity.getRandom();
        if (entity.getType().is(GRAVEYARD) && random.nextDouble() < 0.7) {
            int sample = random.nextInt(4096);
            int count = sample < 512 ? 1 : sample < 1856 ? 2 : sample < 3256 ? 3 : sample < 3991 ? 4 : 5;
            drop(level, entity, "infected_seeds", count);
        }
        if (entity.getType().is(SKELETITAN) && random.nextDouble() < 0.5) drop(level, entity, "ashen_bone", random.nextBoolean() ? 1 : 2);
        if (entity.getType().is(UNDEAD) && random.nextDouble() < 0.1) drop(level, entity, "tainted_energy", 1);
        var id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (id.getNamespace().equals("thermal_shock_reforged") && FLESH.contains(id.getPath()) && random.nextDouble() < 0.3) drop(level, entity, "infected_flesh", 1);
    }

    private static void drop(ServerLevel level, LivingEntity entity, String id, int count) {
        for (int i = 0; i < count; i++) {
            var drop = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(MaterialContent.items().get(id).get()));
            drop.setDefaultPickUpDelay();
            level.addFreshEntity(drop);
        }
    }
}
