package com.hp.thermalshockreforged.entity.legacy;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.protoi.ProtoiEntityBase;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class SolaEntity extends ProtoiEntityBase {
    public SolaEntity(EntityType<? extends SolaEntity> type, Level level) { super(type, level, "zerkglow", false, 4); xpReward = 10; }
    @Override protected void dropCustomDeathLoot(net.minecraft.server.level.ServerLevel level, DamageSource source, boolean recentlyHit) { super.dropCustomDeathLoot(level, source, recentlyHit); spawnAtLocation(new ItemStack(MaterialContent.items().get("pathiolite_scrap").get())); }
    @Override protected EntityDimensions getDefaultDimensions(Pose pose) { float scale = getHealth() < 4 ? 1 : getHealth() / 3; return super.getDefaultDimensions(pose).scale(scale); }
    @Override protected void registerGoals() { super.registerGoals(); targetSelector.addGoal(5, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(this, Player.class, false, false)); }
    public static AttributeSupplier.Builder createAttributes() { return attributes(.2, 9, 10, 4, 16); }
}
