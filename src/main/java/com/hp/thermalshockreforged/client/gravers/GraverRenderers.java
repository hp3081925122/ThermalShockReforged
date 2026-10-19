package com.hp.thermalshockreforged.client.gravers;

import com.hp.thermalshockreforged.entity.gravers.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public final class GraverRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GraverContent.GRAVER.get(), context -> new Renderer<>(context, "chorpse", .5F, null));
        event.registerEntityRenderer(GraverContent.NEON.get(), context -> new Renderer<>(context, "lime_graver", .5F, "neon_lime_graver"));
        event.registerEntityRenderer(GraverContent.ASSIMILATOR.get(), context -> new Renderer<>(context, "infected_snobol", .5F, null));
        event.registerEntityRenderer(GraverContent.ASSIMILATED.get(), context -> new Renderer<>(context, "infected_graver", .5F, null));
        event.registerEntityRenderer(GraverContent.RAZORAPTOR.get(), context -> new Renderer<>(context, "infected_razorwing", .7F, "razoraptroglow"));
        event.registerEntityRenderer(GraverContent.STRIKE.get(), NoopRenderer::new);
    }
    private static final class Model<T extends GraverFamilyEntity> extends GeoModel<T> {
        private final String name;
        private Model(String name) { this.name = name; }
        @Override public ResourceLocation getModelResource(T entity) { return GraverContent.id("geo/" + name + ".geo.json"); }
        @Override public ResourceLocation getAnimationResource(T entity) { return GraverContent.id("animations/" + name + ".animation.json"); }
        @Override public ResourceLocation getTextureResource(T entity) { return GraverContent.id("textures/entities/" + entity.texture() + ".png"); }
        @Override public void setCustomAnimations(T entity, long instance, AnimationState<T> state) {
            super.setCustomAnimations(entity, instance, state);
            var head = getAnimationProcessor().getBone("head"); var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (head != null && data != null) { head.setRotX(data.headPitch() * Mth.DEG_TO_RAD); head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Renderer<T extends GraverFamilyEntity> extends GeoEntityRenderer<T> {
        private Renderer(EntityRendererProvider.Context context, String model, float shadow, String glow) { super(context, new Model<>(model)); shadowRadius = shadow; if (glow != null) addRenderLayer(new Glow<>(this, glow)); }
        @Override public RenderType getRenderType(T entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
    private static final class Glow<T extends GraverFamilyEntity> extends GeoRenderLayer<T> {
        private final ResourceLocation texture;
        private Glow(Renderer<T> renderer, String texture) { super(renderer); this.texture = GraverContent.id("textures/entities/" + texture + ".png"); }
        @Override public void render(PoseStack pose, T entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
            var glow = RenderType.eyes(texture);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
    private GraverRenderers() { }
}
