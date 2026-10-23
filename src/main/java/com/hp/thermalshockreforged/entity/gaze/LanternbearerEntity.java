package com.hp.thermalshockreforged.entity.gaze;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.base.AnimatedMonsterEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class LanternbearerEntity extends AnimatedMonsterEntity {
    private boolean deathCommitted;
    public LanternbearerEntity(EntityType<? extends LanternbearerEntity> type, Level level) { super(type, level, "lanternbearer", 8); }
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MeleeAttackGoal(this, .8, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); }
        });
        targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        goalSelector.addGoal(3, new RandomStrollGoal(this, .8));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
    }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= getBbWidth() * getBbWidth() + target.getBbWidth(); }
    @Override protected int deathDuration() { return 25; }
    @Override protected void tickEffects() {
        if (!com.hp.thermalshockreforged.content.remainingcombat.CryoCooldownVisual.tickClientEffects(
                this, false, (effect, refresh) -> onEffectUpdated(effect, refresh, null))) super.tickEffects();
    }
    @Override protected int animationTransitionTicks() { return 2; }
    @Override protected List<String> procedureAnimations() { return List.of("idle", "walk", "explode"); }
    @Override public void die(DamageSource source) {
        super.die(source);
        if (!dead || deathCommitted || !(level() instanceof ServerLevel server)) return;
        deathCommitted = true;
        triggerProcedure("explode");
        LanternDeathEntity.submit(server, position(), getAttributeValue(Attributes.ATTACK_DAMAGE));
    }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.SKELETON_AMBIENT; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.SKELETON_STEP, .15F, 1); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "bone_hit")); }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SKELETON_DEATH; }
    @Override public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) { super.addAdditionalSaveData(output); output.putBoolean("LanternDeathCommitted", deathCommitted); }
    @Override public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) { super.readAdditionalSaveData(input); deathCommitted = input.getBooleanOr("LanternDeathCommitted", false); }
    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes().add(Attributes.MOVEMENT_SPEED, .2).add(Attributes.MAX_HEALTH, 24).add(Attributes.ARMOR, 4).add(Attributes.ATTACK_DAMAGE, 3).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.STEP_HEIGHT, .6);
    }
}
