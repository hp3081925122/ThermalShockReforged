package com.hp.thermalshockreforged.world.ore;

import com.hp.thermalshockreforged.world.WorldProgressData;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

final class ConditionalOreFeature extends OreFeature {
    private final int tier;
    private final int choice;
    ConditionalOreFeature(int tier, int choice) {
        super(OreConfiguration.CODEC);
        this.tier = tier;
        this.choice = choice;
    }
    @Override public boolean place(FeaturePlaceContext<OreConfiguration> context) {
        var progress = WorldProgressData.forGeneration(context.level().getLevel());
        return (tier == 1 ? progress.oreTier1() : progress.oreTier2()) == choice && super.place(context);
    }
}
