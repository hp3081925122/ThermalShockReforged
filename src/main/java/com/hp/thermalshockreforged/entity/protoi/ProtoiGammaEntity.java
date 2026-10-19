package com.hp.thermalshockreforged.entity.protoi;
import com.hp.thermalshockreforged.content.MaterialContent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.*;
public final class ProtoiGammaEntity extends ProtoiEntityBase {
    public ProtoiGammaEntity(EntityType<? extends ProtoiGammaEntity> type, Level level) { super(type, level, "protoi_gamma", false, 2.25); xpReward = 0; }
    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() { return attributes(.3, 10, 3, 4, 16).add(Attributes.STEP_HEIGHT, 1); }
    @Override protected boolean persistentByDefault() { return false; }
    @Override public boolean removeWhenFarAway(double distance) { return true; }
    @Override public boolean isWithinMeleeAttackRange(LivingEntity target) { return distanceToSqr(target) <= getBbWidth() * getBbWidth() + target.getBbWidth(); }
    @Override protected void registerGoals() {
        targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false) {
            @Override protected boolean canPerformAttack(LivingEntity target) { return isTimeToAttack() && distanceToSqr(target) <= getBbWidth() * getBbWidth() + target.getBbWidth(); }
        });
        goalSelector.addGoal(3, new RandomStrollGoal(this, 1));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        goalSelector.addGoal(5, new FloatGoal(this));
    }
    @Override protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        var sound = state.getSoundType(level(), pos, this);
        playSound(sound.getStepSound(), sound.getVolume() * .15F, sound.getPitch());
    }
    @Override protected void tickDeath() { if (++deathTime >= 20 && !isRemoved()) remove(RemovalReason.KILLED); }
    @Override public void triggerProcedure(String animation) { if ("idle".equals(animation) || "walk".equals(animation)) triggerAnim("procedure", animation); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 0, state -> PlayState.STOP).triggerableAnim("idle", RawAnimation.begin().thenPlay("idle")).triggerableAnim("walk", RawAnimation.begin().thenPlay("walk"));
        controllers.add(new AnimationController<>(this, "movement", 0, state -> {
            if (procedure.isPlayingTriggeredAnimation()) return PlayState.STOP;
            boolean moving = state.isMoving() || state.getLimbSwingAmount() <= -.15F || state.getLimbSwingAmount() >= .15F;
            return state.setAndContinue(RawAnimation.begin().thenLoop(moving ? "walk" : "idle"));
        }));
        controllers.add(procedure);
    }
    @Override protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) { super.dropCustomDeathLoot(level, source, recentlyHit); spawnAtLocation(new ItemStack(MaterialContent.items().get("helion_coagulate").get())); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "gamma_hurt")); }
    @Override protected SoundEvent getDeathSound() { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("thermal_shock_reforged", "gamma_hurt")); }
}
