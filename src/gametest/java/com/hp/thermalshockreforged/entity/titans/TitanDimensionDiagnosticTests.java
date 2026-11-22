package com.hp.thermalshockreforged.entity.titans;

import com.hp.thermalshockreforged.gametest.TsrGameTests;
import net.minecraft.gametest.framework.GameTestHelper;

public final class TitanDimensionDiagnosticTests {
    public static void register() {
        TsrGameTests.addDedicated("history_titan_dimension_diagnostic_tests_native_cross_dimension_transfer_cancels_old_swing", TitanDimensionDiagnosticTests::nativeCrossDimensionTransferCancelsOldSwing, 440, "supernova_server");
    }

    public static void nativeCrossDimensionTransferCancelsOldSwing(GameTestHelper helper) { TitanServerBehaviorTests.findingCasterInAnotherDimensionCancelsTheOldWorldHit(helper); }

    private TitanDimensionDiagnosticTests() { }
}
