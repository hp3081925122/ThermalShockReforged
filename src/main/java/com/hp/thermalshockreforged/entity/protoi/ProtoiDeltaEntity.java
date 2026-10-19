package com.hp.thermalshockreforged.entity.protoi;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
public final class ProtoiDeltaEntity extends ProtoiEntityBase {
    public ProtoiDeltaEntity(EntityType<? extends ProtoiDeltaEntity> type, Level level) { super(type, level, "protoi_delta", false, 256); }
    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() { return attributes(.25, 50, 8, 5, 16).add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_KNOCKBACK, .2); }
    @Override protected SoundEvent getAmbientSound() { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "delta_ambient")); }
    @Override protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "delta_hurt")); }
    @Override protected SoundEvent getDeathSound() { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "delta_death")); }
    @Override protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) { playSound(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "delta_step")), .15F, 1); }
}
