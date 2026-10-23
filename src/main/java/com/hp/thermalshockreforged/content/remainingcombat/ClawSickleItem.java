package com.hp.thermalshockreforged.content.remainingcombat;
import net.minecraft.world.item.Item;
import com.hp.thermalshockreforged.content.tools.Tier;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.*;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public final class ClawSickleItem extends HoeItem {
    private static final Tier TIER = new Tier() {
        @Override public int getUses() { return 1200; }
        @Override public float getSpeed() { return 10; }
        @Override public float getAttackDamageBonus() { return 4.5F; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_DIAMOND_TOOL; }
        @Override public int getEnchantmentValue() { return 21; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(MaterialContent.items().get("beetle_claw").get(), MaterialContent.items().get("ferrum_ingot").get()); }
    };
    public ClawSickleItem() { super(TIER, new Properties().attributes(DiggerItem.createAttributes(TIER, 0, -2))); }
    @Override public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (slot == net.minecraft.world.entity.EquipmentSlot.MAINHAND && entity instanceof LivingEntity living) RemainingCombatState.refreshNoKnockback(living);
    }
    @Override public void hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        super.hurtEnemy(stack, victim, attacker);
        if (!victim.level().isClientSide()) {
            var existing = victim.getEffect(WeaponContent.SICKLE_WOUND);
            int amplifier = existing == null ? 0 : existing.getAmplifier() + 1;
            victim.addEffect(new MobEffectInstance(WeaponContent.SICKLE_WOUND, 20, amplifier));
            var applied = victim.getEffect(WeaponContent.SICKLE_WOUND);
            if (applied != null && applied.getAmplifier() >= 2) {
                victim.removeEffect(WeaponContent.SICKLE_WOUND);
                ThermalShockReforged.LOGGER.debug("Claw sickle wound burst committed: attacker={}, victim={}, appliedAmplifier={}", attacker.getUUID(), victim.getUUID(), applied.getAmplifier());
                victim.level().playSound(null, victim.blockPosition(), BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "sickle_slash")), SoundSource.NEUTRAL, 1, 1);
                float bonus = attacker.getMainHandItem().is(RemainingCombatContent.CLAW_SICKLE.get()) ? 12 : attacker.getMainHandItem().is(WeaponContent.STRATOSILVER_SICKLE.get()) ? 14 : 10;
                victim.hurt(victim.damageSources().source(DamageTypes.GENERIC), bonus);
            }
        }
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        for (int line = 0; line < 3; line++) tooltipAdder.accept(Component.translatable("item.thermal_shock_reforged.claw_sickle.description_" + line));
    }
}
