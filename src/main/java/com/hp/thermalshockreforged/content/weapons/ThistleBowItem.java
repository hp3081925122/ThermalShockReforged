package com.hp.thermalshockreforged.content.weapons;
import com.geckolib.animation.object.PlayState;
import com.hp.thermalshockreforged.compat.LegacyAnimationController;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import com.geckolib.animatable.manager.AnimatableManager;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.projectile.ThistleBeamEntity;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;

public final class ThistleBowItem extends Item implements GeoItem {
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);

    public ThistleBowItem() {
        super(new Properties().durability(1000).rarity(net.minecraft.world.item.Rarity.UNCOMMON));
        GeoItem.registerSyncedAnimatable(this);
    }

    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public int getEnchantmentValue() { return 5; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity user) { return 20; }

    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
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
        }
    }

    @Override public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity holder, net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, level, holder, slot);
        if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server);
    }

    private void fire(Player player, ItemStack stack) {
        if (!(player.level() instanceof ServerLevel level) || player.getCooldowns().isOnCooldown(this)) return;
        Item ammo = MaterialContent.items().get("specialized_round").get();
        if (!player.getAbilities().instabuild && !player.getInventory().contains(new ItemStack(ammo))) return;
        int emitted = ThistleBeamEntity.fireWeaponLine(level, player);
        if (emitted == 0) return;
        player.getCooldowns().addCooldown(this, 11);
        triggerAnim(player, GeoItem.getOrAssignId(stack, level), "procedure", "1");
        level.playSound(null, player.blockPosition(), SoundEvents.CROSSBOW_SHOOT, SoundSource.NEUTRAL, 1, 1);
        level.playSound(null, player.blockPosition(), SoundEvents.CROSSBOW_QUICK_CHARGE_1.value(), SoundSource.NEUTRAL, 1, 1);
        if (!player.getAbilities().instabuild) player.getInventory().clearOrCountMatchingItems(candidate -> candidate.is(ammo), 1, player.inventoryMenu.getCraftSlots());
    }

    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        for (int line = 0; line < 5; line++) tooltipAdder.accept(Component.translatable("item." + ThermalShockReforged.MOD_ID + ".thistle_bow.description_" + line));
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
