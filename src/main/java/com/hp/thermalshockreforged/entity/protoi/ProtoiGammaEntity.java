package com.hp.thermalshockreforged.entity.protoi;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
public final class ProtoiGammaEntity extends ProtoiEntityBase {
    public ProtoiGammaEntity(EntityType<? extends ProtoiGammaEntity> type, Level level) { super(type, level, "protoi_gamma", false, 2.25); }
    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() { return attributes(.3, 10, 3, 4, 16); }
}
