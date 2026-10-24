package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.armor.ArmorContent;
import com.hp.thermalshockreforged.content.armor.GeoSetArmorItem;
import com.hp.thermalshockreforged.content.armor.fabricated.FabricatedArmorContent;
import com.hp.thermalshockreforged.content.armor.fabricated.FabricatedArmorPayloads;
import com.hp.thermalshockreforged.content.atlas.AtlasContent;
import com.hp.thermalshockreforged.content.centrifuge.CentrifugeMenu;
import com.hp.thermalshockreforged.content.decor.geo.FabricatorMenu;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.decor.nativeblocks.DeskPhotoMenu;
import com.hp.thermalshockreforged.content.npc.benzor.BenzorMenus;
import com.hp.thermalshockreforged.content.npc.concierge.ConciergeContent;
import com.hp.thermalshockreforged.content.npc.concierge.ConciergePage;
import com.hp.thermalshockreforged.content.npc.florist.FloristContent;
import com.hp.thermalshockreforged.content.npc.florist.FloristPage;
import com.hp.thermalshockreforged.content.npc.huntress.HuntressPage;
import com.hp.thermalshockreforged.content.npc.laut.LautContent;
import com.hp.thermalshockreforged.content.npc.laut.LautPage;
import com.hp.thermalshockreforged.content.npc.ly.LyContent;
import com.hp.thermalshockreforged.content.npc.ly.LyPage;
import com.hp.thermalshockreforged.content.npc.portrait.PortraitContent;
import com.hp.thermalshockreforged.content.npc.portrait.PortraitMenu;
import com.hp.thermalshockreforged.content.npc.shutterbug.ShutterbugContent;
import com.hp.thermalshockreforged.content.npc.shutterbug.ShutterbugPage;
import com.hp.thermalshockreforged.content.remainingcombat.RemainingCombatContent;
import com.hp.thermalshockreforged.content.remaininggui.RemainingGuiMenu;
import com.hp.thermalshockreforged.content.remaininggui.RemainingGuiPage;
import com.hp.thermalshockreforged.content.remaininggui.VaultCodePayload;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.content.weapons.BladeImpactEntity;
import com.hp.thermalshockreforged.content.weapons.BuzzsawSweepEntity;
import com.hp.thermalshockreforged.content.weapons.ThermoblasterBurstEntity;
import com.hp.thermalshockreforged.content.weapons.VolcanoBurstEntity;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import com.hp.thermalshockreforged.entity.ModEntities;
import com.hp.thermalshockreforged.entity.ZapWarningEntity;
import com.hp.thermalshockreforged.entity.benzor.BenzorContent;
import com.hp.thermalshockreforged.entity.huntress.HuntressContent;
import com.hp.thermalshockreforged.entity.icesquad.IceSquadContent;
import com.hp.thermalshockreforged.entity.icesquad.IceSquadProjectileEntity;
import com.hp.thermalshockreforged.entity.icesquad.StriderFirePayload;
import com.hp.thermalshockreforged.entity.kofoni.KofoniContent;
import com.hp.thermalshockreforged.entity.kofoni.KofoniPage;
import com.hp.thermalshockreforged.entity.labmachinist.LabContent;
import com.hp.thermalshockreforged.entity.labmachinist.LabPage;
import com.hp.thermalshockreforged.entity.liftbug.LiftbugContent;
import com.hp.thermalshockreforged.entity.projectile.FlareBulletEntity;
import com.hp.thermalshockreforged.entity.projectile.GrenadeBulletEntity;
import com.hp.thermalshockreforged.entity.projectile.LegacyArrowEntity;
import com.hp.thermalshockreforged.entity.projectile.LiteprismBeamEntity;
import com.hp.thermalshockreforged.entity.projectile.MagneticBombEntity;
import com.hp.thermalshockreforged.entity.qo.QoContent;
import com.hp.thermalshockreforged.entity.qo.QoPage;
import com.hp.thermalshockreforged.entity.qo.QoProgressData;
import com.hp.thermalshockreforged.entity.whisper.WhisperContent;
import com.hp.thermalshockreforged.entity.whisper.WhisperPage;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.network.registration.ChannelAttributes;
import net.neoforged.neoforge.network.registration.NetworkChannel;
import net.neoforged.neoforge.network.registration.NetworkPayloadSetup;

public final class PlayerInteractionTests {
    private static final BlockPos NPC = new BlockPos(3, 1, 3);

    private record Gun(String id, String ammo, boolean use, int cooldown, Class<? extends Entity> spawned, boolean lookDown) {
    }

    private PlayerInteractionTests() {
    }

    static void register() {
        TsrGameTests.add("player_npc_right_click_opens_expected_pages", PlayerInteractionTests::npcRightClickOpensExpectedPages, 100);
        TsrGameTests.add("player_lab_and_defector_dialogue_progression", PlayerInteractionTests::labAndDefectorDialogueProgression, 100);
        TsrGameTests.add("player_quest_npcs_branch_on_inventory_and_state", PlayerInteractionTests::questNpcsBranchOnInventoryAndState, 100);
        TsrGameTests.add("player_block_menus_open_and_centrifuge_converts", PlayerInteractionTests::blockMenusOpenAndCentrifugeConverts, 100);
        TsrGameTests.add("player_special_blocks_consume_keys_and_buckets", PlayerInteractionTests::specialBlocksConsumeKeysAndBuckets, 100);
        TsrGameTests.add("player_firearms_consume_ammo_spawn_shots_and_cool_down", PlayerInteractionTests::firearmsConsumeAmmoSpawnShotsAndCoolDown, 100);
        TsrGameTests.add("player_melee_weapons_apply_wounds_impacts_and_parry", PlayerInteractionTests::meleeWeaponsApplyWoundsImpactsAndParry, 100);
        TsrGameTests.add("player_blinding_lantern_stuns_but_spares_pets", PlayerInteractionTests::blindingLanternStunsButSparesPets, 100);
        TsrGameTests.add("player_armor_sets_apply_effects_on_tick", PlayerInteractionTests::armorSetsApplyEffectsOnTick, 100);
        TsrGameTests.add("player_carborundum_set_cancels_fall_and_lingers_gravity", PlayerInteractionTests::carborundumSetCancelsFallAndLingersGravity, 100);
        TsrGameTests.add("player_sentry_set_sneaking_makes_entities_glow", PlayerInteractionTests::sentrySetSneakingMakesEntitiesGlow, 60);
        TsrGameTests.add("player_propulsion_boots_launch_packet", PlayerInteractionTests::propulsionBootsLaunchPacket, 40);
        TsrGameTests.add("player_strider_fire_packet_requires_rider_and_cooldown", PlayerInteractionTests::striderFirePacketRequiresRiderAndCooldown, 60);
        TsrGameTests.add("player_vault_code_packet_rewards_only_correct_code", PlayerInteractionTests::vaultCodePacketRewardsOnlyCorrectCode, 40);
        TsrGameTests.add("player_liftbug_mount_toggles_lift", PlayerInteractionTests::liftbugMountTogglesLift, 40);
    }

    public static ServerPlayer player(GameTestHelper helper, GameType type, double x, double y, double z) {
        var server = helper.getLevel().getServer();
        var cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "tsr-test-player"), false);
        var player = new ServerPlayer(server, helper.getLevel(), cookie.gameProfile(), cookie.clientInformation());
        var connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        negotiateAllChannels(connection);
        server.getPlayerList().placeNewPlayer(connection, player, cookie);
        player.connection.markClientLoaded();
        player.setGameMode(type);
        var position = helper.absoluteVec(new Vec3(x, y, z));
        player.snapTo(position.x, position.y, position.z, 0, 0);
        return player;
    }

    public static void release(GameTestHelper helper, ServerPlayer player) {
        player.closeContainer();
        helper.getLevel().getServer().getPlayerList().remove(player);
    }

    @SuppressWarnings("unchecked")
    private static void negotiateAllChannels(Connection connection) {
        try {
            var field = net.neoforged.neoforge.network.registration.NetworkRegistry.class.getDeclaredField("PAYLOAD_REGISTRATIONS");
            field.setAccessible(true);
            var registrations = (Map<ConnectionProtocol, Map<Identifier, net.neoforged.neoforge.network.registration.PayloadRegistration<?>>>) field.get(null);
            Map<Identifier, NetworkChannel> channels = new java.util.HashMap<>();
            registrations.getOrDefault(ConnectionProtocol.PLAY, Map.of()).forEach((id, registration) -> {
                if (!id.equals(net.neoforged.neoforge.network.payload.ConfigFilePayload.TYPE.id())) channels.put(id, new NetworkChannel(id, registration.version()));
            });
            ChannelAttributes.setPayloadSetup(connection, new NetworkPayloadSetup(Map.of(ConnectionProtocol.PLAY, channels)));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot read NeoForge payload registrations", e);
        }
    }

    private static void send(ServerPlayer player, CustomPacketPayload payload) {
        player.connection.handleCustomPayload(new ServerboundCustomPayloadPacket(payload));
    }

    private static void swing(ServerPlayer player) {
        player.connection.handleAnimate(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
    }

    private static InteractionResult use(ServerPlayer player) {
        return player.gameMode.useItem(player, player.level(), player.getMainHandItem(), InteractionHand.MAIN_HAND);
    }

    private static InteractionResult useBlock(GameTestHelper helper, ServerPlayer player, BlockPos relative) {
        var pos = helper.absolutePos(relative);
        return player.gameMode.useItemOn(player, player.level(), player.getMainHandItem(), InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
    }

    private static InteractionResult interact(ServerPlayer player, Entity entity) {
        return player.interactOn(entity, InteractionHand.MAIN_HAND, entity.position());
    }

    private static String menu(ServerPlayer player) {
        if (player.containerMenu == player.inventoryMenu) return "";
        var key = BuiltInRegistries.MENU.getKey(player.containerMenu.getType());
        return key == null ? "?" : key.getPath();
    }

    private static Item item(GameTestHelper helper, String id) {
        var item = BuiltInRegistries.ITEM.getValue(TsrGameTests.id(id));
        helper.assertTrue(item != Items.AIR, "Item must be registered: " + id);
        return item;
    }

    private static Block block(GameTestHelper helper, String id) {
        var block = BuiltInRegistries.BLOCK.getValue(TsrGameTests.id(id));
        helper.assertTrue(block != net.minecraft.world.level.block.Blocks.AIR, "Block must be registered: " + id);
        return block;
    }

    private static int count(ServerPlayer player, Item item) {
        return player.getInventory().clearOrCountMatchingItems(stack -> stack.is(item), 0, player.inventoryMenu.getCraftSlots());
    }

    private static <T extends Entity> int entities(GameTestHelper helper, Class<T> type) {
        return helper.getLevel().getEntitiesOfClass(type, new AABB(helper.absolutePos(BlockPos.ZERO)).inflate(48)).size();
    }

    private static int amplifier(LivingEntity entity, Holder<MobEffect> effect) {
        var instance = entity.getEffect(effect);
        return instance == null ? -1 : instance.getAmplifier();
    }

    private static <T extends Mob> T expectNpc(GameTestHelper helper, ServerPlayer player, EntityType<T> type, String page) {
        T npc = helper.spawn(type, NPC);
        interact(player, npc);
        helper.assertTrue(menu(player).equals(page), "Right clicking " + BuiltInRegistries.ENTITY_TYPE.getKey(type) + " must open " + page + " but opened " + menu(player));
        player.closeContainer();
        return npc;
    }

    public static void npcRightClickOpensExpectedPages(GameTestHelper helper) {
        var player = player(helper, GameType.SURVIVAL, 3.5, 1, 5.5);
        try {
            expectNpc(helper, player, WhisperContent.WHISPER.get(), WhisperPage.MAIN.id).discard();
            expectNpc(helper, player, HuntressContent.NPC.get(), HuntressPage.MAIN.id).discard();
            expectNpc(helper, player, LyContent.LY.get(), LyPage.MAIN.id).discard();
            expectNpc(helper, player, FloristContent.FLORIST.get(), FloristPage.MAIN.id).discard();
            expectNpc(helper, player, ConciergeContent.CONCIERGE.get(), ConciergePage.MAIN.id).discard();
            expectNpc(helper, player, KofoniContent.KOFONI.get(), KofoniPage.MAIN.id).discard();
            var benzor = expectNpc(helper, player, BenzorContent.BENZOR.get(), com.hp.thermalshockreforged.content.npc.benzor.BenzorPage.MAIN.id);
            helper.assertTrue(player.getPersistentData().getBooleanOr(BenzorMenus.INFO, false), "Opening Benzor main page must record the info flag");
            benzor.setShiftKeyDown(true);
            interact(player, benzor);
            helper.assertTrue(menu(player).isEmpty() && !benzor.isShiftKeyDown(), "Sneaking Benzor must play its emerge cue and stand up instead of opening a page");
            benzor.discard();
            for (EntityType<? extends Mob> type : List.<EntityType<? extends Mob>>of(KofoniContent.KOFONI.get(), ConciergeContent.CONCIERGE.get())) {
                Mob npc = helper.spawn(type, NPC);
                npc.setShiftKeyDown(true);
                interact(player, npc);
                helper.assertTrue(menu(player).isEmpty(), "Sneaking " + BuiltInRegistries.ENTITY_TYPE.getKey(type) + " must arm its wake schedule without opening a page");
                npc.discard();
            }
            var portrait = helper.spawn(PortraitContent.PORTRAIT.get(), NPC);
            interact(player, portrait);
            helper.assertTrue(player.containerMenu instanceof PortraitMenu menu && !menu.ibex, "Default portrait must open the default portrait dialogue");
            player.closeContainer();
            player.setGameMode(GameType.CREATIVE);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(AtlasContent.KEYSTONE.get()));
            interact(player, portrait);
            helper.assertTrue(menu(player).isEmpty() && portrait.texture().equals("portrait_pente"), "Creative keystone use must cycle the portrait texture without opening a page");
            portrait.discard();
        } finally {
            release(helper, player);
        }
        helper.succeed();
    }

    public static void labAndDefectorDialogueProgression(GameTestHelper helper) {
        var player = player(helper, GameType.SURVIVAL, 3.5, 1, 5.5);
        try {
            var machinist = helper.spawn(LabContent.LAB_MACHINIST.get(), NPC);
            interact(player, machinist);
            helper.assertTrue(menu(player).equals(LabPage.INTRO.id), "First Lab Machinist contact must open the introduction");
            player.closeContainer();
            interact(player, machinist);
            helper.assertTrue(menu(player).equals(LabPage.MAIN.id), "Introduced players must reach the main lab page");
            player.closeContainer();
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(MaterialContent.items().get("phobotite").get()));
            interact(player, machinist);
            helper.assertTrue(menu(player).equals(QoPage.PHOBOTITE.id), "Holding phobotite must open the phobotite trade page");
            player.closeContainer();
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            machinist.discard();
            var defector = helper.spawn(QoContent.DEFECTOR_MACHINIST.get(), NPC);
            var progress = QoProgressData.get(helper.getLevel());
            QoPage[] intro = {QoPage.INTRO_1, QoPage.INTRO_2, QoPage.INTRO_3, QoPage.INTRO_4, QoPage.INTRO_5};
            for (int contacts = (int) progress.contacts(); contacts < 5; contacts++) {
                interact(player, defector);
                helper.assertTrue(menu(player).equals(intro[contacts].id), "Defector contact " + contacts + " must open " + intro[contacts].id + " but opened " + menu(player));
                helper.assertTrue(progress.contacts() == contacts + 1, "Every opened defector page must advance the shared contact counter");
                player.closeContainer();
            }
            helper.assertTrue(count(player, QoContent.ICE_ARTIFACT.get()) == 1, "The fifth defector contact must grant exactly one ice artifact");
            interact(player, defector);
            helper.assertTrue(menu(player).equals(QoPage.EXHAUST.id), "Exhausted defector contacts must open the exhaust page");
            defector.discard();
        } finally {
            release(helper, player);
        }
        helper.succeed();
    }

    public static void questNpcsBranchOnInventoryAndState(GameTestHelper helper) {
        var player = player(helper, GameType.SURVIVAL, 3.5, 1, 5.5);
        try {
            var laut = helper.spawn(LautContent.LAUT.get(), NPC);
            interact(player, laut);
            helper.assertTrue(menu(player).equals(LautPage.NEED.id), "Laut must ask for plenkies when the player has fewer than three");
            player.closeContainer();
            player.getInventory().add(new ItemStack(GeoDecorContent.item(GeoDecorKind.PLENKY), 3));
            interact(player, laut);
            helper.assertTrue(menu(player).equals(LautPage.HAVE.id), "Laut must offer the hand-in page when three plenkies are carried");
            player.closeContainer();
            laut.discard();
            expectNpc(helper, player, ShutterbugContent.SHUTTERBUG.get(), ShutterbugPage.INTRO_1.id).discard();
        } finally {
            release(helper, player);
        }
        helper.succeed();
    }

    public static void blockMenusOpenAndCentrifugeConverts(GameTestHelper helper) {
        var player = player(helper, GameType.SURVIVAL, 3.5, 1, 5.5);
        try {
            helper.setBlock(new BlockPos(3, 1, 3), block(helper, "centrifuge"));
            useBlock(helper, player, new BlockPos(3, 1, 3));
            helper.assertTrue(player.containerMenu instanceof CentrifugeMenu, "Right clicking the centrifuge must open its menu, got " + menu(player));
            var pyro = MaterialContent.items().get("pyrogenic_energy").get();
            player.containerMenu.getSlot(0).set(new ItemStack(pyro));
            player.containerMenu.getSlot(1).set(new ItemStack(pyro));
            player.containerMenu.getSlot(2).set(new ItemStack(item(helper, "frost_pearl")));
            player.connection.handleContainerButtonClick(new ServerboundContainerButtonClickPacket(player.containerMenu.containerId, 0));
            helper.assertTrue(count(player, MaterialContent.items().get("cryonic_energy").get()) == 1, "Centrifuge button must convert two pyrogenic energy and a frost pearl into cryonic energy");
            helper.assertTrue(player.containerMenu.getSlot(0).getItem().isEmpty() && player.containerMenu.getSlot(2).getItem().isEmpty(), "Centrifuge conversion must consume its inputs");
            player.connection.handleContainerButtonClick(new ServerboundContainerButtonClickPacket(player.containerMenu.containerId, 0));
            helper.assertTrue(count(player, MaterialContent.items().get("cryonic_energy").get()) == 1, "Centrifuge must not convert without inputs");
            player.closeContainer();
            helper.setBlock(new BlockPos(3, 1, 3), block(helper, "fabricator"));
            useBlock(helper, player, new BlockPos(3, 1, 3));
            helper.assertTrue(player.containerMenu instanceof FabricatorMenu, "Right clicking the fabricator must open the fabricator guide, got " + menu(player));
            player.closeContainer();
            helper.setBlock(new BlockPos(3, 1, 3), block(helper, "desk_photo"));
            useBlock(helper, player, new BlockPos(3, 1, 3));
            helper.assertTrue(player.containerMenu instanceof DeskPhotoMenu, "Right clicking the desk photo must open its screen, got " + menu(player));
            player.closeContainer();
        } finally {
            release(helper, player);
        }
        helper.succeed();
    }

    public static void specialBlocksConsumeKeysAndBuckets(GameTestHelper helper) {
        var player = player(helper, GameType.SURVIVAL, 3.5, 1, 5.5);
        try {
            helper.setBlock(new BlockPos(3, 1, 3), block(helper, "cauldron_of_trilobite"));
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
            useBlock(helper, player, new BlockPos(3, 1, 3));
            helper.assertTrue(count(player, Items.BUCKET) == 0 && count(player, item(helper, "loque_milk")) == 1, "Trilobite cauldron must trade one bucket for loque milk");
            var cache = block(helper, "cryonic_cache");
            helper.setBlock(new BlockPos(5, 1, 3), cache);
            useBlock(helper, player, new BlockPos(5, 1, 3));
            helper.assertBlockPresent(cache, new BlockPos(5, 1, 3));
            var key = MaterialContent.items().get("cryonic_key").get();
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(key, 2));
            useBlock(helper, player, new BlockPos(5, 1, 3));
            helper.assertBlockNotPresent(cache, new BlockPos(5, 1, 3));
            helper.assertTrue(player.getMainHandItem().getCount() == 1, "Opening a cryonic cache must consume exactly one key");
        } finally {
            release(helper, player);
        }
        helper.succeed();
    }

    public static void firearmsConsumeAmmoSpawnShotsAndCoolDown(GameTestHelper helper) {
        List<Gun> guns = List.of(
            new Gun("flintsteel_rifle", "light_round", false, 11, AbstractArrow.class, false),
            new Gun("carbor_gl", "heavy_round", false, 6, GrenadeBulletEntity.class, false),
            new Gun("volcano", "heavy_round", false, 31, VolcanoBurstEntity.class, false),
            new Gun("cobalt_thermoblaster", "specialized_round", false, 20, com.hp.thermalshockreforged.entity.projectile.ThermoblasterShotgunBulletEntity.class, false),
            new Gun("zenith_thermoblaster", "specialized_round", false, 10, ThermoblasterBurstEntity.class, false),
            new Gun("zenith_thermoblaster", "specialized_round", true, 20, com.hp.thermalshockreforged.entity.projectile.ThermoblasterShotgunBulletEntity.class, false),
            new Gun("magnetic_crossbow", "specialized_round", false, 40, MagneticBombEntity.class, false),
            new Gun("zenith_crossbow", "specialized_round", false, 40, MagneticBombEntity.class, false),
            new Gun("zap_gauntlet", "specialized_round", false, 90, ZapWarningEntity.class, true),
            new Gun("liteprism_laser", "specialized_round", false, 5, LiteprismBeamEntity.class, false),
            new Gun("core_eject_flare_gun", "light_round", false, 25, FlareBulletEntity.class, false),
            new Gun("net_launcher", "string", false, 21, LegacyArrowEntity.class, false),
            new Gun("the_judge", "heavy_round", false, 32, LegacyArrowEntity.class, false),
            new Gun("buzzsaw", null, false, 11, BuzzsawSweepEntity.class, false));
        double x = 0.5;
        for (Gun gun : guns) {
            var player = player(helper, GameType.SURVIVAL, x, 1, 1.5);
            x += 0.5;
            try {
                var weapon = new ItemStack(item(helper, gun.id()));
                Item ammo = gun.ammo() == null ? null : gun.ammo().equals("string") ? Items.STRING : MaterialContent.items().get(gun.ammo()).get();
                player.setItemInHand(InteractionHand.MAIN_HAND, weapon);
                if (gun.lookDown()) player.setXRot(90);
                if (ammo != null) {
                    player.getInventory().setItem(1, new ItemStack(ammo, 2));
                    helper.assertTrue(!player.getCooldowns().isOnCooldown(weapon), gun.id() + " must start ready");
                }
                int before = entities(helper, gun.spawned());
                if (gun.use()) use(player);
                else swing(player);
                helper.assertTrue(player.getCooldowns().isOnCooldown(weapon), gun.id() + " must start its cooldown after firing");
                helper.assertTrue(entities(helper, gun.spawned()) > before, gun.id() + " must spawn " + gun.spawned().getSimpleName());
                if (ammo != null) helper.assertTrue(count(player, ammo) == 1, gun.id() + " must consume exactly one " + gun.ammo());
                if (gun.use()) use(player);
                else swing(player);
                if (ammo != null) helper.assertTrue(count(player, ammo) == 1, gun.id() + " must not fire again during cooldown");
                for (int tick = 1; tick < gun.cooldown(); tick++) player.getCooldowns().tick();
                helper.assertTrue(player.getCooldowns().isOnCooldown(weapon), gun.id() + " cooldown must last " + gun.cooldown() + " ticks");
                player.getCooldowns().tick();
                helper.assertTrue(!player.getCooldowns().isOnCooldown(weapon), gun.id() + " cooldown must end after " + gun.cooldown() + " ticks");
                if (ammo != null) {
                    player.getInventory().clearOrCountMatchingItems(stack -> stack.is(ammo), 64, player.inventoryMenu.getCraftSlots());
                    int dry = entities(helper, gun.spawned());
                    if (gun.use()) use(player);
                    else swing(player);
                    helper.assertTrue(!player.getCooldowns().isOnCooldown(weapon) && entities(helper, gun.spawned()) == dry, gun.id() + " must dry fire without ammo");
                }
            } finally {
                release(helper, player);
            }
        }
        helper.killAllEntities();
        helper.succeed();
    }

    public static void meleeWeaponsApplyWoundsImpactsAndParry(GameTestHelper helper) {
        var player = player(helper, GameType.SURVIVAL, 3.5, 1, 4.5);
        try {
            var cow = helper.spawnWithNoFreeWill(EntityType.COW, 3, 1, 3);
            cow.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200);
            cow.setHealth(200);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item(helper, "ruby_sickle")));
            player.attack(cow);
            helper.assertTrue(amplifier(cow, WeaponContent.SICKLE_WOUND) == 0, "First sickle hit must apply wound level one");
            cow.invulnerableTime = 0;
            player.attack(cow);
            helper.assertTrue(amplifier(cow, WeaponContent.SICKLE_WOUND) == 1, "Second sickle hit must stack the wound");
            cow.invulnerableTime = 0;
            float beforeBurst = cow.getHealth();
            player.attack(cow);
            helper.assertTrue(amplifier(cow, WeaponContent.SICKLE_WOUND) == -1 && cow.getHealth() < beforeBurst, "Third sickle hit must consume the wound stack in a burst");
            cow.invulnerableTime = 0;
            int impacts = entities(helper, BladeImpactEntity.class);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item(helper, "shock_stick")));
            player.attack(cow);
            helper.assertTrue(entities(helper, BladeImpactEntity.class) > impacts, "Shock stick hits must submit a blade impact");
            var edge = new ItemStack(item(helper, "knights_edge"));
            player.setItemInHand(InteractionHand.MAIN_HAND, edge);
            use(player);
            int expected = switch (helper.getLevel().getDifficulty()) { case NORMAL -> 18; case HARD -> 12; default -> 25; };
            var parry = player.getEffect(StatusContent.PARRYING);
            helper.assertTrue(parry != null && parry.getDuration() == expected, "Knight's Edge must open a parry window of " + expected + " ticks");
            helper.assertTrue(player.getCooldowns().isOnCooldown(edge), "Knight's Edge parry must start its cooldown");
            player.removeEffect(StatusContent.PARRYING);
            use(player);
            helper.assertTrue(!player.hasEffect(StatusContent.PARRYING), "Knight's Edge parry must not reopen during cooldown");
        } finally {
            release(helper, player);
        }
        helper.killAllEntities();
        helper.succeed();
    }

    public static void blindingLanternStunsButSparesPets(GameTestHelper helper) {
        var player = player(helper, GameType.SURVIVAL, 3.5, 1, 3.5);
        try {
            var cow = helper.spawnWithNoFreeWill(EntityType.COW, 5, 1, 3);
            var pet = helper.spawnWithNoFreeWill(ModEntities.PROBOSKER.get(), 1, 1, 3);
            pet.tame(player);
            float petHealth = pet.getHealth();
            var lantern = new ItemStack(item(helper, "blinding_lantern"));
            player.setItemInHand(InteractionHand.MAIN_HAND, lantern);
            swing(player);
            helper.assertTrue(amplifier(cow, StatusContent.STUNNED) == 1 && cow.getEffect(StatusContent.STUNNED).getDuration() == 60, "Lantern flash must stun nearby mobs for 60 ticks");
            helper.assertTrue(!pet.hasEffect(StatusContent.STUNNED) && pet.getHealth() == petHealth, "Lantern flash must spare tamed pets");
            helper.assertTrue(player.getCooldowns().isOnCooldown(lantern), "Lantern must start its long cooldown");
            cow.removeEffect(StatusContent.STUNNED);
            swing(player);
            helper.assertTrue(!cow.hasEffect(StatusContent.STUNNED), "Lantern must not flash again during cooldown");
        } finally {
            release(helper, player);
        }
        helper.killAllEntities();
        helper.succeed();
    }

    private static void wear(ServerPlayer player, List<? extends net.neoforged.neoforge.registries.DeferredItem<GeoSetArmorItem>> set) {
        for (var entry : set) player.setItemSlot(entry.get().getType().getSlot(), new ItemStack(entry.get()));
    }

    private static void strip(ServerPlayer player) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND}) player.setItemSlot(slot, ItemStack.EMPTY);
        player.removeAllEffects();
    }

    public static void armorSetsApplyEffectsOnTick(GameTestHelper helper) {
        var player = player(helper, GameType.SURVIVAL, 3.5, 1, 3.5);
        try {
            wear(player, ArmorContent.RUBY);
            player.doTick();
            helper.assertTrue(amplifier(player, MobEffects.HASTE) == 0, "Full ruby set must grant Haste I");
            strip(player);
            wear(player, ArmorContent.RUBY_ZENITH);
            player.doTick();
            helper.assertTrue(amplifier(player, MobEffects.HASTE) == 1, "Full ruby zenith set must grant Haste II");
            strip(player);
            wear(player, ArmorContent.BLOOD_ZENITH);
            player.doTick();
            helper.assertTrue(amplifier(player, MobEffects.RESISTANCE) == 0, "Full blood zenith set must grant Resistance I");
            strip(player);
            wear(player, ArmorContent.BLOOD);
            player.doTick();
            helper.assertTrue(amplifier(player, MobEffects.RESISTANCE) == 0 && !player.hasEffect(MobEffects.SLOWNESS), "Blood set without a weapon must only grant Resistance I");
            var weapon = BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.create(TsrGameTests.id("weapon"))).iterator().next().value();
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(weapon));
            player.doTick();
            helper.assertTrue(amplifier(player, MobEffects.SLOWNESS) == 1, "Blood set with a tagged weapon must add Slowness II");
            strip(player);
            var partial = ArmorContent.RUBY.subList(0, 3);
            wear(player, partial);
            player.doTick();
            helper.assertTrue(!player.hasEffect(MobEffects.HASTE), "Three ruby pieces must not grant the set bonus");
            strip(player);
            player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ArmorContent.CORRIAL_DIVING_HELMET.get()));
            player.doTick();
            helper.assertTrue(player.hasEffect(MobEffects.WATER_BREATHING), "Corrial diving helmet must grant water breathing");
        } finally {
            release(helper, player);
        }
        helper.succeed();
    }

    public static void carborundumSetCancelsFallAndLingersGravity(GameTestHelper helper) {
        var player = player(helper, GameType.SURVIVAL, 3.5, 1, 3.5);
        try {
            var gravityId = TsrGameTests.id("carborundum_set_gravity");
            wear(player, ArmorContent.CARBORUNDUM);
            player.doTick();
            var gravity = player.getAttribute(Attributes.GRAVITY);
            helper.assertTrue(gravity.hasModifier(gravityId) && Math.abs(gravity.getModifier(gravityId).amount() + 0.06) < 1.0E-6, "Carborundum set must lower gravity by 0.06");
            float health = player.getHealth();
            player.causeFallDamage(30, 1, player.damageSources().fall());
            helper.assertTrue(player.getHealth() == health, "Carborundum set must cancel fall damage");
            strip(player);
            for (int tick = 0; tick < 9; tick++) player.doTick();
            helper.assertTrue(gravity.hasModifier(gravityId), "Gravity reduction must linger for ten ticks after removing the set");
            player.doTick();
            helper.assertTrue(!gravity.hasModifier(gravityId), "Gravity reduction must clear after the linger window");
            player.causeFallDamage(30, 1, player.damageSources().fall());
            helper.assertTrue(player.getHealth() < health, "Fall damage must return without the set");
        } finally {
            release(helper, player);
        }
        helper.succeed();
    }

    public static void sentrySetSneakingMakesEntitiesGlow(GameTestHelper helper) {
        var player = player(helper, GameType.SURVIVAL, 3.5, 1, 3.5);
        var cow = helper.spawnWithNoFreeWill(EntityType.COW, 5, 1, 5);
        wear(player, ArmorContent.SENTRY);
        player.setShiftKeyDown(true);
        helper.succeedWhen(() -> {
            helper.assertTrue(cow.hasEffect(MobEffects.GLOWING), "Sneaking in the sentry set must make living entities glow");
            release(helper, player);
        });
    }

    public static void propulsionBootsLaunchPacket(GameTestHelper helper) {
        var player = player(helper, GameType.SURVIVAL, 3.5, 1, 3.5);
        var key = "thermal_shock_reforged_propulsion_remaining";
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(FabricatedArmorContent.PROPULSION_BOOTS.get()));
        send(player, FabricatedArmorPayloads.Launch.INSTANCE);
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(player.getPersistentData().getIntOr(key, 0) == 0, "Launch must require sprinting");
            player.setSprinting(true);
            player.setYRot(0);
            player.setXRot(0);
            send(player, FabricatedArmorPayloads.Launch.INSTANCE);
        });
        helper.runAfterDelay(2, () -> {
            var look = player.getLookAngle();
            var motion = player.getDeltaMovement();
            helper.assertTrue(player.getPersistentData().getIntOr(key, 0) > 0, "Sprinting launch must start the 135 tick cooldown");
            helper.assertTrue(player.hasEffect(FabricatedArmorContent.COOLDOWN_VISUAL), "Launch must show the cooldown marker");
            helper.assertTrue(Math.abs(motion.y - 1) < 1.0E-6 && Math.abs(motion.z - look.z * 3) < 1.0E-6, "Launch must push forward three times and upward once");
            player.setDeltaMovement(Vec3.ZERO);
            send(player, FabricatedArmorPayloads.Launch.INSTANCE);
        });
        helper.runAfterDelay(3, () -> {
            helper.assertTrue(player.getDeltaMovement().y == 0, "Launch must not repeat during cooldown");
            release(helper, player);
            helper.succeed();
        });
    }

    public static void striderFirePacketRequiresRiderAndCooldown(GameTestHelper helper) {
        var rider = player(helper, GameType.SURVIVAL, 3.5, 1, 4.5);
        var stranger = player(helper, GameType.SURVIVAL, 4.5, 1, 4.5);
        var strider = helper.spawnWithNoFreeWill(IceSquadContent.STRIDER.get(), 3, 1, 3);
        send(stranger, StriderFirePayload.INSTANCE);
        interact(rider, strider);
        helper.assertTrue(rider.getVehicle() == strider, "Right clicking the strider must mount it");
        int[] shots = new int[1];
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(entities(helper, IceSquadProjectileEntity.class) == 0, "Non-riders must not fire the strider");
            send(rider, StriderFirePayload.INSTANCE);
        });
        helper.runAfterDelay(2, () -> {
            shots[0] = entities(helper, IceSquadProjectileEntity.class);
            helper.assertTrue(shots[0] == 1, "Rider fire packet must launch one ice projectile");
            send(rider, StriderFirePayload.INSTANCE);
        });
        helper.runAfterDelay(3, () -> helper.assertTrue(entities(helper, IceSquadProjectileEntity.class) == shots[0], "Strider fire must respect its ten tick cooldown"));
        helper.runAfterDelay(13, () -> {
            helper.assertTrue(entities(helper, IceSquadProjectileEntity.class) == 0, "Strider projectile must expire after five ticks");
            send(rider, StriderFirePayload.INSTANCE);
        });
        helper.runAfterDelay(14, () -> {
            helper.assertTrue(entities(helper, IceSquadProjectileEntity.class) == 1, "Strider must fire again after the cooldown");
            rider.stopRiding();
            release(helper, rider);
            release(helper, stranger);
            helper.killAllEntities();
            helper.succeed();
        });
    }

    public static void vaultCodePacketRewardsOnlyCorrectCode(GameTestHelper helper) {
        var player = player(helper, GameType.SURVIVAL, 3.5, 1, 3.5);
        var anchor = helper.absolutePos(new BlockPos(3, 1, 3));
        player.openMenu(new SimpleMenuProvider((id, inventory, owner) -> {
            var data = new FriendlyByteBuf(Unpooled.buffer());
            data.writeBlockPos(anchor);
            return new RemainingGuiMenu(RemainingGuiPage.LIMBO_VAULT_GUI, id, inventory, data);
        }, Component.literal("vault")), buffer -> buffer.writeBlockPos(anchor));
        helper.assertTrue(player.containerMenu instanceof RemainingGuiMenu menu && menu.page == RemainingGuiPage.LIMBO_VAULT_GUI, "Vault menu must open");
        int container = player.containerMenu.containerId;
        var antennae = RemainingCombatContent.ANTENNAE.get();
        send(player, new VaultCodePayload(container, "dumbbug"));
        send(player, new VaultCodePayload(container + 1, "DumbBug"));
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(count(player, antennae) == 0, "Wrong code or stale container id must not reward the vault");
            send(player, new VaultCodePayload(container, "DumbBug"));
        });
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(count(player, antennae) == 1, "Correct vault code must reward one antennae");
            release(helper, player);
            helper.succeed();
        });
    }

    public static void liftbugMountTogglesLift(GameTestHelper helper) {
        var player = player(helper, GameType.SURVIVAL, 3.5, 1, 4.5);
        try {
            var bug = helper.spawnWithNoFreeWill(LiftbugContent.LIFTBUG.get(), 3, 1, 3);
            interact(player, bug);
            helper.assertTrue(player.getVehicle() == bug && bug.isShiftKeyDown(), "First right click must mount the liftbug and start lifting");
            interact(player, bug);
            helper.assertTrue(player.getVehicle() == bug && !bug.isShiftKeyDown(), "Second right click must toggle the lift off");
            player.stopRiding();
        } finally {
            release(helper, player);
        }
        helper.killAllEntities();
        helper.succeed();
    }
}
