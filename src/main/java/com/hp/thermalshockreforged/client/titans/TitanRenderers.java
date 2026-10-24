package com.hp.thermalshockreforged.client.titans;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.titans.SkeletalTitanEntity;
import com.hp.thermalshockreforged.entity.titans.TitanContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;

public final class TitanRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(TitanContent.SKELETITAN.get(), Renderer::new);
        event.registerEntityRenderer(TitanContent.FROST_TITAN.get(), Renderer::new);
        event.registerEntityRenderer(TitanContent.MANTLETITAN.get(), Renderer::new);
    }

    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model extends TsrGeoModel<SkeletalTitanEntity> {
        private String modelName(SkeletalTitanEntity entity) { return entity.kind() == SkeletalTitanEntity.Kind.FROST ? "frosttitan" : "skeletitan"; }
        @Override public Identifier getModelResource(SkeletalTitanEntity entity) { return resource("geo/" + modelName(entity) + ".geo.json"); }
        @Override public Identifier getTextureResource(SkeletalTitanEntity entity) { return resource("textures/entities/" + entity.texture() + ".png"); }
        @Override public Identifier getAnimationResource(SkeletalTitanEntity entity) { return resource("animations/" + modelName(entity) + ".animation.json"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { lookAt(state, bones, "head"); }
    }

    private static final class Renderer extends TsrGeoEntityRenderer<SkeletalTitanEntity> {
        private Renderer(EntityRendererProvider.Context context) {
            super(context, new Model());
            shadowRadius = .8F;
            withScale(1.6F);
            withRenderLayer(new Glow(this));
        }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }

    private static final class Glow extends GeoEyesLayer<SkeletalTitanEntity, Void, EntityRenderState> {
        private static final Identifier TEXTURE = resource("textures/entities/mantletitanglow.png");
        private Glow(Renderer renderer) { super(renderer); }
        @Override protected Identifier texture(SkeletalTitanEntity entity) { return entity.kind() == SkeletalTitanEntity.Kind.MANTLE ? TEXTURE : null; }
    }

    private TitanRenderers() { }
}
