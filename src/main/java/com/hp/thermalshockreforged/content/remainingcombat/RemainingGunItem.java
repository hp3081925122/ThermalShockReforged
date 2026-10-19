package com.hp.thermalshockreforged.content.remainingcombat;

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
        var properties = new Properties().durability(920).rarity(judge ? Rarity.EPIC : Rarity.UNCOMMON);
        return judge ? properties.fireResistant() : properties;
    }
    public boolean isJudge() { return judge; }
    @Override public String modelName() { return judge ? "super_shotgun" : "net_launcher"; }
    @Override public String textureName() { return judge ? "supershotgun" : "net_launcher"; }
    @Override public int getEnchantmentValue() { return 5; }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (selected && entity instanceof LivingEntity living) HeldWeaponState.refreshNoMelee(living);
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
            LegacyArrowEntity bullet = judge ? ProjectileContent.VOLCANO_BULLET.get().create(server) : ProjectileContent.NET_BULLET.get().create(server);
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
            player.setDeltaMovement(look.scale(-1)); player.hasImpulse = true; player.hurtMarked = true;
        } else {
            CombatItemTaskEntity.sound(server, player.position(), "minecraft:entity.firework_rocket.shoot", .2F, 3);
            CombatItemTaskEntity.sound(server, player.position(), "minecraft:item.crossbow.shoot", .7F, 1);
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 0, true, false));
        }
        player.getInventory().clearOrCountMatchingItems(candidate -> candidate.is(ammo), 1, player.inventoryMenu.getCraftSlots());
        return false;
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        String prefix = "item.thermal_shock_reforged." + (judge ? "the_judge" : "net_launcher") + ".description_";
        for (int line = 0; line < (judge ? 3 : 2); line++) lines.add(Component.translatable(prefix + line));
    }
}
