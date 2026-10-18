package com.hp.thermalshockreforged.world.sanctum;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.decor.geo.*;
import com.hp.thermalshockreforged.entity.sanctum.SanctumContent;
import java.util.*;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public final class SanctumRelease {
    private static final List<Row> ROWS = rows();
    private SanctumRelease() { }
    private record Row(int y, int x, int minZ, int maxZ) { }
    private static List<Row> rows() {
        var rows = new ArrayList<Row>();
        for (int y = -29; y <= 29; y++) for (int x = -79; x <= 79; x++) {
            int min = 80, max = -80;
            for (int z = -79; z <= 79; z++) {
                double distance = (double)(x * x) / (79 * 79) + (double)(y * y) / (29 * 29) + (double)(z * z) / (79 * 79);
                if (distance <= 1) { min = Math.min(min, z); max = z; }
            }
            if (min <= max) rows.add(new Row(y, x, min, max));
        }
        return List.copyOf(rows);
    }
    public static void release(ServerLevel level, Vec3 anchor) {
        long started = System.nanoTime();
        var light = MaterialContent.blocks().get("sanctum_light").get();
        var redLight = MaterialContent.blocks().get("red_sanctum_light").get();
        var capsule = GeoDecorContent.block(GeoDecorKind.CAPSULE);
        Predicate<BlockState> relevant = state -> state.is(light) || state.is(capsule);
        var chunks = new HashMap<Long, LevelChunk>();
        var position = new BlockPos.MutableBlockPos();
        int[] zPositions = new int[159];
        for (int z = -79; z <= 79; z++) zPositions[z + 79] = Mth.floor(anchor.z + z);
        int lights = 0, capsules = 0, spawned = 0, inspected = 0, skipped = 0;
        for (Row row : ROWS) {
            int y = Mth.floor(anchor.y + row.y);
            if (level.isOutsideBuildHeight(y)) continue;
            int x = Mth.floor(anchor.x + row.x), chunkX = x >> 4;
            for (int z = row.minZ; z <= row.maxZ;) {
                int chunkZ = zPositions[z + 79] >> 4;
                int end = z;
                while (end < row.maxZ && zPositions[end + 80] >> 4 == chunkZ) end++;
                long chunkKey = ChunkPos.asLong(chunkX, chunkZ);
                LevelChunk chunk = chunks.get(chunkKey);
                if (chunk == null) { chunk = level.getChunk(chunkX, chunkZ); chunks.put(chunkKey, chunk); }
                var section = chunk.getSection(chunk.getSectionIndex(y));
                if (section.hasOnlyAir() || !section.maybeHas(relevant)) { skipped += end - z + 1; z = end + 1; continue; }
                for (; z <= end; z++) {
                    position.set(x, y, zPositions[z + 79]);
                    BlockState state = chunk.getBlockState(position);
                    inspected++;
                    if (state.is(light)) { level.setBlock(position, redLight.withPropertiesOf(state), 3); lights++; }
                    else if (state.is(capsule)) {
                        capsules++;
                        EntityType<? extends Mob> type = switch (state.getValue(CapsuleBlock.VARIANT)) {
                            case 1 -> SanctumContent.SANCTUM_ERRANT.get();
                            case 2 -> SanctumContent.FROSTILYTE_GUNNER.get();
                            case 3 -> SanctumContent.KILOLYTE.get();
                            case 4 -> SanctumContent.SANCTUM_RIPPLE.get();
                            default -> null;
                        };
                        if (type != null) {
                            var spawnPos = BlockPos.containing(anchor.x + row.x + .5, anchor.y + row.y, anchor.z + z + .5);
                            var entity = type.spawn(level, spawnPos, MobSpawnType.MOB_SUMMONED);
                            if (entity != null) { entity.setYRot(level.random.nextFloat() * 360); spawned++; }
                        }
                        level.destroyBlock(position, false);
                    }
                }
            }
        }
        ThermalShockReforged.LOGGER.debug("Sanctum release completed: origin={}, lights={}, capsules={}, spawned={}, inspected={}, paletteSkipped={}, chunks={}, elapsedMs={}", anchor, lights, capsules, spawned, inspected, skipped, chunks.size(), (System.nanoTime() - started) / 1_000_000D);
    }
}
