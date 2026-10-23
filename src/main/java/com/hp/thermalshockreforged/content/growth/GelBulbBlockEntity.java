package com.hp.thermalshockreforged.content.growth;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.content.farming.FarmInventoryBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class GelBulbBlockEntity extends FarmInventoryBlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private String previousAnimation = "0";
    public GelBulbBlockEntity(BlockPos pos, BlockState state) {
        super(GrowthContent.GEL_ENTITY.get(), pos, state);
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new LegacyAnimationController<>(this, "controller", 0, state -> getBlockState().getValue(GelBulbBlock.ANIMATION) == 0 ? state.setAndContinue(RawAnimation.begin().thenLoop("0")) : PlayState.STOP));
        controllers.add(new LegacyAnimationController<>(this, "procedurecontroller", 0, state -> {
            String requested = Integer.toString(getBlockState().getValue(GelBulbBlock.ANIMATION));
            if ((!requested.equals("0") && state.getController().getAnimationState() == LegacyAnimationController.State.STOPPED)
                    || (!requested.equals(previousAnimation) && !requested.equals("0"))) {
                if (!requested.equals(previousAnimation)) state.getController().forceAnimationReset();
                state.getController().setAnimation(RawAnimation.begin().thenPlay(requested));
                if (state.getController().getAnimationState() == LegacyAnimationController.State.STOPPED) {
                    if (level != null) level.setBlock(worldPosition, getBlockState().setValue(GelBulbBlock.ANIMATION, 0), 3);
                    state.getController().forceAnimationReset();
                }
            } else if (requested.equals("0")) {
                previousAnimation = "0";
                return PlayState.STOP;
            }
            previousAnimation = requested;
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
