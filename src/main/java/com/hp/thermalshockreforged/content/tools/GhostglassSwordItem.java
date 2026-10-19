package com.hp.thermalshockreforged.content.tools;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.projectile.ProjectileContent;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;

public final class GhostglassSwordItem extends SwordItem {
    public GhostglassSwordItem(Tier tier, Properties properties) { super(tier, properties); }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        boolean result = super.onEntitySwing(stack, wielder, hand);
        if (!(wielder.level() instanceof ServerLevel server) || wielder instanceof Player player && player.getCooldowns().isOnCooldown(this)) return result;
        var bullet = ProjectileContent.GHOSTGLASS_SWORD_BULLET.get().create(server);
        if (bullet == null) return result;
        bullet.setOwner(wielder); bullet.setBaseDamage(1.5); bullet.setSilent(true);
        bullet.setPos(wielder.getX(), wielder.getEyeY() - .1, wielder.getZ());
        var look = wielder.getLookAngle(); bullet.shoot(look.x, look.y, look.z, 1, 0);
        if (!server.addFreshEntity(bullet)) return result;
        server.playSound(null, wielder.blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "ghostbullet")), SoundSource.NEUTRAL, .5F, 1);
        if (wielder instanceof Player player) player.getCooldowns().addCooldown(this, 5);
        return result;
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("item.thermal_shock_reforged.ghostglass_sword.description_0"));
        lines.add(Component.translatable("item.thermal_shock_reforged.ghostglass_sword.description_1"));
    }
}
