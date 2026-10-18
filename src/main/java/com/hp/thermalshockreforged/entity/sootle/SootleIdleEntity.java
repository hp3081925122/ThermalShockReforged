package com.hp.thermalshockreforged.entity.sootle;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.AnimationState;

public final class SootleIdleEntity extends AbstractSootleEntity {
    private boolean conversionClaimed;
    private int conversionRemaining = -1;
    private String conversionDimension = "";
    private boolean resumeAnimation;
    public SootleIdleEntity(EntityType<? extends SootleIdleEntity> type, Level level) { super(type, level, 0); }
    @Override
    protected void registerGoals() {
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false) { @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && isWithinMeleeAttackRange(target); } });
        goalSelector.addGoal(3, new FloatGoal(this));
    }
    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= 25; }
    @Override
    public boolean doHurtTarget(Entity target) { beginConversion(10); return false; }
    @Override
    public boolean hurt(DamageSource source, float amount) { beginConversion(7); return super.hurt(source, amount); }
    public void beginConversion(int ticks) {
        if (level().isClientSide || !isAlive() || conversionClaimed) return;
        conversionClaimed = true;
        conversionRemaining = ticks == 7 ? 7 : 10;
        conversionDimension = level().dimension().location().toString();
        setAnimation("alert");
    }
    @Override
    public void tick() {
        boolean wasPending = conversionRemaining >= 0;
        super.tick();
        if (level().isClientSide || conversionRemaining < 0) return;
        if (!isAlive()) { conversionRemaining = -1; resumeAnimation = false; return; }
        if (!conversionDimension.equals(level().dimension().location().toString())) { conversionRemaining = -1; conversionClaimed = false; resumeAnimation = false; return; }
        if (!wasPending) return;
        if (resumeAnimation) { setAnimation("alert"); resumeAnimation = false; }
        if (--conversionRemaining > 0) return;
        conversionRemaining = -1;
        if (!(level() instanceof ServerLevel server)) return;
        var position = BlockPos.containing(position());
        float rotation = getYRot();
        discard();
        var active = SootleContent.SOOTLE.get().spawn(server, position, MobSpawnType.MOB_SUMMONED);
        if (active != null) { active.setYRot(rotation); active.yBodyRot = rotation; active.yHeadRot = rotation; active.setDeltaMovement(0, 0, 0); }
    }
    @Override
    protected String movement(AnimationState<?> state) { return "dig"; }
    @Override
    public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putBoolean("ConversionClaimed", conversionClaimed); tag.putInt("ConversionRemaining", conversionRemaining); tag.putString("ConversionDimension", conversionDimension); }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        conversionClaimed = tag.getBoolean("ConversionClaimed"); conversionRemaining = conversionClaimed && tag.contains("ConversionRemaining") && tag.getInt("ConversionRemaining") >= 0 ? Math.max(1, Math.min(10, tag.getInt("ConversionRemaining"))) : -1; conversionDimension = tag.getString("ConversionDimension"); resumeAnimation = conversionClaimed && conversionRemaining >= 0;
    }
}
