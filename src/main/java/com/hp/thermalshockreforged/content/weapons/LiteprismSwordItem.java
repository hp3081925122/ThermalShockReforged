package com.hp.thermalshockreforged.content.weapons;
import com.hp.thermalshockreforged.content.tools.SwordItem;
import com.hp.thermalshockreforged.content.tools.Tier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public final class LiteprismSwordItem extends SwordItem {
    private static final String NEXT_CAST = "thermal_shock_reforged:liteprism_sword_next_cast";
    private static final Tier TIER = new Tier() {
        @Override public int getUses() { return 500; }
        @Override public float getSpeed() { return 10; }
        @Override public float getAttackDamageBonus() { return -1.5F; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_IRON_TOOL; }
        @Override public int getEnchantmentValue() { return 19; }
        @Override public TagKey<Item> getRepairItems() { return Tier.repairTag("bismuth"); }
    };
    public LiteprismSwordItem() { super(TIER, 3, 2, com.hp.thermalshockreforged.ModRegistration.item()); }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity wielder, InteractionHand hand) {
        if (!(wielder.level() instanceof ServerLevel level)) return super.onEntitySwing(stack, wielder, hand);
        level.playSound(null, wielder.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "weapon_swing")), SoundSource.NEUTRAL, .7F, 1);
        if (!wielder.isShiftKeyDown()) return super.onEntitySwing(stack, wielder, hand);
        if (wielder instanceof Player player) {
            if (player.getCooldowns().isOnCooldown(getDefaultInstance())) return super.onEntitySwing(stack, wielder, hand);
        } else if (wielder.getPersistentData().getLongOr(NEXT_CAST, 0L) > level.getGameTime()) return super.onEntitySwing(stack, wielder, hand);
        var position = wielder.getEyePosition().add(wielder.getLookAngle().scale(10));
        var supernova = WeaponContent.PRISMATIC_SUPERNOVA.get().spawn(level, BlockPos.containing(position), EntitySpawnReason.MOB_SUMMONED);
        if (supernova != null && !supernova.isRemoved() && level.getEntity(supernova.getUUID()) == supernova) {
            supernova.setDeltaMovement(0, 0, 0);
            if (wielder instanceof Player player) player.getCooldowns().addCooldown(getDefaultInstance(), 20);
            else wielder.getPersistentData().putLong(NEXT_CAST, level.getGameTime() + 20);
        }
        return super.onEntitySwing(stack, wielder, hand);
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.liteprism_sword.description_0"));
        tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.liteprism_sword.description_1"));
    }
}
