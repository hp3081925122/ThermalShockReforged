package com.hp.thermalshockreforged.content.farming;

import com.hp.thermalshockreforged.gametest.TsrGameTests;
import com.mojang.authlib.GameProfile;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
public final class SickleServerBehaviorTests {
    private static final String NS="thermal_shock_reforged:";
    private static final Set<String> ANCIENT_LOOT=Set.of("ancient_skorio_seed","ancient_barbubble_seed","dewleaf_seed","hearthfly_branch_seed","frost_lotus_seed","neoflorm_seed","volatine_seed");
    private static final String[] PRODUCTS={"skorio_petal","barbubble_melon","skorio_petal","barbubble_melon_slice","dew_bottle","hearth_honey","frost_pearl","neoflorm_crystal","minecraft:gunpowder"};
    private static final int[] BASE={2,2,5,5,1,2,2,2,2};
    private static final int[] STRATO={3,3,7,8,2,2,3,2,2};

    public static void register() {
        TsrGameTests.add("history_sickle_server_behavior_tests_empty_hand_fallback_harvests_exactly_one_of_each_of_nine_mature_crops", SickleServerBehaviorTests::emptyHandFallbackHarvestsExactlyOneOfEachOfNineMatureCrops, 100, "supernova_server");
        TsrGameTests.add("history_sickle_server_behavior_tests_ruby_all_nine_harvest_branches_preserve_products_seeds_and_lifetime", SickleServerBehaviorTests::rubyAllNineHarvestBranchesPreserveProductsSeedsAndLifetime, 100, "supernova_server");
        TsrGameTests.add("history_sickle_server_behavior_tests_claw_all_nine_harvest_branches_preserve_corrected_barbubble_and_single_ancient_yield", SickleServerBehaviorTests::clawAllNineHarvestBranchesPreserveCorrectedBarbubbleAndSingleAncientYield, 100, "supernova_server");
        TsrGameTests.add("history_sickle_server_behavior_tests_stratosilver_all_nine_harvest_branches_preserve_expanded_yields_and_extra_rewards", SickleServerBehaviorTests::stratosilverAllNineHarvestBranchesPreserveExpandedYieldsAndExtraRewards, 100, "supernova_server");
        TsrGameTests.add("history_sickle_server_behavior_tests_bloodstone_and_carborundum_each_use_all_nine_base_harvest_branches", SickleServerBehaviorTests::bloodstoneAndCarborundumEachUseAllNineBaseHarvestBranches, 100, "supernova_server");
        TsrGameTests.add("history_sickle_server_behavior_tests_unrelated_tool_never_receives_sickle_resource_rewards", SickleServerBehaviorTests::unrelatedToolNeverReceivesSickleResourceRewards, 100, "supernova_server");
        TsrGameTests.add("history_sickle_server_behavior_tests_null_player_harvest_guard_produces_nothing_for_all_nine_crops", SickleServerBehaviorTests::nullPlayerHarvestGuardProducesNothingForAllNineCrops, 100, "supernova_server");
        TsrGameTests.add("history_sickle_server_behavior_tests_actual_native_survival_break_has_no_additional_vanilla_crop_loot", SickleServerBehaviorTests::actualNativeSurvivalBreakHasNoAdditionalVanillaCropLoot, 100, "supernova_server");
        TsrGameTests.add("history_sickle_server_behavior_tests_breaking_either_barbubble_half_harvests_once_and_removes_both_halves", SickleServerBehaviorTests::breakingEitherBarbubbleHalfHarvestsOnceAndRemovesBothHalves, 100, "supernova_server");
        TsrGameTests.add("history_sickle_server_behavior_tests_cancelled_native_break_keeps_all_nine_crops_and_does_not_harvest", SickleServerBehaviorTests::cancelledNativeBreakKeepsAllNineCropsAndDoesNotHarvest, 100, "supernova_server");
        TsrGameTests.add("history_sickle_server_behavior_tests_seven_geo_mature_inventories_release_all_nine_slots_once_alongside_harvest", SickleServerBehaviorTests::sevenGeoMatureInventoriesReleaseAllNineSlotsOnceAlongsideHarvest, 100, "supernova_server");
        TsrGameTests.add("history_sickle_server_behavior_tests_replacing_geo_mature_crops_releases_inventory_without_inventing_harvest", SickleServerBehaviorTests::replacingGeoMatureCropsReleasesInventoryWithoutInventingHarvest, 100, "supernova_server");
        TsrGameTests.add("history_sickle_server_behavior_tests_stale_destruction_callbacks_must_not_reward_failed_second_removal", SickleServerBehaviorTests::staleDestructionCallbacksMustNotRewardFailedSecondRemoval, 100, "supernova_server");
    }
    public static void emptyHandFallbackHarvestsExactlyOneOfEachOfNineMatureCrops(GameTestHelper h){
        var player=player(h,"air");
        for(var crop:CropKind.values()){
            harvest(h,crop,player);var drops=drops(h);
            h.assertTrue(drops.size()==1&&drops.getFirst().getItem().is(FarmingContent.crop(crop).asItem())&&drops.getFirst().getItem().getCount()==1&&drops.getFirst().getAge()==0,"Fallback must return exactly the actual mature crop item with normal lifetime: "+crop);
            checkDelay(h,drops);clear(h);
        }
        h.succeed();
    }
    public static void rubyAllNineHarvestBranchesPreserveProductsSeedsAndLifetime(GameTestHelper h){runTable(h,"ruby_sickle",1,12);h.succeed();}
    public static void clawAllNineHarvestBranchesPreserveCorrectedBarbubbleAndSingleAncientYield(GameTestHelper h){runTable(h,"claw_sickle",2,12);h.succeed();}
    public static void stratosilverAllNineHarvestBranchesPreserveExpandedYieldsAndExtraRewards(GameTestHelper h){runTable(h,"stratosilver_sickle",3,20);h.succeed();}
    public static void bloodstoneAndCarborundumEachUseAllNineBaseHarvestBranches(GameTestHelper h){runTable(h,"bloodstone_sickle",1,4);runTable(h,"carborundum_sickle",1,4);h.succeed();}
    public static void unrelatedToolNeverReceivesSickleResourceRewards(GameTestHelper h){
        var player=player(h,"minecraft:diamond_hoe");
        for(var crop:CropKind.values()){harvest(h,crop,player);var d=drops(h);h.assertTrue(d.size()==1&&d.getFirst().getItem().is(FarmingContent.crop(crop).asItem()),"An unrelated real tool must use crop fallback rather than a sickle branch: "+crop);clear(h);}
        h.succeed();
    }
    public static void nullPlayerHarvestGuardProducesNothingForAllNineCrops(GameTestHelper h){for(var crop:CropKind.values()){var state=plant(h,crop);var loot=Block.getDrops(state,h.getLevel(),pos(h),h.getLevel().getBlockEntity(pos(h)));h.assertTrue(loot.isEmpty(),"A block loot context without THIS_ENTITY must not create sickle harvest resources: "+crop);}h.succeed();}
    public static void actualNativeSurvivalBreakHasNoAdditionalVanillaCropLoot(GameTestHelper h){
        var player=player(h,"ruby_sickle");
        for(var crop:CropKind.values()){
            plant(h,crop);h.assertTrue(player.gameMode.destroyBlock(pos(h)),"Native survival game mode must accept a planted mature crop: "+crop);
            assertTable(h,crop,1,drops(h));h.assertTrue(h.getLevel().getBlockState(pos(h)).isAir(),"Native break must remove the crop");clear(h);
        }
        h.succeed();
    }
    public static void breakingEitherBarbubbleHalfHarvestsOnceAndRemovesBothHalves(GameTestHelper h){
        var player=player(h,"ruby_sickle");
        for(int half=0;half<2;half++){
            plant(h,CropKind.BARBUBBLE);h.assertTrue(player.gameMode.destroyBlock(pos(h).above(half)),"Both real plant halves must be breakable");assertTable(h,CropKind.BARBUBBLE,1,drops(h));
            h.assertTrue(h.getLevel().getBlockState(pos(h)).isAir()&&h.getLevel().getBlockState(pos(h).above()).isAir(),"Removing either half must remove both without a second harvest");clear(h);
        }
        h.succeed();
    }
    public static void cancelledNativeBreakKeepsAllNineCropsAndDoesNotHarvest(GameTestHelper h){
        var player=player(h,"stratosilver_sickle");Consumer<BreakBlockEvent> veto=e->{if(e.getPlayer()==player)e.setCanceled(true);};NeoForge.EVENT_BUS.addListener(veto);
        try{for(var crop:CropKind.values()){plant(h,crop);h.assertTrue(!player.gameMode.destroyBlock(pos(h))&&h.getLevel().getBlockState(pos(h)).is(FarmingContent.crop(crop))&&drops(h).isEmpty(),"Cancelled break must preserve the real crop and produce no harvest: "+crop);clear(h);}}finally{NeoForge.EVENT_BUS.unregister(veto);}
        h.succeed();
    }
    public static void sevenGeoMatureInventoriesReleaseAllNineSlotsOnceAlongsideHarvest(GameTestHelper h){
        var player=player(h,"ruby_sickle");
        for(var crop:CropKind.values())if(crop.geo){
            plant(h,crop);h.assertTrue(h.getLevel().getBlockEntity(pos(h)) instanceof FarmInventoryBlockEntity,"Every geo mature crop must expose its actual inventory");var be=(FarmInventoryBlockEntity)h.getLevel().getBlockEntity(pos(h));
            for(int slot=0;slot<9;slot++)be.setItem(slot,new ItemStack(Items.DIAMOND,slot+1));
            h.assertTrue(player.gameMode.destroyBlock(pos(h)),"Native inventory crop break must succeed");var d=drops(h);int inventory=d.stream().filter(e->e.getItem().is(Items.DIAMOND)).mapToInt(e->e.getItem().getCount()).sum();
            h.assertTrue(inventory==45&&h.getLevel().getBlockEntity(pos(h))==null,"All nine actual slots must be released once and the old BE removed: "+crop);assertTable(h,crop,1,d.stream().filter(e->!e.getItem().is(Items.DIAMOND)).toList());clear(h);
        }
        h.succeed();
    }
    public static void replacingGeoMatureCropsReleasesInventoryWithoutInventingHarvest(GameTestHelper h){
        for(var crop:CropKind.values())if(crop.geo){plant(h,crop);var be=(FarmInventoryBlockEntity)h.getLevel().getBlockEntity(pos(h));be.setItem(8,new ItemStack(Items.EMERALD,23));h.getLevel().setBlock(pos(h),Blocks.STONE.defaultBlockState(),3);var d=drops(h);h.assertTrue(d.stream().allMatch(e->e.getItem().is(Items.EMERALD))&&d.stream().mapToInt(e->e.getItem().getCount()).sum()==23,"Non-player replacement must only release the inventory and must not run sickle harvesting: "+crop);clear(h);}
        h.succeed();
    }
    public static void staleDestructionCallbacksMustNotRewardFailedSecondRemoval(GameTestHelper h){
        var player=player(h,"ruby_sickle");var leaks=new ArrayList<String>();
        for(var crop:CropKind.values()){
            plant(h,crop);h.assertTrue(player.gameMode.destroyBlock(pos(h)),"The first real player break must succeed");clearDrops(h);
            player.gameMode.destroyBlock(pos(h));
            if(!h.getLevel().getBlockState(pos(h)).isAir()||!drops(h).isEmpty())leaks.add(crop+":state="+h.getLevel().getBlockState(pos(h))+",drops="+drops(h).size());clear(h);
        }
        h.assertTrue(leaks.isEmpty(),"A repeated player break against the removed crop must not issue a second loot-table harvest: "+leaks);h.succeed();
    }

    private static void runTable(GameTestHelper h,String tool,int mode,int repetitions){
        var player=player(h,tool);
        for(var crop:CropKind.values())for(int i=0;i<repetitions;i++){harvest(h,crop,player);assertTable(h,crop,mode,drops(h));clear(h);}
    }

    private static void assertTable(GameTestHelper h,CropKind crop,int mode,List<ItemEntity> drops){
        int index=crop.ordinal();String product=key(PRODUCTS[index]);String seed=key(crop.id+"_seed");int min=mode==3?STRATO[index]:BASE[index];int max=min;
        if(crop==CropKind.DEWLEAF)max=mode==3?4:2;
        if(crop==CropKind.NEOFLORM)max=mode==3?6:mode==2?3:2;
        int seedMax=mode==3&&crop!=CropKind.NEOFLORM&&crop!=CropKind.VOLATINE?3:2;
        int productCount=0,seedCount=0,loot=0,bonus=0;
        for(var e:drops){
            String id=BuiltInRegistries.ITEM.getKey(e.getItem().getItem()).toString();int count=e.getItem().getCount();h.assertTrue(count>0,"Every native loot entry must contain at least one item");
            if(id.equals(product)){productCount+=count;h.assertTrue(e.getAge()==0,"Primary products must retain normal lifetime: "+crop);}
            else if(e.getAge()==-32768&&id.equals(seed))seedCount+=count;
            else if(mode>=2&&e.getAge()==0&&id.startsWith(NS)&&ANCIENT_LOOT.contains(id.substring(NS.length())))loot+=count;
            else if(mode==3&&e.getAge()==-32768&&((crop==CropKind.SKORIO&&id.equals(key("ancient_skorio_seed")))||(crop==CropKind.BARBUBBLE&&id.equals(key("ancient_barbubble_seed")))||(crop==CropKind.NEOFLORM&&id.equals(key("stellar_scrap")))))bonus+=count;
            else h.fail("Unexpected crop harvest output/lifetime: "+crop+" mode="+mode+" id="+id+" age="+e.getAge());
        }
        h.assertTrue(productCount>=min&&productCount<=max&&seedCount>=1&&seedCount<=seedMax,"Exact/ranged main yields and unlimited seed count must match: "+crop+" mode="+mode+" products="+productCount+" seeds="+seedCount);
        h.assertTrue(loot<=(mode>=2?1:0)&&bonus<=(mode==3?1:0),"Independent advanced rewards must occur at most once per harvest");checkDelay(h,drops);
    }

    private static void checkDelay(GameTestHelper h,List<ItemEntity> drops){for(var e:drops)h.assertTrue(TsrGameTests.roundTrip(h,e).getShortOr("PickupDelay",(short)-1)==10,"All harvested drops must retain original ten-tick pickup delay");}
    private static String key(String id){return id.contains(":")?id:NS+id;}
    private static BlockPos pos(GameTestHelper h){return h.absolutePos(new BlockPos(10,2,8));}
    private static List<ItemEntity> drops(GameTestHelper h){return h.getLevel().getEntitiesOfClass(ItemEntity.class,new AABB(pos(h)).inflate(3));}
    private static void clearDrops(GameTestHelper h){drops(h).forEach(Entity::discard);}
    private static void clear(GameTestHelper h){h.getLevel().setBlock(pos(h),Blocks.AIR.defaultBlockState(),3);h.getLevel().setBlock(pos(h).above(),Blocks.AIR.defaultBlockState(),3);clearDrops(h);}
    private static FakePlayer player(GameTestHelper h,String tool){var player=new FakePlayer(h.getLevel(),new GameProfile(UUID.randomUUID(),"sickle-test"));player.setPos(pos(h).getCenter().add(0,0,2));h.assertTrue(player.gameMode.getGameModeForPlayer()==GameType.SURVIVAL,"Native fixture game mode must be survival");if(!tool.equals("air")){var item=BuiltInRegistries.ITEM.getOptional(Identifier.parse(key(tool))).orElseThrow();h.assertTrue(item!=Items.AIR,"Harvest tool must be a real registered item");player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(item));}return player;}
    private static BlockState plant(GameTestHelper h,CropKind crop){
        clear(h);h.getLevel().setBlock(pos(h).below(),(crop.geo?FarmingContent.PLANTER_BOX.get():Blocks.DIRT).defaultBlockState(),3);var state=FarmingContent.crop(crop).defaultBlockState();
        if(crop==CropKind.BARBUBBLE)DoublePlantBlock.placeAt(h.getLevel(),state,pos(h),3);else h.getLevel().setBlock(pos(h),state,3);
        h.assertTrue(h.getLevel().getBlockState(pos(h)).is(FarmingContent.crop(crop)),"Mature crop must actually remain placed before harvesting: "+crop);
        if(crop==CropKind.BARBUBBLE)h.assertTrue(h.getLevel().getBlockState(pos(h).above()).is(FarmingContent.crop(crop))&&h.getLevel().getBlockState(pos(h).above()).getValue(DoublePlantBlock.HALF)==DoubleBlockHalf.UPPER,"Barbubble fixture must have both native halves");return state;
    }
    private static void harvest(GameTestHelper h,CropKind crop,FakePlayer player){plant(h,crop);h.assertTrue(player.gameMode.destroyBlock(pos(h)),"A real survival player break must harvest the mature crop: "+crop);h.assertTrue(h.getLevel().getBlockState(pos(h)).isAir(),"Successful harvest must consume the actual block");}
    private SickleServerBehaviorTests(){}
}
