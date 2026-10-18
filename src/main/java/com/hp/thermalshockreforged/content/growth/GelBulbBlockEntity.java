package com.hp.thermalshockreforged.content.growth;

import com.hp.thermalshockreforged.content.farming.FarmInventoryBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class GelBulbBlockEntity extends FarmInventoryBlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    private String previousAnimation = "0";
    public GelBulbBlockEntity(BlockPos pos, BlockState state) {
        super(GrowthContent.GEL_ENTITY.get(), pos, state);
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> getBlockState().getValue(GelBulbBlock.ANIMATION) == 0 ? state.setAndContinue(RawAnimation.begin().thenLoop("0")) : PlayState.STOP));
        controllers.add(new AnimationController<>(this, "procedurecontroller", 0, state -> {
            String requested = Integer.toString(getBlockState().getValue(GelBulbBlock.ANIMATION));
            if ((!requested.equals("0") && state.getController().getAnimationState() == AnimationController.State.STOPPED)
                    || (!requested.equals(previousAnimation) && !requested.equals("0"))) {
                if (!requested.equals(previousAnimation)) state.getController().forceAnimationReset();
                state.getController().setAnimation(RawAnimation.begin().thenPlay(requested));
                if (state.getController().getAnimationState() == AnimationController.State.STOPPED) {
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
