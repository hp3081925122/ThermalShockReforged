package com.hp.thermalshockreforged.entity;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ZapDetonationEntity extends Entity implements ServerEndTickTask {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<ZapDetonationEntity>> TYPE = TYPES.register("zap_detonation", () -> EntityType.Builder.<ZapDetonationEntity>of(ZapDetonationEntity::new, MobCategory.MISC)
            .sized(.1F, .1F).clientTrackingRange(4).updateInterval(20).build(ThermalShockReforged.MOD_ID + ":zap_detonation"));
    private static final Identifier SOUND = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "sparkexplode");
    private UUID source;
    private UUID caster;
    private double damage;
    private boolean ignoreCaster;
    private boolean armed;
    private int remaining;
    private String dimension = "";

    public ZapDetonationEntity(EntityType<? extends ZapDetonationEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static void register(IEventBus bus) { TYPES.register(bus); }
    static ZapDetonationEntity submit(ServerLevel server, ZapWarningEntity source, Vec3 origin, UUID caster, double damage, boolean ignoreCaster, int remaining) {
        var task = TYPE.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
        if (task == null) return null;
        task.source = source.getUUID(); task.setPos(origin); task.dimension = server.dimension().identifier().toString();
        task.configure(caster, damage, ignoreCaster); task.remaining = Math.clamp(remaining, 1, 20); task.armed = true;
        if (server.addFreshEntity(task)) return task;
        task.discard(); return null;
    }
    void configure(UUID caster, double damage, boolean ignoreCaster) {
        this.caster = caster; this.damage = Double.isFinite(damage) ? Math.clamp(damage, 0, Float.MAX_VALUE) : 0; this.ignoreCaster = ignoreCaster;
    }
    void cancel() { armed = false; discard(); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || !dimension.equals(server.dimension().identifier().toString())) cancel();
            else ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel server) {
        if (isRemoved() || !armed) return;
        if (!dimension.equals(server.dimension().identifier().toString())) { cancel(); return; }
        if (--remaining > 0) { ServerEndTickTasks.schedule(server, getUUID()); return; }
        armed = false;
        ZapWarningEntity warning = source != null && server.getEntity(source) instanceof ZapWarningEntity value ? value : null;
        if (warning != null) warning.completeDetonation(getUUID());
        server.playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(SOUND), SoundSource.NEUTRAL, 1, 2);
        var targets = server.getEntitiesOfClass(Entity.class, new AABB(position(), position()).inflate(1.5), entity -> !(entity instanceof ItemEntity)
                && !(entity instanceof ZapDetonationEntity) && (!ignoreCaster || caster == null || !caster.equals(entity.getUUID())));
        targets.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(position())));
        var damageSource = damageSources().lightningBolt();
        for (Entity target : targets) target.hurt(damageSource, (float) damage);
        ThermalShockReforged.LOGGER.debug("Zap detonation completed: task={}, warning={}, targets={}", getUUID(), source, targets.size());
        if (warning != null && !warning.isRemoved()) warning.discard();
        discard();
    }
    @Override public void remove(RemovalReason reason) {
        if (reason.shouldDestroy() && level() instanceof ServerLevel server && source != null && server.getEntity(source) instanceof ZapWarningEntity warning) {
            armed = false; warning.completeDetonation(getUUID());
        }
        super.remove(reason);
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (source != null) output.putString("Warning", source.toString()); if (caster != null) output.putString("Caster", caster.toString());
        output.putDouble("Damage", damage); output.putBoolean("IgnoreCaster", ignoreCaster); output.putBoolean("Armed", armed);
        output.putInt("RemainingEndTicks", remaining); output.putString("Dimension", dimension);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        source = input.getString("Warning").map(java.util.UUID::fromString).orElse(null);
        configure(input.getString("Caster").map(java.util.UUID::fromString).orElse(null), input.getDoubleOr("Damage", 0.0), input.getBooleanOr("IgnoreCaster", false));
        remaining = Math.clamp(input.getIntOr("RemainingEndTicks", 0), 0, 20); dimension = input.getStringOr("Dimension", "");
        armed = input.getBooleanOr("Armed", false) && source != null && remaining > 0;
        noPhysics = true; setNoGravity(true);
    }
}
