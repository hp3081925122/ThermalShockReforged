package com.hp.thermalshockreforged.entity.sopel;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.*;

public final class SopelEntityContent {
    static final String OWNER_COOLDOWN = "thermal_shock_reforged:SopelOwnerCooldown";
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<HighDirectorSopelEntity>> SOPEL = TYPES.register("high_director_sopel", () -> EntityType.Builder.of(HighDirectorSopelEntity::new, MobCategory.MONSTER).sized(.7F, 3).setShouldReceiveVelocityUpdates(true).clientTrackingRange(64).updateInterval(3).build(id("high_director_sopel").toString()));
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    public static void register(IEventBus bus) { TYPES.register(bus); bus.addListener(SopelEntityContent::attributes); NeoForge.EVENT_BUS.addListener(SopelEntityContent::attack); NeoForge.EVENT_BUS.addListener(SopelEntityContent::tickOwnerCooldown); }
    private static void attributes(EntityAttributeCreationEvent event) { event.put(SOPEL.get(), HighDirectorSopelEntity.createAttributes().build()); }
    private static void attack(AttackAttemptEvent event) {
        var owner = event.getSource().getEntity();
        if (owner != null && event.getSource().getDirectEntity() instanceof HighDirectorSopelEntity direct) {
            event.setCanceled(true);
            if (!event.getEntity().level().isClientSide) direct.displace(event.getEntity(), owner);
        }
    }
    private static void tickOwnerCooldown(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof LivingEntity living && !(living instanceof HighDirectorSopelEntity) && !living.level().isClientSide) {
            var data = living.getPersistentData();
            if (data.contains(OWNER_COOLDOWN)) { int remaining = Math.clamp(data.getInt(OWNER_COOLDOWN), 0, 72000) - 1; if (remaining > 0) data.putInt(OWNER_COOLDOWN, remaining); else data.remove(OWNER_COOLDOWN); }
        }
    }
    public static boolean clearOwnerCooldown(LivingEntity living) { var data = living.getPersistentData(); if (living.level().isClientSide || !data.contains(OWNER_COOLDOWN)) return false; data.remove(OWNER_COOLDOWN); return true; }
    private SopelEntityContent() { }
}
