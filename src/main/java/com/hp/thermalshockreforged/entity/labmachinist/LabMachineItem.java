package com.hp.thermalshockreforged.entity.labmachinist;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class LabMachineItem extends BlockItem implements GeoItem {
    public final boolean wallbore;
    private final AnimatableInstanceCache animations = GeckoLibUtil.createInstanceCache(this);
    public LabMachineItem(Block block, boolean wallbore) { super(block, new Properties()); this.wallbore = wallbore; }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { controllers.add(new AnimationController<>(this, "controller", 0, state -> PlayState.CONTINUE)); }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
