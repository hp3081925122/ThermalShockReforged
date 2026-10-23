package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.cavebloomer.CavebloomerContent;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class MagneticDetonationEntity extends Entity implements ServerEndTickTask {
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) { return false; }
    private UUID source;
    private int remaining = 10;
    private boolean epsilon;
    private float damage;
    private boolean armed;
    public MagneticDetonationEntity(EntityType<? extends MagneticDetonationEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, MagneticBombEntity bomb) {
        var task = ProjectileContent.MAGNETIC_DETONATION.get().create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (task == null) return false;
        task.source = bomb.getUUID(); task.epsilon = bomb.epsilon(); task.setPos(bomb.position());
        double attack = bomb.getAttributeValue(Attributes.ATTACK_DAMAGE);
        task.damage = Double.isFinite(attack) ? (float) Math.max(0, attack) : 0;
        task.armed = true;
        if (!level.addFreshEntity(task)) return false;
        ServerEndTickTasks.schedule(level, task.getUUID());
        return true;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canTeleport(Level from, Level to) { return false; }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed) discard();
            else ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || !armed || --remaining > 0) return;
        armed = false;
        level.playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "magnet_bomb")), SoundSource.NEUTRAL, 1, 1);
        level.sendParticles(CavebloomerContent.MAGNETIC_BOOM.get(), getX(), getY(), getZ(), 1, 0, 0, 0, 0);
        var targets = level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(position(), 8, 8, 8), target -> !(target instanceof Player)
                && !target.getUUID().equals(source) && !(target instanceof MagneticBombEntity bomb && bomb.epsilon() == epsilon));
        targets.sort(Comparator.comparingDouble(target -> target.distanceToSqr(position())));
        for (LivingEntity target : targets) {
            if (epsilon) target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 30, 12));
            else if (damage > 0) target.hurt(level.damageSources().generic(), damage);
        }
        if (source != null && level.getEntity(source) instanceof MagneticBombEntity bomb) bomb.discard();
        discard();
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (source != null) output.putString("MagneticSource", source.toString());
        output.putInt("MagneticRemaining", remaining); output.putBoolean("Epsilon", epsilon); output.putFloat("MagneticDamage", damage); output.putBoolean("Armed", armed);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        source = input.getString("MagneticSource").map(java.util.UUID::fromString).orElse(null);
        remaining = Math.clamp(input.getIntOr("MagneticRemaining", 0), 1, 10); epsilon = input.getBooleanOr("Epsilon", false);
        damage = Float.isFinite(input.getFloatOr("MagneticDamage", 0.0F)) ? Math.clamp(input.getFloatOr("MagneticDamage", 0.0F), 0, 2048) : 0;
        armed = input.getBooleanOr("Armed", false) && source != null;
    }
}
