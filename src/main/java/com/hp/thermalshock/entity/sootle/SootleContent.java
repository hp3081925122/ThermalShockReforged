package com.hp.thermalshock.entity.sootle;

import com.hp.thermalshock.ThermalShock;
import com.hp.thermalshock.combat.AttackAttemptEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class SootleContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShock.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShock.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<SootleEntity>> SOOTLE = TYPES.register("sootle", () -> EntityType.Builder.of(SootleEntity::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.8F).clientTrackingRange(64).updateInterval(3).build("thermal_shock:sootle"));
    public static final DeferredHolder<EntityType<?>, EntityType<SootleIdleEntity>> SOOTLE_IDLE = TYPES.register("sootle_idle", () -> EntityType.Builder.of(SootleIdleEntity::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.8F).clientTrackingRange(64).updateInterval(3).build("thermal_shock:sootle_idle"));
    static { ITEMS.register("sootle_spawn_egg", () -> new DeferredSpawnEggItem(SOOTLE, -1, -1, new Item.Properties())); }
    private SootleContent() { }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(SootleContent::attributes); NeoForge.EVENT_BUS.addListener(SootleContent::attackAttempt); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(SOOTLE.get(), AbstractSootleEntity.createAttributes().build()); event.put(SOOTLE_IDLE.get(), AbstractSootleEntity.createAttributes().build()); }
    private static void attackAttempt(AttackAttemptEvent event) {
        if (event.getSource().getEntity() instanceof SootleEntity active) { event.setCanceled(true); active.beginAttack(event.getEntity()); }
        else if (event.getSource().getEntity() instanceof SootleIdleEntity idle) { event.setCanceled(true); idle.beginConversion(10); }
    }
}
