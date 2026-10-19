package com.hp.thermalshockreforged.entity.huntress;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import com.hp.thermalshockreforged.content.npc.huntress.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class HuntressContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<HuntressBossEntity>> BOSS = TYPES.register("huntress_boss", () -> EntityType.Builder.of(HuntressBossEntity::new, MobCategory.MONSTER).sized(1.2F, 4.4F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(id("huntress_boss").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HuntressEntity>> NPC = TYPES.register("huntress", () -> EntityType.Builder.of(HuntressEntity::new, MobCategory.MONSTER).sized(1.2F, 4.4F).fireImmune().setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(id("huntress").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HuntressTaskEntity>> TASK = TYPES.register("huntress_task", () -> EntityType.Builder.<HuntressTaskEntity>of(HuntressTaskEntity::new, MobCategory.MISC).sized(.01F, .01F).noSummon().fireImmune().clientTrackingRange(0).updateInterval(20).build(id("huntress_task").toString()));
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static void register(IEventBus bus) { TYPES.register(bus); HuntressMenus.register(bus); bus.addListener(HuntressContent::attributes); NeoForge.EVENT_BUS.addListener(HuntressContent::attack); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(BOSS.get(), HuntressBossEntity.createAttributes().build()); event.put(NPC.get(), HuntressEntity.createAttributes().build()); }
    private static void attack(AttackAttemptEvent event) { if (!event.getEntity().level().isClientSide && event.getSource().getEntity() instanceof HuntressBossEntity owner) { event.setCanceled(true); owner.beginAttack(event.getEntity()); } }
    private HuntressContent() { }
}
