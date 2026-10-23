package com.hp.thermalshockreforged.content.remainingutility;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RemainingUtilityContent {
    private static final DeferredRegister.Items ITEMS = com.hp.thermalshockreforged.ModRegistration.items(ThermalShockReforged.MOD_ID);
    public static final DeferredItem<DevCharacterToolItem> DEV_DELETOR = ITEMS.register("dev_deletor", () -> new DevCharacterToolItem(true));
    public static final DeferredItem<DevCharacterToolItem> DEV_ROTATOR = ITEMS.register("dev_rotator", () -> new DevCharacterToolItem(false));
    public static final DeferredItem<SnowMelterItem> HPSM = ITEMS.register("hpsm", SnowMelterItem::new);
    public static final DeferredItem<IgnitionDetectorItem> IGNITION_DETECTOR = ITEMS.register("ignition_detector", IgnitionDetectorItem::new);
    public static final DeferredItem<FixedStructureItem> MICRO_DOME = ITEMS.register("micro_dome", () -> new FixedStructureItem("shalespring_august", true));
    public static final DeferredItem<FixedStructureItem> MODEL_TRAIN = ITEMS.register("model_train", () -> new FixedStructureItem("shalespring_trainstation", false));
    public static final DeferredItem<SprintweedSodaItem> SPRINTWEED_SODA = ITEMS.register("sprintweed_soda", SprintweedSodaItem::new);

    private RemainingUtilityContent() { }

    public static void register(IEventBus bus) { ITEMS.register(bus); }
    static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    static SoundEvent sound(String path) { return BuiltInRegistries.SOUND_EVENT.getValue(id(path)); }
}
