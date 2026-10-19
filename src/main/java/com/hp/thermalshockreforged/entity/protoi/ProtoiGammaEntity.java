package com.hp.thermalshockreforged.entity.protoi;
import com.hp.thermalshockreforged.content.MaterialContent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
public final class ProtoiGammaEntity extends ProtoiEntityBase {
    public ProtoiGammaEntity(EntityType<? extends ProtoiGammaEntity> type, Level level) { super(type, level, "protoi_gamma", false, 2.25); }
    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() { return attributes(.3, 10, 3, 4, 16); }
    @Override protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) { super.dropCustomDeathLoot(level, source, recentlyHit); spawnAtLocation(new ItemStack(MaterialContent.items().get("helion_coagulate").get())); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "gamma_hurt")); }
    @Override protected SoundEvent getDeathSound() { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "gamma_hurt")); }
}
