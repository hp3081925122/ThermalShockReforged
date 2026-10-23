package com.hp.thermalshockreforged.entity.weaponcore;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.entity.cavebloomer.CavebloomerContent;
import com.hp.thermalshockreforged.mixin.LivingDeathStateAccess;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

final class CoreDetonation {
    private static final ThreadLocal<Chain> ACTIVE = new ThreadLocal<>();
    private static final Identifier FROST_TITAN = id("frost_titan");
    private static final Identifier CHALLENGE = id("frost_titan_core_kill_get");
    private CoreDetonation() { }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    static void submit(ServerLevel level, UUID core, Vec3 origin, Entity attacker) {
        Chain chain = ACTIVE.get();
        if (chain != null) { chain.enqueue(new Burst(level, core, origin, attacker)); return; }
        chain = new Chain();
        ACTIVE.set(chain);
        try {
            chain.enqueue(new Burst(level, core, origin, attacker));
            while (!chain.pending.isEmpty()) detonate(chain.pending.removeFirst());
            ThermalShockReforged.LOGGER.debug("Flare core detonation chain completed: cores={}", chain.seen.size());
        } finally {
            ACTIVE.remove();
        }
    }
    private static void detonate(Burst burst) {
        ServerLevel level = burst.level();
        Vec3 origin = burst.origin();
        DamageSource source = new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS), burst.attacker());
        level.sendParticles(CavebloomerContent.RED_EXPLODE.get(), origin.x, origin.y, origin.z, 1, .001, .001, .001, 0);
        var nearby = targets(level, origin, 3);
        for (Entity target : nearby) {
            target.hurt(source, 7);
            if (target instanceof LivingEntity living && BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).equals(FROST_TITAN)
                    && ((LivingDeathStateAccess) (Object) living).thermalShockReforged$isDeathCommitted()
                    && level.isEmptyBlock(BlockPos.containing(origin.x, origin.y - 1, origin.z)) && burst.attacker() instanceof ServerPlayer player) {
                var advancement = player.getServer().getAdvancements().get(CHALLENGE);
                if (advancement != null) {
                    var progress = player.getAdvancements().getOrStartProgress(advancement);
                    if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
                }
            }
        }
        for (Entity target : targets(level, origin, 12)) if (target.isUnderWater()) target.hurt(source, 10);
    }
    private static List<Entity> targets(ServerLevel level, Vec3 origin, double radius) {
        var targets = level.getEntitiesOfClass(Entity.class, new AABB(origin, origin).inflate(radius), entity -> true);
        targets.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(origin)));
        return targets;
    }
    private record Burst(ServerLevel level, UUID core, Vec3 origin, Entity attacker) { }
    private static final class Chain {
        private final Set<UUID> seen = new HashSet<>();
        private final ArrayDeque<Burst> pending = new ArrayDeque<>();
        private void enqueue(Burst burst) { if (seen.add(burst.core())) pending.addLast(burst); }
    }
}
