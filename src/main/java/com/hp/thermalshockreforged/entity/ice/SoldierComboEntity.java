package com.hp.thermalshockreforged.entity.ice;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class SoldierComboEntity extends Entity implements ServerEndTickTask {
    private static final ResourceKey<DamageType> ERRANT = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "errant_damage"));
    private UUID caster;
    private UUID target;
    private Vec3 casterPosition = Vec3.ZERO;
    private Vec3 look = new Vec3(0, 0, 1);
    private BlockPos soundOrigin = BlockPos.ZERO;
    private String dimension = "";
    private double damage = 5;
    private int phase;
    private int remaining = 11;
    public SoldierComboEntity(EntityType<? extends SoldierComboEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, IceCreatureEntity owner, UUID target, Vec3 origin, int elapsed) {
        if (elapsed >= 22) return true;
        var task = IceContent.SOLDIER_COMBO.get().create(level);
        if (task == null) return false;
        task.caster = owner.getUUID(); task.target = target; task.soundOrigin = BlockPos.containing(origin); task.dimension = level.dimension().location().toString();
        task.phase = elapsed < 11 ? 0 : elapsed < 16 ? 1 : 2;
        task.remaining = (task.phase == 0 ? 11 : task.phase == 1 ? 16 : 22) - Math.max(0, elapsed);
        task.capture(owner); task.setPos(owner.position());
        return level.addFreshEntity(task);
    }
    private void capture(IceCreatureEntity owner) {
        casterPosition = owner.position(); look = owner.getLookAngle();
        double value = owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
        damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (caster != null && phase < 3 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (caster == null || phase >= 3 || !dimension.equals(server.dimension().location().toString())) { discard(); return; }
            if (server.getEntity(caster) instanceof IceCreatureEntity owner) { capture(owner); setPos(owner.position()); }
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || caster == null || phase >= 3 || !dimension.equals(level.dimension().location().toString())) return;
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        IceCreatureEntity owner = level.getEntity(caster) instanceof IceCreatureEntity ice ? ice : null;
        if (owner == null) for (ServerLevel other : level.getServer().getAllLevels()) {
            if (other != level && other.getEntity(caster) != null) { phase = 3; discard(); return; }
        }
        if (owner != null) capture(owner);
        int committed = phase++;
        remaining = committed == 0 ? 5 : 6;
        if (committed == 0) {
            sound(level, SoundEvents.PLAYER_ATTACK_SWEEP, 1, 2); strike(level, .6);
        } else if (committed == 1) {
            sound(level, SoundEvents.SHIELD_BLOCK, 1, 1);
            if (owner != null) owner.addEffect(new MobEffectInstance(StatusContent.PARRYING, 6, 0));
        } else {
            sound(level, custom("magnet_bow_25"), 1, 1); sound(level, custom("propulsionbootsactivate"), .5F, 1);
            strike(level, .3);
            if (owner != null) owner.setDeltaMovement(look.x, 0, look.z);
        }
        if (phase >= 3) discard(); else ServerEndTickTasks.schedule(level, getUUID());
    }
    private void strike(ServerLevel level, double armorFactor) {
        if (target == null || target.equals(caster) || !(level.getEntity(target) instanceof LivingEntity victim)) return;
        float dx = (float)(victim.getX() - casterPosition.x), dy = (float)(victim.getY() - casterPosition.y), dz = (float)(victim.getZ() - casterPosition.z);
        if (Mth.sqrt(dx * dx + dy * dy + dz * dz) > 2.5F) return;
        double amount = damage * 1.8 - victim.getArmorValue() * armorFactor;
        if (Double.isFinite(amount) && amount > 0) victim.hurt(new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ERRANT)), (float)Math.min(amount, Float.MAX_VALUE));
    }
    private void sound(ServerLevel level, SoundEvent sound, float volume, float pitch) { level.playSound(null, soundOrigin, sound, SoundSource.HOSTILE, volume, pitch); }
    private static SoundEvent custom(String name) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, name)); }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        if (caster != null) tag.putUUID("SoldierCaster", caster); if (target != null) tag.putUUID("SoldierTarget", target);
        tag.putDouble("SoldierX", casterPosition.x); tag.putDouble("SoldierY", casterPosition.y); tag.putDouble("SoldierZ", casterPosition.z);
        tag.putDouble("SoldierLookX", look.x); tag.putDouble("SoldierLookY", look.y); tag.putDouble("SoldierLookZ", look.z);
        tag.putLong("SoldierSoundOrigin", soundOrigin.asLong()); tag.putString("SoldierDimension", dimension); tag.putDouble("SoldierDamage", damage);
        tag.putInt("SoldierPhase", phase); tag.putInt("SoldierRemaining", remaining);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        caster = tag.hasUUID("SoldierCaster") ? tag.getUUID("SoldierCaster") : null; target = tag.hasUUID("SoldierTarget") ? tag.getUUID("SoldierTarget") : null;
        casterPosition = new Vec3(tag.getDouble("SoldierX"), tag.getDouble("SoldierY"), tag.getDouble("SoldierZ"));
        look = new Vec3(tag.getDouble("SoldierLookX"), tag.getDouble("SoldierLookY"), tag.getDouble("SoldierLookZ"));
        soundOrigin = BlockPos.of(tag.getLong("SoldierSoundOrigin")); dimension = tag.getString("SoldierDimension");
        double value = tag.getDouble("SoldierDamage"); damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
        phase = Math.clamp(tag.getInt("SoldierPhase"), 0, 3); remaining = Math.clamp(tag.getInt("SoldierRemaining"), 1, phase == 0 ? 11 : phase == 1 ? 5 : 6);
        if (!finite(casterPosition) || !finite(look)) phase = 3;
    }
    private static boolean finite(Vec3 value) { return Double.isFinite(value.x) && Double.isFinite(value.y) && Double.isFinite(value.z); }
}
