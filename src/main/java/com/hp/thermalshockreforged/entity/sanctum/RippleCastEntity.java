package com.hp.thermalshockreforged.entity.sanctum;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import java.util.UUID;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;

public final class RippleCastEntity extends Entity implements ServerEndTickTask {
    private UUID caster;
    private UUID target;
    private Vec3 targetEyes = Vec3.ZERO;
    private String dimension = "";
    private int remaining = 29;
    private boolean consumed;
    public RippleCastEntity(EntityType<? extends RippleCastEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, RippleEntity owner, LivingEntity victim) {
        var task = SanctumContent.RIPPLE_CAST.get().create(level);
        if (task == null) return false;
        task.caster = owner.getUUID(); task.target = victim.getUUID(); task.targetEyes = victim.getEyePosition();
        task.dimension = level.dimension().location().toString(); task.setPos(victim.position());
        return level.addFreshEntity(task);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (caster != null && !consumed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (caster == null || consumed || !dimension.equals(server.dimension().location().toString())) { discard(); return; }
            Entity victim = target == null ? null : server.getEntity(target);
            if (victim != null) targetEyes = victim.getEyePosition();
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || consumed || !dimension.equals(level.dimension().location().toString())) return;
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        consumed = true;
        level.playSound(null, blockPosition(), sound("magnet_bow_25"), SoundSource.HOSTILE, 1, 1);
        if (level.getEntity(caster) instanceof RippleEntity owner && owner.isAlive()
                && !owner.hasEffect(StatusContent.ELECTRIFIED) && !owner.hasEffect(MobEffects.WEAKNESS) && !owner.hasEffect(StatusContent.INFECTED)) {
            Entity victim = target == null ? null : level.getEntity(target);
            owner.lookAt(EntityAnchorArgument.Anchor.EYES, victim == null ? targetEyes : victim.getEyePosition());
            level.playSound(null, blockPosition(), SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 1, 1);
            var hit = level.clip(new ClipContext(owner.getEyePosition(), owner.getEyePosition().add(owner.getViewVector(1).scale(4)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, owner));
            BlockPos pos = BlockPos.containing(hit.getBlockPos().getX(), owner.getY() + 4, hit.getBlockPos().getZ());
            var err = SanctumContent.ERR.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
            if (err != null) err.setYRot(level.random.nextFloat() * 360);
            for (Player player : level.getEntitiesOfClass(Player.class, new AABB(position(), position()).inflate(3.5), entity -> true)) {
                double resistance = net.minecraft.util.Mth.clamp(player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0, 1);
                if (resistance < 1) { player.setDeltaMovement(new Vec3(0, .5 * (1 - resistance), 0)); player.hasImpulse = true; player.hurtMarked = true; }
            }
        } else {
            level.playSound(null, blockPosition(), sound("galvanitedeath"), SoundSource.HOSTILE, .6F, 3);
            level.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 5, .0001, .0001, .0001, 1);
        }
        discard();
    }
    private static SoundEvent sound(String path) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path)); }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        if (caster != null) tag.putUUID("RippleCaster", caster);
        if (target != null) tag.putUUID("RippleTarget", target);
        tag.putDouble("RippleTargetX", targetEyes.x); tag.putDouble("RippleTargetY", targetEyes.y); tag.putDouble("RippleTargetZ", targetEyes.z);
        tag.putString("RippleDimension", dimension); tag.putInt("RippleRemaining", remaining); tag.putBoolean("RippleConsumed", consumed);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        caster = tag.hasUUID("RippleCaster") ? tag.getUUID("RippleCaster") : null;
        target = tag.hasUUID("RippleTarget") ? tag.getUUID("RippleTarget") : null;
        targetEyes = new Vec3(tag.getDouble("RippleTargetX"), tag.getDouble("RippleTargetY"), tag.getDouble("RippleTargetZ"));
        dimension = tag.getString("RippleDimension"); remaining = Math.clamp(tag.getInt("RippleRemaining"), 1, 29); consumed = tag.getBoolean("RippleConsumed");
        if (!Double.isFinite(targetEyes.x) || !Double.isFinite(targetEyes.y) || !Double.isFinite(targetEyes.z)) consumed = true;
    }
}
