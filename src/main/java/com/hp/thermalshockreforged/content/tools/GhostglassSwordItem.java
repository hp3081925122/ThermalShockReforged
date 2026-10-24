package com.hp.thermalshockreforged.content.tools;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.projectile.ProjectileContent;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.hp.thermalshockreforged.content.tools.Tier;
import net.minecraft.world.item.TooltipFlag;

public final class GhostglassSwordItem extends SwordItem {
    public GhostglassSwordItem(Tier tier, float damage, float speed, Properties properties) { super(tier, damage, speed, properties); }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        boolean result = super.onEntitySwing(stack, wielder, hand);
        if (!(wielder.level() instanceof ServerLevel server) || wielder instanceof Player player && player.getCooldowns().isOnCooldown(getDefaultInstance())) return result;
        var bullet = ProjectileContent.GHOSTGLASS_SWORD_BULLET.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
        if (bullet == null) return result;
        bullet.setOwner(wielder); bullet.setBaseDamage(1.5); bullet.setSilent(true);
        bullet.setPos(wielder.getX(), wielder.getEyeY() - .1, wielder.getZ());
        var look = wielder.getLookAngle(); bullet.shoot(look.x, look.y, look.z, 1, 0);
        if (!server.addFreshEntity(bullet)) return result;
        server.playSound(null, wielder.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "ghostbullet")), SoundSource.NEUTRAL, .5F, 1);
        if (wielder instanceof Player player) player.getCooldowns().addCooldown(getDefaultInstance(), 5);
        return result;
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.ghostglass_sword.description_0"));
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.ghostglass_sword.description_1"));
    }
}
