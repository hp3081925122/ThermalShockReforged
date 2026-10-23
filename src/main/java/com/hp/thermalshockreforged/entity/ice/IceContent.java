package com.hp.thermalshockreforged.entity.ice;
import net.minecraft.world.item.SpawnEggItem;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.registries.*;

public final class IceContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<IceCreatureEntity>> FROSTILYTE_SOLDIER = creature("frostilyte_soldier", 0.6F, 1.7F);
    public static final DeferredHolder<EntityType<?>, EntityType<IceCreatureEntity>> FROSTILYTE_CRYOMANCER = creature("frostilyte_cryomancer", 0.6F, 1.7F);
    public static final DeferredHolder<EntityType<?>, EntityType<IceCreatureEntity>> ICIDRONE = creature("icidrone", 0.8F, 0.8F);
    public static final DeferredHolder<EntityType<?>, EntityType<IceCreatureEntity>> ICIDROZERK = creature("icidrozerk", 1.8F, 2.2F);
    public static final DeferredHolder<EntityType<?>, EntityType<IceBulletEntity>> ICE_BULLET = TYPES.register("ice_bullet", () -> EntityType.Builder.<IceBulletEntity>of(IceBulletEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(64).updateInterval(1).build(ThermalShockReforged.MOD_ID + ":ice_bullet"));
    public static final DeferredHolder<EntityType<?>, EntityType<SoldierComboEntity>> SOLDIER_COMBO = TYPES.register("soldier_combo_task", () -> EntityType.Builder.<SoldierComboEntity>of(SoldierComboEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(ThermalShockReforged.MOD_ID + ":soldier_combo_task"));
    static {
        ITEMS.register("frostilyte_soldier_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(FROSTILYTE_SOLDIER.get())));
        ITEMS.register("frostilyte_cryomancer_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(FROSTILYTE_CRYOMANCER.get())));
        ITEMS.register("icidrone_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(ICIDRONE.get())));
        ITEMS.register("icidrozerk_spawn_egg", () -> new SpawnEggItem(new Item.Properties().spawnEgg(ICIDROZERK.get())));
    }
    private IceContent() { }
    private static DeferredHolder<EntityType<?>, EntityType<IceCreatureEntity>> creature(String id, float width, float height) {
        return TYPES.register(id, () -> EntityType.Builder.of(IceCreatureEntity::new, MobCategory.MONSTER).sized(width, height).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(ThermalShockReforged.MOD_ID + ":" + id));
    }
    public static void register(IEventBus bus) { TYPES.register(bus); ITEMS.register(bus); bus.addListener(IceContent::attributes); NeoForge.EVENT_BUS.addListener(IceContent::attack); NeoForge.EVENT_BUS.addListener(IceContent::join); }
    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(FROSTILYTE_SOLDIER.get(), IceCreatureEntity.createAttributes(0).build());
        event.put(FROSTILYTE_CRYOMANCER.get(), IceCreatureEntity.createAttributes(1).build());
        event.put(ICIDRONE.get(), IceCreatureEntity.createAttributes(2).build());
        event.put(ICIDROZERK.get(), IceCreatureEntity.createAttributes(3).build());
    }
    private static void attack(AttackAttemptEvent event) { if (event.getSource().getEntity() instanceof IceCreatureEntity caster && caster.attackAttempt(event.getEntity())) event.setCanceled(true); }
    private static void join(EntityJoinLevelEvent event) { if (event.getLevel() instanceof ServerLevel && event.getEntity() instanceof IceCreatureEntity creature) creature.applyConfiguredAttributes(); }
}
