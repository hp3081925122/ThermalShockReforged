package com.hp.thermalshockreforged.client.fauna;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.fauna.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.*;
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
        event.registerBlockEntityRenderer(FaunaEntities.CAULDRON_ENTITY.get(), CauldronRenderer::new);
    }

    public static void particles(net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(FaunaEntities.WHITE_ORB.get(), WhiteOrbParticle.Provider::new);
    }

    public static void extensions(RegisterClientExtensionsEvent event) {
        GeoRenderProviders.register(TsrRenderProvider.item(CauldronItemRenderer::new), FaunaEntities.CAULDRON_ITEM.get());
    }

    private static <T extends Mob & GeoEntity> EntityRendererProvider<T> factory(String id, Function<T, String> texture, boolean head, float shadow, float scale, boolean glow) {
        return context -> new Renderer<>(context, new Model<>(id, texture, head), shadow, scale, glow);
    }

    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", path); }

    private static final class Model<T extends Mob & GeoEntity> extends TsrGeoModel<T> {
        private final String id;
        private final Function<T, String> texture;
        private final boolean head;

        private Model(String id, Function<T, String> texture, boolean head) {
            this.id = id;
            this.texture = texture;
            this.head = head;
        }

        @Override
        public Identifier getModelResource(T entity) { return resource("geo/" + id + ".geo.json"); }

        @Override
        public Identifier getTextureResource(T entity) { return resource("textures/entities/" + texture.apply(entity) + ".png"); }

        @Override
        public Identifier getAnimationResource(T entity) { return resource("animations/" + id + ".animation.json"); }

        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { if (!head) return; lookAt(state, bones, this.id.equals("chromefish") ? "body" : "head"); }
    }

    private static final class Renderer<T extends Mob & GeoEntity> extends TsrGeoEntityRenderer<T> {
        private Renderer(EntityRendererProvider.Context context, TsrGeoModel<T> model, float shadow, float scale, boolean glow) {
            super(context, model);
            shadowRadius = shadow;
            withScale(scale);
            if (glow) withRenderLayer(new Glow<>(this));
        }

        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }

    private static final class Glow<T extends Mob & GeoEntity> extends GeoEyesLayer<T, Void, EntityRenderState> {
        private static final Identifier TEXTURE = resource("textures/entities/torpedoglow.png");

        private Glow(TsrGeoEntityRenderer<T> renderer) { super(renderer); }

        @Override protected Identifier texture(T entity) { return TEXTURE; }
    }
}
