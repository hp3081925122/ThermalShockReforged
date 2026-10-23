package com.hp.thermalshockreforged.entity.giantlaser;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class GiantLaserTestContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<GiantLaserTestEntity>> GIANT_LASER = TYPES.register("giant_laser_test", () -> EntityType.Builder.of(GiantLaserTestEntity::new, MobCategory.MONSTER).sized(3, 10).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(512).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("giant_laser_test")));
    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static void register(IEventBus bus) { TYPES.register(bus); bus.addListener(GiantLaserTestContent::attributes); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(GIANT_LASER.get(), GiantLaserTestEntity.createAttributes().build()); }
    private GiantLaserTestContent() { }
}
