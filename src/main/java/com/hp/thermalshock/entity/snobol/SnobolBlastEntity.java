package com.hp.thermalshock.entity.snobol;

import net.minecraft.nbt.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.Vec3;
import java.util.*;

public final class SnobolBlastEntity extends Entity {
    private UUID owner;
    private boolean detached;
    private final List<Blast> pending = new ArrayList<>();

    public SnobolBlastEntity(EntityType<? extends SnobolBlastEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    void arm(ExplosiveSnobolEntity caster) {
        owner = caster.getUUID();
        setPos(caster.position());
    }

    void enqueue(Vec3 position, double damage) {
        if (pending.isEmpty() && !detached) pending.add(new Blast(position, Math.max(0, damage), 20, level().getGameTime()));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;
        Entity caster = owner == null ? null : server.getEntity(owner);
        if (!detached && (!(caster instanceof ExplosiveSnobolEntity living) || !living.isAlive())) {
            discard();
            return;
        }
        var due = new ArrayList<Blast>();
        for (var blast : pending) if (blast.startedAt != server.getGameTime() && --blast.remaining <= 0) due.add(blast);
        pending.removeAll(due);
        if (!due.isEmpty() && !detached) {
            detached = true;
            caster.discard();
        }
        for (var blast : due) detonate(server, blast.position, blast.damage);
        if (pending.isEmpty()) discard();
    }

    static void detonate(ServerLevel server, Vec3 position, double damage) {
        var calculator = new ExplosionDamageCalculator() {
            @Override
            public float getEntityDamageAmount(Explosion explosion, Entity target) {
                return (float) (super.getEntityDamageAmount(explosion, target) * damage);
            }
            @Override
            public float getKnockbackMultiplier(Entity target) {
                return target instanceof LivingEntity living ? (float) (0.6 * (1 - living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE))) : 0.6F;
            }
        };
        server.explode(null, null, calculator, position.x, position.y, position.z, 4, false, Level.ExplosionInteraction.MOB);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (owner != null) tag.putUUID("Owner", owner);
        tag.putBoolean("Detached", detached);
        var tasks = new ListTag();
        for (var blast : pending) {
            var task = new CompoundTag();
            task.putDouble("X", blast.position.x);
            task.putDouble("Y", blast.position.y);
            task.putDouble("Z", blast.position.z);
            task.putDouble("Damage", blast.damage);
            task.putInt("Remaining", blast.remaining);
            tasks.add(task);
        }
        tag.put("PendingBlasts", tasks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        owner = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        detached = tag.getBoolean("Detached");
        pending.clear();
        var tasks = tag.getList("PendingBlasts", Tag.TAG_COMPOUND);
        for (int i = 0; i < Math.min(1, tasks.size()); i++) {
            var task = tasks.getCompound(i);
            double damage = Math.max(0, task.getDouble("Damage"));
            if (!Double.isFinite(damage)) damage = 0;
            pending.add(new Blast(new Vec3(task.getDouble("X"), task.getDouble("Y"), task.getDouble("Z")), damage, Math.max(1, Math.min(20, task.getInt("Remaining"))), Long.MIN_VALUE));
        }
    }

    private static final class Blast {
        private final Vec3 position;
        private final double damage;
        private int remaining;
        private final long startedAt;
        private Blast(Vec3 position, double damage, int remaining, long startedAt) {
            this.position = position;
            this.damage = damage;
            this.remaining = remaining;
            this.startedAt = startedAt;
        }
    }
}
