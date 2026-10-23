package com.hp.thermalshockreforged.content.remainingcombat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.weapons.HeldWeaponState;
import com.hp.thermalshockreforged.entity.projectile.LegacyArrowEntity;
import com.hp.thermalshockreforged.entity.projectile.ProjectileContent;
import com.hp.thermalshockreforged.mixin.ArrowPiercingAccess;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class RemainingGunItem extends AnimatedCombatItem {
    private final boolean judge;
    public RemainingGunItem(boolean judge) { super(properties(judge)); this.judge = judge; }
    private static Properties properties(boolean judge) {
        var properties = com.hp.thermalshockreforged.ModRegistration.item().enchantable(5).durability(920).rarity(judge ? Rarity.EPIC : Rarity.UNCOMMON);
        return judge ? properties.fireResistant() : properties;
    }
    public boolean isJudge() { return judge; }
    @Override public String modelName() { return judge ? "super_shotgun" : "net_launcher"; }
    @Override public String textureName() { return judge ? "supershotgun" : "net_launcher"; }
    @Override public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (slot == net.minecraft.world.entity.EquipmentSlot.MAINHAND && entity instanceof LivingEntity living) HeldWeaponState.refreshNoMelee(living);
    }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        if (!(wielder instanceof Player player) || !(player.level() instanceof ServerLevel server) || player.getCooldowns().isOnCooldown(this)) return false;
        Item ammo = judge ? MaterialContent.items().get("heavy_round").get() : Items.STRING;
        if (!player.getInventory().contains(new ItemStack(ammo))) {
            server.playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.STONE_BUTTON_CLICK_ON, net.minecraft.sounds.SoundSource.NEUTRAL, 1, 5);
            return false;
        }
        boolean spawned = false;
        var look = player.getLookAngle();
        for (int index = 0; index < (judge ? 6 : 1); index++) {
            LegacyArrowEntity bullet = judge ? ProjectileContent.VOLCANO_BULLET.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED) : ProjectileContent.NET_BULLET.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
            if (bullet == null) continue;
            bullet.setOwner(player); bullet.setBaseDamage(judge ? 30 : 1); bullet.setImpactKnockback(judge ? 3 : 0);
            bullet.setSilent(true); bullet.setCritArrow(false);
            if (judge) {
                ((ArrowPiercingAccess) (Object) bullet).thermalShockReforged$setPierceLevel((byte) 3);
                bullet.igniteForSeconds(100);
            }
            bullet.setPos(player.getX(), player.getY() + 1.5, player.getZ());
            bullet.shoot(look.x, look.y, look.z, 3, judge ? 4 : 1);
            spawned |= server.addFreshEntity(bullet);
        }
        if (!spawned) return false;
        animate(player, stack);
        player.getCooldowns().addCooldown(this, judge ? 32 : 21);
        if (!CombatItemTaskEntity.submit(server, player.position(), judge ? CombatItemTaskEntity.JUDGE : CombatItemTaskEntity.NET))
            com.hp.thermalshockreforged.ThermalShockReforged.LOGGER.debug("Combat item reload task rejected: player={}, judge={}", player.getUUID(), judge);
        if (judge) {
            CombatItemTaskEntity.sound(server, player.position(), "minecraft:entity.generic.explode", .2F, 3);
            CombatItemTaskEntity.sound(server, player.position(), "volcanofire", .8F, .5F);
            player.setDeltaMovement(look.scale(-1)); player.hurtMarked = true; player.hurtMarked = true;
        } else {
            CombatItemTaskEntity.sound(server, player.position(), "minecraft:entity.firework_rocket.shoot", .2F, 3);
            CombatItemTaskEntity.sound(server, player.position(), "minecraft:item.crossbow.shoot", .7F, 1);
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 2, 0, true, false));
        }
        player.getInventory().clearOrCountMatchingItems(candidate -> candidate.is(ammo), 1, player.inventoryMenu.getCraftSlots());
        return false;
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        String prefix = "item.thermal_shock_reforged." + (judge ? "the_judge" : "net_launcher") + ".description_";
        for (int line = 0; line < (judge ? 3 : 2); line++) tooltipAdder.accept(Component.translatable(prefix + line));
    }
}
