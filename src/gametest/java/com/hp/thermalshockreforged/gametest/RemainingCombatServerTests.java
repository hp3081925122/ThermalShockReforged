package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.remainingcombat.CombatItemTaskEntity;
import com.hp.thermalshockreforged.content.remainingcombat.RemainingCombatContent;
import com.hp.thermalshockreforged.content.remainingcombat.RemainingCombatState;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import com.hp.thermalshockreforged.entity.gaze.GazeContent;
import com.hp.thermalshockreforged.entity.projectile.ShortBeamEntity;
import com.hp.thermalshockreforged.entity.projectile.ThistleBeamEntity;
import com.hp.thermalshockreforged.entity.projectile.LiteprismBeamEntity;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

public final class RemainingCombatServerTests {
    private RemainingCombatServerTests() { }

    public static void register() {
        TsrGameTests.add("ice_bomb_commits_one_consumption_and_freezes_only_tagged_targets_at_twenty", RemainingCombatServerTests::iceBomb, 100, "supernova_server");
        TsrGameTests.add("flower_bomb_reuses_item_and_moves_only_tagged_targets_at_twenty", RemainingCombatServerTests::flowerBomb, 100, "supernova_server");
        TsrGameTests.add("rejected_bomb_task_does_not_consume_or_cool_down", RemainingCombatServerTests::rejectedBomb, 100, "supernova_server");
        TsrGameTests.add("both_umbrellas_keep_launch_effect_and_cooldown_boundaries", RemainingCombatServerTests::umbrellas, 100, "supernova_server");
        TsrGameTests.add("piston_gauntlet_requires_offhand_and_preserves_parry_cost", RemainingCombatServerTests::pistonGauntlet, 100, "supernova_server");
        TsrGameTests.add("thistle_bow_switches_projectile_below_half_health_and_requires_ammo", RemainingCombatServerTests::thistleBow, 100, "supernova_server");
        TsrGameTests.add("sylvia_rifle_requires_four_charges_and_consumes_specialized_round", RemainingCombatServerTests::sylviaRifle, 100, "supernova_server");
        TsrGameTests.add("hailfall_keeps_both_ammo_saving_outcomes_and_five_tick_cooldown", RemainingCombatServerTests::hailfall, 100, "supernova_server");
    }

    public static void iceBomb(GameTestHelper helper) { bomb(helper, true); }
    public static void flowerBomb(GameTestHelper helper) { bomb(helper, false); }

    private static void bomb(GameTestHelper helper, boolean ice) {
        var player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 5, 3, 5);
        try {
            var item = ice ? RemainingCombatContent.ICE_BOMB.get() : RemainingCombatContent.FLOWER_BOMB.get();
            var stack = new ItemStack(item, ice ? 2 : 1);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            var target = helper.spawnWithNoFreeWill(GazeContent.SPIRIT.get(), 5, 3, 6);
            var control = helper.spawnWithNoFreeWill(EntityType.COW, 6, 3, 5);
            Vec3 original = target.position(), controlOriginal = control.position();
            item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
            var tasks = helper.getLevel().getEntitiesOfClass(CombatItemTaskEntity.class, player.getBoundingBox().inflate(2));
            helper.assertTrue(tasks.size() == 1 && stack.getCount() == 1, "Bomb must submit one carrier and preserve the original item consumption rule");
            item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
            helper.assertTrue(stack.getCount() == 1 && helper.getLevel().getEntitiesOfClass(CombatItemTaskEntity.class, player.getBoundingBox().inflate(2)).size() == 1, "Cooldown retry must not consume or submit twice");
            var task = tasks.getFirst();
            for (int tick = 0; tick < 19; tick++) task.endTick(helper.getLevel());
            helper.assertTrue(!task.isRemoved() && target.position().equals(original) && !target.hasEffect(RemainingCombatContent.CRYO_VISUAL), "Bomb must not resolve before its twentieth END tick");
            task.endTick(helper.getLevel());
            helper.assertTrue(task.isRemoved() && control.position().equals(controlOriginal) && !control.hasEffect(RemainingCombatContent.CRYO_VISUAL), "Bomb must finish once and spare untagged creatures");
            if (ice) {
                var slowness = target.getEffect(MobEffects.SLOWNESS);
                helper.assertTrue(target.getTicksFrozen() == 600 && RemainingCombatState.blocksCryoAttack(target)
                        && target.hasEffect(RemainingCombatContent.CRYO_VISUAL) && slowness != null && slowness.getDuration() == 120 && slowness.getAmplifier() == 9,
                        "Ice bomb must retain the full freeze, attack lock and slowness behavior");
            } else helper.assertTrue(Math.abs(target.getY() - (original.y - target.getEyeHeight())) < .001, "Flower bomb must lower the tagged target by its eye height");
            cooldown(helper, player, stack, ice ? 60 : 600);
        } finally { PlayerInteractionTests.release(helper, player); }
        helper.succeed();
    }

    public static void rejectedBomb(GameTestHelper helper) {
        var player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 5, 3, 5);
        Consumer<EntityJoinLevelEvent> veto = event -> {
            if (event.getLevel() == helper.getLevel() && event.getEntity() instanceof CombatItemTaskEntity
                    && event.getEntity().distanceToSqr(player) < 1) event.setCanceled(true);
        };
        NeoForge.EVENT_BUS.addListener(veto);
        try {
            for (var item : List.of(RemainingCombatContent.ICE_BOMB.get(), RemainingCombatContent.FLOWER_BOMB.get())) {
                var stack = new ItemStack(item);
                player.setItemInHand(InteractionHand.MAIN_HAND, stack);
                item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
                helper.assertTrue(stack.getCount() == 1 && !player.getCooldowns().isOnCooldown(stack), "Rejected bomb submission must remain retryable without spending an item");
            }
        } finally { NeoForge.EVENT_BUS.unregister(veto); PlayerInteractionTests.release(helper, player); }
        helper.succeed();
    }

    public static void umbrellas(GameTestHelper helper) {
        var player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 5, 3, 5);
        try {
            for (boolean legendary : new boolean[]{false, true}) {
                player.removeAllEffects();
                var item = legendary ? RemainingCombatContent.LEGENDARY_JELLY_UMBRELLA.get() : RemainingCombatContent.JELLY_UMBRELLA.get();
                var stack = new ItemStack(item);
                player.setItemInHand(InteractionHand.MAIN_HAND, stack);
                item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
                var effect = player.getEffect(MobEffects.SLOW_FALLING);
                helper.assertTrue(player.getDeltaMovement().equals(new Vec3(0, .5, 0)) && effect != null
                        && effect.getDuration() == (legendary ? 60 : 120) && effect.getAmplifier() == 1, "Umbrella must preserve its launch and distinct slow-falling duration");
                player.setDeltaMovement(Vec3.ZERO);
                item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
                helper.assertTrue(player.getDeltaMovement().equals(Vec3.ZERO), "Umbrella cooldown must block repeated launches");
                cooldown(helper, player, stack, legendary ? 80 : 200);
            }
        } finally { PlayerInteractionTests.release(helper, player); }
        helper.succeed();
    }

    public static void pistonGauntlet(GameTestHelper helper) {
        var player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 5, 3, 5);
        try {
            var item = RemainingCombatContent.PISTON_GAUNTLET.get();
            var main = new ItemStack(item);
            player.setItemInHand(InteractionHand.MAIN_HAND, main);
            item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
            helper.assertTrue(!player.hasEffect(StatusContent.PARRYING) && !player.getCooldowns().isOnCooldown(main), "Gauntlet requires the original offhand condition");
            player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(item));
            item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
            var parry = player.getEffect(StatusContent.PARRYING);
            helper.assertTrue(main.getDamageValue() == 1 && parry != null && parry.getDuration() == 15 && parry.getAmplifier() == 1, "Gauntlet must spend one durability and grant the original parry");
            item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
            helper.assertTrue(main.getDamageValue() == 1, "Gauntlet retry must not spend durability during cooldown");
            cooldown(helper, player, main, 60);
        } finally { PlayerInteractionTests.release(helper, player); }
        helper.succeed();
    }

    public static void thistleBow(GameTestHelper helper) {
        var player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 5, 3, 5);
        try {
            var item = WeaponContent.THISTLE_BOW.get();
            var stack = new ItemStack(item);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            player.setXRot(-90);
            item.onEntitySwing(stack, player, InteractionHand.MAIN_HAND);
            helper.assertTrue(!player.getCooldowns().isOnCooldown(stack), "Dry Thistle bow must not spend its cooldown");
            var ammo = MaterialContent.items().get("specialized_round").get();
            player.getInventory().setItem(1, new ItemStack(ammo, 3));
            for (boolean lowHealth : new boolean[]{false, true}) {
                player.setHealth(lowHealth ? 9 : 10);
                item.onEntitySwing(stack, player, InteractionHand.MAIN_HAND);
                var shots = ownedBeams(helper, player);
                helper.assertTrue(!shots.isEmpty() && shots.stream().allMatch(shot -> lowHealth ? shot instanceof ThistleBeamEntity : shot instanceof LiteprismBeamEntity), "Thistle projectile must switch strictly below half health");
                helper.assertTrue(player.getInventory().getItem(1).getCount() == (lowHealth ? 1 : 2), "Thistle bow must consume one specialized round per successful line");
                shots.forEach(Entity::discard);
                cooldown(helper, player, stack, 11);
            }
        } finally { PlayerInteractionTests.release(helper, player); }
        helper.succeed();
    }

    public static void sylviaRifle(GameTestHelper helper) {
        var player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 5, 3, 5);
        try {
            var item = BuiltInRegistries.ITEM.getValue(TsrGameTests.id("sylvia_rifle"));
            helper.assertTrue(item != Items.AIR, "Sylvia rifle must remain registered");
            var stack = new ItemStack(item);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            player.setXRot(-90);
            var ammo = MaterialContent.items().get("specialized_round").get();
            player.getInventory().setItem(1, new ItemStack(ammo, 2));
            for (int charge = 1; charge <= 4; charge++) {
                item.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
                cooldown(helper, player, stack, 10);
                if (charge < 4) {
                    item.onEntitySwing(stack, player, InteractionHand.MAIN_HAND);
                    helper.assertTrue(!player.getCooldowns().isOnCooldown(stack) && ownedBeams(helper, player).isEmpty() && player.getInventory().getItem(1).getCount() == 2, "Insufficient Sylvia charge must not fire or spend ammo");
                }
            }
            item.onEntitySwing(stack, player, InteractionHand.MAIN_HAND);
            var shots = ownedBeams(helper, player);
            var direction = player.getLookAngle();
            var recoil = new Vec3(-direction.x * 4, -direction.y * 2, -direction.z * 4);
            helper.assertTrue(!shots.isEmpty() && player.getInventory().getItem(1).getCount() == 1 && player.getDeltaMovement().distanceToSqr(recoil) < .000001, "Fully charged Sylvia rifle must fire once, consume one round and apply recoil");
            item.onEntitySwing(stack, player, InteractionHand.MAIN_HAND);
            helper.assertTrue(player.getInventory().getItem(1).getCount() == 1, "Sylvia must not refire during its cooldown");
            shots.forEach(Entity::discard);
            cooldown(helper, player, stack, 600);
        } finally { PlayerInteractionTests.release(helper, player); }
        helper.succeed();
    }

    public static void hailfall(GameTestHelper helper) {
        var player = PlayerInteractionTests.player(helper, GameType.SURVIVAL, 5, 3, 5);
        try {
            var item = WeaponContent.HAILFALL.get();
            var stack = new ItemStack(item);
            var ammo = MaterialContent.items().get("light_round").get();
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            player.getInventory().setItem(1, new ItemStack(ammo, 64));
            boolean saved = false, consumed = false;
            for (int seed = 0; seed < 32; seed++) {
                helper.getLevel().getRandom().setSeed(91000L + seed * 0x9E3779B9L);
                int before = player.getInventory().getItem(1).getCount();
                item.onEntitySwing(stack, player, InteractionHand.MAIN_HAND);
                int spent = before - player.getInventory().getItem(1).getCount();
                var shots = helper.getLevel().getEntitiesOfClass(com.hp.thermalshockreforged.content.weapons.FirearmBulletEntity.class, player.getBoundingBox().inflate(3), shot -> shot.getType() == WeaponContent.HAILFALL_BULLET.get() && shot.getOwner() == player);
                helper.assertTrue(shots.size() == 1 && (spent == 0 || spent == 1), "Hailfall must emit one projectile and spend at most one round");
                saved |= spent == 0;
                consumed |= spent == 1;
                shots.forEach(Entity::discard);
                cooldown(helper, player, stack, 5);
            }
            helper.assertTrue(saved && consumed, "Hailfall must retain both ammo-saving and ammo-consuming outcomes");
        } finally { PlayerInteractionTests.release(helper, player); }
        helper.succeed();
    }

    private static List<ShortBeamEntity> ownedBeams(GameTestHelper helper, ServerPlayer player) {
        return helper.getLevel().getEntitiesOfClass(ShortBeamEntity.class, player.getBoundingBox().inflate(45), beam -> beam.getOwner() == player);
    }

    private static void cooldown(GameTestHelper helper, ServerPlayer player, ItemStack stack, int ticks) {
        for (int tick = 1; tick < ticks; tick++) player.getCooldowns().tick();
        helper.assertTrue(player.getCooldowns().isOnCooldown(stack), "Skill cooldown must retain its original final tick: " + stack);
        player.getCooldowns().tick();
        helper.assertTrue(!player.getCooldowns().isOnCooldown(stack), "Skill cooldown must end on its original boundary: " + ticks);
    }
}
