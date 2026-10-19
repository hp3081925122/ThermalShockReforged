package com.hp.thermalshockreforged.entity.beetles;

import com.hp.thermalshockreforged.entity.attack.BossDeathEffectEntity;
import com.hp.thermalshockreforged.entity.mangler.ManglerContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.*;
import net.minecraft.world.level.storage.loot.LootTable;

public final class BeetleCrasherEntity extends BeetleCombatEntity {
    private boolean initialized;
    private boolean deathVisual;
    public BeetleCrasherEntity(EntityType<? extends BeetleCrasherEntity> type, Level level) { super(type, level, "beetle_bastion", 100); }
    public double auraDamage(int amplifier) { double value = getAttributeValue(Attributes.ATTACK_DAMAGE) * BeetleConfig.CRASHER_AURA.get() * Math.max(0, amplifier) / 12; return Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0; }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data) {
        var result = super.finalizeSpawn(world, difficulty, reason, data);
        if (!initialized && world instanceof ServerLevel server) { initialized = true; startEmergence(); server.playSound(null, blockPosition(), SoundEvents.WARDEN_EMERGE, SoundSource.NEUTRAL, 1, 2); }
        return result;
    }
    @Override protected ResourceKey<LootTable> getDefaultLootTable() { return ResourceKey.create(Registries.LOOT_TABLE, BeetleContent.id("beetle_crasher_loot")); }
    @Override public void die(DamageSource source) {
        super.die(source);
        if (isDeadOrDying() && !deathVisual && level() instanceof ServerLevel server) { deathVisual = true; BossDeathEffectEntity.submit(server, ManglerContent.HERALD_DEATH_EFFECT.get(), position(), getEyeHeight()); }
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putBoolean("BeetleInitialized", initialized); tag.putBoolean("BeetleDeathVisual", deathVisual); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); initialized = !tag.contains("BeetleInitialized") || tag.getBoolean("BeetleInitialized"); deathVisual = tag.getBoolean("BeetleDeathVisual"); }
}
