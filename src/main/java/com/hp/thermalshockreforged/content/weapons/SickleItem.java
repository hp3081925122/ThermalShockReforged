package com.hp.thermalshockreforged.content.weapons;
import com.hp.thermalshockreforged.content.tools.SwordItem;
import com.hp.thermalshockreforged.content.tools.Tier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.*;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.sounds.SoundSource;

public final class SickleItem extends HoeItem {
    private final String id;
    public SickleItem(String id, int durability, float speed, String repair) {
        this(id, durability, speed, repair, 4, 21);
    }
    public SickleItem(String id, int durability, float speed, String repair, float damage, int enchantmentValue) {
        this(id, new SickleTier(durability, speed, repair, damage, enchantmentValue));
    }
    private SickleItem(String id, Tier tier) {
        super(tier, new Properties().attributes(DiggerItem.createAttributes(tier, 0, -2).withModifierAdded(Attributes.ATTACK_KNOCKBACK,
                new AttributeModifier(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "sickle_attack_knockback"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.MAINHAND)));
        this.id = id;
    }
    @Override public void hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        super.hurtEnemy(stack, victim, attacker);
        if (!victim.level().isClientSide() && victim != attacker) {
            var wound = victim.getEffect(WeaponContent.SICKLE_WOUND);
            int level = wound == null ? 0 : wound.getAmplifier() + 1;
            victim.addEffect(new MobEffectInstance(WeaponContent.SICKLE_WOUND, 20, level));
            var applied = victim.getEffect(WeaponContent.SICKLE_WOUND);
            if (applied != null && applied.getAmplifier() >= 2) {
                victim.removeEffect(WeaponContent.SICKLE_WOUND);
                ThermalShockReforged.LOGGER.debug("Sickle wound burst committed: attacker={}, victim={}, appliedAmplifier={}", attacker.getUUID(), victim.getUUID(), applied.getAmplifier());
                var sound = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "sickle_slash"));
                victim.level().playSound(null, victim.blockPosition(), sound, SoundSource.NEUTRAL, 1, 1);
                victim.hurt(victim.damageSources().source(DamageTypes.GENERIC), attacker.getMainHandItem().is(WeaponContent.STRATOSILVER_SICKLE.get()) ? 14 : 10);
            }
        }
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("item." + ThermalShockReforged.MOD_ID + "." + id + ".description_0"));
        tooltipAdder.accept(Component.translatable("item." + ThermalShockReforged.MOD_ID + "." + id + ".description_1"));
        if (id.equals("stratosilver_sickle")) tooltipAdder.accept(Component.translatable("item." + ThermalShockReforged.MOD_ID + "." + id + ".description_2"));
    }
    private record SickleTier(int durability, float speed, String repair, float damage, int enchantmentValue) implements Tier {
        @Override public int getUses() { return durability; }
        @Override public float getSpeed() { return speed; }
        @Override public float getAttackDamageBonus() { return damage; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_DIAMOND_TOOL; }
        @Override public int getEnchantmentValue() { return enchantmentValue; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(MaterialContent.items().get(repair).get()); }
    }
    public static final class WoundEffect extends MobEffect {
        public WoundEffect() { super(MobEffectCategory.HARMFUL, -52429); }
    }
}
