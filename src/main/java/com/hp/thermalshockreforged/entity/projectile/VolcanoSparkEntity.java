package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class VolcanoSparkEntity extends Entity implements ServerEndTickTask {
    private final List<PendingSpark> pending = new ArrayList<>(3);
    private UUID source;
    private static final class PendingSpark {
        private int delay;
        private final BlockPos soundAnchor;
        private PendingSpark(int delay, BlockPos soundAnchor) { this.delay = delay; this.soundAnchor = soundAnchor; }
    }
    public VolcanoSparkEntity(EntityType<? extends VolcanoSparkEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static UUID submit(ServerLevel level, VolcanoBulletEntity bullet, UUID previous) {
        VolcanoSparkEntity task;
        if (previous != null && level.getEntity(previous) instanceof VolcanoSparkEntity existing && !existing.isRemoved()) {
            task = existing;
        } else {
            task = ProjectileContent.VOLCANO_SPARK_TASK.get().create(level);
            if (task == null) return null;
            task.source = bullet.getUUID();
            task.setPos(bullet.position());
            if (!level.addFreshEntity(task)) {
                ThermalShockReforged.LOGGER.warn("Unable to submit volcano spark task for projectile {}", bullet.getUUID());
                return null;
            }
        }
        if (task.pending.size() < 3) task.pending.add(new PendingSpark(3, bullet.blockPosition()));
        task.setPos(bullet.position());
        ServerEndTickTasks.schedule(level, task.getUUID());
        return task.getUUID();
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (pending.isEmpty()) discard();
            else ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved()) return;
        Entity bullet = source == null ? null : level.getEntity(source);
        if (bullet instanceof VolcanoBulletEntity && !bullet.isRemoved()) setPos(bullet.position());
        var iterator = pending.iterator();
        while (iterator.hasNext()) {
            PendingSpark spark = iterator.next();
            if (--spark.delay > 0) continue;
            level.sendParticles(ProjectileContent.VOLCANO_SPARK.get(), getX(), getY(), getZ(), 1, .0001, .0001, .0001, 0);
            level.playSound(null, spark.soundAnchor, SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.NEUTRAL, .5F, 1);
            if (bullet instanceof VolcanoBulletEntity) bullet.discard();
            iterator.remove();
        }
        if (pending.isEmpty()) discard();
        else ServerEndTickTasks.schedule(level, getUUID());
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (source != null) output.putString("VolcanoSource", source.toString());
        ListTag sparks = new ListTag();
        for (PendingSpark spark : pending) {
            CompoundTag value = new CompoundTag();
            value.putInt("Delay", spark.delay); value.putLong("SoundAnchor", spark.soundAnchor.asLong());
            sparks.add(value);
        }
        tag.put("PendingSparks", sparks);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        source = input.getString("VolcanoSource").map(java.util.UUID::fromString).orElse(null);
        pending.clear();
        ListTag sparks = tag.getList("PendingSparks", Tag.TAG_COMPOUND);
        for (int i = 0; i < Math.min(3, sparks.size()); i++) {
            CompoundTag value = sparks.getCompound(i);
            pending.add(new PendingSpark(Math.clamp(value.getInt("Delay"), 1, 3), BlockPos.of(value.getLong("SoundAnchor"))));
        }
        if (!Double.isFinite(getX()) || !Double.isFinite(getY()) || !Double.isFinite(getZ())) pending.clear();
    }
}
