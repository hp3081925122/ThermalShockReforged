package com.hp.thermalshockreforged.entity.jellercel;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.NeoForgeMod;

public enum JellercelKind {
    CERULEAN("cerulean_jellercel", .4F, .4F, 4, 1, 1, .2F, "cerulean_jellercel"),
    FUSCHIA("fuschia_jellercel", 1.2F, 1, 6, 1, .8, .2F, "fuschia_jellercel_glow"),
    VERIDIAN("veridian_jellercel", 2, 2, 10, .1, 1, 1, "veridian_jellercel_glow");

    public final String id;
    public final float width;
    public final float height;
    public final double health;
    public final double speed;
    public final double swimmingGoalSpeed;
    public final float shadow;
    public final String glow;

    JellercelKind(String id, float width, float height, double health, double speed, double swimmingGoalSpeed, float shadow, String glow) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.health = health;
        this.speed = speed;
        this.swimmingGoalSpeed = swimmingGoalSpeed;
        this.shadow = shadow;
        this.glow = glow;
    }

    public AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, speed).add(Attributes.MAX_HEALTH, health)
                .add(Attributes.ARMOR, 4).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.STEP_HEIGHT, .6).add(Attributes.KNOCKBACK_RESISTANCE, this == VERIDIAN ? 1 : 0)
                .add(NeoForgeMod.SWIM_SPEED, speed);
    }
}
