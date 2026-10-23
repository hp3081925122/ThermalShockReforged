package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class DiskResonanceEntity extends Entity implements ServerEndTickTask {
    private UUID owner;
    private int ownerEntityId;
    private int elapsed;
    private boolean armed;
    public DiskResonanceEntity(EntityType<? extends DiskResonanceEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, Vec3 anchor, Entity owner) {
        var task = WeaponContent.DISK_RESONANCE.get().create(level);
        if (task == null || owner == null) return false;
        task.setPos(anchor); task.owner = owner.getUUID(); task.ownerEntityId = owner.getId(); task.armed = true;
        if (!level.addFreshEntity(task)) return false;
        task.burst(level, owner);
        return true;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) { if (armed) ServerEndTickTasks.schedule(server, getUUID()); else discard(); } }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        int stage = ++elapsed;
        if (stage >= 6) { armed = false; discard(); }
        if (stage == 3 || stage == 6) {
            Entity attacker = level.getServer().getPlayerList().getPlayer(owner);
            if (attacker == null) attacker = level.getEntity(owner);
            burst(level, attacker != null && attacker.getId() == ownerEntityId && !attacker.isRemoved() ? attacker : null);
        }
        if (armed) ServerEndTickTasks.schedule(level, getUUID());
    }
    private void burst(ServerLevel level, Entity attacker) {
        level.playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "prism_breakout")), SoundSource.NEUTRAL, 1, 1);
        level.sendParticles(WeaponContent.PRISM_CLUSTER.get(), getX(), getY(), getZ(), 1, .5, .5, .5, 0);
        var targets = level.getEntitiesOfClass(Entity.class, new AABB(position(), position()).inflate(3), entity -> entity != this);
        targets.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(position())));
        var source = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS), attacker);
        for (Entity target : targets) target.hurt(source, 4);
        ThermalShockReforged.LOGGER.debug("Liteprism core resonance: task={}, elapsed={}, targets={}", getUUID(), elapsed, targets.size());
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (owner != null) output.putString("DiskResonanceOwner", owner.toString());
        output.putInt("DiskResonanceOwnerEntityId", ownerEntityId); output.putInt("DiskResonanceElapsed", elapsed); output.putBoolean("DiskResonanceArmed", armed);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        owner = input.getString("DiskResonanceOwner").map(java.util.UUID::fromString).orElse(null);
        ownerEntityId = input.getIntOr("DiskResonanceOwnerEntityId", 0); elapsed = Math.clamp(input.getIntOr("DiskResonanceElapsed", 0), 0, 6);
        armed = owner != null && elapsed < 6 && input.getBooleanOr("DiskResonanceArmed", false);
    }
}
