package com.hp.thermalshockreforged.entity.protoi;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.config.CombatConfig;
import com.hp.thermalshockreforged.entity.attack.BossDeathEffectEntity;
import com.hp.thermalshockreforged.entity.mangler.ManglerContent;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.*;
public final class ProtoiSunbeamEntity extends ProtoiEntityBase {
    private int cooldown;
    private UUID attackTask;
    private boolean attributesApplied;
    private boolean loadedFromSave;
    private boolean deathVisualStarted;
    private boolean experiencePending;
    private UUID experienceAttacker;
    public ProtoiSunbeamEntity(EntityType<? extends ProtoiSunbeamEntity> type, Level level) {
        super(type, level, "protoi_sunbeam", true, 100);
        xpReward = 100; setNoGravity(true); applyConfiguredAttributes();
    }
    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return attributes(0, 100, 0, 20, 16).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.FLYING_SPEED, 0).add(Attributes.STEP_HEIGHT, 0);
    }
    private void applyConfiguredAttributes() {
        if (level().isClientSide || attributesApplied || !CombatConfig.SPEC.isLoaded()) return;
        float health = getHealth();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(CombatConfig.SUNBEAM_HEALTH.get());
        getAttribute(Attributes.ARMOR).setBaseValue(CombatConfig.SUNBEAM_ARMOR.get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(CombatConfig.SUNBEAM_DAMAGE.get());
        getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(CombatConfig.SUNBEAM_FOLLOW_RANGE.get());
        setHealth(loadedFromSave ? Math.min(health, getMaxHealth()) : getMaxHealth()); attributesApplied = true;
    }
    public void beginAttack(LivingEntity victim) {
        if (!(level() instanceof ServerLevel server) || victim == this || victim.level() != level() || cooldown > 0) return;
        UUID task = SunbeamAttackEntity.submit(server, this, victim);
        if (task == null) { ThermalShockReforged.LOGGER.warn("Protoi Sunbeam attack submission failed: caster={}, target={}", getUUID(), victim.getUUID()); return; }
        attackTask = task; cooldown = CombatConfig.SUNBEAM_COOLDOWN.get();
        setYRot((float)(Math.atan2(getZ() - victim.getZ(), getX() - victim.getX()) * 57.2958 + 90));
        setXRot((float)(Math.atan2(getY() - victim.getY(), Math.sqrt((getX() - victim.getX()) * (getX() - victim.getX()) + (getZ() - victim.getZ()) * (getZ() - victim.getZ()))) * 57.2958));
        setYBodyRot(getYRot()); setYHeadRot(getYRot()); yRotO = getYRot(); xRotO = getXRot(); yBodyRotO = getYRot(); yHeadRotO = getYRot();
        server.playSound(null, victim.blockPosition(), sound("protoi_cry"), SoundSource.HOSTILE, 1, -1);
        triggerAnim("procedure", "attack");
    }
    void finishAttack(UUID task) { if (task.equals(attackTask)) attackTask = null; }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 100; }
    @Override protected PathNavigation createNavigation(Level level) { return new FlyingPathNavigation(this, level); }
    @Override public void setNoGravity(boolean ignored) { super.setNoGravity(true); }
    @Override protected void registerGoals() {
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); }
        });
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }
    private static SoundEvent sound(String id) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, id)); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return sound("delta_hurt"); }
    @Override protected SoundEvent getDeathSound() { return sound("delta_death"); }
    @Override public void baseTick() { super.baseTick(); if (!level().isClientSide && cooldown > 0) cooldown--; }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (attackTask != null && server.getEntity(attackTask) instanceof SunbeamAttackEntity task) task.capture(this);
            if (dead && !deathVisualStarted) startDeathVisual(server);
        }
    }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); applyConfiguredAttributes(); }
    @Override public void die(DamageSource source) { super.die(source); if (dead && !deathVisualStarted && level() instanceof ServerLevel server) startDeathVisual(server); }
    private void startDeathVisual(ServerLevel server) {
        deathVisualStarted = BossDeathEffectEntity.submit(server, ManglerContent.HERALD_DEATH_EFFECT.get(), position(), getEyeHeight());
        if (!deathVisualStarted) ThermalShockReforged.LOGGER.debug("Protoi Sunbeam death effect submission failed: entity={}", getUUID());
    }
    @Override protected void dropExperience(Entity attacker) { experiencePending = true; experienceAttacker = attacker == null ? null : attacker.getUUID(); }
    @Override protected void tickDeath() {
        if (++deathTime >= 60 && !isRemoved()) {
            remove(RemovalReason.KILLED);
            if (experiencePending && level() instanceof ServerLevel server) {
                experiencePending = false;
                super.dropExperience(experienceAttacker == null ? null : server.getEntity(experienceAttacker));
            }
        }
    }
    @Override public void triggerProcedure(String animation) { if ("idle".equals(animation) || "attack".equals(animation)) triggerAnim("procedure", animation); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 0, state -> PlayState.STOP).triggerableAnim("idle", RawAnimation.begin().thenPlay("idle")).triggerableAnim("attack", RawAnimation.begin().thenPlay("attack"));
        controllers.add(new AnimationController<>(this, "movement", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
        controllers.add(procedure);
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); tag.putInt("SunbeamCooldown", cooldown); tag.putBoolean("SunbeamDeathVisualStarted", deathVisualStarted);
        if (attackTask != null) tag.putUUID("SunbeamAttackTask", attackTask);
        tag.putBoolean("SunbeamExperiencePending", experiencePending);
        if (experienceAttacker != null) tag.putUUID("SunbeamExperienceAttacker", experienceAttacker);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        loadedFromSave = true; super.readAdditionalSaveData(tag); attributesApplied = false; applyConfiguredAttributes();
        cooldown = Math.clamp(tag.getInt("SunbeamCooldown"), 0, 72000); deathVisualStarted = tag.getBoolean("SunbeamDeathVisualStarted");
        attackTask = tag.hasUUID("SunbeamAttackTask") ? tag.getUUID("SunbeamAttackTask") : null;
        experiencePending = tag.getBoolean("SunbeamExperiencePending");
        experienceAttacker = tag.hasUUID("SunbeamExperienceAttacker") ? tag.getUUID("SunbeamExperienceAttacker") : null;
    }
}
