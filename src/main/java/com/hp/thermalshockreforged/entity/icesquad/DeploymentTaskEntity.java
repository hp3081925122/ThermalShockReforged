package com.hp.thermalshockreforged.entity.icesquad;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.UUID;

public final class DeploymentTaskEntity extends Entity {
    private boolean armed;
    private boolean consumed;
    private int mode;
    private int remaining;
    private Vec3 anchor = Vec3.ZERO;
    private String dimension = "";
    private UUID source;
    private long createdAt = Long.MIN_VALUE;
    public DeploymentTaskEntity(EntityType<? extends DeploymentTaskEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel server, Vec3 anchor, UUID source, boolean requestPod) {
        DeploymentTaskEntity task = IceSquadContent.DEPLOYMENT_TASK.get().create(server);
        if (task == null) return false;
        task.armed = true; task.mode = requestPod ? 0 : 1; task.remaining = requestPod ? 22 : 1; task.anchor = anchor; task.setPos(anchor);
        task.source = source; task.dimension = server.dimension().location().toString(); task.createdAt = server.getGameTime();
        return server.addFreshEntity(task);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;
        if (!armed || consumed || !dimension.equals(server.dimension().location().toString())) { discard(); return; }
        if (server.getGameTime() == createdAt || --remaining > 0) return;
        consumed = true; discard();
        BlockPos spawn = BlockPos.containing(anchor.x + 0.5, anchor.y, anchor.z + 0.5);
        if (mode == 0) {
            var pod = IceSquadContent.DROP_POD.get().spawn(server, spawn, MobSpawnType.MOB_SUMMONED);
            if (pod != null) pod.setDeltaMovement(Vec3.ZERO);
            server.getServer().getPlayerList().broadcastSystemMessage(Component.literal("§dMessage recieved!"), false);
            ThermalShockReforged.LOGGER.debug("Blizzard pod request consumed: task={}, source={}, pod={}", getUUID(), source, pod == null ? null : pod.getUUID());
        } else {
            var gunner = IceSquadContent.GUNNER.get().spawn(server, spawn, MobSpawnType.MOB_SUMMONED);
            if (gunner != null) gunner.setYRot(server.random.nextFloat() * 360);
            var burner = IceSquadContent.BURNER.get().spawn(server, spawn, MobSpawnType.MOB_SUMMONED);
            if (burner != null) burner.setYRot(server.random.nextFloat() * 360);
            ThermalShockReforged.LOGGER.debug("Blizzard deployment consumed: task={}, source={}, gunner={}, burner={}", getUUID(), source, gunner == null ? null : gunner.getUUID(), burner == null ? null : burner.getUUID());
        }
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("Armed", armed); tag.putBoolean("Consumed", consumed); tag.putInt("Mode", mode); tag.putInt("Remaining", remaining); tag.putLong("CreatedAt", createdAt);
        tag.putDouble("AnchorX", anchor.x); tag.putDouble("AnchorY", anchor.y); tag.putDouble("AnchorZ", anchor.z); tag.putString("DeploymentDimension", dimension);
        if (source != null) tag.putUUID("DeploymentSource", source);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        armed = tag.getBoolean("Armed"); consumed = tag.getBoolean("Consumed"); mode = tag.getInt("Mode");
        remaining = Math.clamp(tag.getInt("Remaining"), 1, mode == 0 ? 22 : 1);
        anchor = new Vec3(tag.getDouble("AnchorX"), tag.getDouble("AnchorY"), tag.getDouble("AnchorZ")); dimension = tag.getString("DeploymentDimension");
        source = tag.hasUUID("DeploymentSource") ? tag.getUUID("DeploymentSource") : null; createdAt = tag.contains("CreatedAt") ? tag.getLong("CreatedAt") : Long.MIN_VALUE;
        if (mode < 0 || mode > 1 || !Double.isFinite(anchor.x) || !Double.isFinite(anchor.y) || !Double.isFinite(anchor.z)) armed = false;
        noPhysics = true; setNoGravity(true);
    }
}
