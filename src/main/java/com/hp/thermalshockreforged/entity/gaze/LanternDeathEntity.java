package com.hp.thermalshockreforged.entity.gaze;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.Vec3;

public final class LanternDeathEntity extends Entity implements ServerEndTickTask {
    private int elapsed;
    private boolean armed;
    private double damageScale = 1;
    public LanternDeathEntity(EntityType<? extends LanternDeathEntity> type, Level level) { super(type, level); noPhysics = true; }
    static void submit(ServerLevel level, Vec3 position, double attackDamage) {
        var task = GazeContent.LANTERN_DEATH.get().create(level);
        if (task == null) return;
        task.setPos(position);
        task.armed = true;
        task.damageScale = Double.isFinite(attackDamage) ? Math.clamp(attackDamage / 3, 0, 1000000) : 0;
        if (level.addFreshEntity(task) && level.getEntity(task.getUUID()) == task) task.pulse(level);
        else ThermalShockReforged.LOGGER.debug("Lanternbearer death task submission rejected at {}", position);
    }
    private void pulse(ServerLevel level) {
        level.sendParticles(ParticleTypes.EXPLOSION, getX(), getY(), getZ(), 1, .5, .5, .5, 0);
        level.playSound(null, BlockPos.containing(position()), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "mob_death_explode")), SoundSource.HOSTILE, 1, 3);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) { if (armed) ServerEndTickTasks.schedule(server, getUUID()); else discard(); } }
    @Override public void endTick(ServerLevel level) {
        if (!armed || isRemoved()) return;
        var anchor = blockPosition();
        if (elapsed >= 23 && !level.hasChunksAt(anchor.getX() - 8, anchor.getZ() - 8, anchor.getX() + 8, anchor.getZ() + 8)) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        elapsed++;
        if (elapsed <= 16 && elapsed % 4 == 0) pulse(level);
        if (elapsed < 24) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        armed = false;
        var origin = position();
        discard();
        var calculator = new ExplosionDamageCalculator() {
            @Override public float getEntityDamageAmount(Explosion explosion, Entity target) {
                float diameter = explosion.radius() * 2;
                double exposure = (1 - Math.sqrt(target.distanceToSqr(explosion.center())) / diameter) * Explosion.getSeenPercent(explosion.center(), target);
                int originalDamage = (int)((exposure * exposure + exposure) / 2 * 7 * diameter + 1);
                return (float)(originalDamage * damageScale);
            }
            @Override public float getKnockbackMultiplier(Entity target) { return target instanceof LivingEntity living ? (float)(1 - living.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE)) : 1; }
        };
        level.explode(null, null, calculator, origin, 3, false, Level.ExplosionInteraction.MOB);
        level.sendParticles(ParticleTypes.FLAME, origin.x, origin.y, origin.z, 25, 1, 1, 1, 1);
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { output.putInt("Elapsed", elapsed); output.putBoolean("Armed", armed); output.putDouble("DamageScale", damageScale); }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { elapsed = Math.clamp(input.getIntOr("Elapsed", 0), 0, 24); armed = input.getBooleanOr("Armed", false); double saved = input.getDoubleOr("DamageScale", 0.0); damageScale = Double.isFinite(saved) ? Math.clamp(saved, 0, 1000000) : 0; }
}
