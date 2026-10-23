package com.hp.thermalshockreforged.entity.labmachinist;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.util.GeckoLibUtil;

public final class LabMachineItem extends BlockItem implements GeoItem {
    public final boolean wallbore;
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public LabMachineItem(Block block, boolean wallbore) { super(block, new Properties()); this.wallbore = wallbore; }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { controllers.add(new LegacyAnimationController<>(this, "controller", 0, state -> PlayState.CONTINUE)); }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
