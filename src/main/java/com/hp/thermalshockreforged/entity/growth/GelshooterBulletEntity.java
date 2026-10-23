package com.hp.thermalshockreforged.entity.growth;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public final class GelshooterBulletEntity extends AbstractArrow implements ItemSupplier {
    public GelshooterBulletEntity(EntityType<? extends GelshooterBulletEntity> type,Level level) { super(type,level); }
    public GelshooterBulletEntity(LivingEntity owner) { super(GrowthEntities.GELSHOOTER_BULLET.get(),owner,owner.level(),projectileItem(),null); }
    private static ItemStack projectileItem() { return new ItemStack(BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath("thermal_shock_reforged","springelatin_ball"))); }
    @Override public ItemStack getItem() { return projectileItem(); }
    @Override protected ItemStack getDefaultPickupItem() { return projectileItem(); }
    @Override protected void doPostHurtEffects(LivingEntity target) { super.doPostHurtEffects(target); target.setArrowCount(target.getArrowCount()-1); }
    @Override protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        if(!level().isClientSide()&&hit.getEntity() instanceof LivingEntity target) target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS,20,0));
    }
    @Override public void tick() { super.tick(); if(inGround)discard(); }
    public static GelshooterBulletEntity shoot(LivingEntity owner,LivingEntity target) {
        var arrow=new GelshooterBulletEntity(owner);
        double dx=target.getX()-owner.getX();
        double dy=target.getY()+target.getEyeHeight()-1.1;
        double dz=target.getZ()-owner.getZ();
        arrow.shoot(dx,dy-arrow.getY()+Math.hypot(dx,dz)*.2,dz,1.4f,12);
        arrow.setSilent(true);
        arrow.setCritArrow(false);
        arrow.setBaseDamage(owner.getAttributeValue(Attributes.ATTACK_DAMAGE)*(2d/3d));
        owner.level().addFreshEntity(arrow);
        owner.level().playSound(null,owner.getX(),owner.getY(),owner.getZ(),SoundEvents.DISPENSER_LAUNCH,SoundSource.PLAYERS,1,1/(owner.getRandom().nextFloat()*.5f+1));
        return arrow;
    }
}
