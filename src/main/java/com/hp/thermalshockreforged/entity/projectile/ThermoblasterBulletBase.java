package com.hp.thermalshockreforged.entity.projectile;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
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
    private static final TagKey<EntityType<?>> PYRO_WEAK = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "pyro_weak"));
    private int remainingTicks = 40;
    protected ThermoblasterBulletBase(EntityType<? extends ThermoblasterBulletBase> type, Level level) { super(type, level); }
    protected ThermoblasterBulletBase(EntityType<? extends ThermoblasterBulletBase> type, LivingEntity owner, Level level) { super(type, owner, level, ItemStack.EMPTY); setRemainingFireTicks(100); }
    protected abstract boolean shotgun();
    @Override protected ItemStack projectileItem() { return new ItemStack(shotgun() ? MaterialContent.items().get("cobalt_splatter").get() : GeoDecorContent.item(GeoDecorKind.BLUE_ORB)); }
    @Override public void tick() {
        super.tick();
        if (!shotgun() && level().isClientSide) level().addParticle(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME, getX(), getY(), getZ(), 0, 0, 0);
        if (!level().isClientSide && --remainingTicks <= 0) discard();
    }
    @Override protected void onHitEntity(EntityHitResult hit) { super.onHitEntity(hit); impact(hit.getEntity(), hit.getEntity().blockPosition()); }
    @Override protected void onHitBlock(BlockHitResult hit) { super.onHitBlock(hit); impact(null, hit.getBlockPos()); }
    private void impact(Entity target, BlockPos pos) {
        if (!(level() instanceof ServerLevel server)) return;
        if (target != null && !(target instanceof Player)) {
            server.playSound(null, pos, SoundEvents.GENERIC_HURT, SoundSource.NEUTRAL, 1, 1);
            server.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.NEUTRAL, 1, 1);
            if (shotgun() && target instanceof LivingEntity living) living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3, false, false));
            if (target.getType().is(PYRO_WEAK)) target.hurt(new net.minecraft.world.damagesource.DamageSource(server.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS)), 6);
        }
        discard();
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putInt("ThermoblasterRemainingTicks", remainingTicks); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); remainingTicks = Math.clamp(tag.getInt("ThermoblasterRemainingTicks"), 1, 40); setRemainingFireTicks(100); }
}
