package com.hp.thermalshockreforged.entity.gaze;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class LensEntity extends AnimatedMonsterEntity {
    private static final Map<ServerPlayer, Integer> LAST_GAZE_TICK = new WeakHashMap<>();
    public LensEntity(EntityType<? extends LensEntity> type, Level level) {
        super(type, level, "lens", 12);
        moveControl = new FlyingMoveControl(this, 10, true);
        setNoGravity(true);
    }
    static void clearGazeCache() { LAST_GAZE_TICK.clear(); }
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); }
        });
        targetSelector.addGoal(2, new HurtByTargetGoal(this));
        goalSelector.addGoal(3, new FloatGoal(this));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= getBbWidth() * getBbWidth() + target.getBbWidth(); }
    @Override protected PathNavigation createNavigation(Level level) { return new FlyingPathNavigation(this, level); }
    @Override public void setNoGravity(boolean ignored) { super.setNoGravity(true); }
    @Override public boolean causeFallDamage(float distance, float multiplier, DamageSource source) { return false; }
    @Override public boolean hurt(DamageSource source, float amount) { return !source.is(DamageTypes.DROWN) && super.hurt(source, amount); }
    @Override protected int animationTransitionTicks() { return 5; }
    @Override protected String movementAnimation(boolean moving) { return isShiftKeyDown() ? "open" : "idle"; }
    @Override protected List<String> procedureAnimations() { return List.of("open", "idle"); }
    @Override public void baseTick() {
        super.baseTick();
        if (!(level() instanceof ServerLevel server)) return;
        boolean active = isAlive() && server.getNearestPlayer(this, 8) != null;
        setShiftKeyDown(active);
        if (!active) return;
        var area = new AABB(position(), position()).inflate(8);
        int tick = server.getServer().getTickCount();
        for (var player : server.players()) {
            if (!player.getBoundingBox().intersects(area) || Integer.valueOf(tick).equals(LAST_GAZE_TICK.get(player))) continue;
            LAST_GAZE_TICK.put(player, tick);
            LensEntity nearest = this;
            double bestDistance = distanceToSqr(player);
            double radius = Math.sqrt(bestDistance) + 1;
            for (var lens : server.getEntitiesOfClass(LensEntity.class, player.getBoundingBox().inflate(radius), Entity::isAlive)) {
                double distance = lens.distanceToSqr(player);
                if (distance < bestDistance || distance == bestDistance && lens.getId() < nearest.getId()) { nearest = lens; bestDistance = distance; }
            }
            player.lookAt(Anchor.FEET, nearest, Anchor.FEET);
            if (server.getDifficulty() != Difficulty.EASY) player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 1));
        }
        server.sendParticles(random.nextBoolean() ? GazeContent.LENS_VISAGE_1.get() : GazeContent.LENS_VISAGE_2.get(), getX(), getY() + 2, getZ(), 1, .0001, .0001, .0001, 0);
    }
    @Override protected SoundEvent getAmbientSound() { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "lensambient")); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GENERIC_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }
    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.MAX_HEALTH, 35).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 4).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.ATTACK_KNOCKBACK, 1.5).add(Attributes.FLYING_SPEED, 0).add(Attributes.STEP_HEIGHT, 1);
    }
}
