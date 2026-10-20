package com.hp.thermalshockreforged.content.growth;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class GelBulbItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public GelBulbItem(Block block) { super(block, new Properties()); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "controller", 0, state -> state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
