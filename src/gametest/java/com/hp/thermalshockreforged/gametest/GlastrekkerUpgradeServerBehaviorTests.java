package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.glastrekker.GlastrekkerContent;
import com.hp.thermalshockreforged.entity.glastrekker.GlastrekkerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.gametest.framework.GameTestHelper;
public final class GlastrekkerUpgradeServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_glastrekker_upgrade_server_behavior_tests_armor_upgrade_preserves_state_and_consumes_one_item", GlastrekkerUpgradeServerBehaviorTests::armorUpgradePreservesStateAndConsumesOneItem, 30, "supernova_server");
    }

    public static void armorUpgradePreservesStateAndConsumesOneItem(GameTestHelper helper) {
        GlastrekkerEntity original = helper.spawnWithNoFreeWill(GlastrekkerContent.GLASTREKKER.get(), 2, 2, 2);
        original.setHealth(original.getMaxHealth() * 0.4F);
        original.setCustomName(Component.literal("test-glastrekker"));
        original.setCustomNameVisible(true);
        original.addEffect(new MobEffectInstance(MobEffects.SPEED, 120, 2));
        float healthRatio = original.getHealth() / original.getMaxHealth();

        var server = helper.getLevel().getServer();
        ServerPlayer player = PlayerInteractionTests.player(helper, net.minecraft.world.level.GameType.SURVIVAL, 2, 2, 2);
        player.getAbilities().instabuild = false;
        player.setPos(original.getX(), original.getY(), original.getZ());
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(MaterialContent.items().get("glastrekker_armor").get()));

        original.mobInteract(player, InteractionHand.MAIN_HAND);

        AABB area = new AABB(original.blockPosition()).inflate(4);
        var nearby = helper.getLevel().getEntitiesOfClass(GlastrekkerEntity.class, area);
        helper.assertTrue(original.isRemoved(), "Successful armor upgrade must remove the unarmored Glastrekker");
        helper.assertTrue(nearby.size() == 1 && nearby.getFirst().armored(), "Successful armor upgrade must leave exactly one armored Glastrekker nearby");
        GlastrekkerEntity armored = nearby.getFirst();
        helper.assertTrue(Math.abs(armored.getHealth() / armored.getMaxHealth() - healthRatio) < 0.001F, "Armor upgrade must preserve the original health ratio");
        helper.assertTrue(armored.getCustomName() != null && armored.getCustomName().getString().equals("test-glastrekker") && armored.isCustomNameVisible(), "Armor upgrade must preserve custom name and visibility");
        helper.assertTrue(armored.getEffect(MobEffects.SPEED) != null && armored.getEffect(MobEffects.SPEED).getAmplifier() == 2, "Armor upgrade must copy active effects");
        helper.assertTrue(player.getMainHandItem().isEmpty(), "A successful survival upgrade must consume its single armor item");
        var advancement = server.getAdvancements().get(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "glastrekker_armor_get"));
        helper.assertTrue(advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone(), "Successful armor upgrade must award its advancement");
        PlayerInteractionTests.release(helper, player);
        helper.succeed();
    }

    private GlastrekkerUpgradeServerBehaviorTests() { }
}
