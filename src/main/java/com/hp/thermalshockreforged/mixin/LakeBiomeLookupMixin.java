package com.hp.thermalshockreforged.mixin;

import com.hp.thermalshockreforged.world.LakeBiomeLookup;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LakeFeature.class)
public abstract class LakeBiomeLookupMixin {
    @WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"))
    private Holder<Biome> thermalShockReforged$readLakeBiome(WorldGenLevel level, BlockPos position, Operation<Holder<Biome>> original, FeaturePlaceContext<LakeFeature.Configuration> context) {
        return LakeBiomeLookup.getBiome(level, position, context.config(), original);
    }
}
