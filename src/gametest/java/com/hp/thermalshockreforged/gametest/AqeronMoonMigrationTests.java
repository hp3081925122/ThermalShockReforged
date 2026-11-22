package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.atlas.AtlasContent;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.clock.WorldClocks;

public final class AqeronMoonMigrationTests {
    private static final String[] PHASES = {
            "full_moon", "waning_gibbous", "third_quarter", "waning_crescent",
            "new_moon", "waxing_crescent", "first_quarter", "waxing_gibbous"
    };
    private static final float[] SLIME_CHANCES = {0.5F, 0.375F, 0.25F, 0.125F, 0.0F, 0.125F, 0.25F, 0.375F};

    private AqeronMoonMigrationTests() {
    }

    public static void register() {
        if (!Boolean.getBoolean("tsr.aqeronMoonMigrationTests")) return;
        TsrGameTests.addDedicated("world_progress_integration_aqeron_moon_migration_preserves_all_eight_moon_phases_and_surface_slime_chances",
                AqeronMoonMigrationTests::moonPhaseAndSurfaceSlimeChanceFollowEightDayCycle, 10, "supernova_server");
    }

    public static void moonPhaseAndSurfaceSlimeChanceFollowEightDayCycle(GameTestHelper helper) {
        ServerLevel aqeron = helper.getLevel().getServer().getLevel(AtlasContent.PALACE);
        helper.assertTrue(aqeron != null, "Aqeron server level must be loaded for its environment timeline test");
        if (aqeron == null) return;
        var clock = aqeron.registryAccess().getOrThrow(WorldClocks.OVERWORLD);
        long originalTicks = aqeron.clockManager().getTotalTicks(clock);
        BlockPos sample = new BlockPos(0, 100, 0);
        try {
            for (int day = 0; day < PHASES.length; day++) {
                long ticks = day * 24000L + 18000L;
                aqeron.clockManager().setTotalTicks(clock, ticks);
                Object actualPhase = aqeron.environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, sample);
                String phaseName = actualPhase instanceof Enum<?> value
                        ? value.name().toLowerCase(Locale.ROOT)
                        : String.valueOf(actualPhase).toLowerCase(Locale.ROOT).replace(' ', '_').replace('-', '_');
                float chance = aqeron.environmentAttributes().getValue(EnvironmentAttributes.SURFACE_SLIME_SPAWN_CHANCE, sample);
                float sunAngle = aqeron.environmentAttributes().getValue(EnvironmentAttributes.SUN_ANGLE, sample);
                float starBrightness = aqeron.environmentAttributes().getValue(EnvironmentAttributes.STAR_BRIGHTNESS, sample);
                ThermalShockReforged.LOGGER.info("Aqeron moon migration sample: day={}, ticks={}, phase={}, slimeChance={}, sunAngle={}, starBrightness={}", day, ticks, phaseName, chance, sunAngle, starBrightness);
                helper.assertTrue(PHASES[day].equals(phaseName), "Aqeron moon phase at day " + day + " must be " + PHASES[day] + " but was " + phaseName);
                helper.assertTrue(Math.abs(chance - SLIME_CHANCES[day]) < 0.0001F, "Aqeron surface slime spawn chance at day " + day + " must be " + SLIME_CHANCES[day] + " but was " + chance);
                helper.assertTrue(Math.abs(sunAngle - 180.0F) < 0.0001F, "Aqeron sun angle must remain fixed at 180 degrees");
                helper.assertTrue(Math.abs(starBrightness - 0.5F) < 0.0001F, "Aqeron star brightness must remain 0.5");
            }
        } finally {
            aqeron.clockManager().setTotalTicks(clock, originalTicks);
        }
        helper.succeed();
    }
}
