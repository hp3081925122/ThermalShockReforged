package com.hp.thermalshockreforged.content.terrain;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public final class ThermalSaplingBlock extends SaplingBlock {
    public ThermalSaplingBlock(String name, String feature) {
        super(new TreeGrower(ThermalShockReforged.MOD_ID + ":" + name, Optional.empty(),
                        Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE,
                                ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, feature))), Optional.empty()),
                Properties.of().mapColor(MapColor.PLANT).randomTicks().sound(SoundType.GRASS).instabreak().noCollission()
                        .offsetType(BlockBehaviour.OffsetType.NONE).pushReaction(PushReaction.DESTROY));
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) { return 100; }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) { return 60; }
}
