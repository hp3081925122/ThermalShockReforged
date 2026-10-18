package com.hp.thermalshockreforged.entity.icesquad.mixin;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractArrow.class)
public interface ArrowPiercingAccess {
    @Invoker("setPierceLevel")
    void thermalShockReforged$setPierceLevel(byte level);
}
