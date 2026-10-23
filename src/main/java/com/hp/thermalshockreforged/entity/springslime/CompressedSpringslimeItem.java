package com.hp.thermalshockreforged.entity.springslime;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class CompressedSpringslimeItem extends Item {
    public CompressedSpringslimeItem() { super(new Properties().stacksTo(16)); }
    @Override public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.BOW; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return 5; }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!player.getAbilities().instabuild && findAmmo(player).isEmpty()) return InteractionResult.FAIL;
        player.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }
    @Override public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int remaining) { fire(level, entity); }
    private void fire(Level level, LivingEntity entity) {
        if (!(entity instanceof ServerPlayer player)) return;
        var ammo = findAmmo(player);
        if (!player.getAbilities().instabuild && ammo.isEmpty()) return;
        var projectile = SpringslimeContent.PROJECTILE.get().create(level);
        if (projectile == null) return;
        projectile.setOwner(player);
        projectile.setPos(player.getX(), player.getEyeY() - .1, player.getZ());
        var aim = player.getViewVector(1);
        projectile.shoot(aim.x, aim.y, aim.z, 1.4F, 0);
        projectile.setBaseDamage(0); projectile.setCritArrow(false); projectile.setSilent(true);
        if (!level.addFreshEntity(projectile)) return;
        if (!player.getAbilities().instabuild) { ammo.shrink(1); if (ammo.isEmpty()) player.getInventory().removeItem(ammo); }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SLIME_JUMP_SMALL, SoundSource.PLAYERS, 1, 1 / (level.getRandom().nextFloat() * .5F + 1) + .35F);
    }
    private ItemStack findAmmo(Player player) {
        var held = ProjectileWeaponItem.getHeldProjectile(player, stack -> stack.is(this));
        if (!held.isEmpty()) return held;
        for (var stack : player.getInventory().items) if (stack.is(this)) return stack;
        return ItemStack.EMPTY;
    }
}
