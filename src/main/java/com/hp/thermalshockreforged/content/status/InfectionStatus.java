package com.hp.thermalshockreforged.content.status;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.Comparator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import net.neoforged.neoforge.event.tick.EntityTickEvent;

public final class InfectionStatus {
    private static final String KEY = "thermal_shock_reforged:infection_tasks";
    private static final Identifier LOCK = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "infection_movement_lock");
    private static final TagKey<EntityType<?>> INFECTING_MOBS = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "infecting_mobs"));
    private static final Identifier GRAVER = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "graver");
    private InfectionStatus() { }

    public static void onAttack(LivingEntity patient, DamageSource source) {
        Entity attacker = source.getEntity();
        if (!patient.level().isClientSide() && attacker != null && attacker.getType().is(INFECTING_MOBS)
                && !BuiltInRegistries.ENTITY_TYPE.getKey(patient.getType()).equals(GRAVER)) {
            patient.addEffect(new MobEffectInstance(StatusContent.INFECTED, 200, 1));
        }
    }

    public static void onRemoved(LivingEntity patient) {
        if (!(patient.level() instanceof ServerLevel level) || !patient.isAlive() || patient.hasEffect(StatusContent.PURIFIED)) return;
        CompoundTag state = state(patient);
        String dimension = level.dimension().identifier().toString();
        if (!state.getString("Dimension").equals(dimension)) {
            cancel(patient);
            state = state(patient);
        }
        state.putString("Dimension", dimension);
        if (!(patient instanceof Player)) {
            state.putInt("LockRemaining", 30);
            state.putLong("LockCreated", level.getGameTime());
            applyLock(patient);
        }
        ListTag tasks = state.getList("Tasks", Tag.TAG_COMPOUND);
        if (tasks.size() >= 20) return;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.getCompound(i).getLong("Created") == level.getGameTime()) return;
        }
        level.sendParticles(StatusContent.BLOOD_SPIRAL.get(), patient.getX(), patient.getY() + patient.getEyeHeight() - 0.2, patient.getZ(), 0, 0, 0, 0, 0);
        CompoundTag task = new CompoundTag();
        task.putInt("Remaining", 20);
        task.putLong("Created", level.getGameTime());
        task.putDouble("X", patient.getX());
        task.putDouble("Y", patient.getY());
        task.putDouble("Z", patient.getZ());
        task.putBoolean("Burst", !(patient instanceof Player));
        tasks.add(task);
        state.put("Tasks", tasks);
    }

    private static CompoundTag state(LivingEntity entity) {
        CompoundTag data = entity.getPersistentData();
        if (!data.contains(KEY, Tag.TAG_COMPOUND)) data.put(KEY, new CompoundTag());
        return data.getCompound(KEY);
    }

    private static void applyLock(LivingEntity entity) {
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && !speed.hasModifier(LOCK)) speed.addTransientModifier(new AttributeModifier(LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    public static void cancel(LivingEntity entity) {
        entity.getPersistentData().remove(KEY);
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) speed.removeModifier(LOCK);
    }

    public static void tick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity patient) || !(patient.level() instanceof ServerLevel level)) return;
        if (!patient.getPersistentData().contains(KEY, Tag.TAG_COMPOUND)) return;
        CompoundTag state = state(patient);
        if (!patient.isAlive() || !state.getString("Dimension").equals(level.dimension().identifier().toString())) {
            cancel(patient);
            return;
        }
        int lock = state.getInt("LockRemaining");
        if (lock > 0) {
            applyLock(patient);
            if (state.getLong("LockCreated") != level.getGameTime()) {
                lock--;
                state.putInt("LockRemaining", lock);
            }
            if (lock == 0) {
                AttributeInstance speed = patient.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speed != null) speed.removeModifier(LOCK);
            }
        }
        ListTag tasks = state.getList("Tasks", Tag.TAG_COMPOUND);
        while (tasks.size() > 20) tasks.remove(tasks.size() - 1);
        ListTag ready = new ListTag();
        for (int i = 0; i < tasks.size();) {
            CompoundTag task = tasks.getCompound(i);
            if (task.getLong("Created") == level.getGameTime()) { i++; continue; }
            int remaining = task.getInt("Remaining") - 1;
            task.putInt("Remaining", remaining);
            if (remaining <= 0) { ready.add(task); tasks.remove(i); } else i++;
        }
        state.put("Tasks", tasks);
        for (int i = 0; i < ready.size(); i++) {
            if (!patient.isAlive() || patient.isRemoved() || !patient.level().dimension().equals(level.dimension())) {
                cancel(patient);
                return;
            }
            execute(patient, level, ready.getCompound(i));
        }
        if (state.getList("Tasks", Tag.TAG_COMPOUND).isEmpty() && state.getInt("LockRemaining") <= 0) cancel(patient);
    }

    private static void execute(LivingEntity patient, ServerLevel level, CompoundTag task) {
        if (!(patient instanceof Player) || patient.getRandom().nextDouble() < 0.5) {
            patient.addEffect(new MobEffectInstance(StatusContent.INFECTED, (int) Math.round(120 + patient.getRandom().nextDouble() * 80), 1, false, true));
        }
        if (!task.getBoolean("Burst")) return;
        Vec3 origin = new Vec3(task.getDouble("X"), task.getDouble("Y"), task.getDouble("Z"));
        level.sendParticles(StatusContent.BLOOD_ATTACK.get(), origin.x, origin.y + patient.getEyeHeight() - 0.2, origin.z, 0, 0, 0, 0, 0);
        for (Entity target : level.getEntitiesOfClass(Entity.class, new AABB(origin, origin).inflate(2.25), target -> target != patient)
                .stream().sorted(Comparator.comparingDouble(target -> target.distanceToSqr(origin))).toList()) {
            if (!patient.isAlive() || patient.isRemoved() || !patient.level().dimension().equals(level.dimension())) {
                cancel(patient);
                return;
            }
            target.hurt(level.damageSources().magic(), 5.0F);
            if (target instanceof LivingEntity living) living.addEffect(new MobEffectInstance(StatusContent.INFECTED, (int) Math.round(220 + patient.getRandom().nextDouble() * 80), 1, false, true));
        }
    }



    public static void leave(EntityLeaveLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity patient) || event.getLevel().isClientSide()) return;
        Entity.RemovalReason reason = patient.getRemovalReason();
        if (reason == Entity.RemovalReason.KILLED || reason == Entity.RemovalReason.DISCARDED || reason == Entity.RemovalReason.CHANGED_DIMENSION) cancel(patient);
    }

    public static void join(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity patient) || event.getLevel().isClientSide()
                || !patient.getPersistentData().contains(KEY, Tag.TAG_COMPOUND)) return;
        CompoundTag state = state(patient);
        ListTag tasks = state.getList("Tasks", Tag.TAG_COMPOUND);
        while (tasks.size() > 20) tasks.remove(tasks.size() - 1);
        state.put("Tasks", tasks);
        if (!patient.isAlive() || !state.getString("Dimension").equals(patient.level().dimension().identifier().toString())) {
            cancel(patient);
        } else if (state.getInt("LockRemaining") > 0) {
            applyLock(patient);
        }
    }
}
