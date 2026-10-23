package com.hp.thermalshockreforged.entity.fauna;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.*;

final class FaunaExplosions {
    private FaunaExplosions() { }

    static void explode(LivingEntity owner, float radius) {
        double attack = owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
        var calculator = new ExplosionDamageCalculator() {
            @Override
            public float getEntityDamageAmount(Explosion explosion, Entity target, float seenPercent) {
                return (float) (super.getEntityDamageAmount(explosion, target, seenPercent) * attack);
            }

            @Override
            public float getKnockbackMultiplier(Entity target) {
                return target instanceof LivingEntity living ? (float) (0.6 * (1 - living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE))) : 0.6F;
            }
        };
        owner.level().explode(null, null, calculator, owner.getX(), owner.getY(), owner.getZ(), radius, false, Level.ExplosionInteraction.MOB);
    }
}
