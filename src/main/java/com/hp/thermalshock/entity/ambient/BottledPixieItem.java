package com.hp.thermalshock.entity.ambient;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class BottledPixieItem extends Item {
    public BottledPixieItem() { super(new Properties().stacksTo(16)); }
    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BOW; }
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) { return 32; }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.getAbilities().instabuild && findAmmo(player).isEmpty()) return InteractionResultHolder.fail(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof ServerPlayer player)) return;
        var ammo = findAmmo(player);
        if (!player.getAbilities().instabuild && ammo.isEmpty()) return;
        var bottle = PixieBottleEntity.shoot(level, player, 0.7F);
        if (player.getAbilities().instabuild) bottle.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        else {
            ammo.shrink(1);
            if (ammo.isEmpty()) player.getInventory().removeItem(ammo);
        }
    }
    private ItemStack findAmmo(Player player) {
        var held = ProjectileWeaponItem.getHeldProjectile(player, stack -> stack.is(this));
        if (!held.isEmpty()) return held;
        for (var stack : player.getInventory().items) if (stack.is(this)) return stack;
        return ItemStack.EMPTY;
    }
}
