package com.hp.thermalshockreforged.content.reactor;

import java.util.UUID;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class ReactorCoreBlockEntity extends BlockEntity {
    private UUID sequence;
    public ReactorCoreBlockEntity(BlockPos pos, BlockState state) { super(ReactorContent.CORE_ENTITY.get(), pos, state); }
    void assign(UUID id) { sequence = id; setChanged(); }
    boolean owns(UUID id) { return id.equals(sequence); }
    @Override protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        sequence = input.getString("ReactorSequence").map(java.util.UUID::fromString).orElse(null);
    }
    @Override protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        if (sequence != null) output.putString("ReactorSequence", sequence.toString());
    }
}
