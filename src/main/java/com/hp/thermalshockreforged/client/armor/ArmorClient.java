package com.hp.thermalshockreforged.client.armor;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;


import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.armor.ArmorContent;
import com.hp.thermalshockreforged.content.armor.GeoSetArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class ArmorClient {
    public static void extensions(RegisterClientExtensionsEvent event) {
        var provider = TsrRenderProvider.armor(Renderer::new);
        for (var entry : ArmorContent.ALL) GeoRenderProviders.register(provider, entry.get());
        NeoForge.EVENT_BUS.addListener(ArmorClient::logout);
    }

    public static void playerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide()) return;
        if (event.getEntity() == Minecraft.getInstance().player) {
            for (var entry : ArmorContent.ALL) entry.get().pruneInstances(event.getEntity().level());
        }
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = event.getEntity().getItemBySlot(slot);
            if (!(stack.getItem() instanceof GeoSetArmorItem item)) continue;
            String animation = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString("geckoAnim").orElse("");
            item.observeAnimation(event.getEntity(), stack, slot, animation);
        }
    }

    private static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
        for (var entry : ArmorContent.ALL) entry.get().clearInstances();
    }

    private static final class Renderer extends TsrGeoArmorRenderer<GeoSetArmorItem> {
        private Renderer() { super(new Model()); }

        @Override
        public long getInstanceId(GeoSetArmorItem item, RenderData data) {
            return item.renderInstance(data.entity(), data.itemStack(), data.slot());
        }
    }

    private static final class Model extends TsrGeoModel<GeoSetArmorItem> {
        private Identifier resource(GeoSetArmorItem item, String prefix, String suffix) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, prefix + item.modelName() + suffix);
        }

        @Override public Identifier getModelResource(GeoSetArmorItem item) { return resource(item, "geo/", ".geo.json"); }
        @Override public Identifier getTextureResource(GeoSetArmorItem item) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/item/" + item.textureName() + ".png"); }
        @Override public Identifier getAnimationResource(GeoSetArmorItem item) { return resource(item, "animations/", ".animation.json"); }
    }
}
