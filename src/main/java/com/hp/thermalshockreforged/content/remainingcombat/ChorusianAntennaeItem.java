package com.hp.thermalshockreforged.content.remainingcombat;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import com.geckolib.animatable.manager.AnimatableManager;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;

public final class ChorusianAntennaeItem extends Item implements GeoItem {
    @Override public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) { GeoRenderProviders.accept(this, consumer); }
    private final CombatAnimationCache animations = new CombatAnimationCache(this);
    public ChorusianAntennaeItem() { super(com.hp.thermalshockreforged.ModRegistration.item().humanoidArmor(RemainingCombatContent.ANTENNAE_MATERIAL.value(), ArmorType.HELMET)); }
    @Override public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server);
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.chorusian_antennae_helmet.description_0"));
    }
    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        String[] previous = {"", ""};
        controllers.add(new LegacyAnimationController<>(this, "controller", 5, state -> previous[1].isEmpty()
                ? state.setAndContinue(RawAnimation.begin().thenLoop("0")) : PlayState.STOP));
        controllers.add(new LegacyAnimationController<>(this, "procedureController", 5, state -> {
            ItemStack stack = state.getData(com.hp.thermalshockreforged.compat.TsrDataTickets.ITEMSTACK);
            String request = stack == null ? "" : stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString("geckoAnim").orElse("");
            if (request.equals("empty")) {
                if (!previous[1].isEmpty()) {
                    state.getController().forceAnimationReset();
                    com.hp.thermalshockreforged.ThermalShockReforged.LOGGER.debug("External antennae armor animation cancelled: animation={}", previous[1]);
                }
                previous[0] = "";
                previous[1] = "";
                return PlayState.STOP;
            }
            if (!request.isEmpty() && !request.equals(previous[0])) {
                state.getController().forceAnimationReset();
                state.getController().setAnimation(RawAnimation.begin().thenPlay(request));
                previous[1] = request;
            } else if (state.getController().getAnimationState() == LegacyAnimationController.State.STOPPED) previous[1] = "";
            previous[0] = request;
            return previous[1].isEmpty() ? PlayState.STOP : PlayState.CONTINUE;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
