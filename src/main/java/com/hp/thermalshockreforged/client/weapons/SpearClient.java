package com.hp.thermalshockreforged.client.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.weapons.SpearItem;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

final class SpearClient {
    private SpearClient() { }
    static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private Renderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { if (renderer == null) renderer = new Renderer(); return renderer; }
            @Override public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
                pose.translate((arm == HumanoidArm.RIGHT ? 1 : -1) * .56F, -.52F, -.72F);
                if (player.getUseItem() == stack) pose.translate(.05, .05, .05);
                return true;
            }
        }, WeaponContent.SPELEOTHEM.get(), WeaponContent.RUBY_SPEAR.get(), WeaponContent.CARBORUNDUM_SPEAR.get(), WeaponContent.HI_VOLT_LANCE.get());
    }
    private static final class Renderer extends GeoItemRenderer<SpearItem> {
        private Renderer() { super(new Model()); }
        @Override public RenderType getRenderType(SpearItem item, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Model extends GeoModel<SpearItem> {
        private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
        @Override public ResourceLocation getModelResource(SpearItem item) { return resource("geo/" + item.kind().model + ".geo.json"); }
        @Override public ResourceLocation getTextureResource(SpearItem item) { return resource("textures/item/" + item.kind().texture + ".png"); }
        @Override public ResourceLocation getAnimationResource(SpearItem item) { return resource("animations/" + item.kind().model + ".animation.json"); }
    }
}
