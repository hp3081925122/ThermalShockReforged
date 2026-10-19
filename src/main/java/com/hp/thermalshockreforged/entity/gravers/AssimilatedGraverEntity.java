package com.hp.thermalshockreforged.entity.gravers;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class AssimilatedGraverEntity extends GraverFamilyEntity {
    public AssimilatedGraverEntity(EntityType<? extends AssimilatedGraverEntity> type, Level level) { super(type, level, "infected_graver", 5); }
}
