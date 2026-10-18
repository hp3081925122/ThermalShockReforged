package com.hp.thermalshock.content.terrain.vermilli;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

public final class StructureFeature extends Feature<StructureFeatureConfiguration> {
    public StructureFeature() { super(StructureFeatureConfiguration.CODEC); }

    @Override public boolean place(FeaturePlaceContext<StructureFeatureConfiguration> context) {
        var random = context.random();
        var world = context.level();
        var config = context.config();
        Rotation rotation = config.randomRotation() ? Rotation.getRandom(random) : Rotation.NONE;
        Mirror mirror = config.randomMirror() ? Mirror.values()[random.nextInt(2)] : Mirror.NONE;
        var position = context.origin().offset(config.offset());
        var template = world.getLevel().getServer().getStructureManager().getOrCreate(config.structure());
        var settings = new StructurePlaceSettings().setRotation(rotation).setMirror(mirror).setRandom(random).setIgnoreEntities(false)
                .addProcessor(new BlockIgnoreProcessor(config.ignoredBlocks().stream().map(Holder::value).toList()));
        template.placeInWorld(world, position, position, settings, random, 4);
        return true;
    }
}
