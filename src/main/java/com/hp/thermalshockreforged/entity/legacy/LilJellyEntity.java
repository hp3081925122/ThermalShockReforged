package com.hp.thermalshockreforged.entity.legacy;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import com.hp.thermalshockreforged.entity.projectile.MagnetCrossbowBulletEntity;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public final class LilJellyEntity extends AnimatedMonsterEntity implements RangedAttackMob {
    public LilJellyEntity(EntityType<? extends LilJellyEntity> type, Level level) {
        super(type, level, "lil_jelly", 0);
        setCustomName(Component.translatable("entity.thermal_shock_reforged.lil_jelly").withStyle(ChatFormatting.GREEN));
        setCustomNameVisible(true);
        setPersistenceRequired();
        moveControl = new FlyingMoveControl(this, 10, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.MAX_HEALTH, 99)
                .add(Attributes.ARMOR, 99).add(Attributes.ATTACK_DAMAGE, 3).add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.FLYING_SPEED, 0).add(Attributes.STEP_HEIGHT, .6);
    }

    @Override protected void registerGoals() {
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, false, true));
        goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 20, 10) {
            @Override public boolean canContinueToUse() { return canUse(); }
        });
    }

    @Override protected PathNavigation createNavigation(Level level) { return new FlyingPathNavigation(this, level); }
    @Override protected List<String> procedureAnimations() { return List.of("idle"); }
    @Override protected String movementAnimation(boolean moving) { return "idle"; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean isPushable() { return false; }
    @Override protected void doPush(Entity entity) { }
    @Override protected void pushEntities() { }
    @Override public void setNoGravity(boolean ignored) { super.setNoGravity(true); }
    @Override public void aiStep() { super.aiStep(); setNoGravity(true); }
    @Override public boolean causeFallDamage(double distance, float multiplier, DamageSource source) { return false; }
    @Override protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) { }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }

    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float damage) {
        Entity direct = source.getDirectEntity();
        if (direct instanceof AbstractArrow || direct instanceof Player || direct instanceof AbstractThrownPotion || direct instanceof AreaEffectCloud
                || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS)
                || source.is(DamageTypes.DROWN) || source.is(DamageTypes.LIGHTNING_BOLT) || source.is(DamageTypes.EXPLOSION)
                || source.is(DamageTypes.TRIDENT) || source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.DRAGON_BREATH)
                || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) return false;
        return super.hurtServer(level, source, damage);
    }

    @Override public void performRangedAttack(LivingEntity target, float power) {
        if (!level().isClientSide()) MagnetCrossbowBulletEntity.shoot(this, target, getAttributeValue(Attributes.ATTACK_DAMAGE) * (5.0 / 3));
    }

    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        if (level instanceof ServerLevel server) server.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 5, .1, .1, .1, .1);
        return result;
    }

    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (isAlive() && !isRemoved() && player.getItemInHand(hand).is(MaterialContent.items().get("empty_amaljelly_canister").get())) {
            if (!level().isClientSide()) {
                discard();
                AmaljellyCanisterItem.exchangeOne(player, hand, new ItemStack(LegacyContent.AMALJELLY_CANISTER.get()));
            }
            return InteractionResult.SUCCESS;
        }
        super.mobInteract(player, hand);
        return InteractionResult.SUCCESS;
    }
}
