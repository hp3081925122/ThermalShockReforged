package com.hp.thermalshockreforged.content.music;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MusicContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThermalShockReforged.MOD_ID);
    public static final ResourceKey<JukeboxSong> AZURE_GREY_SONG = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "azure_grey"));
    public static final DeferredItem<Item> AZURE_GREY = ITEMS.register("azure_grey", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AZURE_GREY_SONG)));
    public static final ResourceKey<JukeboxSong> PHOBOS_LAMENT_SONG = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "phobos_lament"));
    public static final DeferredItem<Item> PHOBOS_LAMENT = ITEMS.register("phobos_lament", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(PHOBOS_LAMENT_SONG)));
    public static final ResourceKey<JukeboxSong> PETRICHOR_SONG = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "petrichor"));
    public static final DeferredItem<Item> PETRICHOR = ITEMS.register("petrichor", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(PETRICHOR_SONG)));
    public static final ResourceKey<JukeboxSong> TRAMPLING_FLOWERS_SONG = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "trampling_flowers"));
    public static final DeferredItem<Item> TRAMPLING_FLOWERS = ITEMS.register("trampling_flowers", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(TRAMPLING_FLOWERS_SONG)));
    public static final ResourceKey<JukeboxSong> FLOOR_33_SONG = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "floor_33"));
    public static final DeferredItem<Item> FLOOR_33 = ITEMS.register("floor_33", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(FLOOR_33_SONG)));
    public static final ResourceKey<JukeboxSong> HANAHAKI_SONG = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "hanahaki"));
    public static final DeferredItem<Item> HANAHAKI = ITEMS.register("hanahaki", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(HANAHAKI_SONG)));
    private MusicContent() { }
    public static void register(IEventBus bus) { ITEMS.register(bus); }
}
