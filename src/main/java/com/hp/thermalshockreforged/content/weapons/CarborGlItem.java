package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.projectile.GrenadeBulletEntity;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public final class CarborGlItem extends Item implements GeoItem {
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);
    public CarborGlItem() {
        super(new Properties().durability(1000).fireResistant().rarity(Rarity.UNCOMMON));
        GeoItem.registerSyncedAnimatable(this);
    }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public int getEnchantmentValue() { return 5; }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BOW; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity user) { return 60; }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!hasAmmo(player)) { fire(player, stack); return InteractionResultHolder.fail(stack); }
        player.startUsingItem(hand);
        fire(player, stack);
        return InteractionResultHolder.consume(stack);
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
        if (!(player.level() instanceof ServerLevel server) || player.getCooldowns().isOnCooldown(this)) return;
        Item ammo = MaterialContent.items().get("heavy_round").get();
        if (!player.getAbilities().instabuild && !player.getInventory().contains(new ItemStack(ammo))) {
            server.playSound(null, player.blockPosition(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.NEUTRAL, 1, 5);
            return;
        }
        if (!GrenadeBulletEntity.fireWeaponShot(server, player)) return;
        player.getCooldowns().addCooldown(this, 6);
        triggerAnim(player, GeoItem.getOrAssignId(stack, server), "procedure", "1");
        server.playSound(null, player.blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "flareshoot")), SoundSource.NEUTRAL, 1, 1);
        if (!player.getAbilities().instabuild) player.getInventory().clearOrCountMatchingItems(candidate -> candidate.is(ammo), 1, player.inventoryMenu.getCraftSlots());
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        for (int line = 0; line < 3; line++) lines.add(Component.translatable("item.thermal_shock_reforged.carbor_gl.description_" + line));
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
