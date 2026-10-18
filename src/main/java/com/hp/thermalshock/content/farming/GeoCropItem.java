package com.hp.thermalshock.content.farming;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class GeoCropItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public final CropKind kind;
    public GeoCropItem(Block block, CropKind kind) { super(block, new Properties()); this.kind = kind; }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "controller", 0, state -> PlayState.CONTINUE));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
