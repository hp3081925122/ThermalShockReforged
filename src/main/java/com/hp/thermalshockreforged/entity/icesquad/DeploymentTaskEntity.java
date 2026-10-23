package com.hp.thermalshockreforged.entity.icesquad;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.UUID;

public final class DeploymentTaskEntity extends Entity implements ServerEndTickTask {
    private boolean armed;
    private boolean consumed;
    private int mode;
    private int remaining;
    private Vec3 anchor = Vec3.ZERO;
    private String dimension = "";
    private UUID source;
    public DeploymentTaskEntity(EntityType<? extends DeploymentTaskEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel server, Vec3 anchor, UUID source, boolean requestPod) {
        DeploymentTaskEntity task = IceSquadContent.DEPLOYMENT_TASK.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
        if (task == null) return false;
        task.armed = true; task.mode = requestPod ? 0 : 1; task.remaining = requestPod ? 22 : 1; task.anchor = anchor; task.setPos(anchor);
        task.source = source; task.dimension = server.dimension().identifier().toString();
        return server.addFreshEntity(task);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && !consumed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    public boolean canTeleport(Level from, Level to) { return false; }
    @Override public boolean hurtServer(ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) { return false; }
    @Override public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;
        if (!armed || consumed || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
        ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override public void endTick(ServerLevel server) {
        if (!armed || consumed || isRemoved() || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
        if (--remaining > 0) { ServerEndTickTasks.schedule(server, getUUID()); return; }
        consumed = true; discard();
        BlockPos spawn = BlockPos.containing(anchor.x + 0.5, anchor.y, anchor.z + 0.5);
        if (mode == 0) {
            var pod = IceSquadContent.DROP_POD.get().spawn(server, spawn, EntitySpawnReason.MOB_SUMMONED);
            if (pod != null) pod.setDeltaMovement(Vec3.ZERO);
            server.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("message.thermal_shock_reforged.deployment.received"), false);
            ThermalShockReforged.LOGGER.debug("Blizzard pod request consumed: task={}, source={}, pod={}", getUUID(), source, pod == null ? null : pod.getUUID());
        } else {
            var gunner = IceSquadContent.GUNNER.get().spawn(server, spawn, EntitySpawnReason.MOB_SUMMONED);
            if (gunner != null) gunner.setYRot(server.getRandom().nextFloat() * 360);
            var burner = IceSquadContent.BURNER.get().spawn(server, spawn, EntitySpawnReason.MOB_SUMMONED);
            if (burner != null) burner.setYRot(server.getRandom().nextFloat() * 360);
            ThermalShockReforged.LOGGER.debug("Blizzard deployment consumed: task={}, source={}, gunner={}, burner={}", getUUID(), source, gunner == null ? null : gunner.getUUID(), burner == null ? null : burner.getUUID());
        }
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        output.putBoolean("Armed", armed); output.putBoolean("Consumed", consumed); output.putInt("Mode", mode); output.putInt("Remaining", remaining);
        output.putDouble("AnchorX", anchor.x); output.putDouble("AnchorY", anchor.y); output.putDouble("AnchorZ", anchor.z); output.putString("DeploymentDimension", dimension);
        if (source != null) output.putString("DeploymentSource", source.toString());
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        armed = input.getBooleanOr("Armed", false); consumed = input.getBooleanOr("Consumed", false); mode = input.getIntOr("Mode", 0);
        remaining = Math.clamp(input.getIntOr("Remaining", 0), 1, mode == 0 ? 22 : 1);
        anchor = new Vec3(input.getDoubleOr("AnchorX", 0.0), input.getDoubleOr("AnchorY", 0.0), input.getDoubleOr("AnchorZ", 0.0)); dimension = input.getStringOr("DeploymentDimension", "");
        source = input.getString("DeploymentSource").map(java.util.UUID::fromString).orElse(null);
        if (mode < 0 || mode > 1 || !Double.isFinite(anchor.x) || !Double.isFinite(anchor.y) || !Double.isFinite(anchor.z)) armed = false;
        noPhysics = true; setNoGravity(true);
    }
}
