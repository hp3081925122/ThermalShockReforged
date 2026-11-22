package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.decor.geo.FerroCakeBlock;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlockEntity;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.fluids.FluidContent;
import com.hp.thermalshockreforged.content.food.FerroCakeConsumption;
import com.hp.thermalshockreforged.content.lights.GlowstickBlockEntity;
import com.hp.thermalshockreforged.content.lights.LightsContent;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.content.tools.ToolsContent;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import com.hp.thermalshockreforged.entity.projectile.GhostglassSwordBulletEntity;
import com.hp.thermalshockreforged.entity.weaponcore.PrismaticSupernovaEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;

public final class FluidToolFoodServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_fluid_tool_food_server_behavior_tests_springelatin_contact_replaces_motion_for_living_and_item_entities", FluidToolFoodServerBehaviorTests::springelatinContactReplacesMotionForLivingAndItemEntities, 100, "supernova_server");
        TsrGameTests.add("history_fluid_tool_food_server_behavior_tests_blood_contact_applies_and_refreshes_the_original_infection", FluidToolFoodServerBehaviorTests::bloodContactAppliesAndRefreshesTheOriginalInfection, 100, "supernova_server");
        TsrGameTests.add("history_fluid_tool_food_server_behavior_tests_ghostglass_pick_horizontal_faces_launch_and_use_five_tick_cooldown", FluidToolFoodServerBehaviorTests::ghostglassPickHorizontalFacesLaunchAndUseFiveTickCooldown, 100, "supernova_server");
        TsrGameTests.add("history_fluid_tool_food_server_behavior_tests_ghostglass_pick_rejects_vertical_faces_and_sneaking_without_side_effects", FluidToolFoodServerBehaviorTests::ghostglassPickRejectsVerticalFacesAndSneakingWithoutSideEffects, 100, "supernova_server");
        TsrGameTests.add("history_fluid_tool_food_server_behavior_tests_cobalt_axe_retains_all_eight_stripping_pairs_and_every_axis", FluidToolFoodServerBehaviorTests::cobaltAxeRetainsAllEightStrippingPairsAndEveryAxis, 100, "supernova_server");
        TsrGameTests.add("history_fluid_tool_food_server_behavior_tests_ghostglass_sword_creates_owned_bullets_and_rejects_cooldown_spam", FluidToolFoodServerBehaviorTests::ghostglassSwordCreatesOwnedBulletsAndRejectsCooldownSpam, 100, "supernova_server");
        TsrGameTests.add("history_fluid_tool_food_server_behavior_tests_glowstick_drops_all_stored_items_and_exposes_nine_slots_on_every_side", FluidToolFoodServerBehaviorTests::glowstickDropsAllStoredItemsAndExposesNineSlotsOnEverySide, 100, "supernova_server");
        TsrGameTests.add("history_fluid_tool_food_server_behavior_tests_glowstick_support_removal_drops_the_block_once", FluidToolFoodServerBehaviorTests::glowstickSupportRemovalDropsTheBlockOnce, 100, "supernova_server");
        TsrGameTests.add("history_fluid_tool_food_server_behavior_tests_liteprism_sword_requires_sneaking_and_enforces_twenty_tick_player_cooldown", FluidToolFoodServerBehaviorTests::liteprismSwordRequiresSneakingAndEnforcesTwentyTickPlayerCooldown, 100, "supernova_server");
        TsrGameTests.add("history_fluid_tool_food_server_behavior_tests_liteprism_non_player_cooldown_persists_across_entity_nbt_reload", FluidToolFoodServerBehaviorTests::liteprismNonPlayerCooldownPersistsAcrossEntityNbtReload, 100, "supernova_server");
        TsrGameTests.add("history_fluid_tool_food_server_behavior_tests_ferro_cake_claims_once_and_completes_without_feeding_an_offline_owner", FluidToolFoodServerBehaviorTests::ferroCakeClaimsOnceAndCompletesWithoutFeedingAnOfflineOwner, 35, "supernova_server");
        TsrGameTests.add("history_fluid_tool_food_server_behavior_tests_ferro_cake_tail_must_not_delete_a_replacement_cake", FluidToolFoodServerBehaviorTests::ferroCakeTailMustNotDeleteAReplacementCake, 35, "supernova_server");
    }

    public static void springelatinContactReplacesMotionForLivingAndItemEntities(GameTestHelper helper) {
        var level = helper.getLevel();
        var state = FluidContent.SPRINGELATIN_BLOCK.get().defaultBlockState();
        var position = helper.absolutePos(new BlockPos(5, 2, 8));
        Cow cow = EntityType.COW.create(level, EntitySpawnReason.COMMAND);
        var item = new ItemEntity(level, position.getX(), position.getY(), position.getZ(), new ItemStack(Items.STONE));
        for (var entity : new net.minecraft.world.entity.Entity[]{cow, item}) {
            entity.setDeltaMovement(.3, -.2, .7);
            state.entityInside(level, position, entity, net.minecraft.world.entity.InsideBlockEffectApplier.NOOP, true);
            helper.assertTrue(entity.getDeltaMovement().distanceTo(new Vec3(0, .55, 0)) < 1.0E-8, "Springelatin contact must replace both horizontal motion and vertical motion for all entity kinds");
        }
        helper.succeed();
    }

    public static void bloodContactAppliesAndRefreshesTheOriginalInfection(GameTestHelper helper) {
        var level = helper.getLevel();
        var state = FluidContent.BLOOD_BLOCK.get().defaultBlockState();
        var position = helper.absolutePos(new BlockPos(5, 2, 8));
        Cow cow = EntityType.COW.create(level, EntitySpawnReason.COMMAND);
        state.entityInside(level, position, cow, net.minecraft.world.entity.InsideBlockEffectApplier.NOOP, true);
        var infection = cow.getEffect(StatusContent.INFECTED);
        helper.assertTrue(infection != null && infection.getDuration() == 100 && infection.getAmplifier() == 1, "Blood contact must apply the original one-hundred-tick level-one infection");
        cow.removeEffect(StatusContent.INFECTED);
        cow.addEffect(new MobEffectInstance(StatusContent.INFECTED, 20, 1));
        state.entityInside(level, position, cow, net.minecraft.world.entity.InsideBlockEffectApplier.NOOP, true);
        helper.assertTrue(cow.getEffect(StatusContent.INFECTED).getDuration() == 100, "Further contact must refresh the infection duration");
        var item = new ItemEntity(level, position.getX(), position.getY(), position.getZ(), new ItemStack(Items.STONE));
        state.entityInside(level, position, item, net.minecraft.world.entity.InsideBlockEffectApplier.NOOP, true);
        helper.assertTrue(!item.isRemoved() && item.getItem().getCount() == 1, "Blood contact must accept non-living entities without consuming them");
        helper.succeed();
    }

    public static void ghostglassPickHorizontalFacesLaunchAndUseFiveTickCooldown(GameTestHelper helper) {
        var item = ToolsContent.items().get("ghostglass_pick").get();
        var position = helper.absolutePos(new BlockPos(5, 2, 8));
        for (Direction face : Direction.Plane.HORIZONTAL) {
            var player = helper.makeMockPlayer(GameType.SURVIVAL);
            var stack = new ItemStack(item);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            player.setDeltaMovement(.3, -.2, .4);
            var context = new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(position), face, position, false));
            helper.assertTrue(item.useOn(context) == InteractionResult.SUCCESS && player.getDeltaMovement().distanceTo(new Vec3(0, .4, 0)) < 1.0E-8, "Every horizontal face must perform the original wall jump");
            for (int tick = 0; tick < 4; tick++) player.getCooldowns().tick();
            helper.assertTrue(player.getCooldowns().isOnCooldown(stack), "The pick must remain cooling down after four ticks");
            player.getCooldowns().tick();
            helper.assertTrue(!player.getCooldowns().isOnCooldown(stack), "The pick cooldown must end at five ticks");
        }
        helper.succeed();
    }

    public static void ghostglassPickRejectsVerticalFacesAndSneakingWithoutSideEffects(GameTestHelper helper) {
        var item = ToolsContent.items().get("ghostglass_pick").get();
        var position = helper.absolutePos(new BlockPos(5, 2, 8));
        for (Direction face : new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH}) {
            var player = helper.makeMockPlayer(GameType.SURVIVAL);
            player.setShiftKeyDown(face == Direction.NORTH);
            var stack = new ItemStack(item);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            Vec3 motion = new Vec3(.3, -.2, .4);
            player.setDeltaMovement(motion);
            var context = new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(position), face, position, false));
            helper.assertTrue(item.useOn(context) == InteractionResult.PASS && player.getDeltaMovement().equals(motion) && !player.getCooldowns().isOnCooldown(stack), "Rejected pick interactions must retain motion and avoid starting a cooldown");
        }
        helper.succeed();
    }

    public static void cobaltAxeRetainsAllEightStrippingPairsAndEveryAxis(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var item = ToolsContent.items().get("cobalt_axe").get();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item));
        var position = helper.absolutePos(new BlockPos(5, 2, 8));
        String[][] pairs = {{"ochre_log","stripped_ochre_log"},{"vermilli_log","stripped_vermilli_log"},{"shiver_log","stripped_shiverglitter_log"},{"kyaneos_log","stripped_kyaneos_log"},{"ochre_wood","stripped_ochre_wood"},{"vermilli_wood","stripped_vermilli_wood"},{"shiverglitter_wood","stripped_shiverglitter_wood"},{"kyaneos_wood","stripped_kyaneos_wood"}};
        for (String[] pair : pairs) for (Direction.Axis axis : Direction.Axis.values()) {
            level.setBlock(position, MaterialContent.blocks().get(pair[0]).get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis), 3);
            item.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit(position)));
            var result = level.getBlockState(position);
            helper.assertTrue(result.is(MaterialContent.blocks().get(pair[1]).get()) && result.getValue(RotatedPillarBlock.AXIS) == axis, "Every registered custom stripping pair must preserve the original log axis");
        }
        level.setBlock(position, Blocks.STONE.defaultBlockState(), 3);
        item.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit(position)));
        helper.assertTrue(level.getBlockState(position).is(Blocks.STONE), "Non-strippable input must remain unchanged");
        helper.succeed();
    }

    public static void ghostglassSwordCreatesOwnedBulletsAndRejectsCooldownSpam(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.snapTo(helper.absolutePos(new BlockPos(5, 2, 5)), 0, 0);
        var item = ToolsContent.items().get("ghostglass_sword").get();
        var stack = new ItemStack(item);
        item.onEntitySwing(stack, player, InteractionHand.MAIN_HAND);
        var bullets = level.getEntitiesOfClass(GhostglassSwordBulletEntity.class, helper.getBounds(), bullet -> bullet.getOwner() == player);
        helper.assertTrue(bullets.size() == 1 && baseDamage(bullets.getFirst()) == 1.5 && bullets.getFirst().isSilent(), "A legal swing must create one owned silent bullet with original damage");
        for (int attempt = 0; attempt < 4; attempt++) item.onEntitySwing(stack, player, InteractionHand.MAIN_HAND);
        helper.assertTrue(level.getEntitiesOfClass(GhostglassSwordBulletEntity.class, helper.getBounds(), bullet -> bullet.getOwner() == player).size() == 1, "Cooldown swings must not create extra bullets");
        for (int tick = 0; tick < 5; tick++) player.getCooldowns().tick();
        item.onEntitySwing(stack, player, InteractionHand.MAIN_HAND);
        helper.assertTrue(level.getEntitiesOfClass(GhostglassSwordBulletEntity.class, helper.getBounds(), bullet -> bullet.getOwner() == player).size() == 2 && stack.getDamageValue() == 0, "The next legal swing must fire after five ticks without an invented durability cost");
        helper.succeed();
    }

    public static void glowstickDropsAllStoredItemsAndExposesNineSlotsOnEverySide(GameTestHelper helper) {
        var level = helper.getLevel();
        var position = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(position.above(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(position, LightsContent.GLOWSTICK.get().defaultBlockState(), 3);
        var inventory = (GlowstickBlockEntity) level.getBlockEntity(position);
        helper.assertTrue(inventory != null, "Supported Glowstick must create its inventory");
        for (Direction side : Direction.values()) {
            int[] slots = inventory.getSlotsForFace(side);
            helper.assertTrue(slots.length == 9, "Every Glowstick face must expose nine slots");
            for (int slot = 0; slot < 9; slot++) helper.assertTrue(slots[slot] == slot && inventory.canPlaceItemThroughFace(slot, new ItemStack(Items.STONE), side) && inventory.canTakeItemThroughFace(slot, new ItemStack(Items.STONE), side), "Every Glowstick slot must support both automation directions");
        }
        inventory.setItem(0, new ItemStack(Items.DIAMOND, 7));
        inventory.setItem(4, new ItemStack(Items.GOLD_INGOT, 13));
        inventory.setItem(8, new ItemStack(Items.STONE, 64));
        level.setBlock(position, Blocks.AIR.defaultBlockState(), 3);
        var drops = level.getEntitiesOfClass(ItemEntity.class, new AABB(position).inflate(3));
        for (ItemStack expected : new ItemStack[]{new ItemStack(Items.DIAMOND, 7), new ItemStack(Items.GOLD_INGOT, 13), new ItemStack(Items.STONE, 64)}) {
            int actual = drops.stream().filter(drop -> drop.getItem().is(expected.getItem())).mapToInt(drop -> drop.getItem().getCount()).sum();
            helper.assertTrue(actual == expected.getCount(), "Removing a Glowstick must preserve each stored item count exactly");
        }
        helper.succeed();
    }

    public static void glowstickSupportRemovalDropsTheBlockOnce(GameTestHelper helper) {
        var level = helper.getLevel();
        var position = helper.absolutePos(new BlockPos(5, 2, 8));
        level.setBlock(position.above(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(position, LightsContent.GLOWSTICK.get().defaultBlockState(), 3);
        helper.assertTrue(level.getBlockState(position).is(LightsContent.GLOWSTICK.get()), "A supported Glowstick must remain placed");
        level.setBlock(position.above(), Blocks.AIR.defaultBlockState(), 3);
        int drops = level.getEntitiesOfClass(ItemEntity.class, new AABB(position).inflate(3), entity -> entity.getItem().is(LightsContent.GLOWSTICK.get().asItem())).stream().mapToInt(entity -> entity.getItem().getCount()).sum();
        helper.assertTrue(level.getBlockState(position).isAir() && drops == 1, "Removing the hanging support must remove the Glowstick and drop one block item");
        helper.succeed();
    }

    public static void liteprismSwordRequiresSneakingAndEnforcesTwentyTickPlayerCooldown(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.snapTo(helper.absolutePos(new BlockPos(5, 2, 3)), 0, 0);
        var item = WeaponContent.LITEPRISM_SWORD.get();
        var stack = new ItemStack(item);
        item.onEntitySwing(stack, player, InteractionHand.MAIN_HAND);
        helper.assertTrue(level.getEntitiesOfClass(PrismaticSupernovaEntity.class, helper.getBounds()).isEmpty(), "An ordinary swing must not summon a Supernova");
        player.setShiftKeyDown(true);
        item.onEntitySwing(stack, player, InteractionHand.MAIN_HAND);
        helper.assertTrue(level.getEntitiesOfClass(PrismaticSupernovaEntity.class, helper.getBounds()).size() == 1 && player.getCooldowns().isOnCooldown(stack), "A legal sneaking swing must summon once and start cooldown");
        for (int tick = 0; tick < 19; tick++) player.getCooldowns().tick();
        item.onEntitySwing(stack, player, InteractionHand.MAIN_HAND);
        helper.assertTrue(level.getEntitiesOfClass(PrismaticSupernovaEntity.class, helper.getBounds()).size() == 1, "The nineteenth cooldown tick must still reject summoning");
        player.getCooldowns().tick();
        item.onEntitySwing(stack, player, InteractionHand.MAIN_HAND);
        helper.assertTrue(level.getEntitiesOfClass(PrismaticSupernovaEntity.class, helper.getBounds()).size() == 2 && stack.getDamageValue() == 0, "The twentieth cooldown tick must allow the next summon without spending durability");
        helper.succeed();
    }

    public static void liteprismNonPlayerCooldownPersistsAcrossEntityNbtReload(GameTestHelper helper) {
        var level = helper.getLevel();
        Cow wielder = EntityType.COW.create(level, EntitySpawnReason.COMMAND);
        wielder.snapTo(helper.absolutePos(new BlockPos(5, 2, 3)), 0, 0);
        wielder.setShiftKeyDown(true);
        var item = WeaponContent.LITEPRISM_SWORD.get();
        var stack = new ItemStack(item);
        item.onEntitySwing(stack, wielder, InteractionHand.MAIN_HAND);
        var saved = TsrGameTests.roundTrip(helper, wielder);
        long next = wielder.getPersistentData().getLongOr("thermal_shock_reforged:liteprism_sword_next_cast", 0L);
        helper.assertTrue(next == level.getGameTime() + 20, "Non-player summoning must store its original twenty-tick deadline");
        Cow restored = EntityType.COW.create(level, EntitySpawnReason.COMMAND);
        restored.load(saved);
        restored.setShiftKeyDown(true);
        item.onEntitySwing(stack, restored, InteractionHand.MAIN_HAND);
        helper.assertTrue(restored.getPersistentData().getLongOr("thermal_shock_reforged:liteprism_sword_next_cast", 0L) == next && level.getEntitiesOfClass(PrismaticSupernovaEntity.class, helper.getBounds()).size() == 1, "NBT reload must not bypass the non-player cooldown");
        helper.succeed();
    }

    public static void ferroCakeClaimsOnceAndCompletesWithoutFeedingAnOfflineOwner(GameTestHelper helper) {
        var level = helper.getLevel();
        var position = helper.absolutePos(new BlockPos(5, 2, 8));
        var block = GeoDecorContent.block(GeoDecorKind.FERRO_CAKE);
        level.setBlock(position, block.defaultBlockState(), 3);
        var cake = (GeoDecorBlockEntity) level.getBlockEntity(position);
        FakePlayer player = new FakePlayer(level, new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "test-cake-offline"));
        player.getFoodData().setFoodLevel(0);
        player.getFoodData().setSaturation(0);
        helper.assertTrue(FerroCakeConsumption.begin(level, position, cake, player), "A valid cake must accept its initial consumption");
        var resistance = player.getEffect(MobEffects.RESISTANCE);
        helper.assertTrue(player.getFoodData().getFoodLevel() == 3 && player.getFoodData().getSaturationLevel() == 3 && resistance != null && resistance.getDuration() == 260 && resistance.getAmplifier() == 1, "The initial serving must retain nutrition, saturation and resistance");
        helper.assertTrue(level.getBlockState(position).getValue(FerroCakeBlock.CAKE_ANIMATION) == 1 && cake.getPersistentData().getDoubleOr("slicecount", 0.0) == 1, "Cake consumption must claim the slice and start its animation");
        helper.assertTrue(!FerroCakeConsumption.begin(level, position, cake, player) && level.getEntitiesOfClass(FerroCakeConsumption.class, new AABB(position).inflate(2)).size() == 1 && player.getFoodData().getFoodLevel() == 3, "Repeated activation must not duplicate tasks or nutrition");
        helper.runAfterDelay(18, () -> {
            helper.assertTrue(level.getBlockState(position).isAir() && level.getEntitiesOfClass(FerroCakeConsumption.class, new AABB(position).inflate(2)).isEmpty(), "The finite cake task must finish and consume its owned block");
            helper.assertTrue(player.getFoodData().getFoodLevel() == 3, "An owner absent from the online player list must not receive deferred servings");
            helper.succeed();
        });
    }

    public static void ferroCakeTailMustNotDeleteAReplacementCake(GameTestHelper helper) {
        var level = helper.getLevel();
        var position = helper.absolutePos(new BlockPos(5, 2, 8));
        var block = GeoDecorContent.block(GeoDecorKind.FERRO_CAKE);
        level.setBlock(position, block.defaultBlockState(), 3);
        var cake = (GeoDecorBlockEntity) level.getBlockEntity(position);
        FakePlayer player = new FakePlayer(level, new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "test-cake-replace"));
        helper.assertTrue(FerroCakeConsumption.begin(level, position, cake, player), "The old cake must create its finite task");
        level.setBlock(position, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(position, block.defaultBlockState(), 3);
        var replacement = level.getBlockEntity(position);
        helper.assertTrue(replacement != cake, "The replacement cake must have a new block entity identity");
        helper.runAfterDelay(18, () -> {
            helper.assertTrue(level.getBlockState(position).is(block) && level.getBlockEntity(position) == replacement && level.getEntitiesOfClass(FerroCakeConsumption.class, new AABB(position).inflate(2)).isEmpty(), "The old task must expire without consuming a replacement cake");
            helper.succeed();
        });
    }

    private static double baseDamage(AbstractArrow arrow) {
        try {
            var field = AbstractArrow.class.getDeclaredField("baseDamage");
            field.setAccessible(true);
            return field.getDouble(arrow);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not read arrow damage for server regression", exception);
        }
    }

    private static BlockHitResult hit(BlockPos position) { return new BlockHitResult(Vec3.atCenterOf(position), Direction.NORTH, position, false); }
    private FluidToolFoodServerBehaviorTests() { }
}








