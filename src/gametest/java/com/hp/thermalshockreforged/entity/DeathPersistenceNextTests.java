package com.hp.thermalshockreforged.entity;
import net.minecraft.world.entity.EntitySpawnReason;
import java.util.*;
import java.util.function.Consumer;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.entity.attack.BossDeathEffectEntity;
import com.hp.thermalshockreforged.entity.gravers.*;
import com.hp.thermalshockreforged.entity.huntress.*;
import com.hp.thermalshockreforged.entity.maiden.*;
import com.hp.thermalshockreforged.entity.projectile.BloodBulletEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.gametest.framework.GameTestHelper;
public final class DeathPersistenceNextTests {
    private static final Map<UUID,List<Shot>> SHOTS=new java.util.concurrent.ConcurrentHashMap<>();
    static{NeoForge.EVENT_BUS.addListener(DeathPersistenceNextTests::join);}
    public static void huntressDeathAwardsSevenHundredXpAtTwentyTicks(GameTestHelper h){h.getLevel().getGameRules().set(net.minecraft.world.level.gamerules.GameRules.MOB_DROPS,true,h.getLevel().getServer());var e=huntress(h,8,2,8);var p=testPlayer(h,e.position());h.assertTrue(e.hurtServer(h.getLevel(),h.getLevel().damageSources().playerAttack(p),1000),"Lethal player damage must start Huntress death");h.runAfterDelay(19,()->h.assertTrue(xp(h,e)==0&&!e.isRemoved(),"No XP or removal before death tick 20"));h.runAfterDelay(21,()->{h.assertTrue(e.isRemoved()&&xp(h,e)==700,"Huntress must remove and spawn exactly 700 XP at death tick 20");h.succeed();});}
    public static void cancelledHuntressDeathDoesNotAwardExperience(GameTestHelper h){var e=huntress(h,8,2,8);var veto=(Consumer<LivingDeathEvent>)(x->{if(x.getEntity()==e)x.setCanceled(true);});NeoForge.EVENT_BUS.addListener(veto);try{e.setHealth(0);e.die(h.getLevel().damageSources().generic());h.runAfterDelay(22,()->{String state="dead="+e.isDeadOrDying()+" removed="+e.isRemoved()+" xp="+xp(h,e);System.out.println("HUNTRESS_CANCEL_DEATH "+state);h.assertTrue(xp(h,e)==0,"Canceled LivingDeathEvent must not enter delayed XP removal: "+state);h.succeed();});}finally{h.runAfterDelay(24,()->NeoForge.EVENT_BUS.unregister(veto));}}
    public static void repeatedHuntressDeathCallbacksDoNotDuplicateExperience(GameTestHelper h){var e=huntress(h,8,2,8);var p=testPlayer(h,e.position());e.hurtServer(h.getLevel(),h.getLevel().damageSources().playerAttack(p),1000);e.die(h.getLevel().damageSources().generic());h.runAfterDelay(22,()->{h.assertTrue(xp(h,e)==700,"Repeated die invocation must retain one 700 XP payout");h.succeed();});}
    public static void huntressBossEventTracksObserverAndHealthFraction(GameTestHelper h){try{var e=huntress(h,8,2,8);var p=testPlayer(h,Vec3.ZERO);var field=HuntressBossEntity.class.getDeclaredField("bossInfo");field.setAccessible(true);var bar=(net.minecraft.server.level.ServerBossEvent)field.get(e);e.startSeenByPlayer(p);e.setHealth(e.getMaxHealth()*.4F);for(int i=0;i<3;i++)e.tick();var method=HuntressBossEntity.class.getDeclaredMethod("customServerAiStep",net.minecraft.server.level.ServerLevel.class);method.setAccessible(true);method.invoke(e,h.getLevel());h.assertTrue(bar.getPlayers().contains(p)&&Math.abs(bar.getProgress()-.4F)<.01,"Server boss event must track the observer and current health fraction");e.stopSeenByPlayer(p);h.assertTrue(!bar.getPlayers().contains(p),"Observer removal must clear server boss event membership");com.hp.thermalshockreforged.gametest.PlayerInteractionTests.release(h,p);h.succeed();}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}}
    public static void huntressRestoredTaskResumesAtNextUncommittedSlamStage(GameTestHelper h){var e=huntress(h,8,2,8);e.getRandom().setSeed(slamSeed());var target=new HitCow(h);target.setNoAi(true);target.setNoGravity(true);target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);target.setHealth(100);target.setPos(pos(h,12,2,8));h.getLevel().addFreshEntity(target);e.beginAttack(target);var old=tasks(h,e);h.assertTrue(old.size()==1,"Slam fixture must commit one task");var task=old.getFirst();for(int i=0;i<43;i++)task.endTick(h.getLevel());var saved=save(task);int before=target.hitCount;task.discard();saved.put("UUID",net.minecraft.core.UUIDUtil.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,UUID.randomUUID()).result().orElseThrow());var copy=HuntressContent.TASK.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);restore(h, copy, saved);h.getLevel().addFreshEntity(copy);copy.endTick(h.getLevel());h.assertTrue(copy.isRemoved()&&target.getHealth()<100&&Math.abs(target.getHealth()-90)<.01&&target.hitCount-before==1,"In-memory task NBT at stage 43 must execute stage 44 once, then finish");h.succeed();}
    public static void maidenDeathAwardsFiftyXpAtSixtyTicks(GameTestHelper h){var level=h.getLevel();boolean old=level.getGameRules().get(net.minecraft.world.level.gamerules.GameRules.MOB_DROPS);level.getGameRules().set(net.minecraft.world.level.gamerules.GameRules.MOB_DROPS,true,level.getServer());var e=maiden(h,8,2,8);var p=testPlayer(h,e.position());e.setLastHurtByPlayer(p,200);var drops=new java.util.ArrayList<Integer>();java.util.function.Consumer<net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent> listener=x->{if(x.getEntity()==e)drops.add(x.getDroppedExperience());};NeoForge.EVENT_BUS.addListener(listener);e.setHealth(0);e.die(level.damageSources().generic());System.out.println("MAIDEN_XP_SETUP reward="+e.getExperienceReward(level,p)+" dropRule="+level.getGameRules().get(net.minecraft.world.level.gamerules.GameRules.MOB_DROPS)+" should="+e.shouldDropExperience());h.runAfterDelay(59,()->h.assertTrue(xp(h,e)==0&&!e.isRemoved()&&drops.isEmpty(),"Maiden must not drop XP before its 60 tick death time"));h.runAfterDelay(61,()->{String state="removed="+e.isRemoved()+" deathTime="+e.deathTime+" xpOrbs="+xp(h,e)+" xpEvents="+drops+" pending="+save(e).getBooleanOr("AnimatedExperiencePending", false);System.out.println("MAIDEN_XP "+state);NeoForge.EVENT_BUS.unregister(listener);level.getGameRules().set(net.minecraft.world.level.gamerules.GameRules.MOB_DROPS,old,level.getServer());h.assertTrue(e.isRemoved()&&xp(h,e)==50&&drops.equals(List.of(50)),"Maiden death must spawn and report exactly 50 XP after 60 ticks: "+state);h.succeed();});}    public static void cancelledMaidenDeathDoesNotSubmitExplosionTail(GameTestHelper h){var e=maiden(h,8,2,8);var veto=(Consumer<LivingDeathEvent>)(x->{if(x.getEntity()==e)x.setCanceled(true);});NeoForge.EVENT_BUS.addListener(veto);e.setHealth(0);e.die(h.getLevel().damageSources().generic());h.runAfterDelay(60,()->{String state="removed="+e.isRemoved()+" visuals="+visuals(h,e).size()+" xp="+xp(h,e)+" deathVisual="+save(e).getBooleanOr("MaidenDeathVisual", false);System.out.println("MAIDEN_CANCEL_DEATH "+state);NeoForge.EVENT_BUS.unregister(veto);h.assertTrue(visuals(h,e).isEmpty()&&xp(h,e)==0&&!save(e).getBooleanOr("MaidenDeathVisual", false),"Canceled death must not submit an explosion tail or XP: "+state);h.succeed();});}    public static void maidenCommittedDeathTailEmitsFifteenServerStagesAndEnds(GameTestHelper h){var e=maiden(h,8,2,8);e.setHealth(0);e.die(h.getLevel().damageSources().generic());var fx=visuals(h,e);h.assertTrue(fx.size()==1&&save(fx.getFirst()).getIntOr("Remaining", 0)==14,"Committed death emits stage one immediately and persists fourteen remaining");h.runAfterDelay(55,()->h.assertTrue(!fx.getFirst().isRemoved(),"Death visual must remain through its last four-tick interval"));h.runAfterDelay(59,()->{h.assertTrue(fx.getFirst().isRemoved(),"Fifteenth server-side effect stage must finish after fourteen four-tick intervals");h.succeed();});}
    public static void repeatedMaidenDeathCallsDoNotSubmitDuplicateTail(GameTestHelper h){var e=maiden(h,8,2,8);e.setHealth(0);e.die(h.getLevel().damageSources().generic());e.die(h.getLevel().damageSources().generic());h.assertTrue(visuals(h,e).size()==1,"Repeated die callbacks must submit one death visual task");h.succeed();}
    public static void maidenRestoredVolleyResumesAtStageTwentyNineOnlyOnce(GameTestHelper h){var e=maiden(h,8,2,8);e.getRandom().setSeed(maidenSeed());var target=cow(h,11,2,8,100);e.beginAttack(target);var old=volleys(h,e);h.assertTrue(old.size()==1,"Volley fixture must commit one task");var task=old.getFirst();for(int i=0;i<28;i++)task.endTick(h.getLevel());var tag=save(task);task.discard();tag.put("UUID",net.minecraft.core.UUIDUtil.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,UUID.randomUUID()).result().orElseThrow());var restored=MaidenContent.VOLLEY.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);restore(h, restored, tag);h.getLevel().addFreshEntity(restored);restored.endTick(h.getLevel());restored.endTick(h.getLevel());var shots=SHOTS.getOrDefault(e.getUUID(),List.of());h.assertTrue(restored.isRemoved()&&shots.size()==8&&shots.stream().allMatch(s->s.owner.equals(e.getUUID())),"In-memory NBT at stage 28 must emit exactly one eight-arrow wave at stage 29");h.succeed();}
    public static void assimilatorInfectionEntersThroughRealDamageMixin(GameTestHelper h){var source=graver(h,GraverContent.ASSIMILATOR.get(),8,2,8);var patient=cow(h,9,2,8,100);h.assertTrue(patient.hurtServer(h.getLevel(),h.getLevel().damageSources().mobAttack(source),1)&&patient.hasEffect(StatusContent.INFECTED),"Tagged Assimilator real damage must enter InfectionStatus through LivingEntity.hurt mixin");h.succeed();}
    public static void removedInfectionSchedulesOnePersistentConversionTask(GameTestHelper h){var source=graver(h,GraverContent.ASSIMILATOR.get(),8,2,8);var patient=cow(h,9,2,8,100);patient.hurtServer(h.getLevel(),h.getLevel().damageSources().mobAttack(source),1);patient.removeEffect(StatusContent.INFECTED);var state=patient.getPersistentData().getCompoundOrEmpty("thermal_shock_reforged:infection_tasks");var tasks=state.getListOrEmpty("Tasks");h.assertTrue(tasks.size()==1&&tasks.getCompoundOrEmpty(0).getIntOr("Remaining", 0)==20&&state.getIntOr("LockRemaining", 0)==30,"Real effect removal must snapshot one 20 tick conversion and 30 tick movement lock");h.succeed();}
    public static void infectionConversionAppliesEffectAndBurstDamageAtDeadline(GameTestHelper h){var source=graver(h,GraverContent.ASSIMILATOR.get(),8,2,8);var patient=cow(h,9,2,8,100);var victim=cow(h,10,2,8,100);patient.hurtServer(h.getLevel(),h.getLevel().damageSources().mobAttack(source),1);patient.removeEffect(StatusContent.INFECTED);h.runAfterDelay(22,()->{h.assertTrue(patient.hasEffect(StatusContent.INFECTED)&&victim.getHealth()<100,"After twenty server ticks the infected entity must burst and infect nearby target");h.succeed();});}
    public static void infectionRemovalCannotQueueDuplicateTaskSameTick(GameTestHelper h){var source=graver(h,GraverContent.ASSIMILATOR.get(),8,2,8);var patient=cow(h,9,2,8,100);patient.hurtServer(h.getLevel(),h.getLevel().damageSources().mobAttack(source),1);patient.removeEffect(StatusContent.INFECTED);var before=patient.getPersistentData().getCompoundOrEmpty("thermal_shock_reforged:infection_tasks").getListOrEmpty("Tasks").size();com.hp.thermalshockreforged.content.status.InfectionStatus.onRemoved(patient);var after=patient.getPersistentData().getCompoundOrEmpty("thermal_shock_reforged:infection_tasks").getListOrEmpty("Tasks").size();h.assertTrue(before==1&&after==1,"Repeated effect-removal callback in one tick must remain idempotent");h.succeed();}
    private static final class HitCow extends Cow{int hitCount;HitCow(GameTestHelper h){super(EntityType.COW,h.getLevel());}@Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level,net.minecraft.world.damagesource.DamageSource s,float n){hitCount++;return super.hurtServer(level,s,n);}}
    private static long slamSeed(){for(long i=0;i<100000;i++)if(net.minecraft.util.RandomSource.create(i).nextDouble()>=HuntressConfig.TRIPLE_CHANCE.get())return i;throw new IllegalStateException();}
    private static long maidenSeed(){double chance=MaidenConfig.SINGLE_CHANCE.get();for(long i=0;i<100000;i++)if(net.minecraft.util.RandomSource.create(i).nextDouble()<chance)return i;throw new IllegalStateException();}
    private static net.minecraft.server.level.ServerPlayer testPlayer(GameTestHelper helper, Vec3 at) {
        var player = new net.minecraft.server.level.ServerPlayer(helper.getLevel().getServer(), helper.getLevel(), new com.mojang.authlib.GameProfile(UUID.randomUUID(), "death-test"), net.minecraft.server.level.ClientInformation.createDefault());
        com.hp.thermalshockreforged.gametest.PlayerInteractionTests.connect(helper, player, GameType.SURVIVAL, 8, 2, 8);
        player.snapTo(at.x, at.y, at.z, 0, 0);
        helper.testInfo.addListener(new net.minecraft.gametest.framework.GameTestListener() {
            @Override public void testStructureLoaded(net.minecraft.gametest.framework.GameTestInfo test) { }
            @Override public void testPassed(net.minecraft.gametest.framework.GameTestInfo test, net.minecraft.gametest.framework.GameTestRunner runner) { release(); }
            @Override public void testFailed(net.minecraft.gametest.framework.GameTestInfo test, net.minecraft.gametest.framework.GameTestRunner runner) { release(); }
            @Override public void testAddedForRerun(net.minecraft.gametest.framework.GameTestInfo original, net.minecraft.gametest.framework.GameTestInfo copy, net.minecraft.gametest.framework.GameTestRunner runner) { }
            private void release() {
                if (helper.getLevel().getServer().getPlayerList().getPlayer(player.getUUID()) == player) com.hp.thermalshockreforged.gametest.PlayerInteractionTests.release(helper, player);
            }
        });
        return player;
    }
    private static HuntressBossEntity huntress(GameTestHelper h,int x,int y,int z){var e=HuntressContent.BOSS.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);e.setNoAi(true);e.setPos(pos(h,x,y,z));h.getLevel().addFreshEntity(e);return e;}
    private static MaidenEntity maiden(GameTestHelper h,int x,int y,int z){var e=MaidenContent.MAIDEN.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);e.setNoAi(true);e.setPos(pos(h,x,y,z));h.getLevel().addFreshEntity(e);return e;}
    private static <T extends GraverFamilyEntity>T graver(GameTestHelper h,EntityType<T> type,int x,int y,int z){var e=type.create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);e.setNoAi(true);e.setNoGravity(true);e.setPos(pos(h,x,y,z));h.getLevel().addFreshEntity(e);return e;}
    private static Cow cow(GameTestHelper h,int x,int y,int z,float hp){var e=EntityType.COW.create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);e.setNoAi(true);e.setNoGravity(true);e.getAttribute(Attributes.MAX_HEALTH).setBaseValue(hp);e.setHealth(hp);e.setPos(pos(h,x,y,z));h.getLevel().addFreshEntity(e);return e;}
    private static Vec3 pos(GameTestHelper h,int x,int y,int z){return Vec3.atCenterOf(h.absolutePos(new BlockPos(x,y,z)));}
    private static CompoundTag save(Entity e){return snapshot(e);}
    private static int xp(GameTestHelper h, Entity entity) {
        int total = 0;
        for (var orb : h.getLevel().getEntitiesOfClass(ExperienceOrb.class, new AABB(entity.blockPosition()).inflate(16))) {
            int count = snapshot(orb).getIntOr("Count", 1);
            h.assertTrue(count > 0, "Experience orb multiplicity must remain positive");
            if (count > 1) com.hp.thermalshockreforged.ThermalShockReforged.LOGGER.debug("Death XP probe found merged orb: entity={}, orb={}, value={}, count={}", entity.getUUID(), orb.getUUID(), orb.getValue(), count);
            total += orb.getValue() * count;
        }
        return total;
    }
    private static List<BossDeathEffectEntity> visuals(GameTestHelper h,Entity e){return h.getLevel().getEntitiesOfClass(BossDeathEffectEntity.class,new AABB(e.blockPosition()).inflate(16));}
    private static List<HuntressTaskEntity> tasks(GameTestHelper h,HuntressBossEntity e){return h.getLevel().getEntitiesOfClass(HuntressTaskEntity.class,new AABB(e.blockPosition()).inflate(64),t->uuid(save(t), "HuntressCaster") != null&&uuid(save(t), "HuntressCaster").equals(e.getUUID()));}
    private static List<MaidenVolleyEntity> volleys(GameTestHelper h,MaidenEntity e){return h.getLevel().getEntitiesOfClass(MaidenVolleyEntity.class,new AABB(e.blockPosition()).inflate(64),t->uuid(save(t), "MaidenCaster") != null&&uuid(save(t), "MaidenCaster").equals(e.getUUID()));}
    private static void join(EntityJoinLevelEvent e){if(e.getEntity() instanceof BloodBulletEntity b&&e.getLevel() instanceof net.minecraft.server.level.ServerLevel l){var tag=save(b);if(uuid(tag, "Owner") != null)SHOTS.computeIfAbsent(uuid(tag, "Owner"),k->new java.util.concurrent.CopyOnWriteArrayList<>()).add(new Shot(uuid(tag, "Owner"),l.getGameTime()));}}
    private record Shot(UUID owner,long tick){}


    private static CompoundTag snapshot(Entity entity) { var output=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private static void restore(GameTestHelper helper, Entity entity, CompoundTag tag) { entity.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,helper.getLevel().registryAccess(),tag)); }
    private static java.util.UUID uuid(CompoundTag tag, String key) { net.minecraft.nbt.Tag value=tag.get(key); return value==null?null:net.minecraft.core.UUIDUtil.LENIENT_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE,value).result().orElse(null); }
    public static void register() {
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_death_persistence_next_tests_huntress_death_awards_seven_hundred_xp_at_twenty_ticks", DeathPersistenceNextTests::huntressDeathAwardsSevenHundredXpAtTwentyTicks, 30, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_death_persistence_next_tests_cancelled_huntress_death_does_not_award_experience", DeathPersistenceNextTests::cancelledHuntressDeathDoesNotAwardExperience, 25, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_death_persistence_next_tests_repeated_huntress_death_callbacks_do_not_duplicate_experience", DeathPersistenceNextTests::repeatedHuntressDeathCallbacksDoNotDuplicateExperience, 25, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_death_persistence_next_tests_huntress_boss_event_tracks_observer_and_health_fraction", DeathPersistenceNextTests::huntressBossEventTracksObserverAndHealthFraction, 100, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_death_persistence_next_tests_huntress_restored_task_resumes_at_next_uncommitted_slam_stage", DeathPersistenceNextTests::huntressRestoredTaskResumesAtNextUncommittedSlamStage, 100, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_death_persistence_next_tests_maiden_death_awards_fifty_xp_at_sixty_ticks", DeathPersistenceNextTests::maidenDeathAwardsFiftyXpAtSixtyTicks, 70, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_death_persistence_next_tests_cancelled_maiden_death_does_not_submit_explosion_tail", DeathPersistenceNextTests::cancelledMaidenDeathDoesNotSubmitExplosionTail, 65, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_death_persistence_next_tests_maiden_committed_death_tail_emits_fifteen_server_stages_and_ends", DeathPersistenceNextTests::maidenCommittedDeathTailEmitsFifteenServerStagesAndEnds, 65, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_death_persistence_next_tests_repeated_maiden_death_calls_do_not_submit_duplicate_tail", DeathPersistenceNextTests::repeatedMaidenDeathCallsDoNotSubmitDuplicateTail, 100, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_death_persistence_next_tests_maiden_restored_volley_resumes_at_stage_twenty_nine_only_once", DeathPersistenceNextTests::maidenRestoredVolleyResumesAtStageTwentyNineOnlyOnce, 100, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_death_persistence_next_tests_assimilator_infection_enters_through_real_damage_mixin", DeathPersistenceNextTests::assimilatorInfectionEntersThroughRealDamageMixin, 100, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_death_persistence_next_tests_removed_infection_schedules_one_persistent_conversion_task", DeathPersistenceNextTests::removedInfectionSchedulesOnePersistentConversionTask, 100, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_death_persistence_next_tests_infection_conversion_applies_effect_and_burst_damage_at_deadline", DeathPersistenceNextTests::infectionConversionAppliesEffectAndBurstDamageAtDeadline, 28, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_death_persistence_next_tests_infection_removal_cannot_queue_duplicate_task_same_tick", DeathPersistenceNextTests::infectionRemovalCannotQueueDuplicateTaskSameTick, 100, "supernova_server");
    }
}


















