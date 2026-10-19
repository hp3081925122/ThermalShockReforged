package com.hp.thermalshockreforged.content.decor.geo;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class GeoDecorItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public final GeoDecorKind kind;
    public GeoDecorItem(Block block, GeoDecorKind kind, Properties properties) { super(block, properties); this.kind = kind; }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "controller", 0, state -> kind == GeoDecorKind.EXTRACTOR ? state.setAndContinue(RawAnimation.begin().thenLoop("0")) : PlayState.CONTINUE));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
