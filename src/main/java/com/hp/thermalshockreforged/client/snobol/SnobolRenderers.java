package com.hp.thermalshockreforged.client.snobol;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

import com.hp.thermalshockreforged.entity.snobol.*;
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
import com.geckolib.constant.DataTickets;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.geckolib.renderer.*;
import com.hp.thermalshockreforged.client.compat.LegacyGeoRenderLayer;
import java.util.function.Function;

public final class SnobolRenderers {
    private SnobolRenderers() { }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SnobolContent.SNOBOL.get(), factory("snobol", SnobolEntity::texture, 0.3F, 1, false));
        event.registerEntityRenderer(SnobolContent.EXPLOSIVE_SNOBOL.get(), factory("snobomb", ExplosiveSnobolEntity::texture, 0.3F, 1, false));
        event.registerEntityRenderer(SnobolContent.SIIBOL.get(), factory("sybol", SiibolEntity::texture, 0.3F, 1, false));
        event.registerEntityRenderer(SnobolContent.BLAST.get(), com.hp.thermalshockreforged.client.InvisibleRenderer::new);
    }

    public static void particles(net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SnobolContent.TOXIC_BUBBLE.get(), ToxicBubbleParticle.Provider::new);
    }

    private static <T extends Mob & GeoEntity> EntityRendererProvider<T> factory(String id, Function<T, String> texture, float shadow, float scale, boolean glow) {
        return context -> new Renderer<>(context, new Model<>(id, texture), shadow, scale, glow);
    }

    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath("thermal_shock_reforged", path); }

    private static final class Model<T extends Mob & GeoEntity> extends LegacyGeoModel<T> {
        private final String id;
        private final Function<T, String> texture;

        private Model(String id, Function<T, String> texture) {
            this.id = id;
            this.texture = texture;
        }

        @Override
        public Identifier getModelResource(T entity) { return resource("geo/" + id + ".geo.json"); }

        @Override
        public Identifier getTextureResource(T entity) { return resource("textures/entities/" + texture.apply(entity) + ".png"); }

        @Override
        public Identifier getAnimationResource(T entity) { return resource("animations/" + id + ".animation.json"); }


    }

    private static final class Renderer<T extends Mob & GeoEntity> extends LegacyGeoEntityRenderer<T> {
        private Renderer(EntityRendererProvider.Context context, LegacyGeoModel<T> model, float shadow, float scale, boolean glow) {
            super(context, model);
            shadowRadius = shadow;
            withScale(scale);
        }

        @Override
        public RenderType getRenderType(T entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }

}
