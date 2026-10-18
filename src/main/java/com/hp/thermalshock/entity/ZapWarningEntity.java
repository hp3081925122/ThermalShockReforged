package com.hp.thermalshock.entity;

import com.hp.thermalshock.ThermalShock;
import com.hp.thermalshock.config.CombatConfig;
import java.util.UUID;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class ZapWarningEntity extends Entity {
    private static final ResourceLocation EFFECT = ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, "zapattack");
    private static final ResourceLocation SOUND = ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, "sparkexplode");
    private UUID caster;
    private double multiplier = 1;
    private int remainingTicks = 20;
    private boolean emitted;

    public ZapWarningEntity(EntityType<? extends ZapWarningEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    public void arm(LivingEntity owner, double damageMultiplier) {
        caster = owner.getUUID();
        multiplier = Math.max(0, damageMultiplier);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;
        if (!emitted) {
            AAALevel.addParticle(server, new ParticleEmitterInfo(EFFECT).position(position()));
            emitted = true;
        }
        if (--remainingTicks > 0) return;
        LivingEntity owner = null;
        if (caster != null) {
            Entity found = server.getEntity(caster);
            if (!(found instanceof LivingEntity living) || !living.isAlive()) {
                discard();
                return;
            }
            owner = living;
        }
        double baseDamage = owner == null ? CombatConfig.FORECAST_DAMAGE.get() : owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
        DamageSource source = new DamageSource(server.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.LIGHTNING_BOLT), this, owner);
        server.playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.get(SOUND), SoundSource.NEUTRAL, 1, 2);
        for (Entity target : server.getEntities(this, new AABB(position(), position()).inflate(1.5), entity -> !(entity instanceof ItemEntity) && !(entity instanceof ZapWarningEntity))) {
            target.hurt(source, (float) (baseDamage * multiplier));
        }
        discard();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        caster = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
        multiplier = Math.max(0, tag.getDouble("Multiplier"));
        remainingTicks = Math.max(1, Math.min(20, tag.getInt("RemainingTicks")));
        emitted = false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (caster != null) tag.putUUID("Caster", caster);
        tag.putDouble("Multiplier", multiplier);
        tag.putInt("RemainingTicks", remainingTicks);
    }
}
