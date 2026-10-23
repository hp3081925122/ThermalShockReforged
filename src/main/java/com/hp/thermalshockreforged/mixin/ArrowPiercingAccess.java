package com.hp.thermalshockreforged.mixin;

import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractArrow.class)
public interface ArrowPiercingAccess {
    @Invoker("setPierceLevel")
    void thermalShockReforged$setPierceLevel(byte level);
}
