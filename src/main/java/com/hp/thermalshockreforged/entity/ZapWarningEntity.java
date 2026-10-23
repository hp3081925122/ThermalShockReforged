package com.hp.thermalshockreforged.entity;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.config.CombatConfig;
import com.hp.thermalshockreforged.entity.attack.AttackCarrierBody;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public final class ZapWarningEntity extends AttackCarrierBody {
    private UUID caster;
    private double damage = 10;
    private int remainingTicks = 20;
    private UUID detonationTask;
    private int submissionRetry;
    private boolean ignoreCaster;
    private boolean damageConfigured;
    private boolean initialized;
    private boolean emitted;
    private boolean committed;
    private Vec3 origin = Vec3.ZERO;
    private String originDimension = "";

    public ZapWarningEntity(EntityType<? extends ZapWarningEntity> type, Level level) {
        super(type, level);
    }
    public void arm(LivingEntity owner, double damageMultiplier) {
        if (caster != null || committed) return;
        configureDamage(owner.getUUID(), owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier);
        initialize();
    }
    private void configureDamage(UUID owner, double value) {
        caster = owner;
        damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
        damageConfigured = true;
        if (detonationTask != null && level() instanceof ServerLevel server && server.getEntity(detonationTask) instanceof ZapDetonationEntity task) task.configure(caster, damage, ignoreCaster);
    }
    public static ZapWarningEntity spawn(ServerLevel level, BlockPos pos, UUID owner, double damage, boolean ignoreCaster) {
        var zap = ModEntities.ZAP.get().create(level);
        if (zap == null) return null;
        zap.configureDamage(owner, damage); zap.ignoreCaster = ignoreCaster;
        zap.moveTo(pos.getX() + .5, pos.getY(), pos.getZ() + .5, net.minecraft.util.Mth.wrapDegrees(level.getRandom().nextFloat() * 360), 0);
        zap.yHeadRot = zap.getYRot(); zap.yBodyRot = zap.getYRot();
        net.neoforged.neoforge.event.EventHooks.finalizeMobSpawn(zap, level, level.getCurrentDifficultyAt(pos), EntitySpawnReason.MOB_SUMMONED, null);
        if (!zap.initialized || zap.isSpawnCancelled() || zap.isRemoved()) { zap.discard(); return null; }
        if (!level.addFreshEntity(zap)) { zap.discard(); return null; }
        return zap;
    }
    private void initialize() {
        if (initialized || !(level() instanceof ServerLevel server)) return;
        initialized = true;
        origin = position(); originDimension = server.dimension().identifier().toString();
        if (!emitted) {
            emitted = true;
            server.sendParticles(ModEntities.ZAP_PARTICLE.get(), origin.x, origin.y, origin.z, 0, 0, 0, 0, 1);
        }
    }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        if (!initialized && !damageConfigured) damage = 10;
        initialize();
        return result;
    }
    @Override public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;
        if (committed) { discard(); return; }
        initialize();
        if (!originDimension.equals(server.dimension().identifier().toString())) { committed = true; discard(); return; }
        if (submissionRetry > 0) submissionRetry--;
        submitDetonation();
    }
    private void submitDetonation() {
        if (!(level() instanceof ServerLevel server) || !initialized || committed || isRemoved() || detonationTask != null || submissionRetry > 0) return;
        var task = ZapDetonationEntity.submit(server, this, origin, caster, damage, ignoreCaster, remainingTicks);
        if (task != null) {
            detonationTask = task.getUUID(); remainingTicks = 0;
            ThermalShockReforged.LOGGER.debug("Zap detonation committed: warning={}, task={}", getUUID(), detonationTask);
        } else {
            submissionRetry = 20;
            ThermalShockReforged.LOGGER.debug("Zap detonation insertion failed: warning={}", getUUID());
        }
    }
    void completeDetonation(UUID task) { if (task.equals(detonationTask)) { committed = true; detonationTask = null; remainingTicks = 0; } }
    @Override public void onAddedToLevel() {
        super.onAddedToLevel();
        if (detonationTask != null && level() instanceof ServerLevel server && server.getEntity(detonationTask) == null) {
            committed = true;
            detonationTask = null;
            remainingTicks = 0;
            ThermalShockReforged.LOGGER.debug("Zap warning recovered without detonation task: warning={}", getUUID());
        }
        submitDetonation();
    }
    @Override public void remove(RemovalReason reason) {
        if ((reason.shouldDestroy() && reason != RemovalReason.KILLED || reason == RemovalReason.CHANGED_DIMENSION) && detonationTask != null && level() instanceof ServerLevel server && server.getEntity(detonationTask) instanceof ZapDetonationEntity task) task.cancel();
        super.remove(reason);
    }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (caster != null) output.putString("Caster", caster.toString());
        output.putBoolean("ZapIgnoreCaster", ignoreCaster); output.putBoolean("ZapDamageConfigured", damageConfigured); output.putDouble("ZapDamage", damage); output.putInt("RemainingTicks", remainingTicks);
        output.putBoolean("WarningInitialized", initialized); output.putBoolean("WarningEmitted", emitted); output.putBoolean("WarningCommitted", committed);
        if (detonationTask != null) output.putString("ZapDetonationTask", detonationTask.toString()); output.putInt("ZapSubmissionRetry", submissionRetry);
        output.putDouble("WarningOriginX", origin.x); output.putDouble("WarningOriginY", origin.y); output.putDouble("WarningOriginZ", origin.z);
        output.putString("WarningDimension", originDimension);
    }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        ignoreCaster = input.getBooleanOr("ZapIgnoreCaster", false);
        caster = input.getString("Caster").map(java.util.UUID::fromString).orElse(null);
        double value = input.getString("ZapDamage").isPresent() ? input.getDoubleOr("ZapDamage", 0.0)
                : CombatConfig.FORECAST_DAMAGE.getDefault() * (input.getString("Multiplier").isPresent() ? input.getDoubleOr("Multiplier", 0.0) : 1);
        damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
        damageConfigured = input.getBooleanOr("ZapDamageConfigured", false) || input.getString("ZapDamage").isPresent();
        remainingTicks = input.getString("RemainingTicks").isPresent() ? Math.clamp(input.getIntOr("RemainingTicks", 0), 0, 20) : 20;
        detonationTask = input.getString("ZapDetonationTask").map(java.util.UUID::fromString).orElse(null);
        if (detonationTask == null) remainingTicks = Math.max(1, remainingTicks);
        submissionRetry = Math.clamp(input.getIntOr("ZapSubmissionRetry", 0), 0, 20);
        initialized = input.getBooleanOr("WarningInitialized", false); emitted = input.getBooleanOr("WarningEmitted", false); committed = input.getBooleanOr("WarningCommitted", false);
        origin = new Vec3(input.getDoubleOr("WarningOriginX", 0.0), input.getDoubleOr("WarningOriginY", 0.0), input.getDoubleOr("WarningOriginZ", 0.0));
        if (!Double.isFinite(origin.x) || !Double.isFinite(origin.y) || !Double.isFinite(origin.z)) { committed = true; }
        originDimension = input.getStringOr("WarningDimension", "");
        if (!initialized && !input.getString("WarningInitialized").isPresent() && input.getString("RemainingTicks").isPresent()) {
            initialized = true; emitted = true; origin = position(); originDimension = level().dimension().identifier().toString();
        }
    }
}
