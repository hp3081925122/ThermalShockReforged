package com.hp.thermalshockreforged.client.ambient;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.entity.ambient.*;
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

public final class AmbientRenderers {
    private AmbientRenderers() { }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AmbientContent.PILLBUD.get(), factory("pillbud", PillbudEntity::texture, true, 0.3F, 1, false));
        event.registerEntityRenderer(AmbientContent.PIXIE.get(), factory("pixie", PixieEntity::texture, false, 0, 1, true));
        event.registerEntityRenderer(AmbientContent.SOOTLING.get(), factory("flare_wrangler", SootlingEntity::texture, true, 0.5F, 1, true));
        event.registerEntityRenderer(AmbientContent.PIXIE_BOTTLE.get(), net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
    }
    public static void particles(net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(AmbientContent.PRISMATIC_BOOM.get(), PrismaticBoomParticle.Provider::new);
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

        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { if (!head) return; lookAt(state, bones, this.id.equals("flare_wrangler") ? "body" : "head"); }
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


        private Glow(TsrGeoEntityRenderer<T> renderer) { super(renderer); }

        @Override protected Identifier texture(T entity) { return resource("textures/entities/" + (entity instanceof PixieEntity ? "texturepixieglow" : "sootlingglow") + ".png"); }
    }
}
