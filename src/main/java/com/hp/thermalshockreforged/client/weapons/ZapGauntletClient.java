package com.hp.thermalshockreforged.client.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.weapons.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.*;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

final class ZapGauntletClient {
    private ZapGauntletClient() { }
    static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private Renderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new Renderer();
                return renderer;
            }
            @Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
                return !stack.isEmpty() && entity.getUsedItemHand() == hand ? ZapGauntletArmPose.POSE.getValue() : HumanoidModel.ArmPose.EMPTY;
            }
            @Override public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
                pose.translate((arm == HumanoidArm.RIGHT ? 1 : -1) * .56F, -.52F, -.72F);
                if (player.getUseItem() == stack) pose.translate(.05, .05, .05);
                return true;
            }
        }, WeaponContent.ZAP_GAUNTLET.get());
    }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends GeoItemRenderer<ZapGauntletItem> {
        private Renderer() { super(new Model()); }
        @Override public RenderType getRenderType(ZapGauntletItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Model extends GeoModel<ZapGauntletItem> {
        @Override public ResourceLocation getModelResource(ZapGauntletItem item) { return resource("geo/zap_gauntlet.geo.json"); }
        @Override public ResourceLocation getTextureResource(ZapGauntletItem item) { return resource("textures/item/zap_gauntlet_1.png"); }
        @Override public ResourceLocation getAnimationResource(ZapGauntletItem item) { return resource("animations/zap_gauntlet.animation.json"); }
    }
}
