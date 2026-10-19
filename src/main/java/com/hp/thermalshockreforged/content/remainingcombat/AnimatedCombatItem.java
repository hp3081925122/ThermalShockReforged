package com.hp.thermalshockreforged.content.remainingcombat;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public abstract class AnimatedCombatItem extends Item implements GeoItem {
    private final CombatAnimationCache animations = new CombatAnimationCache(this);
    protected AnimatedCombatItem(Properties properties) { super(properties); GeoItem.registerSyncedAnimatable(this); }
    public abstract String modelName();
    public abstract String textureName();
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server);
    }
    protected void animate(LivingEntity owner, ItemStack stack) {
        if (owner.level() instanceof ServerLevel server) triggerAnim(owner, GeoItem.getOrAssignId(stack, server), "procedure", "1");
    }
    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new AnimationController<>(this, "procedure", 0, state -> PlayState.STOP).triggerableAnim("1", RawAnimation.begin().thenPlay("1"));
        controllers.add(procedure);
        controllers.add(new AnimationController<>(this, "idle", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
