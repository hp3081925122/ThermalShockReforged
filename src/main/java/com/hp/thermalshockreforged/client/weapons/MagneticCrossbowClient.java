package com.hp.thermalshockreforged.client.weapons;
import com.geckolib.renderer.GeoItemRenderer;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.weapons.MagneticCrossbowItem;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.constant.DataTickets;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;

final class MagneticCrossbowClient {
    private MagneticCrossbowClient() { }
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
        }, WeaponContent.MAGNETIC_CROSSBOW.get(), WeaponContent.ZENITH_CROSSBOW.get());
        GeoRenderProviders.register(TsrRenderProvider.item(Renderer::new), WeaponContent.MAGNETIC_CROSSBOW.get(), WeaponContent.ZENITH_CROSSBOW.get());
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends TsrGeoItemRenderer<MagneticCrossbowItem> {
        private Renderer() { super(new Model()); }
    }
    private static final class Model extends TsrGeoModel<MagneticCrossbowItem> {
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) {
            if (!(state.getGeckolibData(GeoItemRenderer.CURRENT_ITEM) instanceof MagneticCrossbowItem item) || !item.isZenith()) return;
            var perspective = state.getGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE);
            if (perspective != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND && perspective != ItemDisplayContext.FIRST_PERSON_LEFT_HAND) return;
            bones.ifPresent("leftbowarm", bone -> bone.setScale(.75F, .75F, .75F));
            bones.ifPresent("rightbowarm", bone -> bone.setScale(.75F, .75F, .75F));
        }
        @Override public Identifier getModelResource(MagneticCrossbowItem item) { return resource("geo/magnetic_crossbow.geo.json"); }
        @Override public Identifier getTextureResource(MagneticCrossbowItem item) { return resource("textures/item/" + item.textureName() + ".png"); }
        @Override public Identifier getAnimationResource(MagneticCrossbowItem item) { return resource("animations/magnetic_crossbow.animation.json"); }
    }
}
