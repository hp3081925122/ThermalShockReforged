package com.hp.thermalshockreforged.content.remainingcombat;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DataTickets;

public final class ChorusianAntennaeItem extends ArmorItem implements GeoItem {
    private final CombatAnimationCache animations = new CombatAnimationCache(this);
    public ChorusianAntennaeItem() { super(RemainingCombatContent.ANTENNAE_MATERIAL, Type.HELMET, new Properties().durability(275)); }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("item.thermal_shock_reforged.chorusian_antennae_helmet.description_0"));
    }
    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        String[] previous = {"", ""};
        controllers.add(new AnimationController<>(this, "controller", 5, state -> previous[1].isEmpty()
                ? state.setAndContinue(RawAnimation.begin().thenLoop("0")) : PlayState.STOP));
        controllers.add(new AnimationController<>(this, "procedureController", 5, state -> {
            ItemStack stack = state.getData(DataTickets.ITEMSTACK);
            String request = stack == null ? "" : stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString("geckoAnim");
            if (request.equals("empty")) request = "";
            if (!request.isEmpty() && !request.equals(previous[0])) {
                state.getController().forceAnimationReset();
                state.getController().setAnimation(RawAnimation.begin().thenPlay(request));
                previous[1] = request;
            } else if (state.getController().getAnimationState() == AnimationController.State.STOPPED) previous[1] = "";
            previous[0] = request;
            return previous[1].isEmpty() ? PlayState.STOP : PlayState.CONTINUE;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
