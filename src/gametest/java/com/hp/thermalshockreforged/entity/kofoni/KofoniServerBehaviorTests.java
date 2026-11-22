package com.hp.thermalshockreforged.entity.kofoni;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.mojang.authlib.GameProfile;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import io.netty.buffer.Unpooled;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.*;
import com.hp.thermalshockreforged.gametest.TsrGameTests;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.*;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.connection.ConnectionType;

public final class KofoniServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_kofoni_server_behavior_tests_tablet_input_accepts_all_nine_lore_tablets_and_rejects_stone_and_empty", KofoniServerBehaviorTests::tabletInputAcceptsAllNineLoreTabletsAndRejectsStoneAndEmpty, 100, "supernova_server");
        TsrGameTests.add("history_kofoni_server_behavior_tests_empty_and_forged_non_tablet_inputs_do_not_produce_rewards", KofoniServerBehaviorTests::emptyAndForgedNonTabletInputsDoNotProduceRewards, 100, "supernova_server");
        TsrGameTests.add("history_kofoni_server_behavior_tests_each_of_the_nine_tablets_trades_through_the_real_menu_and_awards_twenty_xp", KofoniServerBehaviorTests::eachOfTheNineTabletsTradesThroughTheRealMenuAndAwardsTwentyXp, 100, "supernova_server");
        TsrGameTests.add("history_kofoni_server_behavior_tests_full_input_stack_is_consumed_and_reward_loop_resamples_each_iteration", KofoniServerBehaviorTests::fullInputStackIsConsumedAndRewardLoopResamplesEachIteration, 100, "supernova_server");
        TsrGameTests.add("history_kofoni_server_behavior_tests_repeated_exchange_and_removal_cannot_duplicate_committed_rewards", KofoniServerBehaviorTests::repeatedExchangeAndRemovalCannotDuplicateCommittedRewards, 100, "supernova_server");
        TsrGameTests.add("history_kofoni_server_behavior_tests_menu_rejects_foreign_owner_detached_menu_and_unknown_buttons", KofoniServerBehaviorTests::menuRejectsForeignOwnerDetachedMenuAndUnknownButtons, 100, "supernova_server");
        TsrGameTests.add("history_kofoni_server_behavior_tests_menu_checks_exact_distance_alive_npc_and_uuid_identity", KofoniServerBehaviorTests::menuChecksExactDistanceAliveNpcAndUuidIdentity, 100, "supernova_server");
        TsrGameTests.add("history_kofoni_server_behavior_tests_quick_move_transfers_tablets_and_keeps_other_items_out_of_input", KofoniServerBehaviorTests::quickMoveTransfersTabletsAndKeepsOtherItemsOutOfInput, 100, "supernova_server");
        TsrGameTests.add("history_kofoni_server_behavior_tests_closing_unsubmitted_input_returns_it_exactly_once", KofoniServerBehaviorTests::closingUnsubmittedInputReturnsItExactlyOnce, 100, "supernova_server");
        TsrGameTests.add("history_kofoni_server_behavior_tests_full_inventory_close_drops_unsubmitted_payment_instead_of_deleting_it", KofoniServerBehaviorTests::fullInventoryCloseDropsUnsubmittedPaymentInsteadOfDeletingIt, 100, "supernova_server");
        TsrGameTests.add("history_kofoni_server_behavior_tests_all_information_routes_preserve_intro_and_reject_obsolete_page_buttons", KofoniServerBehaviorTests::allInformationRoutesPreserveIntroAndRejectObsoletePageButtons, 100, "supernova_server");
        TsrGameTests.add("history_kofoni_server_behavior_tests_failed_open_can_retry_while_invalid_npc_open_is_rejected_before_provider", KofoniServerBehaviorTests::failedOpenCanRetryWhileInvalidNpcOpenIsRejectedBeforeProvider, 100, "supernova_server");
        TsrGameTests.add("history_kofoni_server_behavior_tests_wake_timer_texture_and_legacy_data_round_trip_preserve_twenty_tick_boundary", KofoniServerBehaviorTests::wakeTimerTextureAndLegacyDataRoundTripPreserveTwentyTickBoundary, 100, "supernova_server");
        TsrGameTests.add("history_kofoni_server_behavior_tests_all_declared_kofoni_immunities_and_ordinary_damage_are_preserved", KofoniServerBehaviorTests::allDeclaredKofoniImmunitiesAndOrdinaryDamageArePreserved, 100, "supernova_server");
    }

    public static void tabletInputAcceptsAllNineLoreTabletsAndRejectsStoneAndEmpty(GameTestHelper h){
        var f=fixture(h);
        for(int i=1;i<=9;i++)h.assertTrue(f.menu.getSlot(0).mayPlace(tablet(i,1)),"All nine decoded lore tablets must be accepted by the real slot");
        h.assertTrue(!f.menu.getSlot(0).mayPlace(new ItemStack(Items.STONE))&&!f.menu.getSlot(0).mayPlace(ItemStack.EMPTY),"Non-tablets and empty stacks must not pass the slot filter");
        h.succeed();
    }
    public static void emptyAndForgedNonTabletInputsDoNotProduceRewards(GameTestHelper h){
        var f=fixture(h);
        h.assertTrue(!f.menu.clickMenuButton(f.player,0)&&xp(h)==0&&muck(h).isEmpty(),"An empty exchange must produce no XP or items");
        f.menu.getSlot(0).set(new ItemStack(Items.STONE,12));
        h.assertTrue(!f.menu.clickMenuButton(f.player,0)&&f.menu.getSlot(0).getItem().getCount()==12&&xp(h)==0&&muck(h).isEmpty(),"The server must reject invalid input even if inserted around the slot filter");
        h.assertTrue(!f.player.getAdvancements().getOrStartProgress(advancement(h)).isDone(),"Rejected input must not award the advancement");
        h.succeed();
    }
    public static void eachOfTheNineTabletsTradesThroughTheRealMenuAndAwardsTwentyXp(GameTestHelper h){
        var f=fixture(h);
        for(int i=1;i<=9;i++){
            int before=xp(h),old=muck(h).size();
            f.menu.getSlot(0).set(tablet(i,1));
            h.assertTrue(f.menu.clickMenuButton(f.player,0)&&f.menu.getSlot(0).getItem().isEmpty(),"Each tagged tablet must be consumed by a successful real exchange");
            int added=muck(h).size()-old;
            h.assertTrue(xp(h)-before==20&&added>=9&&added<=28,"Every tablet exchange must add exactly twenty XP and nine to twenty-eight individual reward entities");
        }
        h.assertTrue(f.player.getAdvancements().getOrStartProgress(advancement(h)).isDone(),"A successful exchange must complete whisper_advancement");
        h.succeed();
    }
    public static void fullInputStackIsConsumedAndRewardLoopResamplesEachIteration(GameTestHelper h){
        var f=fixture(h);
        long seed=9081726L;
        var random=RandomSource.create(seed);
        int expected=0;for(int index=0;index<(int)Math.ceil(Mth.nextDouble(random,9,28));index++)expected++;
        f.player.getRandom().setSeed(seed);
        f.menu.getSlot(0).set(tablet(1,64));h.assertTrue(f.menu.getSlot(0).getItem().getCount()==64,"A legal max-stack-size component fixture must actually retain all sixty-four inputs before exchange");
        h.assertTrue(f.menu.clickMenuButton(f.player,0)&&f.menu.getSlot(0).getItem().isEmpty(),"The original exchange cost must consume the complete stack of sixty-four tablets");
        h.assertTrue(muck(h).size()==expected&&xp(h)==20,"Reward count must preserve condition resampling rather than sampling a single total");
        for(var item:muck(h))h.assertTrue(item.getItem().getCount()==1&&save(item).getShortOr("PickupDelay", (short)-1)==10,"Every reward must remain an individual one-item entity with ten-tick pickup delay");
        h.succeed();
    }
    public static void repeatedExchangeAndRemovalCannotDuplicateCommittedRewards(GameTestHelper h){
        var f=fixture(h);f.menu.getSlot(0).set(tablet(2,3));
        h.assertTrue(f.menu.clickMenuButton(f.player,0),"Initial exchange must succeed");
        int reward=muck(h).size();
        for(int i=0;i<5;i++)h.assertTrue(!f.menu.clickMenuButton(f.player,0),"Repeated empty exchange must be rejected");
        f.menu.removed(f.player);f.menu.removed(f.player);
        h.assertTrue(xp(h)==20&&muck(h).size()==reward&&inventoryCount(f.player,tablet(2,1).getItem())==0,"Retries and repeated close must not add rewards or refund already consumed payment");
        h.succeed();
    }
    public static void menuRejectsForeignOwnerDetachedMenuAndUnknownButtons(GameTestHelper h){
        var f=fixture(h);f.menu.getSlot(0).set(tablet(3,1));
        var other=new TestPlayer(h.getLevel());other.snapTo(f.player.position());other.containerMenu=f.menu;
        h.assertTrue(!f.menu.stillValid(other)&&!f.menu.clickMenuButton(other,0),"Foreign owners must not use the same menu");
        h.assertTrue(!f.menu.clickMenuButton(f.player,-1)&&!f.menu.clickMenuButton(f.player,1),"Unknown exchange buttons must be rejected");
        f.player.containerMenu=f.player.inventoryMenu;
        h.assertTrue(!f.menu.clickMenuButton(f.player,0)&&f.menu.getSlot(0).getItem().getCount()==1&&xp(h)==0,"An obsolete detached menu must preserve payment and produce nothing");
        h.succeed();
    }
    public static void menuChecksExactDistanceAliveNpcAndUuidIdentity(GameTestHelper h){
        var f=fixture(h);
        f.player.snapTo(f.npc.position().add(8,0,0));h.assertTrue(f.menu.stillValid(f.player),"The eight-block boundary must be valid");
        f.player.snapTo(f.npc.position().add(8.01,0,0));h.assertTrue(!f.menu.stillValid(f.player),"Beyond eight blocks must be invalid");
        f.player.snapTo(f.npc.position());
        var wrong=new KofoniMenu(KofoniPage.TABLETS,2,f.player.getInventory(),f.npc.getId(),UUID.randomUUID(),f.npc.blockPosition(),1);
        h.assertTrue(!wrong.stillValid(f.player),"The right entity ID with the wrong UUID must not authorize a menu");
        f.npc.setHealth(0);h.assertTrue(!f.menu.stillValid(f.player),"A dead NPC must invalidate the menu");
        f.npc.discard();h.assertTrue(!f.menu.stillValid(f.player),"A removed NPC must invalidate the menu");
        h.succeed();
    }
    public static void quickMoveTransfersTabletsAndKeepsOtherItemsOutOfInput(GameTestHelper h){
        var f=fixture(h);f.player.getInventory().setItem(9,tablet(4,5));
        h.assertTrue(!f.menu.quickMoveStack(f.player,1).isEmpty()&&f.menu.getSlot(0).getItem().getCount()==5&&f.player.getInventory().getItem(9).isEmpty(),"Shift-click must transfer valid tablets from inventory into the input; input="+f.menu.getSlot(0).getItem()+", source="+f.player.getInventory().getItem(9));
        h.assertTrue(!f.menu.quickMoveStack(f.player,0).isEmpty()&&f.menu.getSlot(0).getItem().isEmpty()&&inventoryCount(f.player,tablet(4,1).getItem())==5,"Shift-clicking input must return the whole stack");
        f.player.getInventory().setItem(9,new ItemStack(Items.STONE,7));
        f.menu.quickMoveStack(f.player,1);
        h.assertTrue(f.menu.getSlot(0).getItem().isEmpty()&&inventoryCount(f.player,Items.STONE)==7,"Non-tablet shift-click may move within inventory but must not enter input or disappear");
        h.assertTrue(f.menu.quickMoveStack(f.player,-1).isEmpty()&&f.menu.quickMoveStack(f.player,999).isEmpty(),"Invalid shift-click indices must safely reject");
        h.succeed();
    }
    public static void closingUnsubmittedInputReturnsItExactlyOnce(GameTestHelper h){
        var f=fixture(h);f.menu.getSlot(0).set(tablet(5,13));h.assertTrue(f.menu.getSlot(0).getItem().getCount()==13,"The refund fixture must actually hold thirteen legal component-stacked tablets");
        f.menu.removed(f.player);f.menu.removed(f.player);
        h.assertTrue(f.menu.getSlot(0).getItem().isEmpty()&&inventoryCount(f.player,tablet(5,1).getItem())==13&&xp(h)==0&&muck(h).isEmpty(),"Closing unused input twice must return the original payment once without rewards; input="+f.menu.getSlot(0).getItem()+", inventory="+inventoryCount(f.player,tablet(5,1).getItem())+", xp="+xp(h)+", muck="+muck(h).size());
        h.assertTrue(!f.menu.stillValid(f.player)&&!f.menu.clickMenuButton(f.player,0),"A closed menu must remain unusable");
        h.succeed();
    }
    public static void fullInventoryCloseDropsUnsubmittedPaymentInsteadOfDeletingIt(GameTestHelper h){
        var f=fixture(h);for(int i=0;i<36;i++)f.player.getInventory().setItem(i,new ItemStack(Items.STONE,64));
        f.menu.getSlot(0).set(tablet(6,11));h.assertTrue(f.menu.getSlot(0).getItem().getCount()==11,"The full-inventory fixture must hold eleven legal tablets before close");f.menu.removed(f.player);f.menu.removed(f.player);
        var dropped=h.getLevel().getEntitiesOfClass(ItemEntity.class,box(h),e->e.getItem().is(tablet(6,1).getItem()));
        h.assertTrue(dropped.stream().mapToInt(e->e.getItem().getCount()).sum()==11&&f.menu.getSlot(0).getItem().isEmpty()&&xp(h)==0,"A full inventory must drop exactly the unsubmitted payment without duplication; count="+dropped.stream().mapToInt(e->e.getItem().getCount()).sum()+", input="+f.menu.getSlot(0).getItem()+", xp="+xp(h));
        h.succeed();
    }
    public static void allInformationRoutesPreserveIntroAndRejectObsoletePageButtons(GameTestHelper h){
        var f=fixture(h);
        h.assertTrue(KofoniContent.open(f.player,f.npc,f.npc.blockPosition(),KofoniPage.MAIN),"The real provider must open the main page");
        var main=(KofoniMenu)f.player.containerMenu;
        int intro=main.intro;h.assertTrue(intro==1||intro==2,"Main intro must preserve both original randomized variants");
        h.assertTrue(main.clickMenuButton(f.player,0)&&((KofoniMenu)f.player.containerMenu).page==KofoniPage.INFO,"Main button zero must route to information");
        h.assertTrue(!main.clickMenuButton(f.player,1),"An obsolete main page must not route a second time");
        var pages=new KofoniPage[]{KofoniPage.SELF,KofoniPage.QO,KofoniPage.BLIZZARD,KofoniPage.MALACHITE,KofoniPage.PYLONS,KofoniPage.BENZOR,KofoniPage.SHALESPRING,KofoniPage.RUBY};
        for(int i=0;i<pages.length;i++){
            var info=(KofoniMenu)f.player.containerMenu;
            h.assertTrue(info.clickMenuButton(f.player,i),"Every original information button must open its page");
            var page=(KofoniMenu)f.player.containerMenu;
            h.assertTrue(page.page==pages[i]&&page.intro==intro&&page.clickMenuButton(f.player,0),"Each information page must preserve intro and return to the information index");
        }
        KofoniContent.open(f.player,f.npc,f.npc.blockPosition(),KofoniPage.MAIN);
        h.assertTrue(((KofoniMenu)f.player.containerMenu).clickMenuButton(f.player,1)&&((KofoniMenu)f.player.containerMenu).page==KofoniPage.TABLETS,"Main button one must open tablets");
        h.assertTrue(f.player.payloadNpc==f.npc.getId()&&f.player.payloadUuid.equals(f.npc.getUUID())&&f.player.payloadAnchor.equals(f.npc.blockPosition()),"The real provider writer must encode the bound NPC and immutable anchor");
        h.succeed();
    }
    public static void failedOpenCanRetryWhileInvalidNpcOpenIsRejectedBeforeProvider(GameTestHelper h){
        var f=fixture(h);KofoniContent.open(f.player,f.npc,f.npc.blockPosition(),KofoniPage.MAIN);
        var main=(KofoniMenu)f.player.containerMenu;
        f.player.rejectOpen=true;h.assertTrue(!main.clickMenuButton(f.player,0),"A refused provider open must not be reported successful");
        f.player.rejectOpen=false;h.assertTrue(main.clickMenuButton(f.player,0),"A refused open must release changedPage so a valid retry succeeds");
        int opens=f.player.opens;
        h.assertTrue(!KofoniContent.open(f.player,null,f.npc.blockPosition(),KofoniPage.MAIN),"Null NPC must be rejected");
        f.player.snapTo(f.npc.position().add(8.01,0,0));
        h.assertTrue(!KofoniContent.open(f.player,f.npc,f.npc.blockPosition(),KofoniPage.MAIN)&&f.player.opens==opens,"Out-of-range NPC must be rejected before opening any provider");
        f.player.snapTo(f.npc.position());f.npc.setHealth(0);
        h.assertTrue(!KofoniContent.open(f.player,f.npc,f.npc.blockPosition(),KofoniPage.MAIN),"Dead NPC must not open a provider");
        h.succeed();
    }
    public static void wakeTimerTextureAndLegacyDataRoundTripPreserveTwentyTickBoundary(GameTestHelper h){
        var f=fixture(h);var tag=save(f.npc);
        tag.putString("Texture","custom-kofoni");tag.putInt("KofoniWakeTicks",Integer.MAX_VALUE);tag.remove("KofoniWakeSchedule");tag.remove("KofoniWakeShifted");
        tag.remove("NeoForgeData");var legacy=new CompoundTag();legacy.putString("FixtureMarker","retained");tag.put("ForgeData",legacy);
        var restored=KofoniContent.KOFONI.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.LOAD);restored.load(input(h, tag));
        var restoredTag=save(restored);int restoredSchedule=restoredTag.getIntOr("KofoniWakeSchedule", 0);boolean restoredShift=restored.isShiftKeyDown();boolean restoredTexture=restored.texture().equals("custom-kofoni");String restoredMarker=restored.getPersistentData().getStringOr("FixtureMarker", "");boolean restoredPersistent=restored.isPersistenceRequired();
        ThermalShockReforged.LOGGER.debug("Kofoni legacy reload probe: schedule={}, shifted={}, texture={}, marker={}, persistent={}, rootKeys={}",restoredSchedule,restoredShift,restoredTexture,restoredMarker,restoredPersistent,tag.keySet());
        h.assertTrue(restoredSchedule==(1 << 19)&&restoredShift&&restoredTexture&&restoredMarker.equals("retained")&&restoredPersistent&&!restored.removeWhenFarAway(1E9),"Reload must clamp wake time, restore texture/legacy data and retain persistence");
        restored.setNoAi(true);restored.setNoGravity(true);
        for(int i=0;i<5;i++)restored.endTick(h.getLevel());
        restored.mobInteract(f.player,InteractionHand.MAIN_HAND);
        h.assertTrue((save(restored).getIntOr("KofoniWakeSchedule", 0)&(1 << 14))!=0,"Repeated wake interaction must not extend the initial pending timer");
        for(int i=0;i<14;i++)restored.endTick(h.getLevel());
        h.assertTrue(restored.isShiftKeyDown()&&(save(restored).getIntOr("KofoniWakeSchedule", 0)&1)!=0,"Wake must remain crouched until tick twenty");
        restored.endTick(h.getLevel());h.assertTrue(!restored.isShiftKeyDown()&&(save(restored).getIntOr("KofoniWakeSchedule", -1)&1)==0,"Wake must finish exactly at tick twenty");
        h.succeed();
    }
    public static void allDeclaredKofoniImmunitiesAndOrdinaryDamageArePreserved(GameTestHelper h){
        var f=fixture(h);var s=h.getLevel().damageSources();
        var sources=new ArrayList<DamageSource>();
        for(var type:List.of(EntityType.ARROW,EntityType.SPLASH_POTION,EntityType.AREA_EFFECT_CLOUD))sources.add(s.source(DamageTypes.MAGIC,type.create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.LOAD)));
        for(var type:List.of(DamageTypes.IN_FIRE,DamageTypes.FALL,DamageTypes.CACTUS,DamageTypes.DROWN,DamageTypes.LIGHTNING_BOLT,DamageTypes.EXPLOSION,DamageTypes.TRIDENT,DamageTypes.FALLING_ANVIL,DamageTypes.DRAGON_BREATH,DamageTypes.WITHER,DamageTypes.WITHER_SKULL))sources.add(s.source(type));
        for(var source:sources){boolean accepted=f.npc.hurtServer(h.getLevel(),source,10);ThermalShockReforged.LOGGER.debug("Kofoni damage probe: type={}, direct={}, accepted={}, health={}",source.type().msgId(),source.getDirectEntity()==null?"none":source.getDirectEntity().getType(),accepted,f.npc.getHealth());h.assertTrue(!accepted&&f.npc.getHealth()==75,"Every declared original immunity must reject damage");}
        float before=f.npc.getHealth();boolean accepted=f.npc.hurtServer(h.getLevel(),s.generic(),10);boolean characterTagged=f.npc.is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE,TsrGameTests.id("character_mobs")));ThermalShockReforged.LOGGER.debug("Kofoni ordinary damage probe: accepted={}, healthBefore={}, healthAfter={}, characterTagged={}",accepted,before,f.npc.getHealth(),characterTagged);
        h.assertTrue(characterTagged&&!accepted&&f.npc.getHealth()==75,"The global character protection must reject ordinary damage without changing Kofoni health");
        h.succeed();
    }

    private static Fixture fixture(GameTestHelper h){var npc=KofoniContent.KOFONI.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.LOAD);npc.snapTo(h.absolutePos(new BlockPos(8,3,8)).getCenter());npc.setNoAi(true);npc.setNoGravity(true);h.getLevel().addFreshEntity(npc);var p=new TestPlayer(h.getLevel());p.snapTo(npc.position());var menu=new KofoniMenu(KofoniPage.TABLETS,1,p.getInventory(),npc.getId(),npc.getUUID(),npc.blockPosition(),1);p.containerMenu=menu;return new Fixture(npc,p,menu);}
    private static ItemStack tablet(int i,int count){var stack=new ItemStack(BuiltInRegistries.ITEM.getOptional(KofoniContent.id("diskette_"+i)).orElseThrow(),count);if(count>1)stack.set(net.minecraft.core.component.DataComponents.MAX_STACK_SIZE,64);return stack;}
    private static AABB box(GameTestHelper h){return new AABB(h.absolutePos(new BlockPos(8,3,8))).inflate(4);}
    private static List<ItemEntity> muck(GameTestHelper h){return h.getLevel().getEntitiesOfClass(ItemEntity.class,box(h),e->e.getItem().is(NativeDecorContent.SPARKLY_MUCK.get().asItem()));}
    private static int xp(GameTestHelper h){return h.getLevel().getEntitiesOfClass(ExperienceOrb.class,box(h)).stream().mapToInt(ExperienceOrb::getValue).sum();}
    private static int inventoryCount(ServerPlayer p,Item item){return p.getInventory().getNonEquipmentItems().stream().filter(s->s.is(item)).mapToInt(ItemStack::getCount).sum();}
    private static CompoundTag save(Entity e){var output=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,e.registryAccess());e.saveWithoutId(output);return output.buildResult();}
    private static net.minecraft.world.level.storage.ValueInput input(GameTestHelper h,CompoundTag tag){return TagValueInput.create(ProblemReporter.DISCARDING,h.getLevel().registryAccess(),tag);}
    private static net.minecraft.advancements.AdvancementHolder advancement(GameTestHelper h){return h.getLevel().getServer().getAdvancements().get(KofoniContent.id("whisper_advancement"));}
    private record Fixture(KofoniEntity npc,TestPlayer player,KofoniMenu menu){}
    private static final class TestPlayer extends ServerPlayer {
        private boolean rejectOpen;
        private int opens,payloadNpc;
        private UUID payloadUuid;
        private BlockPos payloadAnchor;
        TestPlayer(ServerLevel level){super(level.getServer(),level,new GameProfile(UUID.randomUUID(),"kofoni-test"),ClientInformation.createDefault());connection=new net.neoforged.neoforge.common.util.FakePlayer(level,getGameProfile()).connection;}
        @Override public OptionalInt openMenu(MenuProvider provider,Consumer<RegistryFriendlyByteBuf> writer){
            opens++;if(rejectOpen)return OptionalInt.empty();
            if(containerMenu!=inventoryMenu)containerMenu.removed(this);
            AbstractContainerMenu menu=provider.createMenu(opens,getInventory(),this);
            if(menu==null)return OptionalInt.empty();
            var buffer=new RegistryFriendlyByteBuf(Unpooled.buffer(),registryAccess(),ConnectionType.NEOFORGE);
            try {writer.accept(buffer);payloadNpc=buffer.readVarInt();payloadUuid=buffer.readUUID();payloadAnchor=buffer.readBlockPos();int intro=buffer.readVarInt();if(intro!=((KofoniMenu)menu).intro||buffer.isReadable())throw new IllegalStateException("Unexpected menu payload");}
            finally{buffer.release();}
            containerMenu=menu;return OptionalInt.of(opens);
        }
    }
    private KofoniServerBehaviorTests(){}
}
