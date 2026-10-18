package com.hp.thermalshockreforged.content.status;

import java.util.Set;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;

final class ControlEffect extends MobEffect {
    private static final Vec3 STUCK = new Vec3(0.25, 0.05, 0.25);
    private final boolean tremors;

    ControlEffect(boolean tremors) {
        super(MobEffectCategory.HARMFUL, -1);
        this.tremors = tremors;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) { return true; }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (tremors) {
            var random = entity.getRandom();
            entity.setDeltaMovement(entity.getDeltaMovement().add(
                    random.nextDouble() * 0.2 - 0.1,
                    random.nextDouble() * 0.2 - 0.1,
                    random.nextDouble() * 0.2 - 0.1));
        } else entity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), STUCK);
        return true;
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance instance) {
        cures.clear();
        cures.add(EffectCures.PROTECTED_BY_TOTEM);
    }
}
