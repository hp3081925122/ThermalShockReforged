package com.hp.thermalshockreforged.entity.electricsphere;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;

import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.content.npc.florist.LeafcutterItem;
import com.hp.thermalshockreforged.entity.base.AnimatedPathfinderEntity;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public final class ElectricSphereEntity extends AnimatedPathfinderEntity {
    public ElectricSphereEntity(EntityType<? extends ElectricSphereEntity> type, Level level) { super(type, level, "electric_sphere_base", 0); moveControl = new FlyingMoveControl(this, 10, true); setNoGravity(true); setPersistenceRequired(); }
    @Override protected void registerGoals() {
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, false, true));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); }
        });
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= getBbWidth() * getBbWidth() + target.getBbWidth(); }
    @Override protected PathNavigation createNavigation(Level level) { return new FlyingPathNavigation(this, level); }
    @Override public void setNoGravity(boolean value) { super.setNoGravity(true); }
    @Override public void aiStep() { super.aiStep(); setNoGravity(true); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean causeFallDamage(double distance, float multiplier, DamageSource source) { return false; }
    @Override protected void checkFallDamage(double distance, boolean onGround, net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos) { }
    @Override protected String movementAnimation(boolean moving) { return "0"; }
    @Override protected List<String> procedureAnimations() { return List.of("0", "1"); }
    @Override public void baseTick() { super.baseTick(); makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(.25, .05, .25)); }
    @Override public void playerTouch(Player player) {
        super.playerTouch(player);
        if (!(level() instanceof ServerLevel server) || !isAlive() || distanceTo(player) > .75F) return;
        double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * .025;
        if (Double.isFinite(damage) && damage > 0) player.hurt(new DamageSource(server.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS)), (float)Math.min(damage, Float.MAX_VALUE));
    }
    public void rebound(Entity attacker) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || attacker == null || attacker == this || attacker.level() != level()) return;
        setHealth(getMaxHealth());
        double resistance = attacker instanceof LivingEntity living ? Math.clamp(living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0, 1) : 0;
        Vec3 movement = attacker.getDeltaMovement();
        Vec3 horizontal = new Vec3(movement.x, 0, movement.z);
        if (horizontal.lengthSqr() > .25) horizontal = horizontal.normalize().scale(.5);
        horizontal = horizontal.scale(1 - resistance);
        attacker.setDeltaMovement(movement.x + horizontal.x, movement.y + .35 * (1 - resistance), movement.z + horizontal.z);
        attacker.hurtMarked = true;
        server.sendParticles(ElectricSphereContent.ELECTRIC_GLOW.get(), getX(), getY(), getZ(), 1, 0, 0, 0, 0);
        server.playSound(null, blockPosition(), SoundEvents.SHULKER_SHOOT, SoundSource.NEUTRAL, 1, 1);
    }
    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        var parent = super.mobInteract(player, hand);
        if (parent.consumesAction() || hand != InteractionHand.MAIN_HAND || !isAlive() || isRemoved() || level().dimension().identifier().equals(ElectricSphereContent.id("aqeron"))) return parent;
        var stack = player.getMainHandItem();
        if (!stack.is(Items.SHEARS) && !(stack.getItem() instanceof LeafcutterItem)) return parent;
        if (level() instanceof ServerLevel server) {
            boolean spawned = false;
            for (var drop : com.hp.thermalshockreforged.content.loot.LootContent.roll(server, "gameplay/electric_sphere_shearing", this)) if (com.hp.thermalshockreforged.content.loot.LootContent.spawn(server, position(), drop, 0)) spawned = true;
            if (spawned) { discard(); server.playSound(null, blockPosition(), SoundEvents.SNOW_GOLEM_SHEAR, SoundSource.BLOCKS, 1, 1); }
        }
        return InteractionResult.SUCCESS;
    }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
        if (!Float.isFinite(amount) || amount <= 0 || source.is(DamageTypes.IN_FIRE) || source.getDirectEntity() instanceof AbstractArrow || source.getDirectEntity() instanceof AbstractThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud
            || source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.LIGHTNING_BOLT) || source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.TRIDENT)
            || source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.DRAGON_BREATH) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) return false;
        return super.hurtServer(level, source, amount);
    }
    public static AttributeSupplier.Builder attributes() { return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.MAX_HEALTH, 100).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 40).add(Attributes.FOLLOW_RANGE, 0).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.ATTACK_KNOCKBACK, 1).add(Attributes.FLYING_SPEED, 0).add(Attributes.STEP_HEIGHT, 0); }
}
