package com.hp.thermalshockreforged.entity.ambient;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.*;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.neoforged.neoforge.registries.*;

public final class AmbientContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<PillbudEntity>> PILLBUD = TYPES.register("pillbud", () -> EntityType.Builder.of(PillbudEntity::new, MobCategory.AMBIENT).sized(0.8F, 0.8F).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:pillbud"));
    public static final DeferredHolder<EntityType<?>, EntityType<PixieEntity>> PIXIE = TYPES.register("pixie", () -> EntityType.Builder.of(PixieEntity::new, MobCategory.AMBIENT).fireImmune().sized(0.7F, 0.7F).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:pixie"));
    public static final DeferredHolder<EntityType<?>, EntityType<SootlingEntity>> SOOTLING = TYPES.register("sootling", () -> EntityType.Builder.of(SootlingEntity::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.8F).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:sootling"));
    public static final DeferredHolder<EntityType<?>, EntityType<PixieBottleEntity>> PIXIE_BOTTLE = TYPES.register("pixie_bottle", () -> EntityType.Builder.<PixieBottleEntity>of(PixieBottleEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(64).updateInterval(1).build("thermal_shock_reforged:pixie_bottle"));
    public static final DeferredItem<BottledPixieItem> BOTTLED_PIXIE = ITEMS.register("bottled_pixie", BottledPixieItem::new);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PRISMATIC_BOOM = PARTICLES.register("prismatic_boom", () -> new SimpleParticleType(false));
    static {
        ITEMS.register("pillbud_spawn_egg", () -> new DeferredSpawnEggItem(PILLBUD, -1, -1, new Item.Properties()));
        ITEMS.register("pixie_spawn_egg", () -> new DeferredSpawnEggItem(PIXIE, -1, -1, new Item.Properties()));
        ITEMS.register("sootling_spawn_egg", () -> new DeferredSpawnEggItem(SOOTLING, -1, -1, new Item.Properties()));
    }
    private AmbientContent() { }
    public static void register(IEventBus bus) {
        TYPES.register(bus); ITEMS.register(bus); PARTICLES.register(bus);
        bus.addListener(AmbientContent::attributes);
        bus.addListener(AmbientContent::placements);
        NeoForge.EVENT_BUS.addListener(AmbientContent::incomingDamage);
    }
    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(PILLBUD.get(), PillbudEntity.createAttributes().build());
        event.put(PIXIE.get(), PixieEntity.createAttributes().build());
        event.put(SOOTLING.get(), SootlingEntity.createAttributes().build());
    }
    private static void placements(RegisterSpawnPlacementsEvent event) {
        event.register(PILLBUD.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(PIXIE.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
    private static void incomingDamage(AttackAttemptEvent event) {
        if (event.getSource().getEntity() instanceof PillbudEntity pillbud && event.getEntity() instanceof net.minecraft.world.entity.player.Player) {
            event.setCanceled(true);
            pillbud.beginSkill(event.getEntity());
        } else if (event.getSource().getEntity() instanceof SootlingEntity sootling) {
            event.setCanceled(true);
            sootling.beginSkill(event.getEntity());
        }
    }
}
