package com.hp.thermalshockreforged.entity;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.config.CombatConfig;
import com.hp.thermalshockreforged.entity.attack.AttackCarrierBody;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class ZapWarningEntity extends AttackCarrierBody {
    private static final ResourceLocation SOUND = ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "sparkexplode");
    private UUID caster;
    private double damage = 10;
    private int remainingTicks = 19;
    private boolean initialized;
    private boolean emitted;
    private boolean committed;
    private Vec3 origin = Vec3.ZERO;
    private String originDimension = "";
    private long createdAt = Long.MIN_VALUE;

    public ZapWarningEntity(EntityType<? extends ZapWarningEntity> type, Level level) {
        super(type, level);
    }
    public void arm(LivingEntity owner, double damageMultiplier) {
        if (caster != null || committed) return;
        caster = owner.getUUID();
        double value = owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier;
        damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
        initialize();
    }
    private void initialize() {
        if (initialized || !(level() instanceof ServerLevel server)) return;
        initialized = true;
        origin = position(); originDimension = server.dimension().location().toString(); createdAt = server.getGameTime();
        if (!emitted) {
            emitted = true;
            server.sendParticles(ModEntities.ZAP_PARTICLE.get(), origin.x, origin.y, origin.z, 0, 0, 0, 0, 1);
        }
    }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        if (!initialized) damage = CombatConfig.SPEC.isLoaded() ? CombatConfig.FORECAST_DAMAGE.get() : CombatConfig.FORECAST_DAMAGE.getDefault();
        initialize();
        return result;
    }
    @Override public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;
        if (committed) { discard(); return; }
        initialize();
        if (!originDimension.equals(server.dimension().location().toString())) { committed = true; discard(); return; }
        if (server.getGameTime() == createdAt || --remainingTicks > 0) return;
        committed = true;
        server.playSound(null, BlockPos.containing(origin), BuiltInRegistries.SOUND_EVENT.get(SOUND), SoundSource.NEUTRAL, 1, 2);
        var targets = server.getEntitiesOfClass(Entity.class, new AABB(origin, origin).inflate(1.5), entity -> !(entity instanceof ItemEntity))
                .stream().sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(origin))).toList();
        DamageSource source = damageSources().lightningBolt();
        for (Entity target : targets) target.hurt(source, (float) damage);
        discard();
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (caster != null) tag.putUUID("Caster", caster);
        tag.putDouble("ZapDamage", damage); tag.putInt("RemainingTicks", remainingTicks);
        tag.putBoolean("WarningInitialized", initialized); tag.putBoolean("WarningEmitted", emitted); tag.putBoolean("WarningCommitted", committed);
        tag.putDouble("WarningOriginX", origin.x); tag.putDouble("WarningOriginY", origin.y); tag.putDouble("WarningOriginZ", origin.z);
        tag.putString("WarningDimension", originDimension); tag.putLong("WarningCreatedAt", createdAt);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        caster = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
        double value = tag.contains("ZapDamage") ? tag.getDouble("ZapDamage")
                : CombatConfig.FORECAST_DAMAGE.getDefault() * (tag.contains("Multiplier") ? tag.getDouble("Multiplier") : 1);
        damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
        remainingTicks = tag.contains("RemainingTicks") ? Math.clamp(tag.getInt("RemainingTicks"), 0, 20) : 19;
        initialized = tag.getBoolean("WarningInitialized"); emitted = tag.getBoolean("WarningEmitted"); committed = tag.getBoolean("WarningCommitted");
        origin = new Vec3(tag.getDouble("WarningOriginX"), tag.getDouble("WarningOriginY"), tag.getDouble("WarningOriginZ"));
        if (!Double.isFinite(origin.x) || !Double.isFinite(origin.y) || !Double.isFinite(origin.z)) { committed = true; }
        originDimension = tag.getString("WarningDimension"); createdAt = tag.contains("WarningCreatedAt") ? tag.getLong("WarningCreatedAt") : Long.MIN_VALUE;
        if (!initialized && !tag.contains("WarningInitialized") && tag.contains("RemainingTicks")) {
            initialized = true; emitted = true; origin = position(); originDimension = level().dimension().location().toString();
        }
    }
}
