package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public abstract class GreenOrbBulletBase extends LegacyArrowEntity {
    private int remainingTicks = 40;
    protected GreenOrbBulletBase(EntityType<? extends GreenOrbBulletBase> type, Level level) { super(type, level); }
    protected GreenOrbBulletBase(EntityType<? extends GreenOrbBulletBase> type, LivingEntity owner, Level level) { super(type, owner, level, ItemStack.EMPTY); }
    @Override protected ItemStack projectileItem() { return new ItemStack(GeoDecorContent.item(GeoDecorKind.GREEN_ORB)); }
    @Override public void tick() {
        super.tick();
        if (level().isClientSide) level().addParticle(ProjectileContent.GREEN_PARTICLE.get(), getX(), getY(), getZ(), 0, 0, 0);
        if (!level().isClientSide && --remainingTicks <= 0) discard();
    }
    @Override protected void onHitEntity(EntityHitResult hit) { super.onHitEntity(hit); impact(hit.getEntity().blockPosition(), hit.getEntity()); }
    @Override protected void onHitBlock(BlockHitResult hit) { super.onHitBlock(hit); impact(hit.getBlockPos(), null); }
    private void impact(BlockPos pos, Entity target) {
        if (!(level() instanceof ServerLevel server)) return;
        if (target != null && !(target instanceof Player) && !isLilJelly(target)) {
            server.sendParticles(ProjectileContent.GREEN_PARTICLE.get(), getX(), getY(), getZ(), 10, .2, .2, .2, .1);
            server.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.NEUTRAL, 1, 1);
            server.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, .5F, -3);
            if (target instanceof LivingEntity living && server.getRandom().nextFloat() < .2F) living.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 1, true, true));
        } else if (target == null) {
            server.sendParticles(ProjectileContent.GREEN_PARTICLE.get(), getX(), getY(), getZ(), 10, .2, .2, .2, .1);
            server.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.NEUTRAL, 1, 1);
            server.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, .5F, -3);
        }
        discard();
    }
    private static boolean isLilJelly(Entity target) { return net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).getPath().equals("lil_jelly"); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putInt("GreenBulletRemainingTicks", remainingTicks); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); remainingTicks = Math.clamp(tag.getInt("GreenBulletRemainingTicks"), 1, 40); }
}
