package com.hp.thermalshockreforged.content.utility;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

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
    public PesticideItem() { super(com.hp.thermalshockreforged.ModRegistration.item().stacksTo(64).rarity(Rarity.COMMON)); }
    @Override public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.DRINK; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 20; }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag); tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.pesticide.description_0"));
    }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand); return InteractionResult.SUCCESS;
    }
    @Override public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
        if (level instanceof ServerLevel server) {
            var origin = consumer.position();
            server.sendParticles(CavebloomerContent.POISON_CLOUD.get(), origin.x, origin.y, origin.z, 1, 0, 0, 0, 0);
            var source = new DamageSource(server.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.INDIRECT_MAGIC));
            var targets = server.getEntitiesOfClass(SkullscorEntity.class, new AABB(origin, origin).inflate(1.75), entity -> true);
            targets.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(origin)));
            for (SkullscorEntity target : targets) target.hurt(source, 50);
            if (consumer instanceof Player player) { stack.shrink(1); player.getCooldowns().addCooldown(getDefaultInstance(), 80); }
        }
        return stack;
    }
}
