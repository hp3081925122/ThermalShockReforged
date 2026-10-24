package com.hp.thermalshockreforged.compat;

import com.geckolib.animatable.client.GeoRenderProvider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;

public final class GeoRenderProviders {
    private static final Map<Item, GeoRenderProvider> PROVIDERS = new ConcurrentHashMap<>();

    private GeoRenderProviders() { }

    public static void register(GeoRenderProvider provider, Item... items) {
        for (Item item : items) PROVIDERS.put(item, provider);
    }

    public static void accept(Item item, Consumer<GeoRenderProvider> consumer) {
        GeoRenderProvider provider = PROVIDERS.get(item);
        if (provider != null) consumer.accept(provider);
    }
}
