package com.hp.thermalshockreforged.entity.sanctum;

import com.hp.thermalshockreforged.entity.attack.AttackCarrierBody;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.AABB;

public final class ErrFieldEntity extends AttackCarrierBody {
    private int remaining;
    private boolean initialized;
    public ErrFieldEntity(EntityType<? extends ErrFieldEntity> type, Level level) { super(type, level); }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        if (!initialized && level instanceof ServerLevel server) {
            initialized = true;
            remaining = 180;
            server.sendParticles(SanctumContent.ERR_EFFECT.get(), getX(), getY() + 1.5, getZ(), 1, .001, .001, .001, 1);
        }
        return result;
    }
    @Override public void baseTick() {
        super.baseTick();
        if (!(level() instanceof ServerLevel server)) return;
        if (--remaining <= 0) discard();
        for (LivingEntity target : server.getEntitiesOfClass(LivingEntity.class, new AABB(position(), position()).inflate(2), entity -> true)) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 120, 4));
        }
    }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("ErrRemaining", remaining); output.putBoolean("ErrInitialized", initialized);
    }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        remaining = Math.clamp(input.getIntOr("ErrRemaining", 0), 0, 180); initialized = input.getBooleanOr("ErrInitialized", false);
    }
}
