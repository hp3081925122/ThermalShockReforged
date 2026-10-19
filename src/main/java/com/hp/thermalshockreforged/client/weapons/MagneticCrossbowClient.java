package com.hp.thermalshockreforged.client.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.weapons.MagneticCrossbowItem;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

final class MagneticCrossbowClient {
    private MagneticCrossbowClient() { }
    static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private Renderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { if (renderer == null) renderer = new Renderer(); return renderer; }
            @Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
                return !stack.isEmpty() && entity.getUsedItemHand() == hand ? ZapGauntletArmPose.POSE.getValue() : HumanoidModel.ArmPose.EMPTY;
            }
            @Override public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
                pose.translate((arm == HumanoidArm.RIGHT ? 1 : -1) * .56F, -.52F, -.72F);
                if (player.getUseItem() == stack) pose.translate(.05, .05, .05);
                return true;
            }
        }, WeaponContent.MAGNETIC_CROSSBOW.get(), WeaponContent.ZENITH_CROSSBOW.get());
    }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends GeoItemRenderer<MagneticCrossbowItem> {
        private Renderer() { super(new Model()); }
        @Override public RenderType getRenderType(MagneticCrossbowItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Model extends GeoModel<MagneticCrossbowItem> {
        @Override public ResourceLocation getModelResource(MagneticCrossbowItem item) { return resource("geo/magnetic_crossbow.geo.json"); }
        @Override public ResourceLocation getTextureResource(MagneticCrossbowItem item) { return resource("textures/item/" + item.textureName() + ".png"); }
        @Override public ResourceLocation getAnimationResource(MagneticCrossbowItem item) { return resource("animations/magnetic_crossbow.animation.json"); }
    }
}
