package com.hp.thermalshockreforged.content;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public final class MaterialPillarBlock extends RotatedPillarBlock {
    public static final Map<String, String> STRIPPING = Map.of(
            "ochre_log", "stripped_ochre_log", "vermilli_log", "stripped_vermilli_log",
            "shiver_log", "stripped_shiverglitter_log", "kyaneos_log", "stripped_kyaneos_log",
            "ochre_wood", "stripped_ochre_wood", "vermilli_wood", "stripped_vermilli_wood",
            "shiverglitter_wood", "stripped_shiverglitter_wood", "kyaneos_wood", "stripped_kyaneos_wood");
    private final int flammability;

    public MaterialPillarBlock(Properties properties, int flammability) {
        super(properties);
        this.flammability = flammability;
    }

    @Override
    protected int getLightDampening(BlockState state) {
        return 15;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos position, Direction face) {
        return flammability < 0 ? super.getFlammability(state, level, position, face) : flammability;
    }

    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.AXE_STRIP) {
            var id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            if (id.getNamespace().equals(ThermalShockReforged.MOD_ID)) {
                String target = STRIPPING.get(id.getPath());
                if (target != null) {
                    var holder = MaterialContent.blocks().get(target);
                    if (holder != null) return holder.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
                }
            }
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }
}
