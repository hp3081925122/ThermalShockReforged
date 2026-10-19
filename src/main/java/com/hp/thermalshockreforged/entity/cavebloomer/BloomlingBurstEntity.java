package com.hp.thermalshockreforged.entity.cavebloomer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.summon.SummonLedger;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BloomlingBurstEntity extends Entity implements ServerEndTickTask {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<BloomlingBurstEntity>> TYPE = TYPES.register("bloomling_burst", () -> EntityType.Builder.<BloomlingBurstEntity>of(BloomlingBurstEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F).clientTrackingRange(4).updateInterval(20).build(ThermalShockReforged.MOD_ID + ":bloomling_burst"));
    private UUID caster;
    private UUID target;
    private WeakReference<BloomlingEntity> originalCaster;
    private Vec3 soundOrigin = Vec3.ZERO;
    private Vec3 look = Vec3.ZERO;
    private double eyeHeight;
    private double targetEyeHeight;
    private double damage;
    private long summonExpiresAt = -1;
    private int kind;
    private int remaining;
    private String dimension = "";

    public BloomlingBurstEntity(EntityType<? extends BloomlingBurstEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static void register(IEventBus bus) { TYPES.register(bus); }
    static BloomlingBurstEntity submit(ServerLevel server, BloomlingEntity owner, int kind, int remaining, Vec3 soundOrigin, UUID target, double targetEyeHeight, double damage, long summonExpiresAt) {
        var task = TYPE.get().create(server);
        if (task == null) return null;
        task.caster = owner.getUUID(); task.originalCaster = new WeakReference<>(owner); task.target = target;
        task.kind = kind; task.remaining = remaining; task.soundOrigin = soundOrigin; task.targetEyeHeight = targetEyeHeight;
        task.damage = damage; task.summonExpiresAt = summonExpiresAt; task.dimension = server.dimension().location().toString(); task.captureCaster(owner);
        if (server.addFreshEntity(task)) {
            ThermalShockReforged.LOGGER.debug("Bloomling burst committed: caster={}, task={}, kind={}, remainingEndTicks={}", owner.getUUID(), task.getUUID(), kind, remaining);
            return task;
        }
        task.discard(); return null;
    }
    void captureCaster(BloomlingEntity owner) { setPos(owner.position()); look = owner.getLookAngle(); eyeHeight = owner.getEyeHeight(); }
    private BloomlingEntity owner(ServerLevel server) {
        if (caster != null && server.getEntity(caster) instanceof BloomlingEntity owner) { originalCaster = new WeakReference<>(owner); return owner; }
        var original = originalCaster == null ? null : originalCaster.get();
        return original != null && original.level() == server ? original : null;
    }
    void cancel() { kind = 0; remaining = 0; discard(); }
    @Override public void remove(RemovalReason reason) {
        if (reason.shouldDestroy() && level() instanceof ServerLevel server) {
            kind = 0; remaining = 0;
            var owner = owner(server);
            if (owner != null) owner.completeBurst(getUUID());
        }
        super.remove(reason);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (kind != 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;
        if (!validFor(server)) { cancel(); return; }
        var owner = owner(server); if (owner != null) captureCaster(owner);
        ServerEndTickTasks.schedule(server, getUUID());
    }
    private boolean validFor(ServerLevel server) {
        return !isRemoved() && kind != 0 && remaining > 0 && dimension.equals(server.dimension().location().toString())
                && (summonExpiresAt < 0 || SummonLedger.clock(server) < summonExpiresAt);
    }
    @Override public void endTick(ServerLevel server) {
        if (!validFor(server)) { cancel(); return; }
        var owner = owner(server); if (owner != null) captureCaster(owner);
        if (--remaining > 0) { ServerEndTickTasks.schedule(server, getUUID()); return; }
        int completed = kind; kind = 0;
        if (owner != null) owner.completeBurst(getUUID());
        if (completed == 1) explodeDandy(server); else explodeBudding(server);
        if (completed == 1 && owner != null && !owner.isRemoved()) owner.discard();
        discard();
    }
    private void explodeDandy(ServerLevel server) {
        server.playSound(null, BlockPos.containing(soundOrigin), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.HOSTILE, 1, 1);
        Entity victim = target == null ? null : server.getEntity(target);
        double eye = victim == null ? targetEyeHeight : victim.getEyeHeight();
        server.sendParticles(ParticleTypes.EXPLOSION, soundOrigin.x, soundOrigin.y + eye, soundOrigin.z, 4, 0.7, 0.7, 0.7, 0);
        var center = position();
        var victims = server.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(3), entity -> entity != this && !(entity instanceof BloomlingBurstEntity) && !(entity instanceof ItemEntity)
                && !(entity instanceof BloomlingEntity bloom && bloom.dandy()) && !CavebloomerContent.isBoss(entity));
        victims.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)));
        var source = damageSources().source(DamageTypes.EXPLOSION);
        for (var entity : victims) {
            if (entity instanceof LivingEntity living && living.isBlocking()) CavebloomerCombat.damageBlockingItem(living, 1);
            else {
                entity.hurt(source, (float) damage);
                if (entity instanceof LivingEntity living) living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 220, 1));
            }
        }
    }
    private void explodeBudding(ServerLevel server) {
        server.playSound(null, BlockPos.containing(soundOrigin), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1, 1);
        server.playSound(null, BlockPos.containing(soundOrigin), SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.HOSTILE, 1, 1);
        server.sendParticles(CavebloomerContent.RED_EXPLODE.get(), getX(), getY() + eyeHeight, getZ(), 3, 1, 1, 1, 0);
        server.sendParticles(CavebloomerContent.MAGNETIC_BOOM.get(), getX(), getY() + eyeHeight, getZ(), 3, 1, 1, 1, 0);
        var center = position();
        var victims = server.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(4), entity -> !(entity instanceof BloomlingBurstEntity) && !(entity instanceof BloomlingEntity bloom && !bloom.dandy()));
        victims.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)));
        var source = damageSources().source(DamageTypes.EXPLOSION);
        for (var entity : victims) {
            if (entity instanceof LivingEntity living && living.isBlocking()) CavebloomerCombat.damageBlockingItem(living, 10);
            else {
                entity.hurt(source, (float) damage);
                CavebloomerCombat.setResistedVelocity(entity, new Vec3(look.x * 0.1, 0.3, look.z * 0.1));
            }
        }
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        if (caster != null) tag.putUUID("Caster", caster); if (target != null) tag.putUUID("Target", target);
        tag.putInt("Kind", kind); tag.putInt("Remaining", remaining); tag.putString("Dimension", dimension);
        tag.putDouble("SoundX", soundOrigin.x); tag.putDouble("SoundY", soundOrigin.y); tag.putDouble("SoundZ", soundOrigin.z);
        tag.putDouble("LookX", look.x); tag.putDouble("LookY", look.y); tag.putDouble("LookZ", look.z);
        tag.putDouble("EyeHeight", eyeHeight); tag.putDouble("TargetEyeHeight", targetEyeHeight); tag.putDouble("Damage", damage); tag.putLong("SummonExpiresAt", summonExpiresAt);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        caster = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null; target = tag.hasUUID("Target") ? tag.getUUID("Target") : null;
        kind = tag.getInt("Kind"); remaining = Math.clamp(tag.getInt("Remaining"), 0, kind == 1 ? 20 : 40); dimension = tag.getString("Dimension");
        soundOrigin = new Vec3(tag.getDouble("SoundX"), tag.getDouble("SoundY"), tag.getDouble("SoundZ"));
        look = new Vec3(tag.getDouble("LookX"), tag.getDouble("LookY"), tag.getDouble("LookZ"));
        eyeHeight = tag.getDouble("EyeHeight"); targetEyeHeight = tag.getDouble("TargetEyeHeight"); damage = tag.getDouble("Damage"); summonExpiresAt = tag.getLong("SummonExpiresAt");
        if ((kind != 1 && kind != 2) || caster == null || !finite(soundOrigin) || !finite(look) || !Double.isFinite(eyeHeight) || !Double.isFinite(targetEyeHeight) || !Double.isFinite(damage)) { kind = 0; remaining = 0; }
        else { eyeHeight = Math.clamp(eyeHeight, 0, 100); targetEyeHeight = Math.clamp(targetEyeHeight, 0, 100); damage = Math.clamp(damage, 0, Float.MAX_VALUE); }
        noPhysics = true; setNoGravity(true);
    }
    private static boolean finite(Vec3 value) { return Double.isFinite(value.x) && Double.isFinite(value.y) && Double.isFinite(value.z); }
}
