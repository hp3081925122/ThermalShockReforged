package com.hp.thermalshockreforged.entity.protoi;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
public final class ProtoiSunbeamEntity extends ProtoiEntityBase {
    public ProtoiSunbeamEntity(EntityType<? extends ProtoiSunbeamEntity> type, Level level) { super(type, level, "protoi_sunbeam", true, 100); }
    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() { return attributes(0, 100, 0, 0, 16).add(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE, 1); }
    @Override protected void tickDeath() { if (++deathTime >= 60) { remove(RemovalReason.KILLED); dropExperience(null); } }
}
