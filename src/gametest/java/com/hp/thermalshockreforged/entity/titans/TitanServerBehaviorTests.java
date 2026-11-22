package com.hp.thermalshockreforged.entity.titans;

import com.hp.thermalshockreforged.gametest.TsrGameTests;

import java.util.*;
import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import com.hp.thermalshockreforged.gametest.PlayerInteractionTests;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
public final class TitanServerBehaviorTests {
    public static void allThreeRealAttackHooksCancelRawDamageAndDeduplicateCooldownAttempts(GameTestHelper h){
        for(var kind:SkeletalTitanEntity.Kind.values()){
            var owner=titan(h,kind);var victim=cow(h,owner.position().add(3,0,0));
            h.assertTrue(!victim.hurtServer(h.getLevel(), h.getLevel().damageSources().mobAttack(owner),6)&&victim.getHealth()==1000,"Each tagged titan must cancel raw damage and start its real delayed skill");
            var list=tasks(h,owner);
            h.assertTrue(list.size()==1&&save(h, owner).getIntOr("TitanCooldown", 0)==TitanCombatConfig.COOLDOWN[kind.ordinal()].get()&&save(h, owner).getIntOr("TitanMovementLock", 0)==20,"Each titan must own one swing and configured cooldown plus twenty movement-lock ticks");
            for(int i=0;i<4;i++)h.assertTrue(!victim.hurtServer(h.getLevel(), h.getLevel().damageSources().mobAttack(owner),6),"Cooldown attempts must remain cancelled");
            h.assertTrue(tasks(h,owner).size()==1&&victim.getHealth()==1000,"Repeated attacks must not cause direct or duplicated skill damage");
            list.forEach(Entity::discard);owner.discard();victim.discard();
        }
        h.succeed();
    }
    public static void movementLockAndConfiguredCooldownSurviveNbtAndExpireIndependently(GameTestHelper h){
        var owner=titan(h,SkeletalTitanEntity.Kind.SKELETITAN);var victim=cow(h,owner.position().add(3,0,0));owner.beginAttack(victim);
        var restored=TitanContent.SKELETITAN.get().create(h.getLevel(), EntitySpawnReason.LOAD);restored.load(TsrGameTests.roundTrip(h, owner));
        int cooldown=TitanCombatConfig.COOLDOWN[0].get();
        h.assertTrue(save(h, restored).getIntOr("TitanCooldown", 0)==cooldown&&save(h, restored).getIntOr("TitanMovementLock", 0)==20&&restored.getAttributeValue(Attributes.MOVEMENT_SPEED)==0,"Reload must reconstruct both cooldown and the transient native movement modifier");
        for(int i=0;i<19;i++)restored.baseTick();
        h.assertTrue(save(h, restored).getIntOr("TitanMovementLock", 0)==1&&restored.getAttributeValue(Attributes.MOVEMENT_SPEED)==0,"Movement must stay locked before tick twenty");
        restored.baseTick();
        h.assertTrue(save(h, restored).getIntOr("TitanMovementLock", 0)==0&&restored.getAttributeValue(Attributes.MOVEMENT_SPEED)>0,"Movement must unlock after exactly twenty base ticks");
        for(int i=20;i<cooldown-1;i++)restored.baseTick();
        h.assertTrue(save(h, restored).getIntOr("TitanCooldown", 0)==1,"Configured cooldown must retain its final pending tick");restored.baseTick();
        h.assertTrue(save(h, restored).getIntOr("TitanCooldown", 0)==0,"Cooldown must finish at its configured deadline");
        tasks(h,owner).forEach(Entity::discard);h.succeed();
    }
    public static void realServerEndSchedulerAppliesOneSwingAtTwentyAndDiscardsTask(GameTestHelper h){
        var owner=titan(h,SkeletalTitanEntity.Kind.SKELETITAN);var victim=cow(h,owner.position().add(3,0,0));owner.beginAttack(victim);var task=only(h,owner);
        double damage=owner.getAttributeValue(Attributes.ATTACK_DAMAGE)*TitanCombatConfig.SWING_MULTIPLIER[0].get();
        h.runAfterDelay(17,()->h.assertTrue(victim.getHealth()==1000&&!task.isRemoved(),"No damage may be applied before the twentieth END stage"));
        h.runAfterDelay(23,()->{h.assertTrue(Math.abs(victim.getHealth()-(1000-damage))<1E-4&&task.isRemoved()&&save(h, task).getIntOr("TitanSwingElapsed", 0)==20&&victim.hits==1,"The actual server END scheduler must apply one configured swing and remove its task");h.succeed();});
    }
    public static void targetDisplacementJustInsideAndOutsideOnePointSevenIsRespected(GameTestHelper h){
        for(double distance:new double[]{1.699,1.701}){
            var owner=titan(h,SkeletalTitanEntity.Kind.SKELETITAN);var victim=cow(h,owner.position().add(2,0,0));owner.beginAttack(victim);var task=only(h,owner);victim.setPos(victim.position().add(0,0,distance));ends(h,task,20);
            h.assertTrue((victim.getHealth()<1000)==(distance<1.7),"Displacement on each side of the original 1.7-block threshold must accept or reject the committed hit");owner.discard();victim.discard();
        }
        h.succeed();
    }
    public static void swingTracksCasterPositionAndEnforcesTheExactFourBlockRange(GameTestHelper h){
        for(double shift:new double[]{1,1.001}){
            var owner=titan(h,SkeletalTitanEntity.Kind.SKELETITAN);var victim=cow(h,owner.position().add(3,0,0));owner.beginAttack(victim);var task=only(h,owner);owner.setPos(owner.position().add(-shift,0,0));ends(h,task,20);
            h.assertTrue(task.position().equals(owner.position())&&(victim.getHealth()<1000)==(shift==1),"The last stage must follow the caster and hit exactly four blocks but not beyond");owner.discard();victim.discard();
        }
        h.succeed();
    }
    public static void allKindsSnapshotBaseDamageAndOnlyMantleAddsFiveSecondsOfFire(GameTestHelper h){
        for(var kind:SkeletalTitanEntity.Kind.values()){
            var owner=titan(h,kind);owner.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(12);var victim=cow(h,owner.position().add(3,0,0));owner.beginAttack(victim);var task=only(h,owner);owner.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1);ends(h,task,20);
            double expected=12*TitanCombatConfig.SWING_MULTIPLIER[kind.ordinal()].get();
            h.assertTrue(Math.abs(victim.getHealth()-(1000-expected))<1E-4&&victim.lastSource.getEntity()==null,"Swing damage must use the committed base-attack snapshot and remain ownerless without recursion");
            h.assertTrue(kind==SkeletalTitanEntity.Kind.MANTLE?victim.getRemainingFireTicks()==100:!victim.isOnFire(),"Only Mantletitan must ignite the victim for the original five seconds");owner.discard();victim.discard();
        }
        h.succeed();
    }
    public static void nonPlayerTaskReloadPreservesOnlyTheRemainingSwingStages(GameTestHelper h){
        var owner=titan(h,SkeletalTitanEntity.Kind.MANTLE);var victim=cow(h,owner.position().add(3,0,0));owner.beginAttack(victim);var old=only(h,owner);ends(h,old,11);var tag=saveTag(h, old);old.discard();putUuid(tag, "UUID", UUID.randomUUID());var restored=TitanContent.SWING.get().create(h.getLevel(), EntitySpawnReason.LOAD);restored.load(readTag(h, tag));
        h.assertTrue(save(h, restored).getIntOr("TitanSwingElapsed", 0)==11&&save(h, restored).getString("TitanSwingTarget").map(UUID::fromString).orElse(null).equals(victim.getUUID()),"Reload must preserve elapsed time and the non-player target identity");ends(h,restored,8);h.assertTrue(victim.getHealth()==1000&&!restored.isRemoved(),"Reload must not restart or finish the swing early");ends(h,restored,1);restored.endTick(h.getLevel());
        h.assertTrue(victim.getHealth()<1000&&victim.hits==1&&restored.isRemoved()&&victim.getRemainingFireTicks()==100,"The remaining deadline must hit and ignite exactly once");h.succeed();
    }
    public static void reloadedPlayerTaskCannotAttackEvenTheSameStillOnlinePlayerInstance(GameTestHelper h){
        var owner=titan(h,SkeletalTitanEntity.Kind.SKELETITAN);var player = PlayerInteractionTests.player(h, net.minecraft.world.level.GameType.SURVIVAL, 13.5, 2, 8.5);
        AtomicInteger incomingDamageEvents = new AtomicInteger();
        Consumer<LivingIncomingDamageEvent> hitCounter = event -> { if (event.getEntity() == player) incomingDamageEvents.incrementAndGet(); };
        NeoForge.EVENT_BUS.addListener(hitCounter);
        try {owner.beginAttack(player);var task=only(h,owner);var tag=saveTag(h, task);task.discard();putUuid(tag, "UUID", UUID.randomUUID());var restored=TitanContent.SWING.get().create(h.getLevel(), EntitySpawnReason.LOAD);restored.load(readTag(h, tag));ends(h,restored,20);h.assertTrue(incomingDamageEvents.get()==0&&player.getHealth()==20&&restored.isRemoved(),"A restored task must not attack the same still-online player instance");}
        finally{NeoForge.EVENT_BUS.unregister(hitCounter);PlayerInteractionTests.release(h, player);}
        h.succeed();
    }
    public static void malformedTaskOriginsIdsAndCompletedElapsedDisarmSavedSkills(GameTestHelper h){
        var owner=titan(h,SkeletalTitanEntity.Kind.SKELETITAN);var victim=cow(h,owner.position().add(3,0,0));owner.beginAttack(victim);var old=only(h,owner);var base=saveTag(h, old);old.discard();
        for(int mode=0;mode<3;mode++){
            var tag=base.copy();if(mode==0)tag.remove("TitanSwingCaster");if(mode==1)tag.putDouble("TitanSwingTargetX",Double.NaN);if(mode==2)tag.putInt("TitanSwingElapsed",20);
            var restored=TitanContent.SWING.get().create(h.getLevel(), EntitySpawnReason.LOAD);restored.load(readTag(h, tag));h.assertTrue(!save(h, restored).getBooleanOr("TitanSwingArmed", false),"Missing identity, non-finite anchor or completed elapsed must not rearm a saved skill");restored.endTick(h.getLevel());restored.tick();h.assertTrue(restored.isRemoved()&&victim.hits==0,"Malformed tasks must safely expire without damage");
        }
        h.succeed();
    }
    public static void cancelledSwingJoinLeavesCasterReadyWithoutCooldownOrMovementLock(GameTestHelper h){
        var owner=titan(h,SkeletalTitanEntity.Kind.SKELETITAN);var victim=cow(h,owner.position().add(3,0,0));
        Consumer<EntityJoinLevelEvent> veto=e->{if(e.getLevel()==h.getLevel()&&e.getEntity() instanceof TitanSwingEntity&&save(h, e.getEntity()).getString("TitanSwingCaster").map(UUID::fromString).orElse(null).equals(owner.getUUID()))e.setCanceled(true);};
        NeoForge.EVENT_BUS.addListener(veto);try{owner.beginAttack(victim);h.assertTrue(tasks(h,owner).isEmpty()&&save(h, owner).getIntOr("TitanCooldown", 0)==0&&save(h, owner).getIntOr("TitanMovementLock", 0)==0&&owner.getAttributeValue(Attributes.MOVEMENT_SPEED)>0,"A rejected skill carrier must not consume cooldown or leave the caster immobile");}finally{NeoForge.EVENT_BUS.unregister(veto);}
        h.succeed();
    }
    public static void selfDeadAndOtherDimensionTargetsNeverSpendTitanCooldown(GameTestHelper h){
        var owner=titan(h,SkeletalTitanEntity.Kind.SKELETITAN);var dead=cow(h,owner.position().add(3,0,0));dead.setHealth(0);owner.beginAttack(owner);owner.beginAttack(dead);
        var nether=h.getLevel().getServer().getLevel(Level.NETHER);h.assertTrue(nether!=null,"Nether must exist");owner.beginAttack(EntityType.COW.create(nether, EntitySpawnReason.COMMAND));
        h.assertTrue(tasks(h,owner).isEmpty()&&save(h, owner).getIntOr("TitanCooldown", 0)==0,"Self, dead and cross-level targets must not spend cooldown or submit a task");h.succeed();
    }
    public static void removingCasterPreservesItsAlreadyCommittedLegalTail(GameTestHelper h){
        var owner=titan(h,SkeletalTitanEntity.Kind.SKELETITAN);var victim=cow(h,owner.position().add(3,0,0));owner.beginAttack(victim);var task=only(h,owner);owner.discard();ends(h,task,20);
        h.assertTrue(victim.getHealth()<1000&&victim.hits==1&&task.isRemoved(),"The original already committed tail must still execute from its last position when the caster is removed");h.succeed();
    }
    public static void findingCasterInAnotherDimensionCancelsTheOldWorldHit(GameTestHelper h){
        var nether=h.getLevel().getServer().getLevel(Level.NETHER);h.assertTrue(nether!=null,"The server must expose its native Nether");boolean addedForce=nether.setChunkForced(0,0,true);
        System.out.println("TITAN_CHUNK_START addedVanillaForce="+addedForce+" "+netherState(nether));
        nether.getChunkSource().addTicketWithRadius(net.minecraft.server.level.TicketType.FORCED, new net.minecraft.world.level.ChunkPos(0,0), 2);
        awaitNether(h,nether,addedForce,400);
    }

    private static void awaitNether(GameTestHelper h,ServerLevel nether,boolean addedForce,int remaining){
        var destination=new BlockPos(0,240,0);
        if(!nether.isPositionEntityTicking(destination)){
            if(remaining%100==0)System.out.println("TITAN_CHUNK_WAIT remaining="+remaining+" "+netherState(nether));
            if(remaining>0){h.runAfterDelay(1,()->awaitNether(h,nether,addedForce,remaining-1));return;}
            releaseNether(nether,addedForce);
            h.fail("Native Nether destination did not become entity-ticking within the fixture deadline: "+netherState(nether));return;
        }
        Entity moved=null;
        try{
            var owner=titan(h,SkeletalTitanEntity.Kind.SKELETITAN);var victim=cow(h,owner.position().add(3,0,0));owner.beginAttack(victim);var task=only(h,owner);var id=owner.getUUID();
            moved=owner.teleport(new net.minecraft.world.level.portal.TeleportTransition(nether,new Vec3(.5,240,.5),Vec3.ZERO,0,0,net.minecraft.world.level.portal.TeleportTransition.DO_NOTHING));
            System.out.println("TITAN_DIMENSION_FIXTURE ticking="+nether.isPositionEntityTicking(destination)+" moved="+(moved!=null)+" removed="+(moved!=null&&moved.isRemoved())+" uuidVisible="+(nether.getEntity(id)==moved)+" sourceRemoved="+owner.isRemoved());
            h.assertTrue(moved!=null&&moved.level()==nether&&moved.getUUID().equals(id)&&owner.isRemoved()&&nether.getEntity(id)==moved,"Native transfer must preserve UUID and actually expose the moved caster in the destination level");
            ends(h,task,20);h.assertTrue(task.isRemoved()&&victim.hits==0,"A caster known to have changed dimension must not hit from stale coordinates");h.succeed();
        }finally{if(moved!=null)moved.discard();releaseNether(nether,addedForce);}
    }

    private static String netherState(ServerLevel nether){return "time="+nether.getGameTime()+" hasChunk="+nether.getChunkSource().hasChunk(0,0)+" chunkNow="+(nether.getChunkSource().getChunkNow(0,0)!=null)+" entitiesLoaded="+nether.areEntitiesLoaded(0L)+" entityTicking="+nether.isPositionEntityTicking(new BlockPos(0,240,0));}
    private static void releaseNether(ServerLevel nether,boolean addedForce){nether.getChunkSource().removeTicketWithRadius(net.minecraft.server.level.TicketType.FORCED, new net.minecraft.world.level.ChunkPos(0,0), 2);if(addedForce)nether.setChunkForced(0,0,false);}
    public static void mantleStoneInitialSpawnReplacesSkeletitanWithOneRealMantletitan(GameTestHelper h){
        var owner=titan(h,SkeletalTitanEntity.Kind.SKELETITAN);var floor=BlockPos.containing(owner.getX(),owner.getY()-1,owner.getZ());var block=BuiltInRegistries.BLOCK.get(TitanContentId.mantlestone()).orElseThrow().value();h.getLevel().setBlock(floor,block.defaultBlockState(),2);
        owner.finalizeSpawn(h.getLevel(),h.getLevel().getCurrentDifficultyAt(owner.blockPosition()),EntitySpawnReason.NATURAL,null);
        var replacements=h.getLevel().getEntitiesOfClass(SkeletalTitanEntity.class,new AABB(owner.blockPosition()).inflate(3),e->e.kind()==SkeletalTitanEntity.Kind.MANTLE);
        h.assertTrue(owner.isRemoved()&&replacements.size()==1&&save(h, owner).getBooleanOr("TitanSpawnVariantChecked", false),"Mantlestone must deterministically replace the initial source with one actual mantle variant");h.succeed();
    }
    public static void cancelledMantleVariantJoinRetainsTheOriginalSkeletitan(GameTestHelper h){
        var owner=titan(h,SkeletalTitanEntity.Kind.SKELETITAN);var floor=BlockPos.containing(owner.getX(),owner.getY()-1,owner.getZ());h.getLevel().setBlock(floor,BuiltInRegistries.BLOCK.get(TitanContentId.mantlestone()).orElseThrow().value().defaultBlockState(),2);
        Consumer<EntityJoinLevelEvent> veto=e->{if(e.getLevel()==h.getLevel()&&e.getEntity() instanceof SkeletalTitanEntity titan&&titan.kind()==SkeletalTitanEntity.Kind.MANTLE&&titan.distanceToSqr(owner)<4)e.setCanceled(true);};NeoForge.EVENT_BUS.addListener(veto);
        try{owner.finalizeSpawn(h.getLevel(),h.getLevel().getCurrentDifficultyAt(owner.blockPosition()),EntitySpawnReason.NATURAL,null);h.assertTrue(!owner.isRemoved()&&h.getLevel().getEntity(owner.getUUID())==owner,"A cancelled replacement join must preserve the real source instead of deleting both mobs");}finally{NeoForge.EVENT_BUS.unregister(veto);}h.succeed();
    }
    public static void frostOnlyDrowningImmunityAndRealUnderwaterBuffsArePreserved(GameTestHelper h){
        var frost=titan(h,SkeletalTitanEntity.Kind.FROST);var origin=frost.blockPosition();
        for(var p:BlockPos.betweenClosed(origin.offset(-2,0,-2),origin.offset(2,5,2)))h.getLevel().setBlock(p,Blocks.WATER.defaultBlockState(),2);
        for(int i=0;i<3;i++)frost.baseTick();
        h.assertTrue(frost.isUnderWater(),"The underwater fixture must cover the titan's actual eyes and body");
        var speed=frost.getEffect(MobEffects.SPEED);var dolphin=frost.getEffect(MobEffects.DOLPHINS_GRACE);
        h.assertTrue(speed!=null&&speed.getDuration()==10&&speed.getAmplifier()==2&&dolphin!=null&&dolphin.getDuration()==10&&dolphin.getAmplifier()==2,"Frost must refresh both genuine ten-tick level-three underwater buffs");
        float health=frost.getHealth();h.assertTrue(!frost.hurtServer(h.getLevel(), h.getLevel().damageSources().drown(),2)&&frost.getHealth()==health&&frost.hurtServer(h.getLevel(), h.getLevel().damageSources().generic(),2),"Only drowning must be immune while ordinary frost damage remains possible");
        var ordinary=titan(h,SkeletalTitanEntity.Kind.SKELETITAN);h.assertTrue(ordinary.hurtServer(h.getLevel(), h.getLevel().damageSources().drown(),2),"Drowning immunity must not leak to ordinary skeletitans");h.succeed();
    }

    private static Vec3 origin(GameTestHelper h){return h.absolutePos(new BlockPos(10,2,8)).getCenter();}
    private static SkeletalTitanEntity titan(GameTestHelper h,SkeletalTitanEntity.Kind kind){var type=switch(kind){case SKELETITAN->TitanContent.SKELETITAN.get();case FROST->TitanContent.FROST_TITAN.get();case MANTLE->TitanContent.MANTLETITAN.get();};var e=type.create(h.getLevel(), EntitySpawnReason.COMMAND);e.setPos(origin(h));e.setNoAi(true);e.setNoGravity(true);h.getLevel().addFreshEntity(e);return e;}
    private static TestCow cow(GameTestHelper h,Vec3 pos){var c=new TestCow(h.getLevel());c.setPos(pos);c.setNoAi(true);c.setNoGravity(true);c.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1000);c.setHealth(1000);h.getLevel().addFreshEntity(c);return c;}
    private static ValueInput save(GameTestHelper h, Entity e) { return TsrGameTests.roundTrip(h, e); }
    private static CompoundTag saveTag(GameTestHelper h, Entity e) { TagValueOutput o = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, h.getLevel().registryAccess()); e.saveWithoutId(o); return o.buildResult(); }
    private static ValueInput readTag(GameTestHelper h, CompoundTag tag) { return TagValueInput.create(ProblemReporter.DISCARDING, h.getLevel().registryAccess(), tag); }
    private static UUID uuid(ValueInput input, String key) { return input.read(key, UUIDUtil.CODEC).orElse(null); }
    private static void putUuid(CompoundTag tag, String key, UUID value) { tag.put(key, UUIDUtil.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, value).result().orElseThrow()); }
    private static List<TitanSwingEntity> tasks(GameTestHelper h,SkeletalTitanEntity caster){var all=h.getLevel().getEntitiesOfClass(TitanSwingEntity.class,new AABB(caster.blockPosition()).inflate(8));return all.stream().filter(e->save(h,e).getString("TitanSwingCaster").map(UUID::fromString).map(caster.getUUID()::equals).orElse(false)).toList();}
    private static TitanSwingEntity only(GameTestHelper h,SkeletalTitanEntity caster){var tasks=tasks(h,caster);h.assertTrue(tasks.size()==1,"The caster must own exactly one swing");return tasks.getFirst();}
    private static void ends(GameTestHelper h,TitanSwingEntity task,int count){for(int i=0;i<count;i++)task.endTick(h.getLevel());}
    private static final class TestCow extends Cow{private int hits;private DamageSource lastSource;TestCow(ServerLevel level){super(EntityType.COW,level);}@Override public boolean hurtServer(ServerLevel level, DamageSource source, float amount) { hits++; lastSource=source; return super.hurtServer(level, source, amount); }}
    private static final class TitanContentId{private static net.minecraft.resources.Identifier mantlestone(){return net.minecraft.resources.Identifier.fromNamespaceAndPath("thermal_shock_reforged","mantlestone");}}
    public static void register() {
        TsrGameTests.add("history_titan_server_behavior_tests_all_three_real_attack_hooks_cancel_raw_damage_and_deduplicate_cooldown_attempts", TitanServerBehaviorTests::allThreeRealAttackHooksCancelRawDamageAndDeduplicateCooldownAttempts, 100, "supernova_server");
        TsrGameTests.add("history_titan_server_behavior_tests_movement_lock_and_configured_cooldown_survive_nbt_and_expire_independently", TitanServerBehaviorTests::movementLockAndConfiguredCooldownSurviveNbtAndExpireIndependently, 100, "supernova_server");
        TsrGameTests.add("history_titan_server_behavior_tests_real_server_end_scheduler_applies_one_swing_at_twenty_and_discards_task", TitanServerBehaviorTests::realServerEndSchedulerAppliesOneSwingAtTwentyAndDiscardsTask, 30, "supernova_server");
        TsrGameTests.add("history_titan_server_behavior_tests_target_displacement_just_inside_and_outside_one_point_seven_is_respected", TitanServerBehaviorTests::targetDisplacementJustInsideAndOutsideOnePointSevenIsRespected, 100, "supernova_server");
        TsrGameTests.add("history_titan_server_behavior_tests_swing_tracks_caster_position_and_enforces_the_exact_four_block_range", TitanServerBehaviorTests::swingTracksCasterPositionAndEnforcesTheExactFourBlockRange, 100, "supernova_server");
        TsrGameTests.add("history_titan_server_behavior_tests_all_kinds_snapshot_base_damage_and_only_mantle_adds_five_seconds_of_fire", TitanServerBehaviorTests::allKindsSnapshotBaseDamageAndOnlyMantleAddsFiveSecondsOfFire, 100, "supernova_server");
        TsrGameTests.add("history_titan_server_behavior_tests_non_player_task_reload_preserves_only_the_remaining_swing_stages", TitanServerBehaviorTests::nonPlayerTaskReloadPreservesOnlyTheRemainingSwingStages, 100, "supernova_server");
        TsrGameTests.add("history_titan_server_behavior_tests_reloaded_player_task_cannot_attack_even_the_same_still_online_player_instance", TitanServerBehaviorTests::reloadedPlayerTaskCannotAttackEvenTheSameStillOnlinePlayerInstance, 100, "supernova_server");
        TsrGameTests.add("history_titan_server_behavior_tests_malformed_task_origins_ids_and_completed_elapsed_disarm_saved_skills", TitanServerBehaviorTests::malformedTaskOriginsIdsAndCompletedElapsedDisarmSavedSkills, 100, "supernova_server");
        TsrGameTests.add("history_titan_server_behavior_tests_cancelled_swing_join_leaves_caster_ready_without_cooldown_or_movement_lock", TitanServerBehaviorTests::cancelledSwingJoinLeavesCasterReadyWithoutCooldownOrMovementLock, 100, "supernova_server");
        TsrGameTests.add("history_titan_server_behavior_tests_self_dead_and_other_dimension_targets_never_spend_titan_cooldown", TitanServerBehaviorTests::selfDeadAndOtherDimensionTargetsNeverSpendTitanCooldown, 100, "supernova_server");
        TsrGameTests.add("history_titan_server_behavior_tests_removing_caster_preserves_its_already_committed_legal_tail", TitanServerBehaviorTests::removingCasterPreservesItsAlreadyCommittedLegalTail, 100, "supernova_server");
        TsrGameTests.addDedicated("history_titan_server_behavior_tests_finding_caster_in_another_dimension_cancels_the_old_world_hit", TitanServerBehaviorTests::findingCasterInAnotherDimensionCancelsTheOldWorldHit, 440, "supernova_server");
        TsrGameTests.add("history_titan_server_behavior_tests_mantle_stone_initial_spawn_replaces_skeletitan_with_one_real_mantletitan", TitanServerBehaviorTests::mantleStoneInitialSpawnReplacesSkeletitanWithOneRealMantletitan, 100, "supernova_server");
        TsrGameTests.add("history_titan_server_behavior_tests_cancelled_mantle_variant_join_retains_the_original_skeletitan", TitanServerBehaviorTests::cancelledMantleVariantJoinRetainsTheOriginalSkeletitan, 100, "supernova_server");
        TsrGameTests.add("history_titan_server_behavior_tests_frost_only_drowning_immunity_and_real_underwater_buffs_are_preserved", TitanServerBehaviorTests::frostOnlyDrowningImmunityAndRealUnderwaterBuffsArePreserved, 100, "supernova_server");
    }
    private TitanServerBehaviorTests(){}
}
