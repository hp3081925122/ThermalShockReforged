package com.hp.thermalshockreforged.entity.weaponcore;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ambient.AmbientContent;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class SupernovaBlastEntity extends Entity implements ServerEndTickTask {
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) { return false; }
    private UUID sourceId;
    private int remaining;
    private boolean armed;
    public SupernovaBlastEntity(EntityType<? extends SupernovaBlastEntity> type, Level level) { super(type, level); noPhysics = true; }
    public void arm(PrismaticSupernovaEntity source) { sourceId = source.getUUID(); remaining = 40; armed = true; setPos(source.position()); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) { if (armed) ServerEndTickTasks.schedule(server, getUUID()); else discard(); } }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        var source = sourceId == null ? null : level.getEntity(sourceId);
        if (source instanceof PrismaticSupernovaEntity) setPos(source.position());
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        armed = false;
        var origin = position();
        discard();
        level.playSound(null, BlockPos.containing(origin), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 1, 3);
        level.playSound(null, BlockPos.containing(origin), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "supernova_blast")), SoundSource.NEUTRAL, .7F, 1);
        if (source instanceof PrismaticSupernovaEntity) source.discard();
        level.sendParticles(AmbientContent.PRISMATIC_BOOM.get(), origin.x, origin.y, origin.z, 1, .001, .001, .001, 0);
        var damage = new DamageSource(level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.EXPLOSION));
        var targets = level.getEntitiesOfClass(LivingEntity.class, new AABB(origin, origin).inflate(4), entity -> !(entity instanceof Player || entity instanceof PrismaticSupernovaEntity));
        targets.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(origin)));
        for (var target : targets) target.hurt(damage, 9);
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { if (sourceId != null) output.putString("Source", sourceId.toString()); output.putInt("Remaining", remaining); output.putBoolean("Armed", armed); }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { sourceId = input.getString("Source").map(java.util.UUID::fromString).orElse(null); remaining = Math.clamp(input.getIntOr("Remaining", 0), 0, 40); armed = input.getBooleanOr("Armed", false) && sourceId != null; }
}
