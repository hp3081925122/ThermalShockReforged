package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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
import net.minecraft.world.phys.Vec3;

public abstract class GreenOrbBulletBase extends LegacyArrowEntity {
    protected GreenOrbBulletBase(EntityType<? extends GreenOrbBulletBase> type, Level level) { super(type, level); }
    protected GreenOrbBulletBase(EntityType<? extends GreenOrbBulletBase> type, LivingEntity owner, Level level) { super(type, owner, level, ItemStack.EMPTY); }
    @Override protected ItemStack projectileItem() { return new ItemStack(GeoDecorContent.item(GeoDecorKind.GREEN_ORB)); }
    @Override protected boolean fliesWithoutGravity() { return true; }
    @Override protected int lifetimeTicks() { return 40; }
    protected boolean affects(Entity target) {
        return target != null && !(target instanceof Player)
                && !BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).equals(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "lil_jelly"));
    }
    protected boolean discardAfterEntityEffects() { return true; }
    @Override public void tick() {
        super.tick();
        if (level().isClientSide()) level().addParticle(ProjectileContent.GREEN_PARTICLE.get(), getX(), getY(), getZ(), 0, 0, 0);
    }
    @Override protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        applyGreenEffects(hit.getEntity(), position(), true);
        if (level() instanceof ServerLevel && affects(hit.getEntity()) && discardAfterEntityEffects()) discard();
    }
    @Override protected void onHitBlock(BlockHitResult hit) {
        super.onHitBlock(hit);
        BlockPos pos = hit.getBlockPos();
        applyGreenEffects(getOwner(), new Vec3(pos.getX(), pos.getY(), pos.getZ()), false);
        if (level() instanceof ServerLevel && affects(getOwner())) discard();
    }
    protected final void applyGreenEffects(Entity target, Vec3 origin, boolean poison) {
        if (!(level() instanceof ServerLevel server) || !affects(target)) return;
        server.sendParticles(ProjectileContent.GREEN_PARTICLE.get(), origin.x, origin.y, origin.z, 10, .2, .2, .2, .1);
        server.playSound(null, BlockPos.containing(origin), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.NEUTRAL, 1, 1);
        server.playSound(null, BlockPos.containing(origin), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, .5F, -3);
        if (poison && target instanceof LivingEntity living && server.getRandom().nextFloat() < .2F) living.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 1, true, true));
    }
}
