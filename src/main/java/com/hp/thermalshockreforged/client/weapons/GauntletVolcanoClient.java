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

final class GauntletVolcanoClient {
    private GauntletVolcanoClient() { }
    static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new Extension(false), WeaponContent.TITANIUM_MACE.get());
        event.registerItem(new Extension(true), WeaponContent.VOLCANO.get());
    }
    private static final class Extension implements IClientItemExtensions {
        private final boolean volcano;
        private Extension(boolean volcano) { this.volcano = volcano; }

        @Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
            return volcano && !stack.isEmpty() && entity.getUsedItemHand() == hand ? ZapGauntletArmPose.POSE.getValue() : HumanoidModel.ArmPose.EMPTY;
        }
        @Override public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
            pose.translate((arm == HumanoidArm.RIGHT ? 1 : -1) * .56F, -.52F, -.72F);
            if (player.getUseItem() == stack) pose.translate(.05, .05, .05);
            return true;
        }
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class GauntletRenderer extends LegacyGeoItemRenderer<ZerkerGauntletItem> {
        private GauntletRenderer() { super(new GauntletModel()); }
        @Override public RenderType getRenderType(ZerkerGauntletItem item, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class VolcanoRenderer extends LegacyGeoItemRenderer<VolcanoItem> {
        private VolcanoRenderer() { super(new VolcanoModel()); }
        @Override public RenderType getRenderType(VolcanoItem item, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class GauntletModel extends LegacyGeoModel<ZerkerGauntletItem> {
        @Override public Identifier getModelResource(ZerkerGauntletItem item) { return resource("geo/titaniummace.geo.json"); }
        @Override public Identifier getTextureResource(ZerkerGauntletItem item) { return resource("textures/item/novazerk.png"); }
        @Override public Identifier getAnimationResource(ZerkerGauntletItem item) { return resource("animations/titaniummace.animation.json"); }
    }
    private static final class VolcanoModel extends LegacyGeoModel<VolcanoItem> {
        @Override public Identifier getModelResource(VolcanoItem item) { return resource("geo/volcano_rubicon.geo.json"); }
        @Override public Identifier getTextureResource(VolcanoItem item) { return resource("textures/item/volcano_rubicon.png"); }
        @Override public Identifier getAnimationResource(VolcanoItem item) { return resource("animations/volcano_rubicon.animation.json"); }
    }
}
