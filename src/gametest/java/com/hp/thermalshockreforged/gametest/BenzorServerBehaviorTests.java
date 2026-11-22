package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.benzor.BenzorMenu;
import com.hp.thermalshockreforged.content.npc.benzor.BenzorPage;
import com.hp.thermalshockreforged.entity.benzor.BenzorContent;
import com.hp.thermalshockreforged.entity.benzor.BenzorEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.gametest.framework.GameTestHelper;

public final class BenzorServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_benzor_server_behavior_tests_all_declared_damage_immunities_and_ordinary_damage_are_verified", BenzorServerBehaviorTests::allDeclaredDamageImmunitiesAndOrdinaryDamageAreVerified, 50, "supernova_server");
        TsrGameTests.add("history_benzor_server_behavior_tests_texture_and_persistence_survive_entity_nbt_round_trip", BenzorServerBehaviorTests::textureAndPersistenceSurviveEntityNbtRoundTrip, 40, "supernova_server");
        TsrGameTests.add("history_benzor_server_behavior_tests_natural_spawn_initialization_disables_gravity", BenzorServerBehaviorTests::naturalSpawnInitializationDisablesGravity, 30, "supernova_server");
        TsrGameTests.add("history_benzor_server_behavior_tests_death_removes_benzor_after_twenty_ticks_without_experience", BenzorServerBehaviorTests::deathRemovesBenzorAfterTwentyTicksWithoutExperience, 45, "supernova_server");
        TsrGameTests.add("history_benzor_server_behavior_tests_spice_exchange_validates_inputs_and_awards_experience_exactly_once", BenzorServerBehaviorTests::spiceExchangeValidatesInputsAndAwardsExperienceExactlyOnce, 50, "supernova_server");
    }

    public static void allDeclaredDamageImmunitiesAndOrdinaryDamageAreVerified(GameTestHelper helper) {
        BenzorEntity benzor = helper.spawnWithNoFreeWill(BenzorContent.BENZOR.get(), 1, 2, 2);
        var level = helper.getLevel();
        var sources = level.damageSources();
        List<DamageSource> immuneSources = new ArrayList<>();
        Entity arrow = EntityType.ARROW.create(level, net.minecraft.world.entity.EntitySpawnReason.LOAD);
        Entity potion = EntityType.SPLASH_POTION.create(level, net.minecraft.world.entity.EntitySpawnReason.LOAD);
        Entity cloud = EntityType.AREA_EFFECT_CLOUD.create(level, net.minecraft.world.entity.EntitySpawnReason.LOAD);
        helper.assertTrue(arrow instanceof AbstractArrow, "Arrow fixture must match Benzor's direct-entity immunity");
        helper.assertTrue(potion instanceof ThrownSplashPotion, "Potion fixture must match Benzor's direct-entity immunity");
        helper.assertTrue(cloud instanceof AreaEffectCloud, "Cloud fixture must match Benzor's direct-entity immunity");
        immuneSources.add(sources.source(DamageTypes.MAGIC, arrow));
        immuneSources.add(sources.source(DamageTypes.MAGIC, potion));
        immuneSources.add(sources.source(DamageTypes.MAGIC, cloud));
        immuneSources.add(sources.inFire());
        immuneSources.add(sources.fall());
        immuneSources.add(sources.cactus());
        immuneSources.add(sources.drown());
        immuneSources.add(sources.lightningBolt());
        for (var type : List.of(DamageTypes.EXPLOSION, DamageTypes.TRIDENT, DamageTypes.FALLING_ANVIL, DamageTypes.DRAGON_BREATH, DamageTypes.WITHER, DamageTypes.WITHER_SKULL)) {
            immuneSources.add(sources.source(type));
        }
        float initialHealth = benzor.getHealth();
        for (DamageSource source : immuneSources) {
            helper.assertTrue(!benzor.hurtServer(helper.getLevel(), source, 10), "Benzor must reject declared immune source " + source.type().msgId());
        }
        helper.assertTrue(benzor.getHealth() == initialHealth, "Declared immunities must not change Benzor health");
        Cow attacker = helper.spawnWithNoFreeWill(EntityType.COW, 2, 2, 2);
        boolean ordinaryAccepted = benzor.hurtServer(helper.getLevel(), sources.mobAttack(attacker), 50);
        ThermalShockReforged.LOGGER.debug("Benzor character protection probe: type={}, accepted={}, healthBefore={}, healthAfter={}", benzor.getType(), ordinaryAccepted, initialHealth, benzor.getHealth());
        helper.assertTrue(!ordinaryAccepted && benzor.getHealth() == initialHealth, "The global character protection must reject ordinary mob attacks without changing Benzor health");
        helper.succeed();
    }
    public static void textureAndPersistenceSurviveEntityNbtRoundTrip(GameTestHelper helper) {
        BenzorEntity first = BenzorContent.BENZOR.get().create(helper.getLevel(), EntitySpawnReason.LOAD);
        helper.assertTrue(first != null, "Benzor entity type must create an instance");
        first.setTexture("benzor_test_sentinel");
        first.setPersistenceRequired();
        CompoundTag saved = save(first);
        BenzorEntity second = BenzorContent.BENZOR.get().create(helper.getLevel(), EntitySpawnReason.LOAD);
        helper.assertTrue(second != null, "Benzor entity type must create an instance for NBT reload");
        second.load(input(helper, saved));
        helper.assertTrue("benzor_test_sentinel".equals(second.texture()), "Benzor texture key must survive NBT round-trip");
        helper.assertTrue(second.isPersistenceRequired(), "Benzor must remain persistent after NBT reload");
        first.discard();
        second.discard();
        helper.succeed();
    }
    public static void naturalSpawnInitializationDisablesGravity(GameTestHelper helper) {
        BenzorEntity benzor = BenzorContent.BENZOR.get().create(helper.getLevel(), EntitySpawnReason.LOAD);
        helper.assertTrue(benzor != null, "Benzor entity type must create an instance");
        var position = helper.absolutePos(new net.minecraft.core.BlockPos(1, 2, 2));
        benzor.finalizeSpawn(helper.getLevel(), helper.getLevel().getCurrentDifficultyAt(position), EntitySpawnReason.NATURAL, null);
        helper.assertTrue(benzor.isNoGravity(), "Benzor natural-spawn finalization must apply its no-gravity behavior");
        benzor.discard();
        helper.succeed();
    }
    public static void deathRemovesBenzorAfterTwentyTicksWithoutExperience(GameTestHelper helper) {
        BenzorEntity benzor = helper.spawnWithNoFreeWill(BenzorContent.BENZOR.get(), 1, 2, 2);
        var fatalSource = helper.getLevel().damageSources().genericKill();
        boolean fatalDamageAccepted = benzor.hurtServer(helper.getLevel(), fatalSource, 1000);
        ThermalShockReforged.LOGGER.debug("Benzor fatal character protection probe: accepted={}, health={}", fatalDamageAccepted, benzor.getHealth());
        helper.assertTrue(!fatalDamageAccepted && benzor.getHealth() == benzor.getMaxHealth(), "Global character protection must also reject generic fatal damage");
        benzor.setHealth(0);
        benzor.die(fatalSource);
        helper.runAfterDelay(19, () -> helper.assertTrue(!benzor.isRemoved(), "Benzor must remain during its 20-tick death period"));
        helper.runAfterDelay(20, () -> {
            helper.assertTrue(benzor.isRemoved(), "Benzor must be removed after its 20-tick death period");
            AABB area = new AABB(benzor.blockPosition()).inflate(4);
            helper.assertTrue(helper.getLevel().getEntitiesOfClass(ExperienceOrb.class, area).isEmpty(), "Benzor must not drop experience");
            helper.succeed();
        });
    }
    public static void spiceExchangeValidatesInputsAndAwardsExperienceExactlyOnce(GameTestHelper helper) {
        BenzorEntity benzor = helper.spawnWithNoFreeWill(BenzorContent.BENZOR.get(), 1, 2, 2);
        ServerPlayer player = PlayerInteractionTests.player(helper, net.minecraft.world.level.GameType.SURVIVAL, 1.5, 2, 2);
        BenzorMenu menu = new BenzorMenu(BenzorPage.SPICE, 1, player.getInventory(), benzor.getId(), benzor.getUUID(), 0);
        player.containerMenu = menu;
        helper.assertTrue(menu.stillValid(player), "Benzor spice menu must validate its owner and NPC");
        int initialExperience = player.totalExperience;
        helper.assertTrue(!clickTrade(menu, player), "Empty spice slots must reject a trade");
        helper.assertTrue(player.totalExperience == initialExperience, "Empty spice slots must not award experience");
        menu.getSlot(0).set(new ItemStack(Items.STONE));
        helper.assertTrue(!clickTrade(menu, player), "Invalid spice must reject a trade");
        helper.assertTrue(player.totalExperience == initialExperience, "Invalid spice must not award experience");
        helper.assertTrue(menu.getSlot(0).hasItem(), "Invalid spice input must remain unconsumed");
        menu.getSlot(0).set(spice("adamantine_spice", 1));
        menu.getSlot(1).set(spice("mithral_spice", 2));
        menu.getSlot(2).set(spice("tourmaline_spice", 1));
        helper.assertTrue(clickTrade(menu, player), "Valid spice inputs must commit a trade");
        helper.assertTrue(player.totalExperience == initialExperience + 18, "One adamantine, two mithral, and one tourmaline spice must award 18 experience");
        helper.assertTrue(!menu.getSlot(0).hasItem() && !menu.getSlot(1).hasItem() && !menu.getSlot(2).hasItem(), "Successful exchange must consume all validated input");
        helper.assertTrue(!clickTrade(menu, player), "Repeated empty exchange must be rejected");
        helper.assertTrue(player.totalExperience == initialExperience + 18, "A repeated empty exchange click must not award experience twice");
        var advancement = helper.getLevel().getServer().getAdvancements().get(BenzorContent.id("whisper_advancement"));
        helper.assertTrue(advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone(), "Successful exchange must award the required advancement");
        PlayerInteractionTests.release(helper, player);
        helper.succeed();
    }

    private static ItemStack spice(String path, int count) {
        Item item = BuiltInRegistries.ITEM.getOptional(BenzorContent.id(path)).orElseThrow();
        return new ItemStack(item, count);
    }

    private static boolean clickTrade(BenzorMenu menu, Player player) {
        return menu.clickMenuButton(player, 0);
    }

    private static CompoundTag save(Entity entity) { TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private static net.minecraft.world.level.storage.ValueInput input(GameTestHelper helper, CompoundTag tag) { return TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag); }
    private BenzorServerBehaviorTests() { }
}
