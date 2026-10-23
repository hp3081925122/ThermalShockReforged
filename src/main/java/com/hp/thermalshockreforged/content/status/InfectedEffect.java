package com.hp.thermalshockreforged.content.status;

import java.util.Set;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.*;
import com.hp.thermalshockreforged.content.terrain.TerrainContent;

final class InfectedEffect extends MobEffect implements ClientTickedEffect {
    InfectedEffect() { super(MobEffectCategory.HARMFUL, 0xFF0000); }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) { return true; }

    @Override
    public boolean applyEffectTick(net.minecraft.server.level.ServerLevel serverLevel, LivingEntity entity, int amplifier) {
        if (entity.getRandom().nextDouble() < 0.005) {
            entity.hurtServer(serverLevel, entity.damageSources().generic(), 1.0F);
            burst(entity);
        }
        return true;
    }

    @Override
    public void clientEffectTick(LivingEntity entity, int amplifier) {
        if (entity.getRandom().nextDouble() < 0.005) burst(entity);
    }

    private static void burst(LivingEntity entity) {
        entity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.25, 0.05, 0.25));
        entity.level().levelEvent(2001, net.minecraft.core.BlockPos.containing(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ()), Block.getId(TerrainContent.VEINROOT.get().defaultBlockState()));
    }
}
