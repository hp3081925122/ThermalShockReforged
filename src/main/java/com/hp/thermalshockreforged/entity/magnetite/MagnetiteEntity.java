package com.hp.thermalshockreforged.entity.magnetite;

import com.hp.thermalshockreforged.entity.protoi.ProtoiEntityBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;

public final class MagnetiteEntity extends ProtoiEntityBase implements net.minecraft.world.entity.monster.RangedAttackMob {
    public MagnetiteEntity(EntityType<? extends MagnetiteEntity> type, Level level) { super(type, level, "magnetite", false, 3); xpReward = 7; }
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 80, 10));
        goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class, 4, 1, 1.2));
        targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
    }
    @Override public void performRangedAttack(LivingEntity target, float velocity) { MagnetiteBulletEntity.shoot(this, target); }
    @Override public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) { return !(source.getDirectEntity() instanceof AbstractArrow) && !source.is(net.minecraft.world.damagesource.DamageTypes.FALL) && !source.is(net.minecraft.world.damagesource.DamageTypes.DROWN) && super.hurt(source, amount); }
    public static AttributeSupplier.Builder createAttributes() { return attributes(.3, 35, 6, 3, 16).add(Attributes.KNOCKBACK_RESISTANCE, .1).add(Attributes.ATTACK_KNOCKBACK, .1); }
}
