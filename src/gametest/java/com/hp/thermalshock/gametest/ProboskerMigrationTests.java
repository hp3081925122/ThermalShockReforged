package com.hp.thermalshock.gametest;

import com.hp.thermalshock.ThermalShock;
import com.hp.thermalshock.content.MaterialContent;
import com.hp.thermalshock.entity.ModEntities;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ThermalShock.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ProboskerMigrationTests {
    @GameTest(template = "migration_empty")
    public static void ownerCommandsAndFeedingDoNotInterfere(GameTestHelper helper) {
        var pet = helper.spawnWithNoFreeWill(ModEntities.PROBOSKER.get(), 3, 2, 3);
        var owner = new net.neoforged.neoforge.common.util.FakePlayer(helper.getLevel(), new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "probosker-owner"));
        try {
            helper.getLevel().addNewPlayer(owner);
            owner.moveTo(pet.position());
            pet.tame(owner);
            pet.setAge(0);
            owner.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            pet.mobInteract(owner, InteractionHand.MAIN_HAND);
            helper.assertTrue(pet.isOrderedToSit() && pet.isInSittingPose(), "Owner command must enter sitting state");
            var stranger = helper.makeMockPlayer(GameType.SURVIVAL);
            pet.mobInteract(stranger, InteractionHand.MAIN_HAND);
            helper.assertTrue(pet.isOrderedToSit(), "Other players must not toggle owner commands");
            pet.setHealth(20);
            owner.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.COD));
            pet.mobInteract(owner, InteractionHand.MAIN_HAND);
            helper.assertTrue(owner.getMainHandItem().isEmpty(), "Survival feeding must consume cod");
            helper.assertTrue(pet.getHealth() == 22 && pet.isOrderedToSit(), "Cod must heal by nutrition without changing sitting state");
            owner.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(MaterialContent.items().get("raw_crystal_koi").get()));
            pet.mobInteract(owner, InteractionHand.MAIN_HAND);
            helper.assertTrue(owner.getMainHandItem().isEmpty(), "Survival feeding must consume koi");
            helper.assertTrue(pet.getHealth() == 23 && pet.isOrderedToSit(), "Koi healing and sitting must be independent");
            pet.setHealth(pet.getMaxHealth());
            owner.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(MaterialContent.items().get("raw_crystal_koi").get()));
            pet.mobInteract(owner, InteractionHand.MAIN_HAND);
            helper.assertTrue(pet.isInLove() && pet.isOrderedToSit(), "Original superclass love interaction must be retained");
            owner.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            pet.mobInteract(owner, InteractionHand.MAIN_HAND);
            helper.assertTrue(!pet.isOrderedToSit() && !pet.isInSittingPose(), "Second owner command must stand up");
        } finally {
            owner.discard();
        }
        helper.succeed();
    }

    @GameTest(template = "migration_empty", timeoutTicks = 30)
    public static void sittingRetainsDamageAndPersistsColorOwnerAndPose(GameTestHelper helper) {
        var pet = helper.spawnWithNoFreeWill(ModEntities.PROBOSKER.get(), 3, 2, 3);
        var owner = java.util.UUID.randomUUID();
        pet.setOwnerUUID(owner);
        pet.setTame(true, false);
        pet.setOrderedToSit(true);
        pet.setInSittingPose(true);
        pet.setPurple(true);
        pet.setOnGround(true);
        var sitting = new net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal(pet);
        helper.assertTrue(sitting.canUse(), "Tamed pet must accept native sitting goal");
        sitting.start();
        helper.assertTrue(pet.getNavigation().isDone(), "Sitting must stop navigation without a potion");
        CompoundTag saved = new CompoundTag();
        pet.addAdditionalSaveData(saved);
        var restored = ModEntities.PROBOSKER.get().create(helper.getLevel());
        restored.readAdditionalSaveData(saved);
        helper.assertTrue(restored.texture().equals("purple_probosker") && owner.equals(restored.getOwnerUUID()) && restored.isOrderedToSit() && restored.isInSittingPose(), "Color, owner and sitting must survive NBT");
        helper.assertTrue(pet.getAttributeValue(Attributes.ATTACK_DAMAGE) == 6 && pet.getMaxHealth() == 32, "Source combat attributes must be preserved");
        helper.assertTrue(!pet.hurt(helper.getLevel().damageSources().drown(), 4), "Original drowning immunity must remain");
        helper.assertTrue(pet.hurt(helper.getLevel().damageSources().generic(), 4) && pet.getHealth() == 28, "Sitting must not grant damage immunity");
        helper.runAfterDelay(5, () -> {
            helper.assertTrue(!pet.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && !pet.hasEffect(MobEffects.DAMAGE_RESISTANCE), "Sitting must not use extreme potion markers");
            helper.succeed();
        });
    }

    @GameTest(template = "migration_empty")
    public static void tamingKeepsBothRandomOutcomesAndFoodSet(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        for (boolean success : new boolean[]{false, true}) {
            var pet = helper.spawnWithNoFreeWill(ModEntities.PROBOSKER.get(), success ? 5 : 2, 2, 3);
            long seed = 0;
            while ((RandomSource.create(seed).nextInt(3) == 0) != success) seed++;
            pet.getRandom().setSeed(seed);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.SALMON, 2));
            pet.mobInteract(player, InteractionHand.MAIN_HAND);
            helper.assertTrue(pet.isTame() == success && player.getMainHandItem().getCount() == 1 && pet.isPersistenceRequired(), "Taming outcome, consumption and persistence must match source");
            helper.assertTrue(!pet.isOrderedToSit(), "Feeding must not implicitly toggle sitting");
            for (String food : new String[]{"raw_crystal_koi", "raw_sentrapede", "raw_chromefish", "raw_pondler"}) {
                helper.assertTrue(pet.isFood(new ItemStack(MaterialContent.items().get(food).get())), "Original fish food must remain: " + food);
            }
            helper.assertTrue(!pet.isFood(new ItemStack(Items.TROPICAL_FISH)), "Food set must not expand silently");
        }
        helper.succeed();
    }
}
