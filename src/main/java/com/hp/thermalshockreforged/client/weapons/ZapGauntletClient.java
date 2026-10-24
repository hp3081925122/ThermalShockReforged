package com.hp.thermalshockreforged.client.weapons;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.weapons.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.*;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;

final class ZapGauntletClient {
    private ZapGauntletClient() { }
    static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
                return !stack.isEmpty() && entity.getUsedItemHand() == hand ? ZapGauntletArmPose.POSE.getValue() : HumanoidModel.ArmPose.EMPTY;
            }
            @Override public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
                pose.translate((arm == HumanoidArm.RIGHT ? 1 : -1) * .56F, -.52F, -.72F);
                if (player.getUseItem() == stack) pose.translate(.05, .05, .05);
                return true;
            }
        }, WeaponContent.ZAP_GAUNTLET.get());
        GeoRenderProviders.register(TsrRenderProvider.item(Renderer::new), WeaponContent.ZAP_GAUNTLET.get());
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends TsrGeoItemRenderer<ZapGauntletItem> {
        private Renderer() { super(new Model()); }
    }
    private static final class Model extends TsrGeoModel<ZapGauntletItem> {
        @Override public Identifier getModelResource(ZapGauntletItem item) { return resource("geo/zap_gauntlet.geo.json"); }
        @Override public Identifier getTextureResource(ZapGauntletItem item) { return resource("textures/item/zap_gauntlet_1.png"); }
        @Override public Identifier getAnimationResource(ZapGauntletItem item) { return resource("animations/zap_gauntlet.animation.json"); }
    }
}
