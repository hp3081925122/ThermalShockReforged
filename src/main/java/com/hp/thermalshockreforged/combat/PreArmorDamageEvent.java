package com.hp.thermalshockreforged.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public final class PreArmorDamageEvent extends LivingEvent {
    private final DamageSource source;
    private final float amount;

    public PreArmorDamageEvent(LivingEntity entity, DamageSource source, float amount) {
        super(entity);
        this.source = source;
        this.amount = amount;
    }

    public DamageSource getSource() { return source; }
    public float getAmount() { return amount; }
}
