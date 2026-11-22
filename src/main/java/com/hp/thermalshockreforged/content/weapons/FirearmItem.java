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
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;

public final class FirearmItem extends Item implements GeoItem {
    @Override public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) { GeoRenderProviders.accept(this, consumer); }
    private final boolean hailfall;
    private final BoundedItemAnimationCache animations = new BoundedItemAnimationCache(this);
    public FirearmItem(boolean hailfall) {
        super(com.hp.thermalshockreforged.ModRegistration.item().enchantable(5).durability(hailfall ? 1000 : 920).fireResistant().rarity(Rarity.UNCOMMON));
        this.hailfall = hailfall; GeoItem.registerSyncedAnimatable(this);
    }
    public boolean isHailfall() { return hailfall; }
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return false; }
    @Override public ItemUseAnimation getUseAnimation(ItemStack stack) { return hailfall ? ItemUseAnimation.BOW : super.getUseAnimation(stack); }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return hailfall ? 60 : super.getUseDuration(stack, entity); }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!hailfall) return super.use(level, player, hand);
        ItemStack stack = player.getItemInHand(hand);
        if (!WeaponAmmo.has(player, MaterialContent.items().get("light_round").get())) {
            fire(player, stack);
            return InteractionResult.FAIL;
        }
        player.startUsingItem(hand);
        fire(player, stack);
        return InteractionResult.CONSUME;
    }
    @Override public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remaining) {
        super.onUseTick(level, user, stack, remaining);
        if (hailfall && user.getUseItem() == stack && user instanceof Player player) {
            HeldWeaponState.refreshNoMelee(user);
            fire(player, stack);
            if (!WeaponAmmo.has(player, MaterialContent.items().get("light_round").get())) player.stopUsingItem();
        }
    }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) { fire(wielder, stack); return false; }
    @Override public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (level instanceof ServerLevel server) GeoItem.getOrAssignId(stack, server);
        if (slot == net.minecraft.world.entity.EquipmentSlot.MAINHAND && entity instanceof LivingEntity living) {
            HeldWeaponState.refreshNoMelee(living);
            if (hailfall && living.isUsingItem()) fire(living, stack);
        }
    }
    private void fire(LivingEntity wielder, ItemStack stack) {
        if (!(wielder instanceof Player player) || !(player.level() instanceof ServerLevel server) || player.getCooldowns().isOnCooldown(getDefaultInstance())) return;
        Item ammo = MaterialContent.items().get("light_round").get();
        if (!WeaponAmmo.has(player, ammo)) {
            server.playSound(null, player.blockPosition(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.NEUTRAL, 1, 5); return;
        }
        var bullet = (hailfall ? WeaponContent.HAILFALL_BULLET : WeaponContent.FLINTSTEEL_RIFLE_BULLET).get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
        if (bullet == null) return;
        bullet.setOwner(player); bullet.setBaseDamage(hailfall ? .2F : 1); bullet.setSilent(true); bullet.setCritArrow(false);
        bullet.setPos(player.getX(), player.getY() + 1.5, player.getZ());
        var look = player.getLookAngle(); bullet.shoot(look.x, look.y, look.z, 6, hailfall ? 2 : 0);
        if (!server.addFreshEntity(bullet)) return;
        player.getCooldowns().addCooldown(getDefaultInstance(), hailfall ? 5 : 11);
        triggerAnim(player, GeoItem.getOrAssignId(stack, server), "procedure", "1");
        server.playSound(null, player.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, hailfall ? "misc_gun_2" : "flint_gun_25")), SoundSource.NEUTRAL, 1, hailfall ? .75F : 1 + server.getRandom().nextInt(2));
        if (!hailfall) RifleCycleSoundEntity.submit(server, player.getX(), player.getY(), player.getZ());
        if (!hailfall || server.getRandom().nextDouble() < .7) WeaponAmmo.consume(player, ammo);
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable(hailfall ? "tooltip.thermal_shock_reforged.weapon.cryo" : "tooltip.thermal_shock_reforged.weapon.pyro"));
        if (hailfall) tooltipAdder.accept(Component.translatable("tooltip.thermal_shock_reforged.weapon.select_fire"));
        tooltipAdder.accept(Component.translatable("tooltip.thermal_shock_reforged.weapon.moderate_speed")); tooltipAdder.accept(Component.translatable("tooltip.thermal_shock_reforged.weapon.light_rounds"));
        if (hailfall) tooltipAdder.accept(Component.translatable("tooltip.thermal_shock_reforged.weapon.ammo_saving"));
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
