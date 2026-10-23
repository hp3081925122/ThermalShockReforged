package com.hp.thermalshockreforged.entity.beetles;
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

public final class BeetleContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<BeetleCrasherEntity>> CRASHER = TYPES.register("beetle_crasher", () -> EntityType.Builder.of(BeetleCrasherEntity::new, MobCategory.MONSTER).sized(2, 1.8F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("beetle_crasher")));
    public static final DeferredHolder<EntityType<?>, EntityType<BeetleThrasherEntity>> THRASHER = TYPES.register("beetle_thrasher", () -> EntityType.Builder.of(BeetleThrasherEntity::new, MobCategory.MONSTER).sized(.8F, 2.3F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("beetle_thrasher")));
    public static final DeferredHolder<EntityType<?>, EntityType<DormantBeetleCrasherEntity>> DORMANT = TYPES.register("dormant_beetle_crasher", () -> EntityType.Builder.of(DormantBeetleCrasherEntity::new, MobCategory.AMBIENT).sized(0, 0).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("dormant_beetle_crasher")));
    public static final DeferredHolder<EntityType<?>, EntityType<BeetleAttackEntity>> ATTACK = TYPES.register("beetle_attack", () -> EntityType.Builder.<BeetleAttackEntity>of(BeetleAttackEntity::new, MobCategory.MISC).sized(.01F, .01F).noSummon().fireImmune().clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("beetle_attack")));
    static {
        ITEMS.register("beetle_crasher_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(CRASHER.get())));
        ITEMS.register("beetle_thrasher_spawn_egg", () -> new SpawnEggItem(com.hp.thermalshockreforged.ModRegistration.item().spawnEgg(THRASHER.get())));
    }
    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(BeetleContent::attributes); NeoForge.EVENT_BUS.addListener(BeetleContent::attack); }
    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(CRASHER.get(), BeetleCombatEntity.createAttributes(false).build());
        event.put(THRASHER.get(), BeetleCombatEntity.createAttributes(true).build());
        event.put(DORMANT.get(), DormantBeetleCrasherEntity.createAttributes().build());
    }
    private static void attack(AttackAttemptEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getSource().getEntity() instanceof BeetleCombatEntity owner) { event.setCanceled(true); owner.beginAttack(event.getEntity()); }
    }
    private BeetleContent() { }
}
