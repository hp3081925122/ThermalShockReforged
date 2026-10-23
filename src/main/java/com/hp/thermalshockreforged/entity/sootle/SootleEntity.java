package com.hp.thermalshockreforged.entity.sootle;

import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTask;
import com.hp.thermalshockreforged.world.timing.ServerEndTickTasks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animation.state.AnimationTest;

public final class SootleEntity extends AbstractSootleEntity implements ServerEndTickTask {
    private int cooldown;
    private int actionLock;
    private int skillAge = -1;
    private boolean singleAttack;
    private java.util.UUID skillTarget;
    private String skillDimension = "";
    private Vec3 origin = Vec3.ZERO;
    private boolean resumeAnimation;
    public SootleEntity(EntityType<? extends SootleEntity> type, Level level) { super(type, level, 10); }
    @Override
    protected void registerGoals() {
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false) { @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); } });
        goalSelector.addGoal(3, new RandomStrollGoal(this, 1));
        targetSelector.addGoal(4, new HurtByTargetGoal(this));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        goalSelector.addGoal(6, new FloatGoal(this));
    }
    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 4; }
    @Override
    public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity target) { if (target instanceof LivingEntity living) beginAttack(living); return false; }
    public void beginAttack(LivingEntity target) {
        if (level().isClientSide() || !isAlive() || cooldown > 0 || skillAge >= 0 || !target.isAlive() || target.level() != level()) return;
        singleAttack = random.nextDouble() < 0.3;
        cooldown = singleAttack ? 30 : 40;
        actionLock = 18;
        skillAge = 0;
        skillTarget = target.getUUID();
        skillDimension = level().dimension().identifier().toString();
        origin = target.position();
        getNavigation().stop();
        setAnimation(singleAttack ? "attack" : "attack2");
        soundAtOrigin(SoundEvents.WOODEN_BUTTON_CLICK_ON, 1);
        if (level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }
    private LivingEntity skillTarget() {
        if (!(level() instanceof ServerLevel server) || skillTarget == null || !skillDimension.equals(level().dimension().identifier().toString())) return null;
        var found = server.getEntity(skillTarget);
        return found instanceof LivingEntity living && living.isAlive() && living.level() == level() ? living : null;
    }
    private void cancelAttack() { skillAge = -1; skillTarget = null; actionLock = 0; resumeAnimation = false; }
    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (skillAge >= 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override
    public void tick() {
        if (!level().isClientSide() && (skillAge >= 0 || actionLock > 0) && !skillDimension.equals(level().dimension().identifier().toString())) cancelAttack();
        if (!level().isClientSide()) { if (cooldown > 0) cooldown--; if (actionLock > 0) actionLock--; }
        super.tick();
        if (level().isClientSide()) return;
        if (!isAlive()) { cancelAttack(); return; }
        if (actionLock > 0) getNavigation().stop();
        if (skillAge >= 0 && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID());
    }
    @Override
    public void endTick(ServerLevel level) {
        if (skillAge < 0 || isRemoved()) return;
        if (!isAlive() || !skillDimension.equals(level.dimension().identifier().toString())) { cancelAttack(); return; }
        var target = skillTarget();
        if (target == null) { cancelAttack(); return; }
        if (resumeAnimation) { setAnimation(singleAttack ? "attack" : "attack2"); resumeAnimation = false; }
        skillAge++;
        if (singleAttack && skillAge == 7) strike(target, true, 4);
        if (!singleAttack && (skillAge == 8 || skillAge == 10)) strike(target, false, skillAge == 8 ? 4 : 5);
        if (skillAge >= (singleAttack ? 7 : 10)) { skillAge = -1; skillTarget = null; }
        else ServerEndTickTasks.schedule(level, getUUID());
    }
    private void strike(LivingEntity target, boolean single, float pitch) {
        com.hp.thermalshockreforged.ThermalShockReforged.LOGGER.debug("Sootle strike at server end tick: entity={}, age={}, single={}, pitch={}, origin={}", getUUID(), skillAge, single, pitch, origin);
        soundAtOrigin(SoundEvents.PLAYER_ATTACK_SWEEP, pitch);
        if (distanceTo(target) > (single ? 2 : 3)) return;
        var type = single ? DamageTypes.GENERIC : ModDamageTypes.THERM_IFRAME_BYPASS;
        var source = new DamageSource(level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type));
        float damage = (float) (getAttributeValue(Attributes.ATTACK_DAMAGE) * (single ? 4.0 / 3.0 : 1) - target.getArmorValue() * 0.4);
        target.hurt(source, Math.max(0, damage));
    }
    private void soundAtOrigin(SoundEvent sound, float pitch) { level().playSound(null, BlockPos.containing(origin), sound, SoundSource.NEUTRAL, 1, pitch); }
    @Override
    protected boolean isImmobile() { return actionLock > 0 || super.isImmobile(); }
    @Override
    protected String movement(AnimationTest<?> state) {
        if (!isAggressive() && (state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F)) return "walk";
        if (isShiftKeyDown()) return "dig";
        return isAggressive() && state.isMoving() ? "run" : "idle";
    }
    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("SkillCooldown", cooldown); output.putInt("ActionLock", actionLock); output.putInt("SkillAge", skillAge); output.putBoolean("SingleAttack", singleAttack);
        if (skillTarget != null) output.putString("SkillTarget", skillTarget.toString());
        output.putString("SkillDimension", skillDimension); output.putDouble("OriginX", origin.x); output.putDouble("OriginY", origin.y); output.putDouble("OriginZ", origin.z);
    }
    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        cooldown = Math.max(0, input.getIntOr("SkillCooldown", 0)); actionLock = Math.max(0, Math.min(18, input.getIntOr("ActionLock", 0))); skillAge = input.getString("SkillAge").isPresent() ? input.getIntOr("SkillAge", 0) : -1; singleAttack = input.getBooleanOr("SingleAttack", false);
        skillTarget = input.getString("SkillTarget").map(java.util.UUID::fromString).orElse(null); skillDimension = input.getStringOr("SkillDimension", ""); origin = new Vec3(input.getDoubleOr("OriginX", 0.0), input.getDoubleOr("OriginY", 0.0), input.getDoubleOr("OriginZ", 0.0)); resumeAnimation = skillAge >= 0;
    }
}
