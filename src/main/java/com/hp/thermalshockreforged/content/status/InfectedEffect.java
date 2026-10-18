package com.hp.thermalshockreforged.content.status;

import java.util.Set;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.*;
import com.hp.thermalshockreforged.content.terrain.TerrainContent;

final class InfectedEffect extends MobEffect {
    InfectedEffect() { super(MobEffectCategory.HARMFUL, 0xFF0000); }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) { return true; }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.getRandom().nextDouble() < 0.005) {
            entity.hurt(entity.damageSources().generic(), 1.0F);
            entity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.25, 0.05, 0.25));
            entity.level().levelEvent(2001, net.minecraft.core.BlockPos.containing(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ()), Block.getId(TerrainContent.VEINROOT.get().defaultBlockState()));
        }
        return true;
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance instance) {
        cures.clear();
        cures.add(EffectCures.MILK);
        cures.add(EffectCures.HONEY);
        cures.add(EffectCures.PROTECTED_BY_TOTEM);
    }
}
