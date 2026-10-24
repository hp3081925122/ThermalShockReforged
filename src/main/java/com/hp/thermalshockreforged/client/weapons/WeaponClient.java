package com.hp.thermalshockreforged.client.weapons;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.content.weapons.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.common.NeoForge;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;

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
        LiteprismLaserClient.extensions(event);
        ThistleBowClient.extensions(event);
        CoreEjectFlareGunClient.extensions(event);
        CarborGlClient.extensions(event);
        event.registerItem(new IClientItemExtensions() {
            @Override
            public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm,
                    ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
                pose.translate((arm == HumanoidArm.RIGHT ? 1 : -1) * 0.56F, -0.52F, -0.72F);
                if (player.getUseItem() == stack) pose.translate(0.05, 0.05, 0.05);
                return true;
            }
        }, WeaponContent.BLINDING_LANTERN.get());
        GeoRenderProviders.register(TsrRenderProvider.item(Renderer::new), WeaponContent.BLINDING_LANTERN.get());
        NeoForge.EVENT_BUS.addListener(WeaponClient::tick);
        NeoForge.EVENT_BUS.addListener(WeaponClient::logout);
    }

    private static void tick(ClientTickEvent.Post event) { WeaponContent.pruneAnimations(); }
    private static void logout(ClientPlayerNetworkEvent.LoggingOut event) { WeaponContent.clearAnimations(); }

    private static final class Renderer extends TsrGeoItemRenderer<BlindingLanternItem> {
        private Renderer() { super(new Model()); }
    }

    private static final class Model extends TsrGeoModel<BlindingLanternItem> {
        private static final Identifier MODEL = Identifier.fromNamespaceAndPath("thermal_shock_reforged", "geo/blinding_lantern.geo.json");
        private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("thermal_shock_reforged", "textures/item/blindinglantern.png");
        private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath("thermal_shock_reforged", "animations/blinding_lantern.animation.json");
        @Override public Identifier getModelResource(BlindingLanternItem item) { return MODEL; }
        @Override public Identifier getTextureResource(BlindingLanternItem item) { return TEXTURE; }
        @Override public Identifier getAnimationResource(BlindingLanternItem item) { return ANIMATION; }
    }
}
