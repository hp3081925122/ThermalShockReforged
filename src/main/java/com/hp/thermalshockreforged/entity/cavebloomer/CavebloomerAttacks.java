package com.hp.thermalshockreforged.entity.cavebloomer;

import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.entity.summon.SummonLedger;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.EventHooks;

final class CavebloomerAttacks {
    private static final Identifier MOVEMENT_LOCK = Identifier.fromNamespaceAndPath("thermal_shock_reforged", "cavebloomer_movement_lock");
    private static final double[][] RING = {{0.5,0},{0.5,0.5},{0,0.5},{-0.5,0},{-0.5,-0.5},{0,-0.5},{0.5,-0.5},{-0.5,0.5}};
    private final AngryCavebloomerEntity boss;
    private final List<UUID> reservations = new ArrayList<>();
    private int skill = -1, age, cooldown, movementLock;
    private long startedAt = Long.MIN_VALUE;
    private boolean poisonRing;
    private double targetEyeHeight;
    CavebloomerAttacks(AngryCavebloomerEntity boss) { this.boss = boss; }
    void begin(Entity target) {
        if (!(boss.level() instanceof ServerLevel server) || !boss.isAlive() || target == boss || !target.isAlive() || target.level() != server || cooldown > 0 || skill != -1) return;
        skill = boss.getRandom().nextInt(4); age = 0; startedAt = server.getGameTime(); targetEyeHeight = target.getEyeHeight();
        boss.sound(SoundEvents.SHULKER_BOX_OPEN, 1, -2);
        switch (skill) {
            case 0 -> { cooldown = 55; movementLock = 10; rapidShot(server); }
            case 1 -> { cooldown = 55; movementLock = 30; poisonRing = boss.getHealth() < boss.getMaxHealth() * 0.5; boss.setDeltaMovement(0, 0.4, 0); boss.setDeltaMovement(boss.getDeltaMovement()); }
            case 2 -> { cooldown = 90; movementLock = 75; boss.sound("cavebloomer_roar", 1, 1); boss.setAnimation("prime4"); }
            case 3 -> { cooldown = 40; var look = boss.getLookAngle(); boss.setDeltaMovement(look.x * 2.6, 1, look.z * 2.6); boss.setDeltaMovement(boss.getDeltaMovement()); boss.sound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 1, -3); }
        }
        updateMovementLock();
        ServerEndTickTasks.schedule(server, boss.getUUID());
        ThermalShockReforged.LOGGER.debug("Cavebloomer skill scheduled: caster={}, skill={}, gameTime={}, phase=server-end", boss.getUUID(), skill, server.getGameTime());
    }
    void tick(ServerLevel server) {
        if (!boss.isAlive()) { cancel(server); return; }
        if (server.getGameTime() == startedAt) return;
        if (cooldown > 0) cooldown--;
        if (movementLock > 0 && --movementLock == 0) updateMovementLock();
    }
    boolean pending() { return skill != -1; }
    void endTick(ServerLevel server) {
        if (!boss.isAlive()) { cancel(server); return; }
        if (skill == -1) return;
        age++;
        switch (skill) {
            case 0 -> {
                if (age <= 38 && (age & 1) == 0) rapidShot(server);
                if (age >= 38) skill = -1;
            }
            case 1 -> {
                if (age == 20) {
                    boss.setAnimation("prime"); boss.sound(SoundEvents.SHULKER_SHOOT, 1, -2); ring(server, poisonRing, 1, 0.45F);
                    if (!poisonRing) skill = -1;
                }
                if (age == 30) { boss.sound(SoundEvents.SHULKER_SHOOT, 1, 1); ring(server, true, 1.2, 0.7F); skill = -1; }
            }
            case 2 -> {
                if (age == 37) boss.sound("cavebloomer_squeak", 0.8F, 3);
                if (age == 47) { slam(server); skill = -1; }
            }
            case 3 -> {
                if (age == 5) {
                    boss.sound("cavebloomer_squeak", 1, 1);
                    if (boss.getHealth() < boss.getMaxHealth() * 0.5) {
                        long until = SummonLedger.clock(server) + CavebloomerConfig.SUMMON_LIFETIME.get() + 8;
                        reservations.addAll(SummonLedger.get(server).reserveGroup(boss.getUUID(), 5, until, CavebloomerConfig.SUMMON_CAP.get()));
                    }
                }
                if (age >= 5 && age <= 13 && age % 2 == 1 && !reservations.isEmpty()) spawnDandy(server);
                if (age >= 13) { releaseReservations(server); skill = -1; }
            }
        }
    }
    private void rapidShot(ServerLevel server) {
        boss.sound(SoundEvents.SHULKER_SHOOT, 0.7F, 3); var look = boss.getLookAngle();
        shoot(server, false, new Vec3(boss.getX(), boss.getEyeY(), boss.getZ()), look.x * 0.5, 0.5, look.z * 0.5, 0.45F, 7, CavebloomerConfig.RAPID_DIRECT.get(), 0.6);
    }
    private void ring(ServerLevel server, boolean poison, double y, float speed) {
        var origin = new Vec3(boss.getX(), boss.getY() + targetEyeHeight, boss.getZ());
        for (var vector : RING) shoot(server, poison, origin, vector[0], y, vector[1], speed, 0, CavebloomerConfig.RING_DIRECT.get(), 0);
    }
    private void shoot(ServerLevel server, boolean poison, Vec3 origin, double x, double y, double z, float speed, float spread, double directMultiplier, double knockback) {
        var projectile = (poison ? CavebloomerContent.POISON_BOMB : CavebloomerContent.MORTAR_BOMB).get().create(server, EntitySpawnReason.MOB_SUMMONED);
        if (projectile == null) return;
        projectile.configure(boss, directMultiplier, (poison ? CavebloomerConfig.POISON_IMPACT : CavebloomerConfig.MORTAR_IMPACT).get(), knockback, (byte)5);
        projectile.setPos(origin); projectile.shoot(x, y, z, speed, spread); server.addFreshEntity(projectile);
    }
    private void slam(ServerLevel server) {
        boss.setAnimation("prime"); boss.sound("cavebloomer_detonates", 1, -3);
        server.sendParticles(CavebloomerContent.NUKE_CLOUD.get(), boss.getX(), boss.getY(), boss.getZ(), 1, 0.0001, 0.0001, 0.0001, 0);
        var center = boss.position();
        var victims = server.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(10), entity -> !(entity instanceof AngryCavebloomerEntity) && !(entity instanceof ItemEntity));
        victims.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)));
        var damage = boss.damageSources().source(DamageTypes.EXPLOSION); var look = boss.getLookAngle();
        for (var victim : victims) {
            boolean blocked = victim instanceof LivingEntity living && (living.isBlocking() || living.hasEffect(StatusContent.PARRYING));
            boolean blocking = victim instanceof LivingEntity living && living.isBlocking();
            if (victim instanceof LivingEntity living) living.addEffect(new MobEffectInstance(StatusContent.TREMORS, 5, 0, true, false));
            double amount = boss.getAttributeValue(Attributes.ATTACK_DAMAGE) * (blocked ? CavebloomerConfig.SLAM_BLOCKED : CavebloomerConfig.SLAM_DIRECT).get();
            victim.hurtServer(server, damage, Double.isFinite(amount) ? (float)Math.clamp(amount, 0, Float.MAX_VALUE) : 0);
            CavebloomerCombat.setResistedVelocity(victim, blocked ? new Vec3(look.x * 0.8, 0.2, look.z * 0.8) : new Vec3(look.x * 0.5, 0.6, look.z * 0.5));
            if (blocking && victim instanceof LivingEntity living) CavebloomerCombat.damageBlockingItem(living, 82);
        }
    }
    private void spawnDandy(ServerLevel server) {
        UUID reservation = reservations.removeFirst(); var ledger = SummonLedger.get(server);
        var child = CavebloomerContent.DANDY.get().create(server, EntitySpawnReason.MOB_SUMMONED);
        if (child == null) { ledger.release(boss.getUUID(), reservation); return; }
        long until = SummonLedger.clock(server) + CavebloomerConfig.SUMMON_LIFETIME.get();
        child.bindSummon(boss.getUUID(), until);
        var pos = boss.blockPosition(); child.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5); child.setYRot(server.getRandom().nextFloat() * 360);
        child.setYRot(child.getYRot());
        EventHooks.finalizeMobSpawn(child, server, server.getCurrentDifficultyAt(pos), EntitySpawnReason.MOB_SUMMONED, null);
        if (child.isSpawnCancelled() || child.isRemoved()) { ledger.release(boss.getUUID(), reservation); child.discard(); return; }
        child.playAmbientSound(); child.setYRot(server.getRandom().nextFloat() * 360);
        ledger.replace(boss.getUUID(), reservation, child.getUUID(), until);
        if (!server.addFreshEntity(child)) { ledger.release(boss.getUUID(), child.getUUID()); child.discard(); }
    }
    private void releaseReservations(ServerLevel server) {
        if (reservations.isEmpty()) return;
        var ledger = SummonLedger.get(server); for (var reservation : reservations) ledger.release(boss.getUUID(), reservation); reservations.clear();
    }
    void cancel(ServerLevel server) { releaseReservations(server); skill = -1; movementLock = 0; updateMovementLock(); }
    private void updateMovementLock() {
        var speed = boss.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementLock > 0 && !speed.hasModifier(MOVEMENT_LOCK)) speed.addTransientModifier(new AttributeModifier(MOVEMENT_LOCK, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        else if (movementLock == 0) speed.removeModifier(MOVEMENT_LOCK);
    }
    void save(CompoundTag root) {
        var tag = new CompoundTag(); tag.putInt("Skill", skill); tag.putInt("Age", age); tag.putInt("Cooldown", cooldown); tag.putInt("MovementLock", movementLock);
        tag.putLong("StartedAt", startedAt); tag.putBoolean("PoisonRing", poisonRing); tag.putDouble("TargetEyeHeight", targetEyeHeight);
        tag.putBoolean("EndTickAge", true);
        tag.putInt("ReservationCount", reservations.size()); for (int i = 0; i < reservations.size(); i++) tag.putString("Reservation" + i, reservations.get(i).toString());
        root.put("CavebloomerAttacks", tag);
    }
    void load(CompoundTag root) {
        var tag = root.getCompound("CavebloomerAttacks").orElseGet(CompoundTag::new); skill = Math.clamp(tag.getInt("Skill").orElse(0), -1, 3);
        age = tag.getBoolean("EndTickAge").orElse(false) ? Math.clamp(tag.getInt("Age").orElse(0), 0, 47) : Math.clamp(tag.getInt("Age").orElse(0), 0, 46) + (skill >= 0 ? 1 : 0);
        cooldown = Math.clamp(tag.getInt("Cooldown").orElse(0), 0, 90); movementLock = Math.clamp(tag.getInt("MovementLock").orElse(0), 0, 75);
        startedAt = tag.getInt("StartedAt").isPresent() ? tag.getLong("StartedAt").orElse(0L) : Long.MIN_VALUE; poisonRing = tag.getBoolean("PoisonRing").orElse(false);
        double value = tag.getDouble("TargetEyeHeight").orElse(0.0); targetEyeHeight = Double.isFinite(value) ? Math.clamp(value, 0, 100) : 0;
        reservations.clear(); for (int i = 0, count = Math.clamp(tag.getInt("ReservationCount").orElse(0), 0, 5); i < count; i++) if (tag.getString("Reservation" + i).isPresent()) reservations.add(java.util.UUID.fromString(tag.getString("Reservation" + i).orElse("00000000-0000-0000-0000-000000000000")));
        updateMovementLock();
    }
}
