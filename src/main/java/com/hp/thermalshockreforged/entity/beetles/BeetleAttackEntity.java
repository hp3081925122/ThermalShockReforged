package com.hp.thermalshockreforged.entity.beetles;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.content.status.aura.AuraContent;
import com.hp.thermalshockreforged.entity.icesquad.IceSquadContent;
import com.hp.thermalshockreforged.world.timing.*;
import java.lang.ref.WeakReference;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class BeetleAttackEntity extends Entity implements ServerEndTickTask {
    private UUID caster;
    private UUID target;
    private int targetEntityId;
    private boolean playerTarget;
    private WeakReference<LivingEntity> originalPlayer;
    private Vec3 soundOrigin = Vec3.ZERO;
    private Vec3 casterOrigin = Vec3.ZERO;
    private Vec3 look = Vec3.ZERO;
    private String dimension = "";
    private double damage;
    private double knockback;
    private int elapsed;
    private int skill;
    private boolean armed;
    public BeetleAttackEntity(EntityType<? extends BeetleAttackEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    static boolean submit(ServerLevel server, BeetleCombatEntity owner, LivingEntity victim, int skill) {
        var task = BeetleContent.ATTACK.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
        if (task == null) return false;
        task.caster = owner.getUUID(); task.target = victim.getUUID(); task.targetEntityId = victim.getId(); task.playerTarget = victim instanceof Player;
        if (task.playerTarget) task.originalPlayer = new WeakReference<>(victim);
        task.soundOrigin = victim.position(); task.casterOrigin = owner.position(); task.look = owner.getLookAngle(); task.skill = skill; task.dimension = server.dimension().identifier().toString();
        task.damage = finite(owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * (skill < 2 ? BeetleConfig.CRASHER_STRIKE.get() : BeetleConfig.THRASHER_STRIKE.get()));
        task.knockback = finite(BeetleConfig.STRIKE_KNOCKBACK.get()); task.armed = true; task.setPos(owner.position());
        return server.addFreshEntity(task);
    }
    private BeetleCombatEntity owner(ServerLevel server) { return caster != null && server.getEntity(caster) instanceof BeetleCombatEntity owner ? owner : null; }
    private LivingEntity victim(ServerLevel server) { return target != null && !target.equals(caster) && server.getEntity(target) instanceof LivingEntity living && living.isAlive() && (!playerTarget || living.getId() == targetEntityId && originalPlayer != null && originalPlayer.get() == living) ? living : null; }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
            var owner = owner(server);
            if (owner != null) { casterOrigin = owner.position(); look = owner.getLookAngle(); setPos(casterOrigin); }
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel server) {
        if (isRemoved() || !armed || !dimension.equals(server.dimension().identifier().toString())) return;
        var owner = owner(server);
        if (owner == null) for (var other : server.getServer().getAllLevels()) if (other != server && caster != null && other.getEntity(caster) != null) { armed = false; discard(); return; }
        if (owner != null) { casterOrigin = owner.position(); look = owner.getLookAngle(); setPos(casterOrigin); }
        int current = ++elapsed;
        if (skill == 0 && current == 7) {
            if (owner != null) { owner.faceNearestPlayer(server); look = owner.getLookAngle(); }
            strike(server, 4, false);
            sound(server, "minecraft:entity.player.attack.sweep", -1);
        } else if (skill == 1) {
            if (current == 16) {
                sound(server, ThermalShockReforged.MOD_ID + ":propulsionbootsactivate", -3);
                if (owner != null) { owner.addEffect(new MobEffectInstance(AuraContent.DAMAGING_AURA, 10, 12, false, false)); owner.setDeltaMovement(look.x * 1.8, .2, look.z * 1.8); owner.hurtMarked = true; }
            } else if (current == 23) {
                sound(server, "minecraft:intentionally_empty", -3);
                server.sendParticles(IceSquadContent.FOG_CLOUD.get(), soundOrigin.x, soundOrigin.y, soundOrigin.z, 2, 1, 1, 1, 0);
            }
        } else if (skill == 2) {
            if (current == 7 && owner != null) { owner.setDeltaMovement(look); owner.hurtMarked = true; }
            if (current == 7 || current == 9 || current == 11) { strike(server, 2, true); sound(server, "minecraft:entity.player.attack.sweep", 4); }
        } else if (skill == 3) {
            if (current == 12) sound(server, "minecraft:entity.iron_golem.hurt", 7);
            if (current == 6 || current == 12) { if (owner != null) { owner.setDeltaMovement(look); owner.hurtMarked = true; } strike(server, 2, true); sound(server, "minecraft:entity.player.attack.sweep", 4); }
            if (current == 26) {
                sound(server, "minecraft:entity.player.attack.sweep", 4); sound(server, "minecraft:block.anvil.place", 4);
                if (owner != null) { owner.setDeltaMovement(-look.z, .5, -look.x); owner.hurtMarked = true; owner.addEffect(new MobEffectInstance(StatusContent.PARRYING, 5, 1, true, true)); }
            }
        }
        int end = switch (skill) { case 0 -> 7; case 1 -> 23; case 2 -> 11; default -> 26; };
        if (current >= end) { armed = false; discard(); } else ServerEndTickTasks.schedule(server, getUUID());
    }
    private void strike(ServerLevel server, double reach, boolean inclusive) {
        var victim = victim(server);
        if (victim == null) return;
        double distance = victim.position().distanceTo(casterOrigin);
        if (inclusive ? distance > reach : distance >= reach) return;
        if (skill == 0) {
            double resistance = Mth.clamp(victim.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0, 1);
            if (!Double.isFinite(resistance)) resistance = 1;
            double strength = knockback * (1 - resistance);
            if (strength > 0) { victim.setDeltaMovement(look.scale(strength)); victim.hurtMarked = true; victim.hurtMarked = true; }
        }
        float amount = (float) finite(damage - victim.getArmorValue() * .4);
        if (amount > 0) victim.hurt(new DamageSource(server.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.GENERIC)), amount);
    }
    private void sound(ServerLevel server, String name, float pitch) { server.playSound(null, BlockPos.containing(soundOrigin), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse(name)), SoundSource.NEUTRAL, 1, pitch); }
    private static double finite(double value) { return Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0; }
    private static boolean valid(Vec3 vector) { return Double.isFinite(vector.x) && Double.isFinite(vector.y) && Double.isFinite(vector.z); }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (caster != null) output.putString("BeetleCaster", caster.toString()); if (target != null) output.putString("BeetleTarget", target.toString());
        output.putInt("BeetleTargetEntityId", targetEntityId); output.putBoolean("BeetlePlayerTarget", playerTarget); output.putString("BeetleDimension", dimension); output.putInt("BeetleSkill", skill); output.putInt("BeetleElapsed", elapsed); output.putBoolean("BeetleArmed", armed); output.putDouble("BeetleDamage", damage); output.putDouble("BeetleKnockback", knockback);
        output.putDouble("BeetleSoundX", soundOrigin.x); output.putDouble("BeetleSoundY", soundOrigin.y); output.putDouble("BeetleSoundZ", soundOrigin.z); output.putDouble("BeetleCasterX", casterOrigin.x); output.putDouble("BeetleCasterY", casterOrigin.y); output.putDouble("BeetleCasterZ", casterOrigin.z); output.putDouble("BeetleLookX", look.x); output.putDouble("BeetleLookY", look.y); output.putDouble("BeetleLookZ", look.z);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        caster = input.getString("BeetleCaster").map(java.util.UUID::fromString).orElse(null); target = input.getString("BeetleTarget").map(java.util.UUID::fromString).orElse(null); targetEntityId = input.getIntOr("BeetleTargetEntityId", 0); playerTarget = !input.getString("BeetlePlayerTarget").isPresent() || input.getBooleanOr("BeetlePlayerTarget", false); originalPlayer = null;
        dimension = input.getStringOr("BeetleDimension", ""); skill = Math.clamp(input.getIntOr("BeetleSkill", 0), 0, 3); elapsed = Math.clamp(input.getIntOr("BeetleElapsed", 0), 0, 26); damage = finite(input.getDoubleOr("BeetleDamage", 0.0)); knockback = Math.clamp(finite(input.getDoubleOr("BeetleKnockback", 0.0)), 0, 2);
        soundOrigin = new Vec3(input.getDoubleOr("BeetleSoundX", 0.0), input.getDoubleOr("BeetleSoundY", 0.0), input.getDoubleOr("BeetleSoundZ", 0.0)); casterOrigin = new Vec3(input.getDoubleOr("BeetleCasterX", 0.0), input.getDoubleOr("BeetleCasterY", 0.0), input.getDoubleOr("BeetleCasterZ", 0.0)); look = new Vec3(input.getDoubleOr("BeetleLookX", 0.0), input.getDoubleOr("BeetleLookY", 0.0), input.getDoubleOr("BeetleLookZ", 0.0));
        armed = input.getBooleanOr("BeetleArmed", false) && caster != null && target != null && !caster.equals(target) && valid(soundOrigin) && valid(casterOrigin) && valid(look) && look.lengthSqr() <= 1.01;
        noPhysics = true; setNoGravity(true);
    }
}
