package com.hp.thermalshockreforged.entity.drops;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ice.IceContent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public final class IceMobDrops {
    private static final TagKey<EntityType<?>> ICE = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "ice"));

    private IceMobDrops() { }

    public static void death(LivingDeathEvent event) {
        var entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level) || !level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) return;
        var type = entity.getType();
        if (type != IceContent.FROSTILYTE_CRYOMANCER.get() || !type.is(ICE) || entity.getRandom().nextDouble() >= 0.7) return;
        var drone = IceContent.ICIDRONE.get().spawn(level, BlockPos.containing(entity.position()), EntitySpawnReason.MOB_SUMMONED);
        if (drone != null) drone.setDeltaMovement(0, 0.05, 0);
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, entity.getX(), entity.getY(), entity.getZ(), 12, 1, 1, 1, 0.5);
        level.playSound(null, entity.blockPosition(), SoundEvents.WITHER_AMBIENT, SoundSource.NEUTRAL, 0.4F, 4);
    }
}
