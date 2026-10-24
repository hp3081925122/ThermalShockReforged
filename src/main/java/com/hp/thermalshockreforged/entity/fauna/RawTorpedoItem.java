package com.hp.thermalshockreforged.entity.fauna;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class RawTorpedoItem extends Item {
    public RawTorpedoItem() { super(com.hp.thermalshockreforged.ModRegistration.item().stacksTo(1).food(new FoodProperties.Builder().nutrition(12).saturationModifier(2).build())); }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(net.minecraft.network.chat.Component.translatable("item.thermal_shock_reforged.raw_torpedo.description_0"));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!player.getAbilities().instabuild && findAmmo(player).isEmpty()) return InteractionResult.FAIL;
        player.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        var result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide() && level.getRandom().nextDouble() < 0.25) FaunaExplosions.explode(entity, 1);
        return result;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof ServerPlayer player)) return false;
        float power = BowItem.getPowerForTime(getUseDuration(stack, entity) - timeLeft);
        if (power < 0.1F) return false;
        var ammo = findAmmo(player);
        if (!player.getAbilities().instabuild && ammo.isEmpty()) return false;
        var orb = GoreFlayerOrbEntity.shoot(level, entity, power);
        if (player.getAbilities().instabuild) {
            orb.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        } else if (ammo.isDamageableItem()) {
            ammo.hurtAndBreak(1, ((net.minecraft.server.level.ServerLevel) player.level()), player, broken -> { });
            if (ammo.isEmpty()) player.getInventory().removeItem(ammo);
        } else {
            ammo.shrink(1);
            if (ammo.isEmpty()) player.getInventory().removeItem(ammo);
        } return false; }

    private ItemStack findAmmo(Player player) {
        var ammo = ProjectileWeaponItem.getHeldProjectile(player, stack -> stack.is(FaunaEntities.item("gore_projectile")));
        if (!ammo.isEmpty()) return ammo;
        for (var stack : player.getInventory().getNonEquipmentItems()) if (stack.is(FaunaEntities.item("gore_projectile"))) return stack;
        return ItemStack.EMPTY;
    }
}
