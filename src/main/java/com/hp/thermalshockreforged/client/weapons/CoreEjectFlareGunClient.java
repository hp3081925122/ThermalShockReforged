package com.hp.thermalshockreforged.client.weapons;
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
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoItemRenderer;

final class CoreEjectFlareGunClient {
    private CoreEjectFlareGunClient() { }
    static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private Renderer renderer;

            @Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
                return !stack.isEmpty() && entity.getUsedItemHand() == hand ? ZapGauntletArmPose.POSE.getValue() : HumanoidModel.ArmPose.EMPTY;
            }
            @Override public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
                pose.translate((arm == HumanoidArm.RIGHT ? 1 : -1) * .56F, -.52F, -.72F);
                if (player.getUseItem() == stack) pose.translate(.05, .05, .05);
                return true;
            }
        }, WeaponContent.CORE_EJECT_FLARE_GUN.get());
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends LegacyGeoItemRenderer<CoreEjectFlareGunItem> {
        private Renderer() { super(new Model()); }
        @Override public RenderType getRenderType(CoreEjectFlareGunItem item, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Model extends LegacyGeoModel<CoreEjectFlareGunItem> {
        @Override public Identifier getModelResource(CoreEjectFlareGunItem item) { return resource("geo/core_eject_flare.geo.json"); }
        @Override public Identifier getTextureResource(CoreEjectFlareGunItem item) { return resource("textures/item/coreflare.png"); }
        @Override public Identifier getAnimationResource(CoreEjectFlareGunItem item) { return resource("animations/core_eject_flare.animation.json"); }
    }
}
