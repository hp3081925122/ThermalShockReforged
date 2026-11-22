package com.hp.thermalshockreforged.world;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.LakeFeature;

public final class LakeBiomeLookup {
    private static final AtomicBoolean LOGGED_FALLBACK = new AtomicBoolean();

    private LakeBiomeLookup() { }

    public static Holder<Biome> getBiome(WorldGenLevel level, BlockPos position, LakeFeature.Configuration config) {
        return getBiome(level, position, config, args -> ((WorldGenLevel) args[0]).getBiome((BlockPos) args[1]));
    }

    public static Holder<Biome> getBiome(WorldGenLevel level, BlockPos position, LakeFeature.Configuration config, com.llamalad7.mixinextras.injector.wrapoperation.Operation<Holder<Biome>> fallback) {
        if (!(level instanceof WorldGenRegion region) || !level.getLevel().dimension().equals(LoqendiaContent.DIMENSION)) return fallback.call(level, position);
        return level.getBiomeManager().withDifferentSource((x, y, z) -> {
            int chunkX = QuartPos.toSection(x);
            int chunkZ = QuartPos.toSection(z);
            if (region.getCenter().getChessboardDistance(chunkX, chunkZ) <= 1) return level.getNoiseBiome(x, y, z);
            if (LOGGED_FALLBACK.compareAndSet(false, true)) {
                var feature = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).entrySet().stream()
                        .filter(entry -> entry.getValue().config() == config).map(entry -> entry.getKey().identifier().toString()).findFirst().orElse("unregistered");
                ThermalShockReforged.LOGGER.debug("Using uncached lake biome beyond feature chunk dependencies: feature={}, position={}, generatingChunk={}, requestedChunk=({}, {}), seed={}", feature, position, region.getCenter(), chunkX, chunkZ, level.getSeed());
            }
            return level.getUncachedNoiseBiome(x, y, z);
        }).getBiome(position);
    }
}
