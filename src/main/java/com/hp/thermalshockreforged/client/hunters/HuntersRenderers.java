package com.hp.thermalshockreforged.client.hunters;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.hunters.*;
import java.util.function.Function;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class HuntersRenderers {
    private HuntersRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(HuntersContent.FROSTILYTE.get(), context -> new Renderer<>(context, new Model<>("frostilyte_rubicon", FrostilyteEntity::texture, true)));
        event.registerEntityRenderer(HuntersContent.RAZORWING.get(), context -> new Renderer<>(context, new Model<>("razorwing", RazorwingEntity::texture, false)));
        event.registerEntityRenderer(HuntersContent.RUBY_ARMOURED_RAZORWING.get(), context -> new Renderer<>(context, new Model<>("razorwing", RazorwingEntity::texture, false)));
        event.registerEntityRenderer(HuntersContent.BLOOD_ARMOURED_RAZORWING.get(), context -> new Renderer<>(context, new Model<>("razorwing", RazorwingEntity::texture, false)));
    }
    private static ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model<T extends Mob & GeoEntity> extends GeoModel<T> {
        private final String model;
        private final Function<T, String> texture;
        private final boolean head;
        private Model(String model, Function<T, String> texture, boolean head) { this.model = model; this.texture = texture; this.head = head; }
        @Override public ResourceLocation getModelResource(T entity) { return resource("geo/" + model + ".geo.json"); }
        @Override public ResourceLocation getTextureResource(T entity) { return resource("textures/entities/" + texture.apply(entity) + ".png"); }
        @Override public ResourceLocation getAnimationResource(T entity) { return resource("animations/" + model + ".animation.json"); }
        @Override
        public void setCustomAnimations(T entity, long id, AnimationState<T> state) {
            super.setCustomAnimations(entity, id, state);
            if (!head) return;
            var bone = getAnimationProcessor().getBone("head");
            var data = state.getData(DataTickets.ENTITY_MODEL_DATA);
            if (bone != null && data != null) { bone.setRotX(data.headPitch() * Mth.DEG_TO_RAD); bone.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Renderer<T extends Mob & GeoEntity> extends GeoEntityRenderer<T> {
        private Renderer(EntityRendererProvider.Context context, GeoModel<T> model) { super(context, model); shadowRadius = 0.5F; withScale(1); }
        @Override public RenderType getRenderType(T entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityTranslucent(texture); }
    }
}
