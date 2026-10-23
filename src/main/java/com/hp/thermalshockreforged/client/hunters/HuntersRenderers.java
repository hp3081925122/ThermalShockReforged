package com.hp.thermalshockreforged.client.hunters;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.hunters.*;
import java.util.function.Function;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animation.state.AnimationTest;
import com.hp.thermalshockreforged.client.compat.LegacyGeoModel;
import com.hp.thermalshockreforged.client.compat.LegacyGeoEntityRenderer;

public final class HuntersRenderers {
    private HuntersRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(HuntersContent.FROSTILYTE.get(), context -> new Renderer<>(context, new Model<>("frostilyte_rubicon", FrostilyteEntity::texture, true)));
        event.registerEntityRenderer(HuntersContent.RAZORWING.get(), context -> new Renderer<>(context, new Model<>("razorwing", RazorwingEntity::texture, false)));
        event.registerEntityRenderer(HuntersContent.RUBY_ARMOURED_RAZORWING.get(), context -> new Renderer<>(context, new Model<>("razorwing", RazorwingEntity::texture, false)));
        event.registerEntityRenderer(HuntersContent.BLOOD_ARMOURED_RAZORWING.get(), context -> new Renderer<>(context, new Model<>("razorwing", RazorwingEntity::texture, false)));
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model<T extends Mob & GeoEntity> extends LegacyGeoModel<T> {
        private final String model;
        private final Function<T, String> texture;
        private final boolean head;
        private Model(String model, Function<T, String> texture, boolean head) { this.model = model; this.texture = texture; this.head = head; }
        @Override public Identifier getModelResource(T entity) { return resource("geo/" + model + ".geo.json"); }
        @Override public Identifier getTextureResource(T entity) { return resource("textures/entities/" + texture.apply(entity) + ".png"); }
        @Override public Identifier getAnimationResource(T entity) { return resource("animations/" + model + ".animation.json"); }
        @Override
        public void setCustomAnimations(T entity, long id, AnimationTest<T> state) {
            super.setCustomAnimations(entity, id, state);
            if (!head) return;
            var bone = getAnimationProcessor().getBone("head");
            var data = com.hp.thermalshockreforged.compat.LegacyAnimationData.from(state);
            if (bone != null && data != null) { bone.setRotX(data.headPitch() * Mth.DEG_TO_RAD); bone.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD); }
        }
    }
    private static final class Renderer<T extends Mob & GeoEntity> extends LegacyGeoEntityRenderer<T> {
        private Renderer(EntityRendererProvider.Context context, LegacyGeoModel<T> model) { super(context, model); shadowRadius = 0.5F; withScale(1); }
        @Override public RenderType getRenderType(T entity, Identifier texture, MultiBufferSource buffers, float partialTick) { return RenderTypes.entityTranslucent(texture); }
    }
}
