package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.cavebloomer.BloomlingEntity;
import com.hp.thermalshockreforged.entity.cavebloomer.CavebloomerContent;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.EventHooks;

public final class SuspiciousCavebloomerBlockEntity extends GeoDecorBlockEntity {
    private int remaining, delay;
    private long startedAt = Long.MIN_VALUE;
    private boolean emitting, summoning;
    public SuspiciousCavebloomerBlockEntity(BlockPos pos, BlockState state) { super(GeoDecorKind.SUSPICIOUS_CAVEBLOOMER, pos, state); }
    public void interact(ServerLevel server) {
        if (isRemoved() || emitting || summoning || remaining > 0 || server.getBlockEntity(worldPosition) != this) return;
        if (getPersistentData().getDoubleOr("phase", 0) != 1) {
            getPersistentData().putDouble("phase", 1); remaining = 3; delay = 6; startedAt = server.getGameTime(); setChanged();
            server.setBlock(worldPosition, getBlockState().setValue(GeoDecorBlock.ANIMATION, 1).setValue(SuspiciousCavebloomerBlock.SPAWNING, true), 3);
            emitBudding(server);
            return;
        }
        var center = Vec3.atLowerCornerOf(worldPosition);
        if (!server.getEntitiesOfClass(BloomlingEntity.class, AABB.ofSize(center, 32, 32, 32), bloom -> !bloom.dandy()).isEmpty()) return;
        summonBoss(server);
    }
    public void tickSummons(ServerLevel server) {
        if (isRemoved() || server.getBlockEntity(worldPosition) != this || server.getGameTime() == startedAt) return;
        if (remaining <= 0) { updateTicker(server); return; }
        if (--delay > 0) { setChanged(); return; }
        remaining--; delay = remaining > 0 ? 7 : 0; setChanged();
        emitBudding(server);
        if (!isRemoved() && server.getBlockEntity(worldPosition) == this) updateTicker(server);
    }
    private void emitBudding(ServerLevel server) {
        emitting = true;
        try {
            var pos = BlockPos.containing(worldPosition.getX() + Mth.nextDouble(server.getRandom(), -6, 6), worldPosition.getY() + 3, worldPosition.getZ() + Mth.nextDouble(server.getRandom(), -6, 6));
            var bloom = CavebloomerContent.BUDDING.get().spawn(server, pos, EntitySpawnReason.MOB_SUMMONED);
            if (bloom != null) bloom.setYRot(server.getRandom().nextFloat() * 360);
        } finally { emitting = false; }
    }
    private void summonBoss(ServerLevel server) {
        summoning = true;
        try {
            var boss = CavebloomerContent.ANGRY.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
            if (boss == null) return;
            boss.moveTo(worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5, Mth.wrapDegrees(server.getRandom().nextFloat() * 360), 0);
            boss.yHeadRot = boss.getYRot(); boss.yBodyRot = boss.getYRot();
            EventHooks.finalizeMobSpawn(boss, server, server.getCurrentDifficultyAt(worldPosition), EntitySpawnReason.MOB_SUMMONED, null);
            if (boss.isSpawnCancelled() || boss.isRemoved() || isRemoved() || server.getBlockEntity(worldPosition) != this) { boss.discard(); return; }
            boss.playAmbientSound(); boss.setYRot(server.getRandom().nextFloat() * 360);
            if (!server.addFreshEntity(boss)) { boss.discard(); return; }
            if (server.getBlockEntity(worldPosition) == this) server.destroyBlock(worldPosition, false);
            server.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("message.thermal_shock_reforged.cavebloomer.awoken"), false);
            var sound = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "cavebloomer_detonates"));
            server.playSound(null, worldPosition, sound, SoundSource.HOSTILE, 0.6F, 3);
            server.sendParticles(CavebloomerContent.NUKE_CLOUD.get(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 1, 0, 0, 0, 1);
        } finally { summoning = false; }
    }
    private void updateTicker(ServerLevel server) {
        boolean ticking = remaining > 0;
        if (getBlockState().getValue(SuspiciousCavebloomerBlock.SPAWNING) != ticking) {
            var state = getBlockState().setValue(SuspiciousCavebloomerBlock.SPAWNING, ticking);
            if (!ticking) state = state.setValue(GeoDecorBlock.ANIMATION, 0);
            server.setBlock(worldPosition, state, 3);
        }
    }
    @Override public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel server) updateTicker(server);
    }
    @Override protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output); output.putInt("CavebloomerSummonsRemaining", remaining); output.putInt("CavebloomerSummonDelay", delay); tag.putLong("CavebloomerSummonStartedAt", startedAt);
    }
    @Override protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input); remaining = Math.clamp(input.getIntOr("CavebloomerSummonsRemaining", 0), 0, 3); delay = Math.clamp(input.getIntOr("CavebloomerSummonDelay", 0), 0, 7);
        startedAt = input.getString("CavebloomerSummonStartedAt").isPresent() ? input.getLongOr("CavebloomerSummonStartedAt", Long.MIN_VALUE) : Long.MIN_VALUE;
        if (getPersistentData().getDoubleOr("phase", 0) != 1) { remaining = 0; delay = 0; }
    }
}
