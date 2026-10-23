package com.hp.thermalshockreforged.client.armor;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.armor.ArmorContent;
import com.hp.thermalshockreforged.content.armor.GeoSetArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoArmorRenderer;

public final class ArmorClient {
    public static void extensions(RegisterClientExtensionsEvent event) {
        for (var entry : ArmorContent.ALL) event.registerItem(new Extension(), entry.get());
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

    private static final class Extension implements IClientItemExtensions {
        public Model getGenericArmorModel(ItemStack stack, EquipmentClientInfo.LayerType layerType, Model original) { return original; }
    }

    private static final class Renderer extends LegacyGeoArmorRenderer<GeoSetArmorItem> {
        private Renderer() { super(new Model()); }

        @Override
        public long getInstanceId(GeoSetArmorItem item) {
            return item.renderInstance((LivingEntity) currentEntity, currentStack, currentSlot);
        }

        @Override
        public RenderType getRenderType(GeoSetArmorItem item, Identifier texture, MultiBufferSource buffers, float partialTick) {
            return RenderTypes.entityTranslucent(texture);
        }
    }

    private static final class Model extends LegacyGeoModel<GeoSetArmorItem> {
        private Identifier resource(GeoSetArmorItem item, String prefix, String suffix) {
            return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, prefix + item.modelName() + suffix);
        }

        @Override public Identifier getModelResource(GeoSetArmorItem item) { return resource(item, "geo/", ".geo.json"); }
        @Override public Identifier getTextureResource(GeoSetArmorItem item) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/item/" + item.textureName() + ".png"); }
        @Override public Identifier getAnimationResource(GeoSetArmorItem item) { return resource(item, "animations/", ".animation.json"); }
    }
}
