package com.hp.thermalshockreforged.gametest;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

public final class MeleeCooldownServerTests {
    private MeleeCooldownServerTests() { }

    public static void spearsKeepVanillaMeleeDuringItemCooldown(GameTestHelper helper) {
        var player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 3.5, 1, 3.5);
        try {
            for (String id : List.of("speleothem", "ruby_spear", "carborundum_spear", "hi_volt_lance", "bloodstone_halberd", "carborundum_halberd")) {
                var item = BuiltInRegistries.ITEM.getValue(TsrGameTests.id(id));
                helper.assertTrue(item != Items.AIR, "Missing melee regression weapon: " + id);
                var stack = new ItemStack(item);
                player.setItemSlot(EquipmentSlot.MAINHAND, stack);
                item.inventoryTick(stack, helper.getLevel(), player, EquipmentSlot.MAINHAND);
                item.onEntitySwing(stack, player, InteractionHand.MAIN_HAND);
                helper.assertTrue(!player.getCooldowns().isOnCooldown(stack), "Ordinary spear swing must not start an item cooldown: " + id);
                var baseline = helper.spawnWithNoFreeWill(EntityType.COW, 3, 1, 5);
                baseline.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
                baseline.setHealth(100);
                player.resetAttackStrengthTicker();
                player.attack(baseline);
                float damageWithoutItemCooldown = 100 - baseline.getHealth();
                helper.assertTrue(damageWithoutItemCooldown > 0, "Baseline ordinary melee must deal damage: " + id);
                helper.assertTrue(!player.getCooldowns().isOnCooldown(stack), "Ordinary melee hit must not add an item cooldown: " + id);
                baseline.discard();

                player.getCooldowns().addCooldown(stack, 100);
                item.inventoryTick(stack, helper.getLevel(), player, EquipmentSlot.MAINHAND);
                var duringCooldown = helper.spawnWithNoFreeWill(EntityType.COW, 3, 1, 5);
                duringCooldown.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
                duringCooldown.setHealth(100);
                player.resetAttackStrengthTicker();
                player.attack(duringCooldown);
                float damageWithItemCooldown = 100 - duringCooldown.getHealth();
                helper.assertTrue(damageWithItemCooldown > 0 && Math.abs(damageWithItemCooldown - damageWithoutItemCooldown) < .001,
                        "Item cooldown must preserve melee damage at the same vanilla attack charge: " + id + ", baseline=" + damageWithoutItemCooldown + ", duringCooldown=" + damageWithItemCooldown);
                helper.assertTrue(player.getCooldowns().isOnCooldown(stack), "Ordinary melee must not clear the active skill cooldown: " + id);
                player.getCooldowns().removeCooldown(player.getCooldowns().getCooldownGroup(stack));
                duringCooldown.discard();
            }
        } finally {
            PlayerInteractionTests.release(helper, player);
        }
        helper.succeed();
    }
}
