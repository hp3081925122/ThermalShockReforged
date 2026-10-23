package com.hp.thermalshockreforged.content.npc.florist;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class LeafcutterItem extends ShearsItem {
    public LeafcutterItem() { super(com.hp.thermalshockreforged.ModRegistration.item().enchantable(8).durability(380).component(DataComponents.TOOL, ShearsItem.createToolProperties())); }
    @Override public float getDestroySpeed(ItemStack stack, BlockState state) { return 4; }
    @Override public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        boolean result = super.mineBlock(stack, level, state, pos, miner);
        if (level instanceof ServerLevel server) {
            server.playSound(null, pos, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "leafcutter")), SoundSource.NEUTRAL, 1, 1);
        }
        return result;
    }
}
