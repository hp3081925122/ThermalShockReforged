package com.hp.thermalshock.entity.fauna;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

final class FaunaMeleeGoal extends MeleeAttackGoal {
    private final double reachSquared;

    FaunaMeleeGoal(PathfinderMob mob, double reachSquared) {
        super(mob, 1.2, false);
        this.reachSquared = reachSquared;
    }

    @Override
    protected boolean canPerformAttack(LivingEntity target) {
        double reach = reachSquared >= 0 ? reachSquared : mob.getBbWidth() * mob.getBbWidth() + target.getBbWidth();
        return isTimeToAttack() && mob.distanceToSqr(target) <= reach;
    }
}
