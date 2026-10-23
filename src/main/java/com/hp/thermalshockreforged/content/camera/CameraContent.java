package com.hp.thermalshockreforged.content.camera;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class CameraContent {
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredItem<CameraItem> CAMERA = ITEMS.register("e_xpolar_camera", () -> new CameraItem(true));
    public static final DeferredItem<CameraItem> WIDE_ANGLE = ITEMS.register("wide_angle", () -> new CameraItem(false));
    public static final DeferredItem<QuestLootBundleItem> BUNDLE = ITEMS.register("leaf_it_to_me_loot_bundle", QuestLootBundleItem::new);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CAM_PHOTO = PARTICLES.register("cam_photo", () -> new SimpleParticleType(true));
    public static void register(IEventBus bus) { ITEMS.register(bus); PARTICLES.register(bus); }
}
