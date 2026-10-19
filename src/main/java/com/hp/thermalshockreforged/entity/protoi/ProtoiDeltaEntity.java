package com.hp.thermalshockreforged.entity.protoi;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
public final class ProtoiDeltaEntity extends ProtoiEntityBase {
    public ProtoiDeltaEntity(EntityType<? extends ProtoiDeltaEntity> type, Level level) { super(type, level, "protoi_delta", false, 256); }
    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() { return attributes(.25, 50, 8, 5, 16).add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_KNOCKBACK, .2); }
}
