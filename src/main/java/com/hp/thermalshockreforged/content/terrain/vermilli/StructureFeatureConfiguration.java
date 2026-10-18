package com.hp.thermalshockreforged.content.terrain.vermilli;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record StructureFeatureConfiguration(ResourceLocation structure, boolean randomRotation, boolean randomMirror,
        HolderSet<Block> ignoredBlocks, Vec3i offset) implements FeatureConfiguration {
    public static final Codec<StructureFeatureConfiguration> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("structure").forGetter(StructureFeatureConfiguration::structure),
            Codec.BOOL.optionalFieldOf("random_rotation", false).forGetter(StructureFeatureConfiguration::randomRotation),
            Codec.BOOL.optionalFieldOf("random_mirror", false).forGetter(StructureFeatureConfiguration::randomMirror),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("ignored_blocks").forGetter(StructureFeatureConfiguration::ignoredBlocks),
            Vec3i.offsetCodec(48).optionalFieldOf("offset", Vec3i.ZERO).forGetter(StructureFeatureConfiguration::offset)
    ).apply(builder, StructureFeatureConfiguration::new));
}
