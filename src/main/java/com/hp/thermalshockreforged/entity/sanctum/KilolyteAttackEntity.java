package com.hp.thermalshockreforged.entity.sanctum;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ZapWarningEntity;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class KilolyteAttackEntity extends Entity implements ServerEndTickTask {
    private static final ResourceKey<DamageType> DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "errant_damage"));
    private static final int[][] OFFSETS = {{3,0,-3,0}, {0,3,0,-3}, {2,2,-2,-2}, {2,-2,-2,2}};
    private UUID caster;
    private UUID target;
    private Vec3 sourcePosition = Vec3.ZERO;
    private BlockPos soundOrigin = BlockPos.ZERO;
    private String dimension = "";
    private double baseDamage = 13;
    private double meleeMultiplier = 1;
    private double armorFactor = .6;
    private double zapMultiplier = 10D / 13D;
    private int remaining;
    private int phase;
    private boolean ring;
    public KilolyteAttackEntity(EntityType<? extends KilolyteAttackEntity> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static boolean submit(ServerLevel level, KilolyteEntity owner, LivingEntity victim, boolean ring) {
        var task = SanctumContent.KILOLYTE_ATTACK.get().create(level);
        if (task == null) return false;
        var config = SanctumConfig.KILOLYTE;
        task.caster = owner.getUUID(); task.target = victim.getUUID(); task.sourcePosition = owner.position(); task.soundOrigin = victim.blockPosition();
        task.dimension = level.dimension().identifier().toString(); task.baseDamage = finiteDamage(owner.getAttributeValue(Attributes.ATTACK_DAMAGE));
        task.meleeMultiplier = config.meleeMultiplier().get(); task.armorFactor = config.armorFactor().get(); task.zapMultiplier = config.zapMultiplier().get();
        task.ring = ring; task.remaining = ring ? 22 : 17; task.setPos(owner.position());
        return level.addFreshEntity(task);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (caster != null && phase < (ring ? 4 : 1) && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    private void capture(ServerLevel level) {
        if (level.getEntity(caster) instanceof KilolyteEntity owner) {
            sourcePosition = owner.position(); baseDamage = finiteDamage(owner.getAttributeValue(Attributes.ATTACK_DAMAGE)); setPos(sourcePosition);
        }
    }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (caster == null || phase >= (ring ? 4 : 1) || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
            capture(server); ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || caster == null || phase >= (ring ? 4 : 1) || !dimension.equals(level.dimension().identifier().toString())) return;
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        if (level.getEntity(caster) == null) {
            for (ServerLevel other : level.getServer().getAllLevels()) if (other != level && other.getEntity(caster) != null) { discard(); return; }
        }
        capture(level);
        int current = phase++;
        remaining = 2;
        if (ring) {
            if (current == 0) {
                level.playSound(null, soundOrigin, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 1, 1);
                level.sendParticles(SanctumContent.TURQ_EXPLODE.get(), sourcePosition.x, sourcePosition.y, sourcePosition.z, 1, .001, .001, .001, 1);
            }
            int[] offsets = OFFSETS[current];
            for (int i = 0; i < 4; i += 2) {
                var pos = BlockPos.containing(sourcePosition.x + offsets[i], sourcePosition.y, sourcePosition.z + offsets[i + 1]);
                var zap = ZapWarningEntity.spawn(level, pos, caster, finiteDamage(baseDamage * zapMultiplier), true);
                if (zap != null) zap.setYRot(level.getRandom().nextFloat() * 360);
            }
        } else {
            level.playSound(null, soundOrigin, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1, -3);
            level.playSound(null, soundOrigin, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.HOSTILE, 1, -3);
            if (target != null && level.getEntity(target) instanceof LivingEntity victim && !caster.equals(victim.getUUID()) && victim.isAlive() && (float) Math.sqrt(victim.position().distanceToSqr(sourcePosition)) <= 3) {
                double rawDamage = baseDamage * meleeMultiplier - victim.getArmorValue() * armorFactor;
                if (rawDamage < 0) ThermalShockReforged.LOGGER.debug("Clamped negative kilolyte strike damage: caster={}, target={}, rawDamage={}", caster, target, rawDamage);
                if (Double.isFinite(rawDamage)) victim.hurt(new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DAMAGE)), (float) finiteDamage(rawDamage));
            }
        }
        if (phase >= (ring ? 4 : 1)) discard(); else ServerEndTickTasks.schedule(level, getUUID());
    }
    private static double finiteDamage(double value) { return Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0; }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (caster != null) output.putString("KilolyteCaster", caster.toString()); if (target != null) output.putString("KilolyteTarget", target.toString());
        output.putDouble("KilolyteSourceX", sourcePosition.x); output.putDouble("KilolyteSourceY", sourcePosition.y); output.putDouble("KilolyteSourceZ", sourcePosition.z);
        output.putLong("KilolyteSoundOrigin", soundOrigin.asLong()); output.putString("KilolyteDimension", dimension);
        output.putDouble("KilolyteBaseDamage", baseDamage); output.putDouble("KilolyteMeleeMultiplier", meleeMultiplier); output.putDouble("KilolyteArmorFactor", armorFactor); output.putDouble("KilolyteZapMultiplier", zapMultiplier);
        output.putInt("KilolyteRemaining", remaining); output.putInt("KilolytePhase", phase); output.putBoolean("KilolyteRing", ring);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        caster = input.getString("KilolyteCaster").map(java.util.UUID::fromString).orElse(null); target = input.getString("KilolyteTarget").map(java.util.UUID::fromString).orElse(null);
        sourcePosition = new Vec3(input.getDoubleOr("KilolyteSourceX", 0.0), input.getDoubleOr("KilolyteSourceY", 0.0), input.getDoubleOr("KilolyteSourceZ", 0.0));
        soundOrigin = BlockPos.of(input.getLongOr("KilolyteSoundOrigin", 0L)); dimension = input.getStringOr("KilolyteDimension", "");
        baseDamage = finiteDamage(input.getDoubleOr("KilolyteBaseDamage", 0.0)); meleeMultiplier = Math.min(100, finiteDamage(input.getDoubleOr("KilolyteMeleeMultiplier", 0.0)));
        armorFactor = Math.min(100, finiteDamage(input.getDoubleOr("KilolyteArmorFactor", 0.0))); zapMultiplier = Math.min(100, finiteDamage(input.getDoubleOr("KilolyteZapMultiplier", 0.0)));
        ring = input.getBooleanOr("KilolyteRing", false); phase = Math.clamp(input.getIntOr("KilolytePhase", 0), 0, ring ? 4 : 1);
        remaining = Math.clamp(input.getIntOr("KilolyteRemaining", 0), 1, phase == 0 ? (ring ? 22 : 17) : 2);
        if (!Double.isFinite(sourcePosition.x) || !Double.isFinite(sourcePosition.y) || !Double.isFinite(sourcePosition.z)) phase = ring ? 4 : 1;
    }
}
