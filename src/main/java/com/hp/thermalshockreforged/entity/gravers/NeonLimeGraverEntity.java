package com.hp.thermalshockreforged.entity.gravers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class NeonLimeGraverEntity extends GraverFamilyEntity {
    public NeonLimeGraverEntity(EntityType<? extends NeonLimeGraverEntity> type, Level level) { super(type, level, "neon_lime_graver_unglow", 5345); setCustomName(Component.translatable("entity.thermal_shock_reforged.neon_lime_graver").withStyle(ChatFormatting.RED)); setCustomNameVisible(true); }
}
