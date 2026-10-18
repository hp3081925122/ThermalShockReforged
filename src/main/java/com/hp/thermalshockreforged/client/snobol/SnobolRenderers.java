package com.hp.thermalshockreforged.client.snobol;

import com.hp.thermalshockreforged.entity.snobol.*;
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

public final class SnobolRenderers {
    private SnobolRenderers() { }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SnobolContent.SNOBOL.get(), factory("snobol", SnobolEntity::texture, 0.3F, 1, false));
        event.registerEntityRenderer(SnobolContent.EXPLOSIVE_SNOBOL.get(), factory("snobomb", ExplosiveSnobolEntity::texture, 0.3F, 1, false));
        event.registerEntityRenderer(SnobolContent.SIIBOL.get(), factory("sybol", SiibolEntity::texture, 0.3F, 1, false));
        event.registerEntityRenderer(SnobolContent.BLAST.get(), Invisible::new);
    }

    public static void particles(net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SnobolContent.TOXIC_BUBBLE.get(), ToxicBubbleParticle.Provider::new);
    }

    private static final class Invisible extends net.minecraft.client.renderer.entity.EntityRenderer<SnobolBlastEntity> {
        private Invisible(EntityRendererProvider.Context context) { super(context); }
        @Override
        public void render(SnobolBlastEntity entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) { }
        @Override
        public ResourceLocation getTextureLocation(SnobolBlastEntity entity) { return ResourceLocation.withDefaultNamespace("textures/misc/white.png"); }
    }

    private static <T extends Mob & GeoEntity> EntityRendererProvider<T> factory(String id, Function<T, String> texture, float shadow, float scale, boolean glow) {
        return context -> new Renderer<>(context, new Model<>(id, texture), shadow, scale, glow);
    }

    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", path); }

    private static final class Model<T extends Mob & GeoEntity> extends GeoModel<T> {
        private final String id;
        private final Function<T, String> texture;

        private Model(String id, Function<T, String> texture) {
            this.id = id;
            this.texture = texture;
        }

        @Override
        public ResourceLocation getModelResource(T entity) { return resource("geo/" + id + ".geo.json"); }

        @Override
        public ResourceLocation getTextureResource(T entity) { return resource("textures/entities/" + texture.apply(entity) + ".png"); }

        @Override
        public ResourceLocation getAnimationResource(T entity) { return resource("animations/" + id + ".animation.json"); }


    }

    private static final class Renderer<T extends Mob & GeoEntity> extends GeoEntityRenderer<T> {
        private Renderer(EntityRendererProvider.Context context, GeoModel<T> model, float shadow, float scale, boolean glow) {
            super(context, model);
            shadowRadius = shadow;
            withScale(scale);
        }

        @Override
        public RenderType getRenderType(T entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }

}
