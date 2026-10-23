package com.hp.thermalshockreforged.entity.gravers;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class AssimilatorEntity extends GraverFamilyEntity {
    private boolean converted;
    private boolean converting;
    public AssimilatorEntity(EntityType<? extends AssimilatorEntity> type, Level level) { super(type, level, "infected_snobol", 3); }
    @Override public void awardKillScore(Entity victim, int score, DamageSource source) {
        super.awardKillScore(victim, score, source);
        if (!(victim instanceof GraverEntity) || !(level() instanceof ServerLevel server) || !isAlive() || converted || converting || victim.level() != level()) return;
        converting = true;
        try {
            var replacement = GraverContent.ASSIMILATED.get().spawn(server, blockPosition(), EntitySpawnReason.MOB_SUMMONED);
            if (replacement != null && !replacement.isRemoved() && server.getEntity(replacement.getUUID()) == replacement) {
                converted = true;
                replacement.setDeltaMovement(Vec3.ZERO);
                discard();
            } else ThermalShockReforged.LOGGER.debug("Assimilator transformation rejected: source={}, victim={}", getUUID(), victim.getUUID());
        } finally { converting = false; }
    }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putBoolean("AssimilatorConverted", converted); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); converted = input.getBooleanOr("AssimilatorConverted", false); }
}
