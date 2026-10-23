package com.hp.thermalshockreforged.entity.springslime;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class SpringslimeContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<SpringslimeEntity>> SPRING = TYPES.register("springslime", () -> EntityType.Builder.of(SpringslimeEntity::new, MobCategory.AMBIENT).sized(2.5F, 1).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("springslime")));
    public static final DeferredHolder<EntityType<?>, EntityType<SpringslimeProjectile>> PROJECTILE = TYPES.register("springslime_projectile", () -> EntityType.Builder.<SpringslimeProjectile>of(SpringslimeProjectile::new, MobCategory.MISC).sized(.5F, .5F).clientTrackingRange(64).updateInterval(1).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("springslime_projectile")));
    public static final DeferredHolder<EntityType<?>, EntityType<SpringFadeEntity>> FADE = TYPES.register("springslime_fade", () -> EntityType.Builder.of(SpringFadeEntity::new, MobCategory.MISC).sized(.01F, .01F).noSummon().fireImmune().clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("springslime_fade")));
    public static final DeferredItem<Item> COMPRESSED = ITEMS.register("compressed_springslime", CompressedSpringslimeItem::new);
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(SpringslimeContent::attributes); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(SPRING.get(), SpringslimeEntity.createAttributes().build()); }
}
