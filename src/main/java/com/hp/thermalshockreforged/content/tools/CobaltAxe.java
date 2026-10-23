package com.hp.thermalshockreforged.content.tools;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import com.hp.thermalshockreforged.content.tools.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.RotatedPillarBlock;

final class CobaltAxe extends AxeItem {
    private static final Map<String, String> STRIPPING = Map.of(
            "ochre_log", "stripped_ochre_log", "vermilli_log", "stripped_vermilli_log",
            "shiver_log", "stripped_shiverglitter_log", "kyaneos_log", "stripped_kyaneos_log",
            "ochre_wood", "stripped_ochre_wood", "vermilli_wood", "stripped_vermilli_wood",
            "shiverglitter_wood", "stripped_shiverglitter_wood", "kyaneos_wood", "stripped_kyaneos_wood");

    CobaltAxe(Tier tier, float damage, float speed, Properties properties) { super(tier, damage, speed, properties); }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult original = super.useOn(context);
        if (original.consumesAction()) return original;
        var level = context.getLevel();
        var position = context.getClickedPos();
        var state = level.getBlockState(position);
        var id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!id.getNamespace().equals(ThermalShockReforged.MOD_ID)) return original;
        String target = STRIPPING.get(id.getPath());
        if (target == null) return original;
        if (!level.isClientSide()) {
            var stripped = MaterialContent.blocks().get(target).get().defaultBlockState();
            stripped = stripped.setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
            level.setBlock(position, stripped, 3);
            level.playSound(null, position, SoundEvents.AXE_STRIP, SoundSource.NEUTRAL, 1, 1);
        }
        return InteractionResult.SUCCESS;
    }
}
