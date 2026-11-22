package com.hp.thermalshockreforged.entity.gaze;

import net.minecraft.world.entity.EntitySpawnReason;

import net.minecraft.world.level.storage.TagValueOutput;

import net.minecraft.world.level.storage.TagValueInput;

import net.minecraft.util.ProblemReporter;

import com.hp.thermalshockreforged.gametest.TsrGameTests;

import com.hp.thermalshockreforged.content.status.aura.AuraContent;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.UUIDUtil;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import com.hp.thermalshockreforged.gametest.PlayerInteractionTests;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
public final class GazeServerBehaviorTests {
    public static void spiritRealPlayerAttackIsCancelledDuringBothReadyAndCooldownStates(GameTestHelper h) {
        var spirit=spirit(h);
        var player=player(h,spirit.position().add(3,0,0));
        float health=player.getHealth();
        helperAssert(h,!player.hurtServer((ServerLevel)player.level(), h.getLevel().damageSources().mobAttack(spirit),2)&&player.getHealth()==health,"Raw Spirit player damage must be cancelled by the actual attack hook");
        var aura=spirit.getEffect(AuraContent.DAMAGING_AURA);
        helperAssert(h,save(spirit).getIntOr("SpiritCooldown", 0)==40&&aura!=null&&aura.getDuration()==20&&aura.getAmplifier()==3,"The cancelled attack must commit cooldown and the complete real aura; cooldown="+save(spirit).getIntOr("SpiritCooldown", 0)+", aura="+aura);
        helperAssert(h,spirit.getDeltaMovement().distanceTo(spirit.getLookAngle().scale(4))<1E-8&&Math.abs(spirit.getDeltaMovement().length()-4)<.001&&spirit.hurtMarked,"Dash must synchronize its full four-block motion");
        var marker=new Vec3(.1,.2,.3);
        spirit.setDeltaMovement(marker);
        for(int i=0;i<5;i++) helperAssert(h,!player.hurtServer((ServerLevel)player.level(), h.getLevel().damageSources().mobAttack(spirit),2),"Cooldown damage must remain cancelled");
        helperAssert(h,player.getHealth()==health&&spirit.getDeltaMovement().equals(marker)&&save(spirit).getIntOr("SpiritCooldown", 0)==40,"Retries must not deal raw damage, re-dash, or reset cooldown");
        release(h,player);h.succeed();
    }
    public static void spiritCooldownRoundTripAndExactFortyBaseTicksArePreserved(GameTestHelper h) {
        var original=spirit(h);
        var victim=player(h,original.position().add(3,0,0));
        original.beginAttack(victim);
        var restored=GazeContent.SPIRIT.get().create(h.getLevel(), EntitySpawnReason.COMMAND);
        load(restored, save(original));
        helperAssert(h,save(restored).getIntOr("SpiritCooldown", 0)==40,"Reload must keep the committed cooldown");
        for(int i=0;i<39;i++) restored.baseTick();
        helperAssert(h,save(restored).getIntOr("SpiritCooldown", 0)==1,"Cooldown must remain active before base tick forty");
        restored.beginAttack(victim);
        helperAssert(h,save(restored).getIntOr("SpiritCooldown", 0)==1,"An early retry must not reset remaining cooldown");
        restored.baseTick();
        restored.beginAttack(victim);
        helperAssert(h,save(restored).getIntOr("SpiritCooldown", 0)==40,"The next attack must become available exactly after forty base ticks");
        var tag=save(restored);
        tag.putInt("SpiritCooldown",Integer.MAX_VALUE);load(restored, tag);
        helperAssert(h,save(restored).getIntOr("SpiritCooldown", 0)==40,"Corrupt positive cooldown must clamp to forty");
        tag.putInt("SpiritCooldown",-1);load(restored, tag);
        helperAssert(h,save(restored).getIntOr("SpiritCooldown", 0)==0,"Negative cooldown must clamp to zero");
        release(h,victim);h.succeed();
    }
    public static void spiritMeleeTriggerIncludesSevenBlocksAndExcludesBeyond(GameTestHelper h) {
        var spirit=spirit(h);
        var target=EntityType.COW.create(h.getLevel(), EntitySpawnReason.COMMAND);
        target.setPos(spirit.position().add(7,0,0));
        helperAssert(h,spirit.isWithinMeleeAttackRange(target),"Exactly seven blocks must be within the Spirit trigger range");
        target.setPos(spirit.position().add(7.001,0,0));
        helperAssert(h,!spirit.isWithinMeleeAttackRange(target),"Targets beyond seven blocks must not trigger melee");
        h.succeed();
    }
    public static void spiritKeepsAllFourDamageImmunitiesButAcceptsOrdinaryDamage(GameTestHelper h) {
        var spirit=spirit(h);
        var level=h.getLevel();
        var skull=new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.WITHER_SKULL));
        for(var source:new DamageSource[]{level.damageSources().fall(),level.damageSources().drown(),level.damageSources().wither(),skull}) helperAssert(h,!spirit.hurtServer((ServerLevel)spirit.level(), source,1)&&spirit.getHealth()==4,"Each original immunity must reject damage");
        helperAssert(h,spirit.hurtServer((ServerLevel)spirit.level(), level.damageSources().generic(),1)&&spirit.getHealth()<4,"Ordinary damage must remain effective");
        h.succeed();
    }
    public static void spiritAuraScalesBaseAttackExcludesSelfAndItemsAndHasNoOwnerRecursion(GameTestHelper h) {
        var spirit=spirit(h);
        spirit.addEffect(new MobEffectInstance(AuraContent.DAMAGING_AURA,20,3));
        var item=new ItemEntity(h.getLevel(),spirit.getX(),spirit.getY(),spirit.getZ(),new ItemStack(Items.DIAMOND));
        h.getLevel().addFreshEntity(item);
        var target=cow(h,spirit.position().add(.2,0,0));
        AuraContent.DAMAGING_AURA.value().applyEffectTick((ServerLevel)spirit.level(),spirit,3);
        helperAssert(h,Math.abs(target.getHealth()-997)<1E-5&&spirit.getHealth()==4&&!item.isRemoved(),"Default aura must deal three damage without hitting itself or an item");
        target.invulnerableTime=0;
        spirit.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8);
        AuraContent.DAMAGING_AURA.value().applyEffectTick((ServerLevel)spirit.level(),spirit,3);
        helperAssert(h,Math.abs(target.getHealth()-985)<1E-5&&target.lastSource.getEntity()==null&&save(spirit).getIntOr("SpiritCooldown", 0)==0,"Aura must use base attack times 1.5 and retain ownerless damage without reentering dash");
        h.succeed();
    }
    public static void spiritRejectsDeadCasterAndOtherDimensionVictimWithoutSpendingCooldown(GameTestHelper h) {
        var nether=h.getLevel().getServer().getLevel(Level.NETHER);
        helperAssert(h,nether!=null,"The dedicated server must expose the Nether");
        var spirit=GazeContent.SPIRIT.get().create(nether,EntitySpawnReason.COMMAND);spirit.setPos(origin(h));nether.addFreshEntity(spirit);
        var victim=player(h,origin(h));
        spirit.beginAttack(victim);
        helperAssert(h,save(spirit).getIntOr("SpiritCooldown", 0)==0&&!spirit.hasEffect(AuraContent.DAMAGING_AURA),"Cross-dimension victims must not activate the dash");
        spirit.setHealth(0);
        spirit.beginAttack(victim);
        helperAssert(h,save(spirit).getIntOr("SpiritCooldown", 0)==0,"A dead caster must not activate the dash");
        release(h,victim);h.succeed();
    }
    public static void cancelledLanternDeathCannotSubmitADeathExplosion(GameTestHelper h) {
        var mob=lantern(h);
        Consumer<LivingDeathEvent> veto=e->{if(e.getEntity()==mob)e.setCanceled(true);};
        NeoForge.EVENT_BUS.addListener(veto);
        try {
            mob.die(h.getLevel().damageSources().generic());
            helperAssert(h,!save(mob).getBooleanOr("LanternDeathCommitted", false)&&tasks(h).isEmpty(),"A cancelled death must not commit a phantom explosion");
        } finally { NeoForge.EVENT_BUS.unregister(veto); }
        h.succeed();
    }
    public static void repeatedLanternDeathSubmitsExactlyOnePersistentTask(GameTestHelper h) {
        var mob=lantern(h);
        mob.setHealth(0);mob.die(h.getLevel().damageSources().generic());
        mob.die(h.getLevel().damageSources().generic());
        helperAssert(h,tasks(h).size()==1&&save(mob).getBooleanOr("LanternDeathCommitted", false),"Repeated death callbacks must commit only one task");
        var restored=GazeContent.LANTERNBEARER.get().create(h.getLevel(), EntitySpawnReason.COMMAND);load(restored, save(mob));
        restored.die(h.getLevel().damageSources().generic());
        helperAssert(h,tasks(h).size()==1,"A reloaded committed corpse must not resubmit its task");
        tasks(h).forEach(Entity::discard);
        h.succeed();
    }
    public static void lanternExplosionRunsAtTwentyFourEndAndCorpseLivesTwentyFiveTicks(GameTestHelper h) {
        var mob=lantern(h);
        mob.setHealth(0);mob.die(h.getLevel().damageSources().generic());
        var task=tasks(h).getFirst();
        h.runAfterDelay(21,()->helperAssert(h,!task.isRemoved()&&!mob.isRemoved(),"Neither the task nor corpse may finish before the original deadlines: elapsed="+save(task).getIntOr("Elapsed",0)+", taskRemoved="+task.isRemoved()+", mobRemoved="+mob.isRemoved()+", reason="+mob.getRemovalReason()));
        h.runAfterDelay(28,()->{
            helperAssert(h,task.isRemoved()&&mob.isRemoved()&&save(task).getIntOr("Elapsed", 0)==24&&!save(task).getBooleanOr("Armed", false),"The real END scheduler must explode once at 24 and remove the corpse at 25");
            h.succeed();
        });
    }
    public static void lanternTaskReloadKeepsRemainingStagesAndNeverReplaysExplosion(GameTestHelper h) {
        LanternDeathEntity.submit(h.getLevel(),origin(h),6);
        var old=tasks(h).getFirst();
        for(int i=0;i<20;i++) old.endTick(h.getLevel());
        var tag=save(old);old.discard();putUuid(tag, "UUID", UUID.randomUUID());
        var restored=GazeContent.LANTERN_DEATH.get().create(h.getLevel(), EntitySpawnReason.COMMAND);load(restored, tag);
        helperAssert(h,save(restored).getIntOr("Elapsed", 0)==20&&save(restored).getDoubleOr("DamageScale", 0.0)==2,"Reload must keep elapsed time and attack snapshot");
        int[] blasts={0};
        Consumer<ExplosionEvent.Start> observer=e->{if(e.getLevel()==h.getLevel()&&e.getExplosion().center().distanceTo(origin(h))<.1)blasts[0]++;};
        NeoForge.EVENT_BUS.addListener(observer);
        try {
            for(int i=0;i<3;i++)restored.endTick(h.getLevel());
            helperAssert(h,blasts[0]==0&&!restored.isRemoved(),"A restored task must still wait its remaining four END stages");
            restored.endTick(h.getLevel());restored.endTick(h.getLevel());
            helperAssert(h,blasts[0]==1&&restored.isRemoved(),"The remaining deadline must cause exactly one real explosion");
        } finally {NeoForge.EVENT_BUS.unregister(observer);}
        h.succeed();
    }
    public static void lanternExplosionDamageScalesAndRespectsFullKnockbackResistance(GameTestHelper h) {
        var target=cow(h,origin(h).add(1,0,0));
        var resistant=cow(h,origin(h).add(-1,0,0));
        resistant.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        blast(h,3);
        float first=1000-target.getHealth();
        helperAssert(h,first>0&&target.getDeltaMovement().lengthSqr()>0&&resistant.getDeltaMovement().lengthSqr()<1E-12,"A real explosion must damage and push ordinary targets while respecting full knockback resistance");
        target.setHealth(1000);target.invulnerableTime=0;target.setDeltaMovement(Vec3.ZERO);
        resistant.discard();
        blast(h,6);
        helperAssert(h,Math.abs((1000-target.getHealth())-2*first)<1E-4,"Doubling the committed base attack must double explosion damage");
        h.succeed();
    }
    public static void cancelledLanternTaskJoinDoesNotLeakAnArmedEntity(GameTestHelper h) {
        Consumer<EntityJoinLevelEvent> veto=e->{if(e.getLevel()==h.getLevel()&&e.getEntity() instanceof LanternDeathEntity&&e.getEntity().position().distanceTo(origin(h))<.1)e.setCanceled(true);};
        NeoForge.EVENT_BUS.addListener(veto);
        try {LanternDeathEntity.submit(h.getLevel(),origin(h),3);helperAssert(h,tasks(h).isEmpty(),"A cancelled submission must not create an active task");}
        finally {NeoForge.EVENT_BUS.unregister(veto);}
        h.succeed();
    }
    public static void lensPreservesNoGravityDrowningAndFallImmunityButOrdinaryDamageWorks(GameTestHelper h) {
        var lens=lens(h,origin(h));
        lens.setNoGravity(false);
        helperAssert(h,lens.isNoGravity()&&!lens.hurtServer((ServerLevel)lens.level(), h.getLevel().damageSources().drown(),5)&&!lens.causeFallDamage(100,1,h.getLevel().damageSources().fall())&&lens.getHealth()==35,"Lens must keep forced no-gravity and both immunity paths");
        helperAssert(h,lens.hurtServer((ServerLevel)lens.level(), h.getLevel().damageSources().generic(),1)&&lens.getHealth()==34,"Ordinary damage must not be hidden by invulnerability buffs");
        h.succeed();
    }
    public static void lensStaysClosedWithoutAnyNearbyPlayer(GameTestHelper h) {
        var lens=lens(h,origin(h));
        lens.baseTick();
        helperAssert(h,!lens.isShiftKeyDown(),"Lens must remain inactive without a nearby eligible player");
        h.succeed();
    }
    public static void lensTurnsPlayerFeetTowardNearestLensWithoutTeleportingOrClearingVelocity(GameTestHelper h) {
        var far=lens(h,origin(h));
        var near=lens(h,origin(h).add(3,1,0));
        var p=player(h,origin(h).add(4,0,0));
        
        try {
            var before=p.position();var motion=new Vec3(.2,.1,.3);p.setDeltaMovement(motion);
            far.baseTick();
            var desired=near.position().subtract(p.position()).normalize();
            helperAssert(h,lookTracker(p).count==1&&lookTracker(p).from==Anchor.FEET&&lookTracker(p).target.equals(near.position())&&p.getLookAngle().normalize().dot(desired)>.99999,"An active Lens must choose the nearest loaded Lens using FEET-to-FEET orientation; looks="+lookTracker(p).count+", target="+lookTracker(p).target+", nearest="+near.getUUID()+", anchor="+lookTracker(p).from+", dot="+p.getLookAngle().dot(desired)+", look="+p.getLookAngle()+", desired="+desired+", player="+p.position()+", near="+near.position());
            helperAssert(h,p.position().equals(before)&&p.getDeltaMovement().equals(motion),"Gaze must not teleport or clear player velocity");
            if(h.getLevel().getDifficulty()!=Difficulty.EASY){var effect=p.getEffect(MobEffects.NAUSEA);helperAssert(h,effect!=null&&effect.getDuration()==120&&effect.getAmplifier()==1,"Non-easy gaze must preserve the full nausea effect");}
            helperAssert(h,far.isShiftKeyDown(),"The source Lens must enter its open state");
        } finally {release(h,p);}
        h.succeed();
    }
    public static void overlappingLensesDeduplicateEachPlayerButIncludeSpectatorsInTheActiveArea(GameTestHelper h) {
        var a=lens(h,origin(h));var b=lens(h,origin(h).add(1,0,0));
        var active=player(h,origin(h).add(2,0,0));
        var spectator=player(h,origin(h).add(2,0,2));spectator.setGameMode(GameType.SPECTATOR);
        
        try {
            a.baseTick();b.baseTick();a.baseTick();
            helperAssert(h,lookTracker(active).count==1&&lookTracker(spectator).count==1,"Overlapping lenses must rotate each player once per server tick while retaining active-area spectator effects");
        } finally {release(h,active);release(h,spectator);}
        h.succeed();
    }
    public static void lensActivationIsStrictlyInsideEightAndSpectatorsAloneDoNotActivate(GameTestHelper h) {
        var lens=lens(h,origin(h));
        var p=player(h,origin(h).add(8,0,0));
        try {
            lens.baseTick();helperAssert(h,!lens.isShiftKeyDown()&&lookTracker(p).count==0,"A player exactly eight blocks away must not activate the strict radius");
            p.setPos(origin(h).add(7.99,0,0));lens.baseTick();
            helperAssert(h,lens.isShiftKeyDown()&&lookTracker(p).count==1,"A player just inside eight blocks must activate gaze");
            p.setGameMode(GameType.SPECTATOR);lens.baseTick();
            helperAssert(h,!lens.isShiftKeyDown(),"A spectator alone must not activate the Lens");
        } finally {release(h,p);}
        h.succeed();
    }

    private static void helperAssert(GameTestHelper h,boolean value,String message){h.assertTrue(value,message);}
    private static Vec3 origin(GameTestHelper h){return h.absolutePos(new BlockPos(10,4,8)).getCenter();}
    private static CompoundTag save(Entity entity){var output=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess());entity.saveWithoutId(output);return output.buildResult();}
    private static void load(Entity entity,CompoundTag tag){entity.load(TagValueInput.create(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess(),tag));}
    private static void putUuid(CompoundTag tag,String key,UUID value){tag.put(key,UUIDUtil.CODEC.encodeStart(NbtOps.INSTANCE,value).result().orElseThrow());}
    private static SpiritEntity spirit(GameTestHelper h){var e=GazeContent.SPIRIT.get().create(h.getLevel(), EntitySpawnReason.COMMAND);e.setPos(origin(h));e.setNoAi(true);e.setNoGravity(true);e.setPersistenceRequired();h.getLevel().addFreshEntity(e);return e;}
    private static LanternbearerEntity lantern(GameTestHelper h){var e=GazeContent.LANTERNBEARER.get().create(h.getLevel(), EntitySpawnReason.COMMAND);e.setPos(origin(h));e.setNoAi(true);e.setNoGravity(true);e.setPersistenceRequired();h.getLevel().addFreshEntity(e);return e;}
    private static LensEntity lens(GameTestHelper h,Vec3 pos){var e=GazeContent.LENS.get().create(h.getLevel(), EntitySpawnReason.COMMAND);e.setPos(pos);e.setNoAi(true);h.getLevel().addFreshEntity(e);return e;}
    private static java.util.List<LanternDeathEntity> tasks(GameTestHelper h){return h.getLevel().getEntitiesOfClass(LanternDeathEntity.class,new AABB(origin(h),origin(h)).inflate(2));}
    private static ServerPlayer player(GameTestHelper h,Vec3 pos){var p=PlayerInteractionTests.player(h,GameType.SURVIVAL,0,0,0);p.snapTo(pos.x,pos.y,pos.z,0,0);p.connection.getConnection().channel().pipeline().addFirst("tsr_gaze_test_"+p.getUUID(),new LookTracker((ServerLevel)p.level()));return p;}
    private static TestCow cow(GameTestHelper h,Vec3 pos){var e=new TestCow(h.getLevel());e.setPos(pos);e.setNoAi(true);e.setNoGravity(true);e.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1000);e.getAttribute(Attributes.ARMOR).setBaseValue(0);e.setHealth(1000);h.getLevel().addFreshEntity(e);return e;}
    private static void blast(GameTestHelper h,double attack){LanternDeathEntity.submit(h.getLevel(),origin(h),attack);var task=tasks(h).getFirst();for(int i=0;i<24;i++)task.endTick(h.getLevel());helperAssert(h,task.isRemoved(),"The explosion task must be consumed");}
    private static final class TestCow extends Cow {
        private DamageSource lastSource;
        TestCow(ServerLevel level){super(EntityType.COW,level);}
        @Override public boolean hurtServer(ServerLevel level,DamageSource source,float amount){lastSource=source;return super.hurtServer(level,source,amount);}
    }
    private static LookTracker lookTracker(ServerPlayer player){return (LookTracker)player.connection.getConnection().channel().pipeline().get("tsr_gaze_test_"+player.getUUID());}
    private static void release(GameTestHelper h,ServerPlayer player){var pipeline=player.connection.getConnection().channel().pipeline();var name="tsr_gaze_test_"+player.getUUID();if(pipeline.get(name)!=null)pipeline.remove(name);PlayerInteractionTests.release(h,player);}
    private static final class LookTracker extends ChannelOutboundHandlerAdapter{private final ServerLevel level;private int count;private Anchor from;private Vec3 target;LookTracker(ServerLevel level){this.level=level;}@Override public void write(ChannelHandlerContext context,Object message,ChannelPromise promise)throws Exception{if(message instanceof ClientboundPlayerLookAtPacket packet){count++;from=packet.getFromAnchor();target=packet.getPosition(level);}context.write(message,promise);}}

    private GazeServerBehaviorTests(){}
    public static void register() {
        TsrGameTests.add("history_gaze_server_behavior_tests_spirit_real_player_attack_is_cancelled_during_both_ready_and_cooldown_states", GazeServerBehaviorTests::spiritRealPlayerAttackIsCancelledDuringBothReadyAndCooldownStates, 100, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_spirit_cooldown_round_trip_and_exact_forty_base_ticks_are_preserved", GazeServerBehaviorTests::spiritCooldownRoundTripAndExactFortyBaseTicksArePreserved, 100, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_spirit_melee_trigger_includes_seven_blocks_and_excludes_beyond", GazeServerBehaviorTests::spiritMeleeTriggerIncludesSevenBlocksAndExcludesBeyond, 100, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_spirit_keeps_all_four_damage_immunities_but_accepts_ordinary_damage", GazeServerBehaviorTests::spiritKeepsAllFourDamageImmunitiesButAcceptsOrdinaryDamage, 100, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_spirit_aura_scales_base_attack_excludes_self_and_items_and_has_no_owner_recursion", GazeServerBehaviorTests::spiritAuraScalesBaseAttackExcludesSelfAndItemsAndHasNoOwnerRecursion, 100, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_spirit_rejects_dead_caster_and_other_dimension_victim_without_spending_cooldown", GazeServerBehaviorTests::spiritRejectsDeadCasterAndOtherDimensionVictimWithoutSpendingCooldown, 100, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_cancelled_lantern_death_cannot_submit_a_death_explosion", GazeServerBehaviorTests::cancelledLanternDeathCannotSubmitADeathExplosion, 100, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_repeated_lantern_death_submits_exactly_one_persistent_task", GazeServerBehaviorTests::repeatedLanternDeathSubmitsExactlyOnePersistentTask, 100, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_lantern_explosion_runs_at_twenty_four_end_and_corpse_lives_twenty_five_ticks", GazeServerBehaviorTests::lanternExplosionRunsAtTwentyFourEndAndCorpseLivesTwentyFiveTicks, 35, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_lantern_task_reload_keeps_remaining_stages_and_never_replays_explosion", GazeServerBehaviorTests::lanternTaskReloadKeepsRemainingStagesAndNeverReplaysExplosion, 100, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_lantern_explosion_damage_scales_and_respects_full_knockback_resistance", GazeServerBehaviorTests::lanternExplosionDamageScalesAndRespectsFullKnockbackResistance, 100, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_cancelled_lantern_task_join_does_not_leak_an_armed_entity", GazeServerBehaviorTests::cancelledLanternTaskJoinDoesNotLeakAnArmedEntity, 100, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_lens_preserves_no_gravity_drowning_and_fall_immunity_but_ordinary_damage_works", GazeServerBehaviorTests::lensPreservesNoGravityDrowningAndFallImmunityButOrdinaryDamageWorks, 100, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_lens_stays_closed_without_any_nearby_player", GazeServerBehaviorTests::lensStaysClosedWithoutAnyNearbyPlayer, 100, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_lens_turns_player_feet_toward_nearest_lens_without_teleporting_or_clearing_velocity", GazeServerBehaviorTests::lensTurnsPlayerFeetTowardNearestLensWithoutTeleportingOrClearingVelocity, 100, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_overlapping_lenses_deduplicate_each_player_but_include_spectators_in_the_active_area", GazeServerBehaviorTests::overlappingLensesDeduplicateEachPlayerButIncludeSpectatorsInTheActiveArea, 100, "supernova_server");
        TsrGameTests.add("history_gaze_server_behavior_tests_lens_activation_is_strictly_inside_eight_and_spectators_alone_do_not_activate", GazeServerBehaviorTests::lensActivationIsStrictlyInsideEightAndSpectatorsAloneDoNotActivate, 100, "supernova_server");
    }
}
