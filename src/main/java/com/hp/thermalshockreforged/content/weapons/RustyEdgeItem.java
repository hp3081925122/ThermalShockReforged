package com.hp.thermalshockreforged.content.weapons;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.StatusContent;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.*;
import net.minecraft.world.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public final class RustyEdgeItem extends SwordItem {
    private static final Tier TIER = new Tier() {
        @Override public int getUses() { return 180; }
        @Override public float getSpeed() { return 4; }
        @Override public float getAttackDamageBonus() { return 3; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_IRON_TOOL; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
    };
    public RustyEdgeItem() { super(TIER, new Properties().attributes(SwordItem.createAttributes(TIER, 3, -2.6F))); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var result = super.use(level, player, hand);
        if (player.getCooldowns().isOnCooldown(this)) return result;
        var look = player.getLookAngle();
        if (player.isShiftKeyDown()) player.setDeltaMovement(new Vec3(-look.x, 0, -look.z));
        else {
            player.setDeltaMovement(new Vec3(look.x * 1.5, .25, look.z * 1.5));
            if (!level.isClientSide) player.addEffect(new MobEffectInstance(StatusContent.PARRYING, 10, 1));
        }
        player.getCooldowns().addCooldown(this, 100);
        if (level.isClientSide) level.playLocalSound(player.getX(), player.getY(), player.getZ(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "propulsionbootsactivate")), SoundSource.NEUTRAL, 1, 4, false);
        return result;
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("item.thermal_shock_reforged.rusty_edge.description_0"));
        lines.add(Component.translatable("item.thermal_shock_reforged.rusty_edge.description_1"));
    }
}
