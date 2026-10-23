package com.hp.thermalshockreforged.entity.maiden;

import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.entity.projectile.*;
import com.hp.thermalshockreforged.world.timing.*;
import java.lang.ref.WeakReference;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class MaidenVolleyEntity extends Entity implements ServerEndTickTask {
    private static final int[][] DIRECTIONS = {{1,0},{1,1},{0,1},{-1,0},{-1,-1},{0,-1},{1,-1},{-1,1}};
    private UUID caster;
    private UUID target;
    private int targetEntityId;
    private boolean playerTarget;
    private WeakReference<LivingEntity> originalPlayer;
    private Vec3 origin = Vec3.ZERO;
    private Vec3 casterPosition = Vec3.ZERO;
    private String dimension = "";
    private double targetEyeHeight;
    private double damage;
    private double lastDamage;
    private int elapsed;
    private boolean single;
    private boolean armed;
    public MaidenVolleyEntity(EntityType<? extends MaidenVolleyEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    static MaidenVolleyEntity submit(ServerLevel server, MaidenEntity owner, LivingEntity victim, boolean single) {
        var task = MaidenContent.VOLLEY.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED); if (task == null) return null;
        task.caster = owner.getUUID(); task.target = victim.getUUID(); task.targetEntityId = victim.getId(); task.playerTarget = victim instanceof Player; if (task.playerTarget) task.originalPlayer = new WeakReference<>(victim);
        task.origin = victim.position(); task.targetEyeHeight = victim.getEyeHeight(); task.captureCaster(owner); task.dimension = server.dimension().identifier().toString(); task.single = single;
        double base = owner.getAttributeValue(Attributes.ATTACK_DAMAGE); task.damage = finite(base * (single ? MaidenConfig.SINGLE_MULTIPLIER.get() : MaidenConfig.TRIPLE_MULTIPLIER.get())); task.lastDamage = finite(base * (single ? MaidenConfig.SINGLE_MULTIPLIER.get() : MaidenConfig.LAST_MULTIPLIER.get())); task.armed = true;
        return server.addFreshEntity(task) ? task : null;
    }
    private MaidenEntity owner(ServerLevel server) { return caster != null && server.getEntity(caster) instanceof MaidenEntity owner ? owner : null; }
    void captureCaster(MaidenEntity owner) { casterPosition = owner.position(); setPos(casterPosition); }
    private void snapshot(ServerLevel server, MaidenEntity owner) { if (owner != null) captureCaster(owner); if (target != null && server.getEntity(target) instanceof LivingEntity victim && (!playerTarget || victim.getId() == targetEntityId && originalPlayer != null && originalPlayer.get() == victim)) targetEyeHeight = victim.getEyeHeight(); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() { super.tick(); if (level() instanceof ServerLevel server) { if (!armed || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; } snapshot(server, owner(server)); ServerEndTickTasks.schedule(server, getUUID()); } }
    @Override public void endTick(ServerLevel server) {
        if (!armed || isRemoved() || !dimension.equals(server.dimension().identifier().toString())) return;
        var owner = owner(server);
        if (owner == null) for (var other : server.getServer().getAllLevels()) if (other != server && caster != null && other.getEntity(caster) != null) { armed = false; discard(); return; }
        snapshot(server, owner); int current = ++elapsed;
        if (current == 9 && owner != null) owner.addEffect(new MobEffectInstance(StatusContent.STUNNED, single ? 28 : 45, 0, true, false));
        if (current == 29 || !single && (current == 34 || current == 39)) {
            server.playSound(null, BlockPos.containing(origin), SoundEvents.SHULKER_SHOOT, SoundSource.HOSTILE, 1, 2);
            for (int index = 0; index < DIRECTIONS.length; index++) {
                var bullet = new BloodBulletEntity(ProjectileContent.BLOOD_BULLET.get(), server);
                if (owner != null) bullet.setOwner(owner); else { var ownerData = new CompoundTag(); bullet.addAdditionalSaveData(ownerData); ownerData.putUUID("Owner", caster); bullet.readAdditionalSaveData(ownerData); }
                bullet.setBaseDamage(index == 7 ? lastDamage : damage); bullet.setSkillKnockback(0); bullet.setSilent(true); bullet.setCritArrow(false); bullet.setPos(casterPosition.x, casterPosition.y + targetEyeHeight, casterPosition.z);
                bullet.shoot(DIRECTIONS[index][0], .17, DIRECTIONS[index][1], single ? .3F : .4F, 0); server.addFreshEntity(bullet);
            }
        }
        if (current >= (single ? 29 : 39)) { armed = false; discard(); } else ServerEndTickTasks.schedule(server, getUUID());
    }
    private static double finite(double value) { return Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0; }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { if (caster != null) output.putString("MaidenCaster", caster.toString()); if (target != null) output.putString("MaidenTarget", target.toString()); output.putInt("MaidenTargetEntityId", targetEntityId); output.putBoolean("MaidenPlayerTarget", playerTarget); output.putBoolean("MaidenSingle", single); output.putBoolean("MaidenArmed", armed); output.putInt("MaidenElapsed", elapsed); output.putString("MaidenDimension", dimension); output.putDouble("MaidenDamage", damage); output.putDouble("MaidenLastDamage", lastDamage); output.putDouble("MaidenTargetEyeHeight", targetEyeHeight); output.putDouble("MaidenOriginX", origin.x); output.putDouble("MaidenOriginY", origin.y); output.putDouble("MaidenOriginZ", origin.z); output.putDouble("MaidenCasterX", casterPosition.x); output.putDouble("MaidenCasterY", casterPosition.y); output.putDouble("MaidenCasterZ", casterPosition.z); }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { caster = input.getString("MaidenCaster").map(java.util.UUID::fromString).orElse(null); target = input.getString("MaidenTarget").map(java.util.UUID::fromString).orElse(null); targetEntityId = input.getIntOr("MaidenTargetEntityId", 0); playerTarget = !input.getString("MaidenPlayerTarget").isPresent() || input.getBooleanOr("MaidenPlayerTarget", false); originalPlayer = null; single = input.getBooleanOr("MaidenSingle", false); elapsed = Math.clamp(input.getIntOr("MaidenElapsed", 0), 0, 39); dimension = input.getStringOr("MaidenDimension", ""); damage = finite(input.getDoubleOr("MaidenDamage", 0.0)); lastDamage = finite(input.getDoubleOr("MaidenLastDamage", 0.0)); targetEyeHeight = finite(input.getDoubleOr("MaidenTargetEyeHeight", 0.0)); origin = new Vec3(input.getDoubleOr("MaidenOriginX", 0.0), input.getDoubleOr("MaidenOriginY", 0.0), input.getDoubleOr("MaidenOriginZ", 0.0)); casterPosition = new Vec3(input.getDoubleOr("MaidenCasterX", 0.0), input.getDoubleOr("MaidenCasterY", 0.0), input.getDoubleOr("MaidenCasterZ", 0.0)); armed = input.getBooleanOr("MaidenArmed", false) && caster != null && target != null && !caster.equals(target) && elapsed < (single ? 29 : 39) && Double.isFinite(origin.lengthSqr()) && Double.isFinite(casterPosition.lengthSqr()); noPhysics = true; setNoGravity(true); }
}
