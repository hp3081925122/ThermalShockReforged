package com.hp.thermalshock.client.fauna;

import com.hp.thermalshock.entity.fauna.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.*;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import java.util.function.Function;

public final class FaunaRenderers {
    private FaunaRenderers() { }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(FaunaEntities.BONERAHNA.get(), factory("bonerahna", BonerahnaEntity::texture, false, 0.2F, 1, false));
        event.registerEntityRenderer(FaunaEntities.CHROMEFISH.get(), factory("chromefish", ChromefishEntity::texture, true, 0.2F, 1, false));
        event.registerEntityRenderer(FaunaEntities.TORPEDO.get(), factory("torpedo", TorpedoEntity::texture, false, 0.4F, 1.4F, true));
        event.registerEntityRenderer(FaunaEntities.TRILOBITE.get(), factory("trilobit", TrilobiteEntity::texture, true, 0.7F, 1, false));
        event.registerEntityRenderer(FaunaEntities.WILLOGRADE.get(), factory("roly_poly", WillogradeEntity::texture, false, 1, 1, false));
        event.registerEntityRenderer(FaunaEntities.FLARE.get(), factory("flare25", FlareEntity::texture, true, 0.3F, 1, false));
        event.registerEntityRenderer(FaunaEntities.GORE_FLAYER_ORB.get(), net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
        event.registerBlockEntityRenderer(FaunaEntities.CAULDRON_ENTITY.get(), context -> new CauldronRenderer());
    }

    public static void particles(net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(FaunaEntities.WHITE_ORB.get(), WhiteOrbParticle.Provider::new);
    }

    public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private CauldronItemRenderer renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new CauldronItemRenderer();
                return renderer;
            }
        }, FaunaEntities.CAULDRON_ITEM.get());
    }

    private static <T extends Mob & GeoEntity> EntityRendererProvider<T> factory(String id, Function<T, String> texture, boolean head, float shadow, float scale, boolean glow) {
        return context -> new Renderer<>(context, new Model<>(id, texture, head), shadow, scale, glow);
    }

    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath("thermal_shock", path); }

    private static final class Model<T extends Mob & GeoEntity> extends GeoModel<T> {
        private final String id;
        private final Function<T, String> texture;
        private final boolean head;

        private Model(String id, Function<T, String> texture, boolean head) {
            this.id = id;
            this.texture = texture;
            this.head = head;
        }

        @Override
        public ResourceLocation getModelResource(T entity) { return resource("geo/" + id + ".geo.json"); }

        @Override
        public ResourceLocation getTextureResource(T entity) { return resource("textures/entities/" + texture.apply(entity) + ".png"); }

        @Override
        public ResourceLocation getAnimationResource(T entity) { return resource("animations/" + id + ".animation.json"); }

        @Override
        public void setCustomAnimations(T entity, long id, AnimationState<T> state) {
            super.setCustomAnimations(entity, id, state);
            if (!head) return;
            var bone = getAnimationProcessor().getBone(this.id.equals("chromefish") ? "body" : "head");
            var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (bone != null && data != null) {
                bone.setRotX(data.headPitch() * net.minecraft.util.Mth.DEG_TO_RAD);
                bone.setRotY(data.netHeadYaw() * net.minecraft.util.Mth.DEG_TO_RAD);
            }
        }
    }

    private static final class Renderer<T extends Mob & GeoEntity> extends GeoEntityRenderer<T> {
        private Renderer(EntityRendererProvider.Context context, GeoModel<T> model, float shadow, float scale, boolean glow) {
            super(context, model);
            shadowRadius = shadow;
            withScale(scale);
            if (glow) addRenderLayer(new Glow<>(this));
        }

        @Override
        public RenderType getRenderType(T entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }

    private static final class Glow<T extends Mob & GeoEntity> extends GeoRenderLayer<T> {
        private static final ResourceLocation TEXTURE = resource("textures/entities/torpedoglow.png");

        private Glow(GeoRenderer<T> renderer) { super(renderer); }

        @Override
        public void render(PoseStack pose, T entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer vertices, float partialTick, int light, int overlay) {
            var glow = RenderType.eyes(TEXTURE);
            getRenderer().reRender(model, pose, buffers, entity, glow, buffers.getBuffer(glow), partialTick, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
