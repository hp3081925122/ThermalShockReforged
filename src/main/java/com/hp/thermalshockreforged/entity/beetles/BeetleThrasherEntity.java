package com.hp.thermalshockreforged.entity.beetles;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.Level;

public final class BeetleThrasherEntity extends BeetleCombatEntity {
    public BeetleThrasherEntity(EntityType<? extends BeetleThrasherEntity> type, Level level) { super(type, level, "blood_beetle", 25); }
    @Override protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false, entity -> BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).equals(BeetleContent.id("graver"))));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        goalSelector.addGoal(7, new FloatGoal(this));
    }
}
