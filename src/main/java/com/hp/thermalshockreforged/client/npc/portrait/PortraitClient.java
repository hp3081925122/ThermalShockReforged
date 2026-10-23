package com.hp.thermalshockreforged.client.npc.portrait;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.portrait.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.*;
import com.geckolib.animation.state.AnimationTest;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class PortraitClient {
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(PortraitContent.PORTRAIT.get(), Renderer::new); }
    public static void screens(RegisterMenuScreensEvent event) { event.register(PortraitContent.menu(false), PortraitScreen::new); event.register(PortraitContent.menu(true), PortraitScreen::new); }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Renderer extends LegacyGeoEntityRenderer<PortraitCharacterEntity> {
        Renderer(EntityRendererProvider.Context context) { super(context, new Model()); shadowRadius = 0; withScale(1); }
        @Override public RenderType getRenderType(PortraitCharacterEntity entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
    private static final class Model extends LegacyGeoModel<PortraitCharacterEntity> {
        @Override public Identifier getModelResource(PortraitCharacterEntity entity) { return id("geo/portrait_entity.geo.json"); }
        @Override public Identifier getAnimationResource(PortraitCharacterEntity entity) { return id("animations/portrait_entity.animation.json"); }
        @Override public Identifier getTextureResource(PortraitCharacterEntity entity) { return id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(PortraitCharacterEntity entity, long instanceId, AnimationTest<PortraitCharacterEntity> state) {
            super.setCustomAnimations(entity, instanceId, state);
            var head = getAnimationProcessor().getBone("head"); var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
}
