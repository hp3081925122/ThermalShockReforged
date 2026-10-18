package com.hp.thermalshockreforged.entity.mangler;

import java.util.UUID;
import com.hp.thermalshockreforged.entity.summon.SummonLedger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

public final class SummonedManglerEntity extends ManglerEntity {
    private long expiresAt = -1;
    private UUID summonOwner;
    public SummonedManglerEntity(EntityType<? extends SummonedManglerEntity> type, Level level) { super(type, level); if (level instanceof ServerLevel server) expiresAt = SummonLedger.clock(server) + ManglerCombatConfig.SUMMON_LIFETIME.get(); }
    void bind(UUID owner, long until) { summonOwner = owner; expiresAt = until; }
    boolean expireIfDue(ServerLevel server) {
        if (expiresAt < 0) expiresAt = SummonLedger.clock(server) + ManglerCombatConfig.SUMMON_LIFETIME.get();
        if (SummonLedger.clock(server) < expiresAt) return false;
        if (!isRemoved()) { server.sendParticles(net.minecraft.core.particles.ParticleTypes.POOF, getX(), getY(), getZ(), 5, 0.001, 0.001, 0.001, 0); discard(); }
        return true;
    }
    @Override
    public void tick() { if (level() instanceof ServerLevel server && expireIfDue(server)) return; super.tick(); }
    @Override
    public void boost(LivingEntity target) { if (level() instanceof ServerLevel server && expireIfDue(server)) return; super.boost(target); }
    @Override
    public void remove(RemovalReason reason) { if (reason.shouldDestroy() && summonOwner != null && level() instanceof ServerLevel server) SummonLedger.get(server).release(summonOwner, getUUID()); super.remove(reason); }
    @Override
    public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putLong("SummonExpiresAt", expiresAt); if (summonOwner != null) tag.putUUID("SummonOwner", summonOwner); }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); if (tag.contains("SummonExpiresAt")) expiresAt = tag.getLong("SummonExpiresAt"); summonOwner = tag.hasUUID("SummonOwner") ? tag.getUUID("SummonOwner") : null; }
}
