package com.hp.thermalshockreforged.entity.cavebloomer;

import com.hp.thermalshockreforged.entity.attack.AttackCarrierBody;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.Comparator;

public final class CavebloomerImpactEntity extends AttackCarrierBody {
    private boolean initialized;
    private boolean committed;
    private int remaining = 19;
    private long startedAt = Long.MIN_VALUE;
    private Vec3 origin = Vec3.ZERO;
    private String dimension = "";
    private double damage = -1;

    public CavebloomerImpactEntity(EntityType<? extends CavebloomerImpactEntity> type, Level level) { super(type, level); }
    private boolean poison() { return BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath().equals("poison_attack_entity"); }
    public boolean initialized() { return initialized; }
    public void configureDamage(double value) {
        if (!initialized) damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
    }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        if (!initialized && level instanceof ServerLevel server) {
            initialized = true; origin = position(); dimension = server.dimension().location().toString(); startedAt = server.getGameTime();
            if (damage < 0) damage = poison() ? 6 : 10;
            server.sendParticles((poison() ? CavebloomerContent.POISON_CLOUD : CavebloomerContent.SHOCK_ATTACK).get(), origin.x, origin.y, origin.z, 0, 0, 0, 0, 1);
            server.playSound(null, BlockPos.containing(origin), poison() ? SoundEvents.ZOMBIE_VILLAGER_CURE : SoundEvents.CREEPER_PRIMED, SoundSource.NEUTRAL, 1, poison() ? 1 : 3);
            if (poison()) detonate(server);
        }
        return result;
    }
    private void detonate(ServerLevel server) {
        if (committed) return;
        committed = true;
        if (!poison()) server.playSound(null, BlockPos.containing(origin), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 1, 1);
        var source = new DamageSource(server.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(poison() ? DamageTypes.UNATTRIBUTED_FIREBALL : DamageTypes.EXPLOSION));
        var targets = server.getEntitiesOfClass(Player.class, new AABB(origin, origin).inflate(poison() ? 2 : 1.5), entity -> true)
                .stream().sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(origin))).toList();
        for (Player target : targets) {
            if (poison()) target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 3));
            target.hurt(source, (float) damage);
        }
        discard();
    }
    @Override public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;
        if (!initialized || committed || !dimension.equals(server.dimension().location().toString())) { discard(); return; }
        if (server.getGameTime() != startedAt && --remaining <= 0) detonate(server);
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("ImpactInitialized", initialized); tag.putBoolean("ImpactCommitted", committed); tag.putInt("ImpactRemaining", remaining);
        tag.putLong("ImpactStartedAt", startedAt); tag.putString("ImpactDimension", dimension); tag.putDouble("ImpactDamage", damage);
        tag.putDouble("ImpactX", origin.x); tag.putDouble("ImpactY", origin.y); tag.putDouble("ImpactZ", origin.z);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        initialized = tag.getBoolean("ImpactInitialized"); committed = tag.getBoolean("ImpactCommitted");
        remaining = tag.contains("ImpactRemaining") ? Math.clamp(tag.getInt("ImpactRemaining"), 0, 19) : 19;
        startedAt = tag.contains("ImpactStartedAt") ? tag.getLong("ImpactStartedAt") : Long.MIN_VALUE;
        dimension = tag.getString("ImpactDimension"); damage = tag.contains("ImpactDamage") ? tag.getDouble("ImpactDamage") : poison() ? 6 : 10;
        damage = Double.isFinite(damage) ? Math.clamp(damage, 0, Float.MAX_VALUE) : 0;
        origin = new Vec3(tag.getDouble("ImpactX"), tag.getDouble("ImpactY"), tag.getDouble("ImpactZ"));
        if (!Double.isFinite(origin.x) || !Double.isFinite(origin.y) || !Double.isFinite(origin.z)) committed = true;
    }
}
