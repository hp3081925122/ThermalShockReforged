package com.hp.thermalshockreforged.client.geo;

import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.GeoItemRenderer;
import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class TsrRenderProvider implements GeoRenderProvider {
    private final @Nullable Supplier<? extends GeoItemRenderer<?>> item;
    private final @Nullable Supplier<? extends GeoArmorRenderer<?, ?>> armor;

    private TsrRenderProvider(@Nullable Supplier<? extends GeoItemRenderer<?>> item, @Nullable Supplier<? extends GeoArmorRenderer<?, ?>> armor) {
        this.item = item == null ? null : Suppliers.memoize(item::get);
        this.armor = armor == null ? null : Suppliers.memoize(armor::get);
    }

    public static TsrRenderProvider item(Supplier<? extends GeoItemRenderer<?>> item) {
        return new TsrRenderProvider(item, null);
    }

    public static TsrRenderProvider armor(Supplier<? extends GeoArmorRenderer<?, ?>> armor) {
        return new TsrRenderProvider(null, armor);
    }

    @Override
    public @Nullable GeoItemRenderer<?> getGeoItemRenderer() {
        return item == null ? null : item.get();
    }

    @Override
    public @Nullable GeoArmorRenderer<?, ?> getGeoArmorRenderer(ItemStack itemStack, EquipmentSlot equipmentSlot) {
        return armor == null ? null : armor.get();
    }
}
