package com.hp.thermalshockreforged.entity.cavebloomer;

import com.hp.thermalshockreforged.entity.attack.AttackCarrierBody;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
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

public final class CavebloomerImpactEntity extends AttackCarrierBody implements ServerEndTickTask {
    private boolean initialized;
    private boolean committed;
    private int remaining = 20;
    private Vec3 origin = Vec3.ZERO;
    private String dimension = "";
    private double damage = -1;

    public CavebloomerImpactEntity(EntityType<? extends CavebloomerImpactEntity> type, Level level) { super(type, level); }
    private boolean poison() { return BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath().equals("poison_attack_entity"); }
    public boolean initialized() { return initialized; }
    public void configureDamage(double value) {
        if (!initialized) damage = Double.isFinite(value) ? Math.clamp(value, 0, Float.MAX_VALUE) : 0;
    }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        if (!initialized && level instanceof ServerLevel server) {
            initialized = true; origin = position(); dimension = server.dimension().identifier().toString(); remaining = 20;
            if (damage < 0) damage = poison() ? 6 : 10;
            server.sendParticles((poison() ? CavebloomerContent.POISON_CLOUD : CavebloomerContent.SHOCK_ATTACK).get(), origin.x, origin.y, origin.z, 0, 0, 0, 0, 1);
            server.playSound(null, BlockPos.containing(origin), poison() ? SoundEvents.ZOMBIE_VILLAGER_CURE : SoundEvents.CREEPER_PRIMED, SoundSource.NEUTRAL, 1, poison() ? 1 : 3);
            if (poison()) detonate(server);
            else ServerEndTickTasks.schedule(server, getUUID());
        }
        return result;
    }
    @Override public void onAddedToLevel() {
        super.onAddedToLevel();
        if (initialized && !committed && !poison() && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }
    private void detonate(ServerLevel server) {
        if (committed) return;
        committed = true;
        if (!poison()) server.playSound(null, BlockPos.containing(origin), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 1, 1);
        var source = new DamageSource(server.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(poison() ? DamageTypes.UNATTRIBUTED_FIREBALL : DamageTypes.EXPLOSION));
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
        if (!initialized || committed || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
        if (!poison()) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override public void endTick(ServerLevel server) {
        if (!initialized || committed || poison()) return;
        if (!dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
        if (--remaining <= 0) detonate(server); else ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("ImpactInitialized", initialized); output.putBoolean("ImpactCommitted", committed); output.putInt("ImpactRemaining", remaining);
        output.putString("ImpactDimension", dimension); output.putDouble("ImpactDamage", damage);
        output.putDouble("ImpactX", origin.x); output.putDouble("ImpactY", origin.y); output.putDouble("ImpactZ", origin.z);
    }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        initialized = input.getBooleanOr("ImpactInitialized", false); committed = input.getBooleanOr("ImpactCommitted", false);
        remaining = input.read("ImpactRemaining", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? Math.clamp(input.getIntOr("ImpactRemaining", 0), 0, 20) : 20;
        dimension = input.getStringOr("ImpactDimension", ""); damage = input.read("ImpactDamage", com.mojang.serialization.Codec.PASSTHROUGH).isPresent() ? input.getDoubleOr("ImpactDamage", 0.0) : poison() ? 6 : 10;
        damage = Double.isFinite(damage) ? Math.clamp(damage, 0, Float.MAX_VALUE) : 0;
        origin = new Vec3(input.getDoubleOr("ImpactX", 0.0), input.getDoubleOr("ImpactY", 0.0), input.getDoubleOr("ImpactZ", 0.0));
        if (!Double.isFinite(origin.x) || !Double.isFinite(origin.y) || !Double.isFinite(origin.z)) committed = true;
    }
}
