package com.hp.thermalshockreforged.entity.protoi;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.config.CombatConfig;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.*;
public final class ProtoiDeltaEntity extends ProtoiEntityBase {
    private int cooldown;
    private UUID attackTask;
    private boolean attributesApplied;
    private boolean loadedFromSave;
    private boolean experiencePending;
    private UUID experienceAttacker;
    public ProtoiDeltaEntity(EntityType<? extends ProtoiDeltaEntity> type, Level level) { super(type, level, "protoi_delta", false, 256); xpReward = 8; applyConfiguredAttributes(); }
    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() { return attributes(.25, 50, 8, 5, 16).add(Attributes.ATTACK_KNOCKBACK, .2).add(Attributes.STEP_HEIGHT, 1); }
    private void applyConfiguredAttributes() {
        if (level().isClientSide || attributesApplied || !CombatConfig.SPEC.isLoaded()) return;
        float health = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(CombatConfig.DELTA_HEALTH.get());
        getAttribute(Attributes.ARMOR).setBaseValue(CombatConfig.DELTA_ARMOR.get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(CombatConfig.DELTA_DAMAGE.get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(CombatConfig.DELTA_SPEED.get());
        getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(CombatConfig.DELTA_FOLLOW_RANGE.get());
        setHealth(loadedFromSave ? Math.min(health, getMaxHealth()) : getMaxHealth());
        attributesApplied = true;
    }
    public void beginAttack(LivingEntity victim) {
        if (!(level() instanceof ServerLevel server) || victim == this || victim.level() != level() || cooldown > 0) return;
        UUID task = DeltaVolleyEntity.submit(server, this, victim);
        if (task == null) {
            ThermalShockReforged.LOGGER.warn("Protoi Delta volley submission failed: caster={}, target={}", getUUID(), victim.getUUID());
            return;
        }
        attackTask = task; cooldown = CombatConfig.DELTA_COOLDOWN.get();
        setYRot((float)(Math.atan2(getZ() - victim.getZ(), getX() - victim.getX()) * 57.2958 + 90));
        setXRot((float)(Math.atan2(getY() - victim.getY(), Math.sqrt((getX() - victim.getX()) * (getX() - victim.getX()) + (getZ() - victim.getZ()) * (getZ() - victim.getZ()))) * 57.2958));
        setYBodyRot(getYRot()); setYHeadRot(getYRot()); yRotO = getYRot(); xRotO = getXRot(); yBodyRotO = getYRot(); yHeadRotO = getYRot();
        server.playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "protoi_cry")), SoundSource.HOSTILE, 1, 1);
    }
    void finishVolley(UUID task) { if (task.equals(attackTask)) attackTask = null; }
    @Override protected boolean persistentByDefault() { return false; }
    @Override public boolean removeWhenFarAway(double distance) { return true; }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 256; }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && distanceToSqr(target) <= 256; }
        });
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 12));
        goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class, 4, 1, 1.2));
        targetSelector.addGoal(5, new HurtByTargetGoal(this).setAlertOthers());
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }
    @Override public void baseTick() { super.baseTick(); if (!level().isClientSide && cooldown > 0) cooldown--; }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server && attackTask != null && server.getEntity(attackTask) instanceof DeltaVolleyEntity task) task.capture(this);
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); applyConfiguredAttributes(); }
    @Override public void triggerProcedure(String animation) { if (java.util.List.of("idle", "walk", "shoot").contains(animation)) triggerAnim("procedure", animation); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 4, state -> PlayState.STOP);
        for (String clip : java.util.List.of("idle", "walk", "shoot")) procedure.triggerableAnim(clip, RawAnimation.begin().thenPlay(clip));
        controllers.add(new AnimationController<>(this, "movement", 4, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            boolean moving = state.isMoving() || state.getLimbSwingAmount() <= -.15F || state.getLimbSwingAmount() >= .15F;
            return state.setAndContinue(RawAnimation.begin().thenLoop(moving ? "walk" : "idle"));
        }));
        controllers.add(procedure);
    }
    @Override protected void dropExperience(Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() {
        if (++deathTime >= 20 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) {
                experiencePending = false;
                super.dropExperience(experienceAttacker == null ? null : server.getEntity(experienceAttacker));
            }
        }
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); tag.putInt("DeltaCooldown", cooldown);
        if (attackTask != null) tag.putUUID("DeltaAttackTask", attackTask);
        tag.putBoolean("DeltaExperiencePending", experiencePending);
        if (experienceAttacker != null) tag.putUUID("DeltaExperienceAttacker", experienceAttacker);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        loadedFromSave = true; super.readAdditionalSaveData(tag); attributesApplied = false; applyConfiguredAttributes();
        cooldown = Math.clamp(tag.getInt("DeltaCooldown"), 0, 72000);
        attackTask = tag.hasUUID("DeltaAttackTask") ? tag.getUUID("DeltaAttackTask") : null;
        experiencePending = tag.getBoolean("DeltaExperiencePending");
        experienceAttacker = tag.hasUUID("DeltaExperienceAttacker") ? tag.getUUID("DeltaExperienceAttacker") : null;
    }
    @Override protected SoundEvent getAmbientSound() { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "delta_ambient")); }
    @Override protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "delta_hurt")); }
    @Override protected SoundEvent getDeathSound() { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "delta_death")); }
    @Override protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) { playSound(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "delta_step")), .15F, 1); }
}
