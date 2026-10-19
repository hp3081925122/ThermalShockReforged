package com.hp.thermalshockreforged.mixin;

import java.util.UUID;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Projectile.class)
public interface ProjectileOwnerAccess {
    @Accessor("ownerUUID")
    void thermalShockReforged$setOwnerUUID(UUID owner);
}
