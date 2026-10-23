package com.hp.thermalshockreforged.content.lights;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class GlowstickItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public GlowstickItem(Block block, Properties properties) { super(block, com.hp.thermalshockreforged.ModRegistration.blockItem(block, properties)); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new LegacyAnimationController<>(this, "controller", 0, state -> PlayState.CONTINUE));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
