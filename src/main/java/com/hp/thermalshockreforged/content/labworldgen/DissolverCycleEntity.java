package com.hp.thermalshockreforged.content.labworldgen;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.labmachinist.LabMachinistEntity;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.Comparator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;

public final class DissolverCycleEntity extends Entity implements ServerEndTickTask {
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) { return false; }
    private BlockPos anchor = BlockPos.ZERO;
    private String dimension = "";
    private int elapsed;
    private int phase;
    private int voices;
    private boolean armed;
    public DissolverCycleEntity(EntityType<? extends DissolverCycleEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    void prepare(ServerLevel level, BlockPos pos) { anchor = pos.immutable(); dimension = level.dimension().identifier().toString(); armed = true; setPos(pos.getX(), pos.getY(), pos.getZ()); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canTeleport(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && phase < 5 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || phase >= 5 || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
            setPos(anchor.getX(), anchor.getY(), anchor.getZ());
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || !armed || phase >= 5 || !dimension.equals(level.dimension().identifier().toString())) return;
        int due = switch (phase) { case 0 -> 20; case 1 -> 25; case 2 -> 30; case 3 -> 33; default -> 50; };
        if (elapsed + 1 >= due && phase < 3 && !sphereLoaded(level, 1 << phase)) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        elapsed++;
        if (elapsed >= due) {
            int completed = phase++;
            if (completed < 3) {
                dissolve(level, 1 << completed);
                if (completed == 2) { notice(level); if (voices == 0) phase = 5; }
            } else {
                for (int i = 0; i < voices; i++) sound(level, completed == 3 ? "qo_em1_speak" : "qo_em2_speak", SoundSource.VOICE, 1);
            }
        }
        if (phase >= 5) discard(); else ServerEndTickTasks.schedule(level, getUUID());
    }
    private boolean sphereLoaded(ServerLevel level, int radius) {
        for (int x = -radius; x <= radius; x++) for (int z = -radius; z <= radius; z++)
            if (x * x + z * z <= radius * radius && !level.hasChunkAt(anchor.offset(x, 0, z))) return false;
        return true;
    }
    private void dissolve(ServerLevel level, int radius) {
        sound(level, "hailfire", SoundSource.BLOCKS, 3);
        var dissolving = MaterialContent.blocks().get("dissolving_impervium").get();
        for (int y = -radius; y <= radius; y++) for (int x = -radius; x <= radius; x++) for (int z = -radius; z <= radius; z++) {
            if (x * x + y * y + z * z > radius * radius) continue;
            BlockPos pos = anchor.offset(x, y, z);
            if (level.getBlockState(pos).is(dissolving)) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        ThermalShockReforged.LOGGER.debug("Dissolver phase {} completed at {} in {}", phase, anchor, dimension);
    }
    private void notice(ServerLevel level) {
        Vec3 center = Vec3.atLowerCornerOf(anchor);
        var found = level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(4.5), entity -> true).stream().sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(center))).toList();
        for (var entity : found) if (entity instanceof LabMachinistEntity npc) { npc.triggerAnim("procedure", "notice"); voices++; }
    }
    private void sound(ServerLevel level, String name, SoundSource source, float pitch) { level.playSound(null, anchor, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, name)), source, 1, pitch); }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { output.putLong("DissolverAnchor", anchor.asLong()); output.putString("DissolverDimension", dimension); output.putInt("DissolverElapsed", elapsed); output.putInt("DissolverPhase", phase); output.putInt("DissolverVoices", voices); output.putBoolean("DissolverArmed", armed); }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        anchor = BlockPos.of(input.getLongOr("DissolverAnchor", 0L)); dimension = input.getStringOr("DissolverDimension", ""); elapsed = Math.clamp(input.getIntOr("DissolverElapsed", 0), 0, 50); phase = Math.clamp(input.getIntOr("DissolverPhase", 0), 0, 5); voices = Math.max(0, input.getIntOr("DissolverVoices", 0)); armed = input.getBooleanOr("DissolverArmed", false) && input.getString("DissolverAnchor").isPresent(); noPhysics = true; setNoGravity(true);
    }
}
