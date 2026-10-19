package com.hp.thermalshockreforged.entity.protoi;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
public final class ProtoiEpsilonEntity extends ProtoiEntityBase {
    public ProtoiEpsilonEntity(EntityType<? extends ProtoiEpsilonEntity> type, Level level) { super(type, level, "protoi_epsilon", true, 2304); }
    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() { return attributes(.3, 20, 10, 3, 64).add(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE, 1).add(net.minecraft.world.entity.ai.attributes.Attributes.FLYING_SPEED, .3); }
}
