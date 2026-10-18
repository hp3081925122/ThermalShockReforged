package com.hp.thermalshockreforged.entity.sootle;

import com.hp.thermalshockreforged.content.ModDamageTypes;
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
import software.bernie.geckolib.animation.AnimationState;

public final class SootleEntity extends AbstractSootleEntity {
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
    public boolean doHurtTarget(Entity target) { if (target instanceof LivingEntity living) beginAttack(living); return false; }
    public void beginAttack(LivingEntity target) {
        if (level().isClientSide || !isAlive() || cooldown > 0 || skillAge >= 0 || !target.isAlive() || target.level() != level()) return;
        singleAttack = random.nextDouble() < 0.3;
        cooldown = singleAttack ? 30 : 40;
        actionLock = 18;
        skillAge = 0;
        skillTarget = target.getUUID();
        skillDimension = level().dimension().location().toString();
        origin = target.position();
        getNavigation().stop();
        setAnimation(singleAttack ? "attack" : "attack2");
        soundAtOrigin(SoundEvents.WOODEN_BUTTON_CLICK_ON, 1);
    }
    private LivingEntity skillTarget() {
        if (!(level() instanceof ServerLevel server) || skillTarget == null || !skillDimension.equals(level().dimension().location().toString())) return null;
        var found = server.getEntity(skillTarget);
        return found instanceof LivingEntity living && living.isAlive() && living.level() == level() ? living : null;
    }
    private void cancelAttack() { skillAge = -1; skillTarget = null; actionLock = 0; resumeAnimation = false; }
    @Override
    public void tick() {
        if (!level().isClientSide && (skillAge >= 0 || actionLock > 0) && !skillDimension.equals(level().dimension().location().toString())) cancelAttack();
        int previousSkillAge = skillAge;
        if (!level().isClientSide) { if (cooldown > 0) cooldown--; if (actionLock > 0) actionLock--; }
        super.tick();
        if (level().isClientSide) return;
        if (!isAlive()) { cancelAttack(); return; }
        if (actionLock > 0) getNavigation().stop();
        if (skillAge < 0 || previousSkillAge < 0) return;
        var target = skillTarget();
        if (target == null) { cancelAttack(); return; }
        if (resumeAnimation) { setAnimation(singleAttack ? "attack" : "attack2"); resumeAnimation = false; }
        skillAge++;
        if (singleAttack && skillAge == 7) strike(target, true, 4);
        if (!singleAttack && (skillAge == 8 || skillAge == 10)) strike(target, false, skillAge == 8 ? 4 : 5);
        if (skillAge >= (singleAttack ? 7 : 10)) { skillAge = -1; skillTarget = null; }
    }
    private void strike(LivingEntity target, boolean single, float pitch) {
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
    protected String movement(AnimationState<?> state) {
        if (!isAggressive() && (state.isMoving() || Math.abs(state.getLimbSwingAmount()) >= 0.15F)) return "walk";
        if (isShiftKeyDown()) return "dig";
        return isAggressive() && state.isMoving() ? "run" : "idle";
    }
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("SkillCooldown", cooldown); tag.putInt("ActionLock", actionLock); tag.putInt("SkillAge", skillAge); tag.putBoolean("SingleAttack", singleAttack);
        if (skillTarget != null) tag.putUUID("SkillTarget", skillTarget);
        tag.putString("SkillDimension", skillDimension); tag.putDouble("OriginX", origin.x); tag.putDouble("OriginY", origin.y); tag.putDouble("OriginZ", origin.z);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        cooldown = Math.max(0, tag.getInt("SkillCooldown")); actionLock = Math.max(0, Math.min(18, tag.getInt("ActionLock"))); skillAge = tag.contains("SkillAge") ? tag.getInt("SkillAge") : -1; singleAttack = tag.getBoolean("SingleAttack");
        skillTarget = tag.hasUUID("SkillTarget") ? tag.getUUID("SkillTarget") : null; skillDimension = tag.getString("SkillDimension"); origin = new Vec3(tag.getDouble("OriginX"), tag.getDouble("OriginY"), tag.getDouble("OriginZ")); resumeAnimation = skillAge >= 0;
    }
}
