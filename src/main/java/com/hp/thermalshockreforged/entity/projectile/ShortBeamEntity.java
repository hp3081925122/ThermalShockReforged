package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.content.MaterialContent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public abstract class ShortBeamEntity extends LegacyArrowEntity {
    protected ShortBeamEntity(EntityType<? extends ShortBeamEntity> type, Level level) { super(type, level); }
    protected ShortBeamEntity(EntityType<? extends ShortBeamEntity> type, LivingEntity owner, Level level) { super(type, owner, level, ItemStack.EMPTY); }
    protected abstract boolean thistle();
    @Override protected boolean fliesWithoutGravity() { return true; }
    @Override protected int lifetimeTicks() { return 5; }
    @Override protected ItemStack projectileItem() { return new ItemStack(MaterialContent.items().get("bismuth").get()); }
    @Override protected void onHitEntity(EntityHitResult hit) { super.onHitEntity(hit); if (thistle() && level() instanceof ServerLevel server && hit.getEntity() instanceof LivingEntity living) living.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0)); }
}
