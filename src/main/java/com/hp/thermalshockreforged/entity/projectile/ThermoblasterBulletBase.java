package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.entity.orb.OrbContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.Identifier;
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

public abstract class ThermoblasterBulletBase extends LegacyArrowEntity {
    public static void fireWeaponShot(ServerLevel level, LivingEntity shooter, java.util.UUID owner, net.minecraft.world.phys.Vec3 origin, net.minecraft.world.phys.Vec3 direction, boolean shotgun, float speed) {
        ThermoblasterBulletBase bullet = shotgun ? new ThermoblasterShotgunBulletEntity(ProjectileContent.THERMOBLASTER_SHOTGUN_BULLET.get(), level) : new ThermoblasterBulletEntity(ProjectileContent.THERMOBLASTER_BULLET.get(), level);
        if (shooter != null) bullet.setOwner(shooter);
        else ((com.hp.thermalshockreforged.mixin.ProjectileOwnerAccess) (Object) bullet).thermalShockReforged$setOwnerUUID(owner);
        bullet.setPos(origin); bullet.setBaseDamage(shotgun ? 8 : .5); bullet.setImpactKnockback(shotgun ? 1 : 0); bullet.setSilent(true); bullet.setCritArrow(false);
        if (shotgun) ((com.hp.thermalshockreforged.mixin.ArrowPiercingAccess) (Object) bullet).thermalShockReforged$setPierceLevel((byte) 2);
        bullet.shoot(direction.x, direction.y, direction.z, speed, shotgun ? 20 : 1); level.addFreshEntity(bullet);
    }
    private static final TagKey<EntityType<?>> PYRO_WEAK = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "pyro_weak"));
    private static final TagKey<EntityType<?>> CRYO_WEAK = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "cryo_weak"));
    protected ThermoblasterBulletBase(EntityType<? extends ThermoblasterBulletBase> type, Level level) { super(type, level); }
    protected ThermoblasterBulletBase(EntityType<? extends ThermoblasterBulletBase> type, LivingEntity owner, Level level) { super(type, owner, level, ItemStack.EMPTY); }
    protected abstract boolean shotgun();
    @Override protected boolean fliesWithoutGravity() { return !shotgun(); }
    @Override protected int lifetimeTicks() { return 40; }
    @Override protected ItemStack projectileItem() { return new ItemStack(shotgun() ? MaterialContent.items().get("cobalt_splatter").get() : GeoDecorContent.item(GeoDecorKind.BLUE_ORB)); }
    @Override public void tick() {
        super.tick();
        if (!shotgun() && level().isClientSide()) level().addParticle(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME, getX(), getY(), getZ(), 0, 0, 0);
    }
    @Override protected void onHitEntity(EntityHitResult hit) { super.onHitEntity(hit); impact(hit.getEntity(), blockPosition(), shotgun(), getX(), getY(), getZ(), true); }
    @Override protected void onHitBlock(BlockHitResult hit) {
        super.onHitBlock(hit);
        BlockPos pos = hit.getBlockPos();
        impact(getOwner(), pos, false, pos.getX(), pos.getY(), pos.getZ(), false);
    }
    private void impact(Entity target, BlockPos pos, boolean scatter, double x, double y, double z, boolean applyDamage) {
        if (!(level() instanceof ServerLevel server)) return;
        if (target != null && !(target instanceof Player)) {
            server.playSound(null, pos, SoundEvents.GENERIC_HURT, SoundSource.NEUTRAL, 1, 1);
            server.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.NEUTRAL, 1, 1);
            if (!scatter) server.sendParticles(OrbContent.BLUE_X_BLAST.get(), x, y, z, 1, .1, .1, .1, 0);
            if (scatter && target instanceof LivingEntity living) living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 3, false, false));
            if (applyDamage && target.is(scatter ? PYRO_WEAK : CRYO_WEAK)) target.hurt(new net.minecraft.world.damagesource.DamageSource(server.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS)), 6);
        }
    }
}
