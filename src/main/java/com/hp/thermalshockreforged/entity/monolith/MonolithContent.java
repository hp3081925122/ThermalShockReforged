package com.hp.thermalshockreforged.entity.monolith;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class MonolithContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<MonolithEntity>> ACTIVE = TYPES.register("monolith", () -> EntityType.Builder.of(MonolithEntity::new, MobCategory.MONSTER).sized(1.2F, 7.6F).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(128).updateInterval(3).build(id("monolith").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DormantMonolithEntity>> DORMANT = TYPES.register("dormant_monolith", () -> EntityType.Builder.of(DormantMonolithEntity::new, MobCategory.AMBIENT).sized(.8F, 7.6F).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(128).updateInterval(3).build(id("dormant_monolith").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<MonolithTaskEntity>> TASK = TYPES.register("monolith_task", () -> EntityType.Builder.<MonolithTaskEntity>of(MonolithTaskEntity::new, MobCategory.MISC).sized(.01F, .01F).noSummon().fireImmune().clientTrackingRange(0).updateInterval(20).build(id("monolith_task").toString()));
    static { ITEMS.register("dormant_monolith_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(DORMANT.get()))); }
    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(MonolithContent::attributes); NeoForge.EVENT_BUS.addListener(MonolithContent::attack); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(ACTIVE.get(), MonolithBaseEntity.createAttributes(false).build()); event.put(DORMANT.get(), MonolithBaseEntity.createAttributes(true).build()); }
    private static void attack(AttackAttemptEvent event) { if (!event.getEntity().level().isClientSide() && event.getSource().getEntity() instanceof MonolithEntity owner) { event.setCanceled(true); owner.beginAttack(event.getEntity()); } }
    private MonolithContent() { }
}
