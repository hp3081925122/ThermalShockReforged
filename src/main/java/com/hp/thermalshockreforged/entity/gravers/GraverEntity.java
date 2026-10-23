package com.hp.thermalshockreforged.entity.gravers;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;

public final class GraverEntity extends GraverFamilyEntity {
    private boolean initialized;
    private boolean rarePending;
    public GraverEntity(EntityType<? extends GraverEntity> type, Level level) { super(type, level, "gravera", 3); }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData group) {
        var result = super.finalizeSpawn(level, difficulty, reason, group);
        if (!initialized && !isSpawnCancelled()) {
            initialized = true;
            rarePending = random.nextDouble() < (GraverConfig.SPEC.isLoaded() ? GraverConfig.RARE_CHANCE.get() : .00001);
            if (!rarePending) setTexture(random.nextBoolean() ? "gravera" : random.nextBoolean() ? "graverb" : random.nextBoolean() ? "graverc" : "graverd");
        }
        return result;
    }
    @Override public void onAddedToLevel() {
        super.onAddedToLevel();
        if (rarePending && level() instanceof ServerLevel server && !isRemoved()) {
            rarePending = false;
            var replacement = GraverContent.NEON.get().spawn(server, blockPosition(), EntitySpawnReason.MOB_SUMMONED);
            if (replacement != null && server.getEntity(replacement.getUUID()) == replacement && !replacement.isRemoved()) { replacement.setYRot(random.nextFloat() * 360); discard(); }
            else ThermalShockReforged.LOGGER.debug("Rare graver transformation rejected: source={}, position={}", getUUID(), blockPosition());
        }
    }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putBoolean("GraverInitialized", initialized); output.putBoolean("GraverRarePending", rarePending); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); initialized = input.getBooleanOr("GraverInitialized", false); rarePending = input.getBooleanOr("GraverRarePending", false); }
}
