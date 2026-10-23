package com.hp.thermalshockreforged.entity.ribbonblade;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.registries.*;

public final class RibbonbladeContent {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<RibbonbladeGalvaniteEntity>> RIBBONBLADE = TYPES.register("ribbonblade_galvanite", () -> EntityType.Builder.of(RibbonbladeGalvaniteEntity::new, MobCategory.MONSTER).sized(.6F, 2F).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("ribbonblade_galvanite")));
    public static final DeferredHolder<EntityType<?>, EntityType<RibbonbladeActionEntity>> ACTION = TYPES.register("ribbonblade_action_task", () -> EntityType.Builder.<RibbonbladeActionEntity>of(RibbonbladeActionEntity::new, MobCategory.MISC).noSummon().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(com.hp.thermalshockreforged.ThermalShockReforged.entityKey("ribbonblade_action_task")));
    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static void register(IEventBus bus) { TYPES.register(bus); bus.addListener(RibbonbladeContent::attributes); NeoForge.EVENT_BUS.addListener(RibbonbladeContent::attack); NeoForge.EVENT_BUS.addListener(RibbonbladeContent::join); NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, RibbonbladeContent::damage); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(RIBBONBLADE.get(), RibbonbladeGalvaniteEntity.attributes().build()); }
    private static void join(EntityJoinLevelEvent event) { if (event.getLevel() instanceof ServerLevel && event.getEntity() instanceof RibbonbladeGalvaniteEntity entity) entity.applyConfiguredAttributes(); }
    private static void attack(AttackAttemptEvent event) { if (event.getSource().getEntity() instanceof RibbonbladeGalvaniteEntity caster) { event.setCanceled(true); caster.beginAttack(event.getEntity()); } }
    private static void damage(LivingDamageEvent.Pre event) { if (event.getEntity() instanceof RibbonbladeGalvaniteEntity caster && Float.isFinite(event.getNewDamage()) && event.getNewDamage() > caster.getAbsorptionAmount()) caster.reactToHit(); }
    private RibbonbladeContent() { }
}
