package com.hp.thermalshockreforged.content.farming;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;
import java.util.function.Consumer;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class GeoCropItem extends BlockItem implements GeoItem {
    @Override public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) { GeoRenderProviders.accept(this, consumer); }
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public final CropKind kind;
    public GeoCropItem(Block block, CropKind kind) { super(block, com.hp.thermalshockreforged.ModRegistration.blockItem(block)); this.kind = kind; }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new LegacyAnimationController<>(this, "controller", 0, state -> switch (kind) {
            case ANCIENT_SKORIO, ANCIENT_BARBUBBLE, DEWLEAF, HEARTHFLY_BRANCH, NEOFLORM, VOLATINE -> state.setAndContinue(RawAnimation.begin().thenLoop("0"));
            default -> PlayState.CONTINUE;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
