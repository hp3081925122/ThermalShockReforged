package com.hp.thermalshockreforged.client.hunters;
import com.hp.thermalshockreforged.client.geo.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.BoneSnapshots;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

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

public final class HuntersRenderers {
    private HuntersRenderers() { }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(HuntersContent.FROSTILYTE.get(), context -> new Renderer<>(context, new Model<>("frostilyte_rubicon", FrostilyteEntity::texture, true)));
        event.registerEntityRenderer(HuntersContent.RAZORWING.get(), context -> new Renderer<>(context, new Model<>("razorwing", RazorwingEntity::texture, false)));
        event.registerEntityRenderer(HuntersContent.RUBY_ARMOURED_RAZORWING.get(), context -> new Renderer<>(context, new Model<>("razorwing", RazorwingEntity::texture, false)));
        event.registerEntityRenderer(HuntersContent.BLOOD_ARMOURED_RAZORWING.get(), context -> new Renderer<>(context, new Model<>("razorwing", RazorwingEntity::texture, false)));
    }
    private static Identifier resource(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    private static final class Model<T extends Mob & GeoEntity> extends TsrGeoModel<T> {
        private final String model;
        private final Function<T, String> texture;
        private final boolean head;
        private Model(String model, Function<T, String> texture, boolean head) { this.model = model; this.texture = texture; this.head = head; }
        @Override public Identifier getModelResource(T entity) { return resource("geo/" + model + ".geo.json"); }
        @Override public Identifier getTextureResource(T entity) { return resource("textures/entities/" + texture.apply(entity) + ".png"); }
        @Override public Identifier getAnimationResource(T entity) { return resource("animations/" + model + ".animation.json"); }
        @Override public void adjustBones(GeoRenderState state, BoneSnapshots bones) { if (!head) return; lookAt(state, bones, "head"); }
    }
    private static final class Renderer<T extends Mob & GeoEntity> extends TsrGeoEntityRenderer<T> {
        private Renderer(EntityRendererProvider.Context context, TsrGeoModel<T> model) { super(context, model); shadowRadius = 0.5F; withScale(1); }
        @Override protected RenderType visibleRenderType(EntityRenderState state, Identifier texture) { return RenderTypes.entityTranslucent(texture); }
    }
}
