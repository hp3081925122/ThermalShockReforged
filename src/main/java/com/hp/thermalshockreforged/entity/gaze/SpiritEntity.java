package com.hp.thermalshockreforged.entity.gaze;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.aura.AuraContent;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import java.util.List;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.geckolib.animation.*;

public final class SpiritEntity extends AnimatedMonsterEntity {
    private static final EntityDataAccessor<Boolean> COOLDOWN_VISIBLE = SynchedEntityData.defineId(SpiritEntity.class, EntityDataSerializers.BOOLEAN);
    private int cooldown;
    public SpiritEntity(EntityType<? extends SpiritEntity> type, Level level) { super(type, level, "spirit", 5); }
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); }
        });
        targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        goalSelector.addGoal(3, new RandomStrollGoal(this, .8));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 49; }
    @Override protected List<String> procedureAnimations() { return List.of("idle", "idle2", "walk", "run"); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 4, state -> PlayState.STOP);
        for (var clip : procedureAnimations()) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(new LegacyAnimationController<>(this, "movement", 4, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            boolean moving = state.isMoving() || state.getLimbSwingAmount() <= -.15F || state.getLimbSwingAmount() >= .15F;
            String clip = !isAggressive() && moving ? "walk" : isAggressive() && state.isMoving() ? "run" : "idle";
            return state.setAndContinue(RawAnimation.begin().thenLoop(clip));
        }));
        controllers.add(procedure);
    }
    public void beginAttack(Player victim) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || victim.level() != level() || cooldown > 0 || com.hp.thermalshockreforged.content.remainingcombat.RemainingCombatState.blocksCryoAttack(this)) return;
        cooldown = 40;
        entityData.set(COOLDOWN_VISIBLE, true);
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (var player : server.players()) {
            double distance = distanceToSqr(player);
            if (player.isAlive() && !player.isSpectator() && distance < nearestDistance) { nearest = player; nearestDistance = distance; }
        }
        if (nearest != null) {
            lookAt(Anchor.FEET, nearest.position());
            setYBodyRot(getYRot()); yBodyRotO = getYRot(); yHeadRotO = getYRot();
        }
        setDeltaMovement(getLookAngle().scale(4));
        hurtMarked = true;
        server.playSound(null, victim.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "propulsionbootsactivate")), SoundSource.HOSTILE, 1, 1);
        addEffect(new MobEffectInstance(AuraContent.DAMAGING_AURA, 20, 3));
    }
    @Override public void baseTick() {
        super.baseTick();
        if (level() instanceof ServerLevel server) {
            if (cooldown > 0 && --cooldown == 0) entityData.set(COOLDOWN_VISIBLE, false);
            server.sendParticles(ParticleTypes.SMOKE, getX(), getY() + .5, getZ(), 1, .01, .01, .01, 0);
        }
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(COOLDOWN_VISIBLE, false); }
    @Override protected void tickEffects() {
        if (!com.hp.thermalshockreforged.content.remainingcombat.CryoCooldownVisual.tickClientEffects(
                this, entityData.get(COOLDOWN_VISIBLE))) super.tickEffects();
    }
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
        return !source.is(DamageTypes.FALL) && !source.is(DamageTypes.DROWN) && !source.is(DamageTypes.WITHER) && !source.is(DamageTypes.WITHER_SKULL) && super.hurtServer(level, source, amount);
    }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.BLAZE_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.BLAZE_DEATH; }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putInt("SpiritCooldown", cooldown); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); cooldown = Math.clamp(input.getIntOr("SpiritCooldown", 0), 0, 40); entityData.set(COOLDOWN_VISIBLE, cooldown > 0); }
    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes().add(Attributes.MOVEMENT_SPEED, .3).add(Attributes.MAX_HEALTH, 4).add(Attributes.ARMOR, 4).add(Attributes.ATTACK_DAMAGE, 2).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.ATTACK_KNOCKBACK, .8).add(Attributes.STEP_HEIGHT, 1);
    }
}
