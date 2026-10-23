package com.hp.thermalshockreforged.entity.sanctum;

import com.hp.thermalshockreforged.world.sanctum.SanctumRelease;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class PylonActivationEntity extends Entity implements ServerEndTickTask {
    private Vec3 anchor = Vec3.ZERO;
    private String dimension = "";
    private List<UUID> recipients = List.of();
    private int remaining = 80;
    private int phase;
    private boolean armed;
    public PylonActivationEntity(EntityType<? extends PylonActivationEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, Vec3 origin) {
        var task = SanctumContent.PYLON_ACTIVATION.get().create(level);
        if (task == null) return false;
        task.anchor = origin; task.setPos(origin); task.dimension = level.dimension().identifier().toString();
        task.recipients = level.players().stream().map(Entity::getUUID).toList(); task.armed = true;
        return level.addFreshEntity(task);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && phase < 2 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || phase >= 2 || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
            setPos(anchor); ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || !armed || phase >= 2 || !dimension.equals(level.dimension().identifier().toString())) return;
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        if (phase == 0) {
            phase = 1; remaining = 40;
            for (UUID id : recipients) {
                var player = level.getServer().getPlayerList().getPlayer(id);
                if (player != null) player.displayClientMessage(Component.translatable("message.thermal_shock_reforged.sopel.arrival"), false);
            }
            recipients = List.of(); ServerEndTickTasks.schedule(level, getUUID());
        } else {
            phase = 2;
            level.playSound(null, BlockPos.containing(anchor), SoundEvents.END_PORTAL_SPAWN, SoundSource.AMBIENT, 1, -3);
            SanctumRelease.release(level, anchor);
            discard();
        }
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        output.putDouble("PylonAnchorX", anchor.x); output.putDouble("PylonAnchorY", anchor.y); output.putDouble("PylonAnchorZ", anchor.z);
        output.putString("PylonDimension", dimension); output.putInt("PylonRemaining", remaining); output.putInt("PylonPhase", phase); output.putBoolean("PylonArmed", armed);
        var players = new ListTag();
        for (UUID id : recipients) { var entry = new CompoundTag(); entry.putUUID("Player", id); players.add(entry); }
        tag.put("PylonRecipients", players);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        anchor = new Vec3(input.getDoubleOr("PylonAnchorX", 0.0), input.getDoubleOr("PylonAnchorY", 0.0), input.getDoubleOr("PylonAnchorZ", 0.0));
        dimension = input.getStringOr("PylonDimension", ""); phase = Math.clamp(input.getIntOr("PylonPhase", 0), 0, 2);
        remaining = Math.clamp(input.getIntOr("PylonRemaining", 0), 1, phase == 0 ? 80 : 40); armed = input.getBooleanOr("PylonArmed", false);
        var ids = new LinkedHashSet<UUID>(); var players = tag.getList("PylonRecipients", Tag.TAG_COMPOUND);
        if (phase == 0) for (int i = 0; i < players.size(); i++) { var entry = players.getCompound(i); if (entry.hasUUID("Player")) ids.add(entry.getUUID("Player")); }
        recipients = List.copyOf(ids);
        if (!Double.isFinite(anchor.x) || !Double.isFinite(anchor.y) || !Double.isFinite(anchor.z)) armed = false;
    }
}
