package com.hp.thermalshockreforged.content.utility;

import com.hp.thermalshockreforged.entity.cavebloomer.CavebloomerContent;
import com.hp.thermalshockreforged.entity.pots.SkullscorEntity;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class PesticideItem extends Item {
    public PesticideItem() { super(new Properties().stacksTo(64).rarity(Rarity.COMMON)); }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.DRINK; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 20; }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag); lines.add(Component.translatable("item.thermal_shock_reforged.pesticide.description_0"));
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand); return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
    @Override public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
        if (level instanceof ServerLevel server) {
            var origin = consumer.position();
            server.sendParticles(CavebloomerContent.POISON_CLOUD.get(), origin.x, origin.y, origin.z, 1, 0, 0, 0, 0);
            var source = new DamageSource(server.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.INDIRECT_MAGIC));
            var targets = server.getEntitiesOfClass(SkullscorEntity.class, new AABB(origin, origin).inflate(1.75), entity -> true);
            targets.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(origin)));
            for (SkullscorEntity target : targets) target.hurt(source, 50);
            if (consumer instanceof Player player) { stack.shrink(1); player.getCooldowns().addCooldown(this, 80); }
        }
        return stack;
    }
}
