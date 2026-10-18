package com.hp.thermalshockreforged.entity.growth;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class GrowthEntities {
    private static final DeferredRegister<EntityType<?>> TYPES=DeferredRegister.create(Registries.ENTITY_TYPE,ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>,EntityType<GelshooterEntity>> GELSHOOTER=TYPES.register("gelshooter",()->EntityType.Builder.of(GelshooterEntity::new,MobCategory.MONSTER).sized(.7f,.7f).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build("thermal_shock_reforged:gelshooter"));
    public static final DeferredHolder<EntityType<?>,EntityType<GelshooterBulletEntity>> GELSHOOTER_BULLET=TYPES.register("gelshooter_bullet",()->EntityType.Builder.<GelshooterBulletEntity>of(GelshooterBulletEntity::new,MobCategory.MISC).sized(.5f,.5f).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(1).build("thermal_shock_reforged:gelshooter_bullet"));
    public static void register(IEventBus bus) { TYPES.register(bus); bus.addListener(GrowthEntities::attributes); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(GELSHOOTER.get(),GelshooterEntity.createAttributes().build()); }
}
