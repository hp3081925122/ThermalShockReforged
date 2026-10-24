package com.hp.thermalshockreforged.client.weapons;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.weapons.SpearItem;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;

final class SpearClient {
    private SpearClient() { }
    static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
                pose.translate((arm == HumanoidArm.RIGHT ? 1 : -1) * .56F, -.52F, -.72F);
                if (player.getUseItem() == stack) pose.translate(.05, .05, .05);
                return true;
            }
        }, WeaponContent.SPELEOTHEM.get(), WeaponContent.RUBY_SPEAR.get(), WeaponContent.CARBORUNDUM_SPEAR.get(), WeaponContent.HI_VOLT_LANCE.get(), WeaponContent.BLOODSTONE_HALBERD.get(), WeaponContent.CARBORUNDUM_HALBERD.get());
        GeoRenderProviders.register(TsrRenderProvider.item(Renderer::new), WeaponContent.SPELEOTHEM.get(), WeaponContent.RUBY_SPEAR.get(), WeaponContent.CARBORUNDUM_SPEAR.get(), WeaponContent.HI_VOLT_LANCE.get(), WeaponContent.BLOODSTONE_HALBERD.get(), WeaponContent.CARBORUNDUM_HALBERD.get());
    }
    private static final class Renderer extends TsrGeoItemRenderer<SpearItem> {
        private Renderer() { super(new Model()); }
    }
    private static final class Model extends TsrGeoModel<SpearItem> {
        private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
        @Override public Identifier getModelResource(SpearItem item) { return resource("geo/" + item.kind().model + ".geo.json"); }
        @Override public Identifier getTextureResource(SpearItem item) { return resource("textures/item/" + item.kind().texture + ".png"); }
        @Override public Identifier getAnimationResource(SpearItem item) { return resource("animations/" + item.kind().model + ".animation.json"); }
    }
}
