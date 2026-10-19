package com.hp.thermalshockreforged.world.geodes;

import com.hp.thermalshockreforged.world.WorldProgressData;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.GeodeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;

final class ConditionalGeodeFeature extends GeodeFeature {
    private final int choice;

    ConditionalGeodeFeature(int choice) {
        super(GeodeConfiguration.CODEC);
        this.choice = choice;
    }

    @Override
    public boolean place(FeaturePlaceContext<GeodeConfiguration> context) {
        return WorldProgressData.forGeneration(context.level().getLevel()).oreTier2() == choice && super.place(context);
    }
}
