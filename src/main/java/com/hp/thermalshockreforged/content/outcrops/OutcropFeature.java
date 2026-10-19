package com.hp.thermalshockreforged.content.outcrops;

import com.hp.thermalshockreforged.world.WorldProgressData;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.ReplaceBlockFeature;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

final class OutcropFeature extends ReplaceBlockFeature {
    private final int oreChoice;
    OutcropFeature(int oreChoice) { super(ReplaceBlockConfiguration.CODEC); this.oreChoice = oreChoice; }
    @Override public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> context) {
        return WorldProgressData.forGeneration(context.level().getLevel()).oreTier2() == oreChoice && super.place(context);
    }
}
