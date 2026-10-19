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

final class GauntletVolcanoClient {
    private GauntletVolcanoClient() { }
    static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new Extension(false), WeaponContent.TITANIUM_MACE.get());
        event.registerItem(new Extension(true), WeaponContent.VOLCANO.get());
    }
    private static final class Extension implements IClientItemExtensions {
        private final boolean volcano;
        private BlockEntityWithoutLevelRenderer renderer;
        private Extension(boolean volcano) { this.volcano = volcano; }
        @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            if (renderer == null) renderer = volcano ? new VolcanoRenderer() : new GauntletRenderer();
            return renderer;
        }
        @Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
            return volcano && !stack.isEmpty() && entity.getUsedItemHand() == hand ? ZapGauntletArmPose.POSE.getValue() : HumanoidModel.ArmPose.EMPTY;
        }
        @Override public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
            pose.translate((arm == HumanoidArm.RIGHT ? 1 : -1) * .56F, -.52F, -.72F);
            if (player.getUseItem() == stack) pose.translate(.05, .05, .05);
            return true;
        }
    }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class GauntletRenderer extends GeoItemRenderer<ZerkerGauntletItem> {
        private GauntletRenderer() { super(new GauntletModel()); }
        @Override public RenderType getRenderType(ZerkerGauntletItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class VolcanoRenderer extends GeoItemRenderer<VolcanoItem> {
        private VolcanoRenderer() { super(new VolcanoModel()); }
        @Override public RenderType getRenderType(VolcanoItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class GauntletModel extends GeoModel<ZerkerGauntletItem> {
        @Override public ResourceLocation getModelResource(ZerkerGauntletItem item) { return resource("geo/titaniummace.geo.json"); }
        @Override public ResourceLocation getTextureResource(ZerkerGauntletItem item) { return resource("textures/item/novazerk.png"); }
        @Override public ResourceLocation getAnimationResource(ZerkerGauntletItem item) { return resource("animations/titaniummace.animation.json"); }
    }
    private static final class VolcanoModel extends GeoModel<VolcanoItem> {
        @Override public ResourceLocation getModelResource(VolcanoItem item) { return resource("geo/volcano_rubicon.geo.json"); }
        @Override public ResourceLocation getTextureResource(VolcanoItem item) { return resource("textures/item/volcano_rubicon.png"); }
        @Override public ResourceLocation getAnimationResource(VolcanoItem item) { return resource("animations/volcano_rubicon.animation.json"); }
    }
}
