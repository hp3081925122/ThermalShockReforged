package com.hp.thermalshockreforged.content.growth;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;
import java.util.function.Consumer;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class GelBulbItem extends BlockItem implements GeoItem {
    @Override public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) { GeoRenderProviders.accept(this, consumer); }
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public GelBulbItem(Block block) { super(block, com.hp.thermalshockreforged.ModRegistration.blockItem(block)); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new LegacyAnimationController<>(this, "controller", 0, state -> state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
