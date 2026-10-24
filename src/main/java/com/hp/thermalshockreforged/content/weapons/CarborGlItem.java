package com.hp.thermalshockreforged.content.weapons;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.hp.thermalshockreforged.compat.GeoRenderProviders;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.projectile.GrenadeBulletEntity;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;

public final class CarborGlItem extends Item implements GeoItem {
    @Override public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) { GeoRenderProviders.accept(this, consumer); }
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);
    public CarborGlItem() {
        super(com.hp.thermalshockreforged.ModRegistration.item().enchantable(5).durability(1000).fireResistant().rarity(Rarity.UNCOMMON));
        GeoItem.registerSyncedAnimatable(this);
    }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.BOW; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity user) { return 60; }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!hasAmmo(player)) { fire(player, stack); return InteractionResult.FAIL; }
        player.startUsingItem(hand);
        fire(player, stack);
        return InteractionResult.CONSUME;
    }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        if (wielder instanceof Player player) fire(player, stack);
        return false;
    }
    @Override public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remaining) {
        super.onUseTick(level, user, stack, remaining);
        if (user.getUseItem() == stack && user instanceof Player player) {
            HeldWeaponState.refreshNoMelee(user);
            fire(player, stack);
            if (!hasAmmo(player)) player.stopUsingItem();
        }
    }
    private boolean hasAmmo(Player player) {
        return player.getAbilities().instabuild || player.getInventory().contains(new ItemStack(MaterialContent.items().get("heavy_round").get()));
    }
    private void fire(Player player, ItemStack stack) {
        if (!(player.level() instanceof ServerLevel server) || player.getCooldowns().isOnCooldown(getDefaultInstance())) return;
        Item ammo = MaterialContent.items().get("heavy_round").get();
        if (!player.getAbilities().instabuild && !player.getInventory().contains(new ItemStack(ammo))) {
            server.playSound(null, player.blockPosition(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.NEUTRAL, 1, 5);
            return;
        }
        if (!GrenadeBulletEntity.fireWeaponShot(server, player)) return;
        player.getCooldowns().addCooldown(getDefaultInstance(), 6);
        triggerAnim(player, GeoItem.getOrAssignId(stack, server), "procedure", "1");
        server.playSound(null, player.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "flareshoot")), SoundSource.NEUTRAL, 1, 1);
        if (!player.getAbilities().instabuild) player.getInventory().clearOrCountMatchingItems(candidate -> candidate.is(ammo), 1, player.inventoryMenu.getCraftSlots());
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        for (int line = 0; line < 3; line++) tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.carbor_gl.description_" + line));
    }
    public void pruneAnimations() { animations.prune(); }
    public void clearAnimations() { animations.clear(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var procedure = new LegacyAnimationController<>(this, "procedure", 0, state -> PlayState.STOP).triggerableAnim("1", RawAnimation.begin().thenPlay("1"));
        controllers.add(procedure);
        controllers.add(new LegacyAnimationController<>(this, "idle", 0, state -> procedure.isPlayingTriggeredAnimation() ? PlayState.STOP : state.setAndContinue(RawAnimation.begin().thenLoop("0"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animations; }
}
