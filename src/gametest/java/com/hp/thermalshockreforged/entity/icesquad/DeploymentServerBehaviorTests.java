package com.hp.thermalshockreforged.entity.icesquad;

import net.minecraft.world.entity.EntitySpawnReason;

import net.minecraft.world.level.storage.TagValueOutput;

import net.minecraft.world.level.storage.TagValueInput;

import net.minecraft.util.ProblemReporter;

import com.hp.thermalshockreforged.gametest.TsrGameTests;
import com.hp.thermalshockreforged.gametest.PlayerInteractionTests;

import com.mojang.authlib.GameProfile;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
public final class DeploymentServerBehaviorTests {
    public static void survivalAndCreativeBothHandsConsumeOnlyTheActuallyUsedItem(GameTestHelper h){
        for(boolean creative:new boolean[]{false,true})for(var hand:InteractionHand.values()){
            var p=player(h);if(creative)p.gameMode.changeGameModeForPlayer(GameType.CREATIVE);var other=hand==InteractionHand.MAIN_HAND?InteractionHand.OFF_HAND:InteractionHand.MAIN_HAND;
            p.setItemInHand(hand,new ItemStack(IceSquadContent.THINGAMABOB.get()));p.setItemInHand(other,new ItemStack(IceSquadContent.THINGAMABOB.get()));p.getInventory().setItem(9,new ItemStack(IceSquadContent.THINGAMABOB.get()));
            var result=IceSquadContent.THINGAMABOB.get().use(h.getLevel(),p,hand);var task=onlyTask(h,p.getUUID());var tag=save(task);
            h.assertTrue(result.consumesAction()&&p.getItemInHand(hand).isEmpty()&&p.getItemInHand(other).getCount()==1&&p.getInventory().getItem(9).getCount()==1,"Actual hand consumption must remove one selected item while retaining the other hand and backpack in survival and creative");
            h.assertTrue(uuid(tag, "DeploymentSource").equals(p.getUUID())&&tag.getIntOr("Mode", 0)==0&&tag.getIntOr("Remaining", 0)==22&&anchor(tag).equals(p.position()),"The real item entry must submit one finite twenty-two-tick request anchored to its source position");task.discard();PlayerInteractionTests.release(h,p);
        }
        h.succeed();
    }
    public static void emptyAndUnrelatedHandsAreRejectedWithoutSubmittingOrConsuming(GameTestHelper h){
        var p=player(h);for(var stack:List.of(ItemStack.EMPTY,new ItemStack(Items.STICK,12))){p.setItemInHand(InteractionHand.MAIN_HAND,stack);h.assertTrue(!IceSquadContent.THINGAMABOB.get().use(h.getLevel(),p,InteractionHand.MAIN_HAND).consumesAction()&&tasks(h,p.getUUID()).isEmpty()&&(stack.isEmpty()||stack.getCount()==12),"Invalid use must preserve unrelated stack counts and submit no deployment");}PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void creationTickRepeatedTaskTicksDoNotSpendItsInitialDelay(GameTestHelper h){
        var task=task(h,true);for(int i=0;i<30;i++)task.tick();h.assertTrue(save(task).getIntOr("Remaining", 0)==22&&pods(h).isEmpty(),"Repeated calls in the creation game-time must not advance request delay");
        h.runAfterDelay(1,()->{h.assertTrue(save(task).getIntOr("Remaining", 0)==21&&pods(h).isEmpty(),"The following actual server tick must advance exactly once");task.discard();h.succeed();});
    }
    public static void actualServerTicksRunTheWholeTwentyTwoFifteenTwentyFiveOneChain(GameTestHelper h){
        var p=player(h);p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(IceSquadContent.THINGAMABOB.get()));var expected=BlockPos.containing(p.getX()+.5,p.getY(),p.getZ()+.5).getBottomCenter();final BlizzardDropPodEntity[] pod={null};
        final Vec3[] birthPosition={null};final Vec3[] birthMovement={null};final int[] birthTick={-1};Consumer<EntityJoinLevelEvent> birth=e->{if(e.getLevel()==h.getLevel()&&e.getEntity() instanceof BlizzardDropPodEntity spawned&&spawned.position().distanceToSqr(expected)<4){birthPosition[0]=spawned.position();birthMovement[0]=spawned.getDeltaMovement();birthTick[0]=spawned.tickCount;}};NeoForge.EVENT_BUS.addListener(birth);IceSquadContent.THINGAMABOB.get().use(h.getLevel(),p,InteractionHand.MAIN_HAND);var task=onlyTask(h,p.getUUID());p.setPos(p.position().add(3,0,0));
        h.runAfterDelay(21,()->h.assertTrue(pods(h).isEmpty()&&!task.isRemoved(),"No pod may appear before request tick twenty-two"));
        h.runAfterDelay(22,()->{var list=pods(h);h.assertTrue(list.size()==1&&task.isRemoved(),"Actual tick twenty-two must create one pod and consume its task");pod[0]=list.getFirst();boolean bornAtExpected=expected.equals(birthPosition[0])&&Vec3.ZERO.equals(birthMovement[0])&&birthTick[0]==0;NeoForge.EVENT_BUS.unregister(birth);h.assertTrue(bornAtExpected,"Pod must join at the original rounded anchor with zero initial velocity before gravity; expected="+expected+", birthPosition="+birthPosition[0]+", birthMovement="+birthMovement[0]+", birthTick="+birthTick[0]);h.assertTrue(pod[0].position().equals(expected),"The actual tick-twenty-two pod must remain at the original rounded anchor; expected="+expected+", actual="+pod[0].position()+", movement="+pod[0].getDeltaMovement()+", tickCount="+pod[0].tickCount);});
        h.runAfterDelay(36,()->h.assertTrue(pod[0]!=null&&!save(pod[0]).getBooleanOr("PodSmokeSent", false),"No smoke phase before pod age fifteen"));
        h.runAfterDelay(37,()->h.assertTrue(save(pod[0]).getBooleanOr("PodSmokeSent", false)&&save(pod[0]).getIntOr("PodAge", 0)==15,"Actual pod age fifteen must mark the smoke phase"));
        h.runAfterDelay(46,()->h.assertTrue(!pod[0].isRemoved()&&bastions(h).isEmpty(),"Pod must remain until age twenty-five and no bosses may appear early"));
        h.runAfterDelay(47,()->h.assertTrue(pod[0].isRemoved()&&tasks(h,pod[0].getUUID()).size()==1&&bastions(h).isEmpty(),"Actual pod age twenty-five must hand off one independent final task before spawning bosses"));
        h.runAfterDelay(48,()->{assertPair(h);h.assertTrue(tasks(h,pod[0].getUUID()).isEmpty(),"Actual final following tick must consume final task once");PlayerInteractionTests.release(h,p);h.succeed();});
    }
    public static void savedTaskAnchorIsIndependentOfTaskEntityMovement(GameTestHelper h){
        var task=task(h,true);var fixed=anchor(save(task));past(task,h);task.setPos(fixed.add(2,0,0));for(int i=0;i<21;i++)task.endTick(h.getLevel());h.assertTrue(pods(h).isEmpty()&&save(task).getIntOr("Remaining", 0)==1,"Manual END-stage advancement must preserve final remaining tick");task.endTick(h.getLevel());var pod=pods(h).getFirst();h.assertTrue(pods(h).size()==1&&pod.position().equals(BlockPos.containing(fixed.x+.5,fixed.y,fixed.z+.5).getBottomCenter()),"Request must use its persisted original anchor rather than current task coordinates");pod.discard();h.succeed();
    }
    public static void podConstructorDoesNotStartDeploymentAndFinalizeIsIdempotent(GameTestHelper h){
        var pod=pod(h,false);for(int i=0;i<26;i++)pod.tick();h.assertTrue(!save(pod).getBooleanOr("PodInitialized", false)&&save(pod).getIntOr("PodAge", 0)==0&&!pod.isRemoved(),"A raw constructor must not silently start deployment");initialize(h,pod);var first=save(pod);pod.setPos(pod.position().add(2,0,0));initialize(h,pod);var second=save(pod);
        h.assertTrue(second.getBooleanOr("PodInitialized", false)&&second.getDoubleOr("PodAnchorX", 0.0)==first.getDoubleOr("PodAnchorX", 0.0)&&second.getIntOr("PodAge", 0)==0,"Repeated finalize must not reset the original stage anchor or age");for(int i=0;i<20;i++)pod.tick();h.assertTrue(save(pod).getIntOr("PodAge", 0)==0,"Repeated entity ticks during its creation game-time must not spend END-stage age");pod.discard();h.succeed();
    }
    public static void smokeFlagChangesOnlyAtFifteenAndRemainsSet(GameTestHelper h){
        var pod=pod(h,true);past(pod,h);for(int i=0;i<14;i++)pod.endTick(h.getLevel());h.assertTrue(save(pod).getIntOr("PodAge", 0)==14&&!save(pod).getBooleanOr("PodSmokeSent", false),"Age fourteen must not claim the smoke phase");pod.endTick(h.getLevel());h.assertTrue(save(pod).getIntOr("PodAge", 0)==15&&save(pod).getBooleanOr("PodSmokeSent", false),"Age fifteen must claim smoke exactly once");pod.endTick(h.getLevel());h.assertTrue(save(pod).getIntOr("PodAge", 0)==16&&save(pod).getBooleanOr("PodSmokeSent", false),"Smoke claim must remain consumed during later stages");pod.discard();h.succeed();
    }
    public static void podHandoffCommitsOnlyOneFinalTaskAtTwentyFive(GameTestHelper h){
        var pod=pod(h,true);past(pod,h);for(int i=0;i<24;i++)pod.endTick(h.getLevel());h.assertTrue(!pod.isRemoved()&&!save(pod).getBooleanOr("PodHandedOff", false)&&tasks(h,pod.getUUID()).isEmpty(),"Pod must retain its pending handoff through age twenty-four");pod.endTick(h.getLevel());h.assertTrue(pod.isRemoved()&&save(pod).getBooleanOr("PodHandedOff", false)&&tasks(h,pod.getUUID()).size()==1,"Age twenty-five must consume handoff and submit one final task");pod.endTick(h.getLevel());h.assertTrue(tasks(h,pod.getUUID()).size()==1,"Repeated removed-pod END tick must not submit another final task");tasks(h,pod.getUUID()).forEach(Entity::discard);h.succeed();
    }
    public static void finalTaskDeploysExactlyOneGunnerAndBurnerAndConsumedReloadCannotRepeat(GameTestHelper h){
        var task=task(h,false);h.assertTrue(save(task).getIntOr("Remaining", 0)==1&&save(task).getIntOr("Mode", 0)==1,"Final stage must be a one-tick mode-one carrier");task.tick();h.assertTrue(bastions(h).isEmpty(),"A final carrier must not execute in its creation tick");past(task,h);task.endTick(h.getLevel());assertPair(h);h.assertTrue(task.isRemoved()&&save(task).getBooleanOr("Consumed", false),"Final stage must consume its lock before producing a pair");var restored=IceSquadContent.DEPLOYMENT_TASK.get().create(h.getLevel(), EntitySpawnReason.COMMAND);load(restored, save(task));restored.tick();assertPair(h);h.assertTrue(restored.isRemoved(),"Consumed task reload must expire without another pair");h.succeed();
    }
    public static void podReloadRetainsSmokeAgeAnchorAndOnlyItsRemainingHandoff(GameTestHelper h){
        var original=pod(h,true);past(original,h);for(int i=0;i<16;i++)original.endTick(h.getLevel());var tag=save(original);var originalAnchor=new Vec3(tag.getDoubleOr("PodAnchorX", 0.0),tag.getDoubleOr("PodAnchorY", 0.0),tag.getDoubleOr("PodAnchorZ", 0.0));original.discard();putUuid(tag, "UUID", UUID.randomUUID());var restored=IceSquadContent.DROP_POD.get().create(h.getLevel(), EntitySpawnReason.COMMAND);load(restored, tag);restored.setNoAi(true);restored.setNoGravity(true);h.getLevel().addFreshEntity(restored);initialize(h,restored);
        h.assertTrue(save(restored).getIntOr("PodAge", 0)==16&&save(restored).getBooleanOr("PodSmokeSent", false)&&save(restored).getDoubleOr("PodAnchorX", 0.0)==originalAnchor.x,"NBT reload and finalize must preserve age, smoke claim and original anchor");for(int i=0;i<8;i++)restored.endTick(h.getLevel());h.assertTrue(!restored.isRemoved()&&tasks(h,restored.getUUID()).isEmpty(),"Reloaded age sixteen must retain exactly nine remaining handoff stages");restored.endTick(h.getLevel());h.assertTrue(restored.isRemoved()&&tasks(h,restored.getUUID()).size()==1,"Reload must hand off once after only its remaining deadline");tasks(h,restored.getUUID()).forEach(Entity::discard);h.succeed();
    }
    public static void requestReloadRetainsThirteenRemainingTicksRatherThanRestarting(GameTestHelper h){
        var original=task(h,true);past(original,h);for(int i=0;i<9;i++)original.endTick(h.getLevel());var tag=save(original);h.assertTrue(tag.getIntOr("Remaining", 0)==13,"Saved request must retain its thirteen remaining ticks");original.discard();putUuid(tag, "UUID", UUID.randomUUID());var restored=IceSquadContent.DEPLOYMENT_TASK.get().create(h.getLevel(), EntitySpawnReason.COMMAND);load(restored, tag);h.getLevel().addFreshEntity(restored);for(int i=0;i<12;i++)restored.endTick(h.getLevel());h.assertTrue(!restored.isRemoved()&&pods(h).isEmpty()&&save(restored).getIntOr("Remaining", 0)==1,"Restored request must not complete early or restart its full delay");restored.endTick(h.getLevel());h.assertTrue(restored.isRemoved()&&pods(h).size()==1,"The remaining thirteenth stage must produce exactly one pod");pods(h).forEach(Entity::discard);h.succeed();
    }
    public static void malformedUnarmedConsumedAndWrongDimensionTasksCannotDeploy(GameTestHelper h){
        var seed=task(h,true);var baseline=save(seed);seed.discard();
        for(int mode=0;mode<5;mode++){var tag=baseline.copy();putUuid(tag, "UUID", UUID.randomUUID());if(mode==0)tag.putInt("Mode",-1);if(mode==1)tag.putDouble("AnchorX",Double.NaN);if(mode==2)tag.putString("DeploymentDimension","minecraft:the_nether");if(mode==3)tag.putBoolean("Consumed",true);if(mode==4)tag.putBoolean("Armed",false);var restored=IceSquadContent.DEPLOYMENT_TASK.get().create(h.getLevel(), EntitySpawnReason.COMMAND);load(restored, tag);restored.tick();h.assertTrue(restored.isRemoved()&&pods(h).isEmpty()&&bastions(h).isEmpty(),"Malformed, wrong-dimension and consumed tasks must expire without any deployment: "+mode);}
        h.succeed();
    }
    public static void invalidAnchorWrongDimensionAndCompletedPodsCannotCreateTailTasks(GameTestHelper h){
        var original=pod(h,true);var baseline=save(original);original.discard();for(int mode=0;mode<3;mode++){var tag=baseline.copy();putUuid(tag, "UUID", UUID.randomUUID());tag.putInt("PodAge",24);if(mode==0)tag.putDouble("PodAnchorZ",Double.NaN);if(mode==1)tag.putString("PodDimension","minecraft:the_nether");if(mode==2)tag.putBoolean("PodHandedOff",true);var restored=IceSquadContent.DROP_POD.get().create(h.getLevel(), EntitySpawnReason.COMMAND);load(restored, tag);restored.setNoAi(true);restored.setNoGravity(true);restored.tick();h.assertTrue(tasks(h,restored.getUUID()).isEmpty()&&bastions(h).isEmpty(),"Invalid or completed pod state must not recreate a final stage: "+mode);restored.discard();}h.succeed();
    }
    public static void killingPodBeforeHandoffCancelsDeploymentAndRetainsTwentyTickDeath(GameTestHelper h){
        var pod=pod(h,true);past(pod,h);for(int i=0;i<24;i++)pod.endTick(h.getLevel());h.assertTrue(pod.hurtServer((ServerLevel)pod.level(), h.getLevel().damageSources().generic(),100000)&&pod.isDeadOrDying(),"An unprotected real damage type must be able to kill the pod before handoff");pod.tick();pod.endTick(h.getLevel());h.assertTrue(tasks(h,pod.getUUID()).isEmpty()&&!save(pod).getBooleanOr("PodHandedOff", false)&&!pod.isRemoved(),"Death must cancel the unsubmitted phase rather than spawn a final task");for(int i=1;i<20;i++)pod.tickDeath();h.assertTrue(pod.isRemoved()&&pod.deathTime==20&&bastions(h).isEmpty(),"Pod death must finish after twenty death ticks without deployment");h.succeed();
    }
    public static void allFifteenSpecificImmunityPathsRemainAndGenericDamageIsAllowed(GameTestHelper h){
        var pod=pod(h,false);float health=pod.getHealth();var level=h.getLevel();var keys=List.of(DamageTypes.IN_FIRE,DamageTypes.FALL,DamageTypes.CACTUS,DamageTypes.DROWN,DamageTypes.LIGHTNING_BOLT,DamageTypes.EXPLOSION,DamageTypes.TRIDENT,DamageTypes.FALLING_ANVIL,DamageTypes.DRAGON_BREATH,DamageTypes.WITHER,DamageTypes.WITHER_SKULL);
        for(var key:keys){var source=new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key));h.assertTrue(!pod.hurtServer((ServerLevel)pod.level(), source,7)&&pod.getHealth()==health,"Each original pod damage-type immunity must remain: "+key);}
        var arrow=IceSquadContent.STRIDER_ATTACK.get().create(level, EntitySpawnReason.COMMAND);var potion=new ThrownSplashPotion(level,0,0,0,new ItemStack(Items.SPLASH_POTION));var cloud=new AreaEffectCloud(level,0,0,0);var p=player(h);
        DamageSource[] direct={level.damageSources().arrow(arrow,null),level.damageSources().playerAttack(p),level.damageSources().thrown(potion,null),level.damageSources().indirectMagic(cloud,null)};for(var source:direct)h.assertTrue(!pod.hurtServer((ServerLevel)pod.level(), source,7)&&pod.getHealth()==health,"Original direct-source pod immunities must remain");h.assertTrue(pod.hurtServer((ServerLevel)pod.level(), level.damageSources().generic(),7)&&pod.getHealth()<health,"Pod must not gain fake universal invulnerability");pod.discard();PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void cancelledInitialTaskPodAndFinalTaskJoinsRespectOneAttemptBoundaries(GameTestHelper h){
        var p=player(h);p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(IceSquadContent.THINGAMABOB.get()));Consumer<EntityJoinLevelEvent> rejectTask=e->{if(e.getLevel()==h.getLevel()&&e.getEntity() instanceof DeploymentTaskEntity&&e.getEntity().distanceToSqr(Vec3.atCenterOf(pos(h)))<16)e.setCanceled(true);};NeoForge.EVENT_BUS.addListener(rejectTask);
        try{IceSquadContent.THINGAMABOB.get().use(h.getLevel(),p,InteractionHand.MAIN_HAND);h.assertTrue(p.getMainHandItem().isEmpty()&&tasks(h,p.getUUID()).isEmpty(),"Rejected initial carrier must retain original consumed-item attempt boundary");var pod=pod(h,true);past(pod,h);for(int i=0;i<25;i++)pod.endTick(h.getLevel());h.assertTrue(pod.isRemoved()&&tasks(h,pod.getUUID()).isEmpty(),"Rejected final task must still consume the pod handoff once");}
        finally{NeoForge.EVENT_BUS.unregister(rejectTask);}
        var task=task(h,true);past(task,h);Consumer<EntityJoinLevelEvent> rejectPod=e->{if(e.getLevel()==h.getLevel()&&e.getEntity() instanceof BlizzardDropPodEntity&&e.getEntity().distanceToSqr(Vec3.atCenterOf(pos(h)))<16)e.setCanceled(true);};NeoForge.EVENT_BUS.addListener(rejectPod);
        try{for(int i=0;i<22;i++)task.endTick(h.getLevel());h.assertTrue(task.isRemoved()&&save(task).getBooleanOr("Consumed", false)&&pods(h).isEmpty(),"Rejected pod join must not cause infinite request retries");task.endTick(h.getLevel());h.assertTrue(pods(h).isEmpty(),"Consumed rejected request must remain non-reentrant");}finally{NeoForge.EVENT_BUS.unregister(rejectPod);}h.succeed();
    }
    public static void configuredPodAttributesAndTextureReloadDoNotHealSavedHealth(GameTestHelper h){
        var original=pod(h,false);original.applyConfiguredAttributes();h.assertTrue(original.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).getBaseValue()==BastionCombatConfig.POD_HEALTH.get()&&original.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR).getBaseValue()==BastionCombatConfig.POD_ARMOR.get(),"Pod must apply actual configured raw health and armor attributes");original.setHealth(original.getMaxHealth()*.37F);original.setTexture("fixture_texture");float damaged=original.getHealth();var restored=IceSquadContent.DROP_POD.get().create(h.getLevel(), EntitySpawnReason.COMMAND);load(restored, save(original));restored.applyConfiguredAttributes();h.assertTrue(Math.abs(restored.getHealth()-damaged)<1E-4&&restored.texture().equals("fixture_texture")&&restored.isPersistenceRequired(),"Loading configured attributes must preserve injured health, texture and persistence without healing");original.discard();h.succeed();
    }

    private static BlockPos pos(GameTestHelper h){return h.absolutePos(new BlockPos(10,2,8));}
    private static ServerPlayer player(GameTestHelper h){var p=PlayerInteractionTests.player(h,GameType.SURVIVAL,10.3,2.5,8.3);return p;}
    private static CompoundTag save(Entity entity){var output=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess());entity.saveWithoutId(output);return output.buildResult();}
    private static void load(Entity entity,CompoundTag tag){entity.load(TagValueInput.create(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess(),tag));}
    private static void putUuid(CompoundTag tag,String key,UUID value){tag.put(key,net.minecraft.core.UUIDUtil.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,value).result().orElseThrow());}
    private static Vec3 anchor(CompoundTag tag){return new Vec3(tag.getDoubleOr("AnchorX", 0.0),tag.getDoubleOr("AnchorY", 0.0),tag.getDoubleOr("AnchorZ", 0.0));}
    private static UUID uuid(CompoundTag tag,String key){return tag.getString(key).map(UUID::fromString).orElse(null);}
    private static List<DeploymentTaskEntity> tasks(GameTestHelper h,UUID source){return h.getLevel().getEntitiesOfClass(DeploymentTaskEntity.class,new AABB(pos(h)).inflate(6),e->{var tag=save(e);return uuid(tag, "DeploymentSource") != null&&uuid(tag, "DeploymentSource").equals(source);});}
    private static DeploymentTaskEntity onlyTask(GameTestHelper h,UUID source){var list=tasks(h,source);h.assertTrue(list.size()==1,"Exactly one real task must be accepted for this source");return list.getFirst();}
    private static DeploymentTaskEntity task(GameTestHelper h,boolean pod){var source=UUID.randomUUID();h.assertTrue(DeploymentTaskEntity.submit(h.getLevel(),Vec3.atCenterOf(pos(h)).add(-.2,0,-.2),source,pod),"Actual deployment carrier must be accepted");return onlyTask(h,source);}
    private static List<BlizzardDropPodEntity> pods(GameTestHelper h){return h.getLevel().getEntitiesOfClass(BlizzardDropPodEntity.class,new AABB(pos(h)).inflate(5));}
    private static List<BastionEntity> bastions(GameTestHelper h){return h.getLevel().getEntitiesOfClass(BastionEntity.class,new AABB(pos(h)).inflate(5));}
    private static void assertPair(GameTestHelper h){var list=bastions(h);h.assertTrue(list.size()==2&&list.stream().filter(e->e.getType()==IceSquadContent.GUNNER.get()).count()==1&&list.stream().filter(e->e.getType()==IceSquadContent.BURNER.get()).count()==1,"Final stage must produce exactly one actual Gunner and one actual Burner");}
    private static BlizzardDropPodEntity pod(GameTestHelper h,boolean initialize){var p=IceSquadContent.DROP_POD.get().create(h.getLevel(), EntitySpawnReason.COMMAND);p.setPos(Vec3.atCenterOf(pos(h)));p.setNoAi(true);p.setNoGravity(true);h.getLevel().addFreshEntity(p);if(initialize)initialize(h,p);return p;}
    private static void initialize(GameTestHelper h,BlizzardDropPodEntity p){p.finalizeSpawn(h.getLevel(),h.getLevel().getCurrentDifficultyAt(p.blockPosition()),EntitySpawnReason.MOB_SUMMONED,null);}
    private static void past(Entity entity,GameTestHelper h){entity.tick();}
    private DeploymentServerBehaviorTests(){}
    public static void register() {
        TsrGameTests.add("history_deployment_server_behavior_tests_survival_and_creative_both_hands_consume_only_the_actually_used_item", DeploymentServerBehaviorTests::survivalAndCreativeBothHandsConsumeOnlyTheActuallyUsedItem, 100, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_empty_and_unrelated_hands_are_rejected_without_submitting_or_consuming", DeploymentServerBehaviorTests::emptyAndUnrelatedHandsAreRejectedWithoutSubmittingOrConsuming, 100, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_creation_tick_repeated_task_ticks_do_not_spend_its_initial_delay", DeploymentServerBehaviorTests::creationTickRepeatedTaskTicksDoNotSpendItsInitialDelay, 10, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_actual_server_ticks_run_the_whole_twenty_two_fifteen_twenty_five_one_chain", DeploymentServerBehaviorTests::actualServerTicksRunTheWholeTwentyTwoFifteenTwentyFiveOneChain, 60, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_saved_task_anchor_is_independent_of_task_entity_movement", DeploymentServerBehaviorTests::savedTaskAnchorIsIndependentOfTaskEntityMovement, 100, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_pod_constructor_does_not_start_deployment_and_finalize_is_idempotent", DeploymentServerBehaviorTests::podConstructorDoesNotStartDeploymentAndFinalizeIsIdempotent, 100, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_smoke_flag_changes_only_at_fifteen_and_remains_set", DeploymentServerBehaviorTests::smokeFlagChangesOnlyAtFifteenAndRemainsSet, 100, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_pod_handoff_commits_only_one_final_task_at_twenty_five", DeploymentServerBehaviorTests::podHandoffCommitsOnlyOneFinalTaskAtTwentyFive, 100, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_final_task_deploys_exactly_one_gunner_and_burner_and_consumed_reload_cannot_repeat", DeploymentServerBehaviorTests::finalTaskDeploysExactlyOneGunnerAndBurnerAndConsumedReloadCannotRepeat, 100, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_pod_reload_retains_smoke_age_anchor_and_only_its_remaining_handoff", DeploymentServerBehaviorTests::podReloadRetainsSmokeAgeAnchorAndOnlyItsRemainingHandoff, 100, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_request_reload_retains_thirteen_remaining_ticks_rather_than_restarting", DeploymentServerBehaviorTests::requestReloadRetainsThirteenRemainingTicksRatherThanRestarting, 100, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_malformed_unarmed_consumed_and_wrong_dimension_tasks_cannot_deploy", DeploymentServerBehaviorTests::malformedUnarmedConsumedAndWrongDimensionTasksCannotDeploy, 100, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_invalid_anchor_wrong_dimension_and_completed_pods_cannot_create_tail_tasks", DeploymentServerBehaviorTests::invalidAnchorWrongDimensionAndCompletedPodsCannotCreateTailTasks, 100, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_killing_pod_before_handoff_cancels_deployment_and_retains_twenty_tick_death", DeploymentServerBehaviorTests::killingPodBeforeHandoffCancelsDeploymentAndRetainsTwentyTickDeath, 100, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_all_fifteen_specific_immunity_paths_remain_and_generic_damage_is_allowed", DeploymentServerBehaviorTests::allFifteenSpecificImmunityPathsRemainAndGenericDamageIsAllowed, 100, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_cancelled_initial_task_pod_and_final_task_joins_respect_one_attempt_boundaries", DeploymentServerBehaviorTests::cancelledInitialTaskPodAndFinalTaskJoinsRespectOneAttemptBoundaries, 100, "supernova_server");
        TsrGameTests.add("history_deployment_server_behavior_tests_configured_pod_attributes_and_texture_reload_do_not_heal_saved_health", DeploymentServerBehaviorTests::configuredPodAttributesAndTextureReloadDoNotHealSavedHealth, 100, "supernova_server");
    }
}
