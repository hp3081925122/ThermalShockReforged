package com.hp.thermalshockreforged.content.atlas;

import net.minecraft.world.entity.LivingEntity;

public final class AtlasArrivalVisuals {
    private AtlasArrivalVisuals() { }
    public static boolean active(LivingEntity entity) { return entity instanceof AtlasArrivalState state && state.hasArrivalProtectionVisual(); }
    public static void tick(LivingEntity entity) {
        if (!active(entity)) return;
        var random = entity.getRandom();
        boolean emit = entity.isInvisible() ? random.nextInt(15) == 0 : random.nextBoolean();
        if (!emit) return;
        com.hp.thermalshockreforged.content.status.LegacyEffectParticles.emit(entity, 0x9146F0, 10, net.minecraft.world.effect.MobEffects.RESISTANCE);
    }
}
