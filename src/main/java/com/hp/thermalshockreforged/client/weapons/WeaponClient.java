package com.hp.thermalshockreforged.client.weapons;

import com.hp.thermalshockreforged.content.weapons.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.common.NeoForge;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public final class WeaponClient {
    private WeaponClient() { }

    public static void extensions(RegisterClientExtensionsEvent event) {
        ZapGauntletClient.extensions(event);
        FirearmClient.extensions(event);
        MagneticCrossbowClient.extensions(event);
        SpearClient.extensions(event);
        GauntletVolcanoClient.extensions(event);
        ThermoblasterClient.extensions(event);
        BuzzsawClient.extensions(event);
        event.registerItem(new IClientItemExtensions() {
            private Renderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new Renderer();
                return renderer;
            }
            @Override
            public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm,
                    ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
                pose.translate((arm == HumanoidArm.RIGHT ? 1 : -1) * 0.56F, -0.52F, -0.72F);
                if (player.getUseItem() == stack) pose.translate(0.05, 0.05, 0.05);
                return true;
            }
        }, WeaponContent.BLINDING_LANTERN.get());
        NeoForge.EVENT_BUS.addListener(WeaponClient::tick);
        NeoForge.EVENT_BUS.addListener(WeaponClient::logout);
    }

    private static void tick(ClientTickEvent.Post event) { WeaponContent.pruneAnimations(); }
    private static void logout(ClientPlayerNetworkEvent.LoggingOut event) { WeaponContent.clearAnimations(); }

    private static final class Renderer extends GeoItemRenderer<BlindingLanternItem> {
        private Renderer() { super(new Model()); }
        @Override
        public RenderType getRenderType(BlindingLanternItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
            return RenderType.entityTranslucent(texture);
        }
    }

    private static final class Model extends GeoModel<BlindingLanternItem> {
        private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "geo/blinding_lantern.geo.json");
        private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "textures/item/blindinglantern.png");
        private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "animations/blinding_lantern.animation.json");
        @Override public ResourceLocation getModelResource(BlindingLanternItem item) { return MODEL; }
        @Override public ResourceLocation getTextureResource(BlindingLanternItem item) { return TEXTURE; }
        @Override public ResourceLocation getAnimationResource(BlindingLanternItem item) { return ANIMATION; }
    }
}
