package com.hp.thermalshockreforged.client.armor;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.armor.ArmorContent;
import com.hp.thermalshockreforged.content.armor.GeoSetArmorItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class ArmorClient {
    public static void extensions(RegisterClientExtensionsEvent event) {
        for (var entry : ArmorContent.ALL) event.registerItem(new Extension(), entry.get());
        NeoForge.EVENT_BUS.addListener(ArmorClient::logout);
    }

    public static void playerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide) return;
        if (event.getEntity() == Minecraft.getInstance().player) {
            for (var entry : ArmorContent.ALL) entry.get().pruneInstances(event.getEntity().level());
        }
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = event.getEntity().getItemBySlot(slot);
            if (!(stack.getItem() instanceof GeoSetArmorItem item)) continue;
            String animation = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString("geckoAnim");
            item.observeAnimation(event.getEntity(), stack, slot, animation);
        }
    }

    private static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
        for (var entry : ArmorContent.ALL) entry.get().clearInstances();
    }

    private static final class Extension implements IClientItemExtensions {
        private GeoArmorRenderer<GeoSetArmorItem> renderer;

        @Override
        public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
            if (renderer == null) renderer = new Renderer();
            renderer.prepForRender(entity, stack, slot, original);
            return renderer;
        }
    }

    private static final class Renderer extends GeoArmorRenderer<GeoSetArmorItem> {
        private Renderer() { super(new Model()); }

        @Override
        public long getInstanceId(GeoSetArmorItem item) {
            return item.renderInstance((LivingEntity) currentEntity, currentStack, currentSlot);
        }

        @Override
        public RenderType getRenderType(GeoSetArmorItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
            return RenderType.entityTranslucent(texture);
        }
    }

    private static final class Model extends GeoModel<GeoSetArmorItem> {
        private ResourceLocation resource(GeoSetArmorItem item, String prefix, String suffix) {
            return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, prefix + item.modelName() + suffix);
        }

        @Override public ResourceLocation getModelResource(GeoSetArmorItem item) { return resource(item, "geo/", ".geo.json"); }
        @Override public ResourceLocation getTextureResource(GeoSetArmorItem item) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/item/" + item.textureName() + ".png"); }
        @Override public ResourceLocation getAnimationResource(GeoSetArmorItem item) { return resource(item, "animations/", ".animation.json"); }
    }
}
