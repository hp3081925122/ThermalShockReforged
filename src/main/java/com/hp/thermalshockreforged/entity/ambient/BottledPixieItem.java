package com.hp.thermalshockreforged.entity.ambient;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class BottledPixieItem extends Item {
    public BottledPixieItem() { super(com.hp.thermalshockreforged.ModRegistration.item().stacksTo(16)); }
    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.BOW; }
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) { return 32; }
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!player.getAbilities().instabuild && findAmmo(player).isEmpty()) return InteractionResult.FAIL;
        player.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }
    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof ServerPlayer player)) return false;
        var ammo = findAmmo(player);
        if (!player.getAbilities().instabuild && ammo.isEmpty()) return false;
        var bottle = PixieBottleEntity.shoot(level, player, 0.7F);
        if (player.getAbilities().instabuild) bottle.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        else {
            ammo.shrink(1);
            if (ammo.isEmpty()) player.getInventory().removeItem(ammo);
        } return false; }
    private ItemStack findAmmo(Player player) {
        var held = ProjectileWeaponItem.getHeldProjectile(player, stack -> stack.is(this));
        if (!held.isEmpty()) return held;
        for (var stack : player.getInventory().getNonEquipmentItems()) if (stack.is(this)) return stack;
        return ItemStack.EMPTY;
    }
}
