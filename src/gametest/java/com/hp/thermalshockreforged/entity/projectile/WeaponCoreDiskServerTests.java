package com.hp.thermalshockreforged.entity.projectile;

import com.mojang.authlib.GameProfile;
import com.hp.thermalshockreforged.gametest.PlayerInteractionTests;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.ModDamageTypes;
import com.hp.thermalshockreforged.content.weapons.CoreEjectFlareGunItem;
import com.hp.thermalshockreforged.content.weapons.DiskResonanceEntity;
import com.hp.thermalshockreforged.content.weapons.FlareGunCycleEntity;
import com.hp.thermalshockreforged.content.weapons.LiteprismDiskItem;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import com.hp.thermalshockreforged.entity.weaponcore.FlareGunCoreEntity;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import com.hp.thermalshockreforged.gametest.TsrGameTests;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.minecraft.world.level.block.Blocks;

public final class WeaponCoreDiskServerTests {
        public static void register() {
        TsrGameTests.add("history_weapon_core_disk_server_tests_core_eject_primary_consumes_one_round_without_weapon_wear", WeaponCoreDiskServerTests::coreEjectPrimaryConsumesOneRoundWithoutWeaponWear, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_core_eject_primary_rejects_missing_light_round", WeaponCoreDiskServerTests::coreEjectPrimaryRejectsMissingLightRound, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_core_eject_alternate_use_spawns_core_and_consumes_one_heavy_round", WeaponCoreDiskServerTests::coreEjectAlternateUseSpawnsCoreAndConsumesOneHeavyRound, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_core_eject_alternate_use_rejects_missing_heavy_round", WeaponCoreDiskServerTests::coreEjectAlternateUseRejectsMissingHeavyRound, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_core_eject_inputs_share_cooldown_and_preserve_rejected_mode_ammo", WeaponCoreDiskServerTests::coreEjectInputsShareCooldownAndPreserveRejectedModeAmmo, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_core_eject_cycle_runs_exactly_eighteen_and_twenty_end_stages", WeaponCoreDiskServerTests::coreEjectCycleRunsExactlyEighteenAndTwentyEndStages, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_flare_gun_core_ignores_fall_and_canceled_death_does_not_detonate", WeaponCoreDiskServerTests::flareGunCoreIgnoresFallAndCanceledDeathDoesNotDetonate, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_flare_gun_core_death_without_entity_attacker_does_not_detonate", WeaponCoreDiskServerTests::flareGunCoreDeathWithoutEntityAttackerDoesNotDetonate, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_flare_gun_core_player_kill_detonates_seven_with_original_attacker", WeaponCoreDiskServerTests::flareGunCorePlayerKillDetonatesSevenWithOriginalAttacker, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_flare_gun_core_chain_processes_each_nearby_core_once", WeaponCoreDiskServerTests::flareGunCoreChainProcessesEachNearbyCoreOnce, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_flare_gun_core_expires_on_its_one_hundred_twentieth_end_tick", WeaponCoreDiskServerTests::flareGunCoreExpiresOnItsOneHundredTwentiethEndTick, 140, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_liteprism_disk_use_starts_without_firing_or_wearing_the_item", WeaponCoreDiskServerTests::liteprismDiskUseStartsWithoutFiringOrWearingTheItem, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_liteprism_disk_on_use_tick_fires_once_wears_one_and_stops_use", WeaponCoreDiskServerTests::liteprismDiskOnUseTickFiresOnceWearsOneAndStopsUse, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_liteprism_first_hit_creates_build_up_layer_zero", WeaponCoreDiskServerTests::liteprismFirstHitCreatesBuildUpLayerZero, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_liteprism_sixth_hit_stops_at_layer_five_without_discharging", WeaponCoreDiskServerTests::liteprismSixthHitStopsAtLayerFiveWithoutDischarging, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_liteprism_seventh_hit_discharges_ten_and_clears_build_up", WeaponCoreDiskServerTests::liteprismSeventhHitDischargesTenAndClearsBuildUp, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_liteprism_build_up_expires_after_twenty_ticks_without_discharge", WeaponCoreDiskServerTests::liteprismBuildUpExpiresAfterTwentyTicksWithoutDischarge, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_liteprism_hit_on_flare_core_submits_resonance_with_original_owner", WeaponCoreDiskServerTests::liteprismHitOnFlareCoreSubmitsResonanceWithOriginalOwner, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_disk_resonance_bursts_at_submit_three_and_six_with_owner_and_radius", WeaponCoreDiskServerTests::diskResonanceBurstsAtSubmitThreeAndSixWithOwnerAndRadius, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_carbor_gl_use_without_heavy_round_fails_without_starting_or_firing", WeaponCoreDiskServerTests::carborGlUseWithoutHeavyRoundFailsWithoutStartingOrFiring, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_carbor_gl_use_fires_owner_bound_three_damage_grenade_and_consumes_one_round", WeaponCoreDiskServerTests::carborGlUseFiresOwnerBoundThreeDamageGrenadeAndConsumesOneRound, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_carbor_gl_use_tick_honors_cooldown_and_release_stops_further_shots", WeaponCoreDiskServerTests::carborGlUseTickHonorsCooldownAndReleaseStopsFurtherShots, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_carbor_entity_hit_attributes_direct_and_blast_damage_separately", WeaponCoreDiskServerTests::carborEntityHitAttributesDirectAndBlastDamageSeparately, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_carbor_entity_hit_on_player_does_not_create_the_tnt_blast", WeaponCoreDiskServerTests::carborEntityHitOnPlayerDoesNotCreateTheTntBlast, 100, "supernova_server");
        TsrGameTests.add("history_weapon_core_disk_server_tests_carbor_block_hit_creates_its_two_power_tnt_blast", WeaponCoreDiskServerTests::carborBlockHitCreatesItsTwoPowerTntBlast, 100, "supernova_server");
    }

public static void coreEjectPrimaryConsumesOneRoundWithoutWeaponWear(GameTestHelper h) {
        var p=player(h,4);var item=WeaponContent.CORE_EJECT_FLARE_GUN.get();var stack=new ItemStack(item);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);p.getInventory().setItem(9,new ItemStack(ammo("light_round"),2));item.onEntitySwing(stack,p,net.minecraft.world.InteractionHand.MAIN_HAND);var shots=owned(h,FlareBulletEntity.class,p);
        h.assertTrue(shots.size()==1&&shots.get(0).getOwner()==p&&baseDamage(shots.get(0))==2&&pickup(shots.get(0)).equals("DISALLOWED")&&count(h,FlareGunCycleEntity.class)==1&&countAmmo(p,ammo("light_round"))==1&&stack.getDamageValue()==0&&p.getCooldowns().isOnCooldown(p.getMainHandItem()),"Primary Core Eject must submit one owner-bound non-pickup flare, spend one light round, start cooldown, and preserve weapon durability");PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void coreEjectPrimaryRejectsMissingLightRound(GameTestHelper h) {
        var p=player(h,4);var item=WeaponContent.CORE_EJECT_FLARE_GUN.get();var stack=new ItemStack(item);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);item.onEntitySwing(stack,p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(owned(h,FlareBulletEntity.class,p).isEmpty()&&count(h,FlareGunCycleEntity.class)==0&&!p.getCooldowns().isOnCooldown(p.getMainHandItem())&&stack.getDamageValue()==0,"Primary Core Eject without light ammunition must not create a projectile or cycle, consume durability, or start cooldown");PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void coreEjectAlternateUseSpawnsCoreAndConsumesOneHeavyRound(GameTestHelper h) {
        var p=player(h,4);var item=WeaponContent.CORE_EJECT_FLARE_GUN.get();var stack=new ItemStack(item);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);p.getInventory().setItem(9,new ItemStack(ammo("heavy_round"),2));item.use(h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(count(h,FlareGunCoreEntity.class)==1&&count(h,FlareGunCycleEntity.class)==1&&countAmmo(p,ammo("heavy_round"))==1&&stack.getDamageValue()==0&&p.getCooldowns().isOnCooldown(p.getMainHandItem()),"Alternate Core Eject use must spawn one core and one alternate cycle, consume one heavy round, start cooldown, and preserve weapon durability");PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void coreEjectAlternateUseRejectsMissingHeavyRound(GameTestHelper h) {
        var p=player(h,4);var item=WeaponContent.CORE_EJECT_FLARE_GUN.get();var stack=new ItemStack(item);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);item.use(h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(count(h,FlareGunCoreEntity.class)==0&&count(h,FlareGunCycleEntity.class)==0&&!p.getCooldowns().isOnCooldown(p.getMainHandItem())&&stack.getDamageValue()==0,"Alternate Core Eject use without heavy ammunition must not spawn its core or cycle, wear the gun, or start cooldown");PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void coreEjectInputsShareCooldownAndPreserveRejectedModeAmmo(GameTestHelper h) {
        var p=player(h,4);var item=WeaponContent.CORE_EJECT_FLARE_GUN.get();var stack=new ItemStack(item);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);p.getInventory().setItem(9,new ItemStack(ammo("light_round"),2));p.getInventory().setItem(10,new ItemStack(ammo("heavy_round"),2));item.onEntitySwing(stack,p,net.minecraft.world.InteractionHand.MAIN_HAND);item.use(h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(owned(h,FlareBulletEntity.class,p).size()==1&&count(h,FlareGunCoreEntity.class)==0&&count(h,FlareGunCycleEntity.class)==1&&countAmmo(p,ammo("light_round"))==1&&countAmmo(p,ammo("heavy_round"))==2,"Alternate use during the primary cooldown must be rejected without spawning its core or consuming heavy ammunition");PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void coreEjectCycleRunsExactlyEighteenAndTwentyEndStages(GameTestHelper h) {
        var normal=player(h,3);var alternate=player(h,12);var item=WeaponContent.CORE_EJECT_FLARE_GUN.get();var normalStack=new ItemStack(item);var alternateStack=new ItemStack(item);normal.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,normalStack);alternate.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,alternateStack);normal.getInventory().setItem(9,new ItemStack(ammo("light_round"),1));alternate.getInventory().setItem(9,new ItemStack(ammo("heavy_round"),1));var normalPos=normal.position();var alternatePos=alternate.position();var ticks=new long[]{0};var traces=new ArrayList<CycleTrace>();Consumer<ServerTickEvent.Post> clock=e->{if(e.getServer()==h.getLevel().getServer())ticks[0]++;};Consumer<EntityJoinLevelEvent> joins=e->{if(e.getLevel()==h.getLevel()&&e.getEntity() instanceof FlareGunCycleEntity cycle&&(cycle.position().distanceToSqr(normalPos)<0.01||cycle.position().distanceToSqr(alternatePos)<0.01))traces.add(new CycleTrace(cycle,fieldBoolean(cycle,"alternate"),ticks[0]));};Consumer<ServerTickEvent.Post> observer=e->{if(e.getServer()==h.getLevel().getServer())for(var trace:traces){int elapsed=fieldInt(trace.entity,"elapsed");if(elapsed>0&&(trace.stages.isEmpty()||trace.stages.get(trace.stages.size()-1)!=elapsed)){trace.stages.add(elapsed);trace.tickOffsets.add(ticks[0]-trace.submittedTick);}if(trace.entity.isRemoved()&&trace.removedOffset==null)trace.removedOffset=ticks[0]-trace.submittedTick;}};NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST,ServerTickEvent.Post.class,clock);NeoForge.EVENT_BUS.addListener(EntityJoinLevelEvent.class,joins);NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,ServerTickEvent.Post.class,observer);item.onEntitySwing(normalStack,normal,net.minecraft.world.InteractionHand.MAIN_HAND);item.use(h.getLevel(),alternate,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.runAfterDelay(22,()->{NeoForge.EVENT_BUS.unregister(observer);NeoForge.EVENT_BUS.unregister(joins);NeoForge.EVENT_BUS.unregister(clock);var a=traces.stream().filter(t->!t.alternate).findFirst().orElse(null);var b=traces.stream().filter(t->t.alternate).findFirst().orElse(null);h.assertTrue(traces.size()==2&&a!=null&&b!=null&&a.stages.equals(intRange(1,18))&&a.tickOffsets.equals(longRange(1,18))&&a.removedOffset==18&&b.stages.equals(intRange(1,20))&&b.tickOffsets.equals(longRange(1,20))&&b.removedOffset==20,"Flare cycle task must execute each sequential END stage through 18 for primary and 20 for alternate; primary="+(a==null?"missing":a.stages+"/"+a.tickOffsets+"/"+a.removedOffset)+", alternate="+(b==null?"missing":b.stages+"/"+b.tickOffsets+"/"+b.removedOffset));PlayerInteractionTests.release(h,normal);PlayerInteractionTests.release(h,alternate);h.succeed();});
    }
    public static void flareGunCoreIgnoresFallAndCanceledDeathDoesNotDetonate(GameTestHelper h) {
        var core=spawnCore(h,8);var nearby=cow(h,core.position().add(1,0,0),new long[]{0});boolean fallHit=core.hurtServer(h.getLevel(),h.getLevel().damageSources().fall(),5);core.causeFallDamage(12,1,h.getLevel().damageSources().fall());Consumer<LivingDeathEvent> veto=e->{if(e.getEntity()==core)e.setCanceled(true);};NeoForge.EVENT_BUS.addListener(veto);try{core.die(h.getLevel().damageSources().generic());}finally{NeoForge.EVENT_BUS.unregister(veto);}
        h.assertTrue(!fallHit&&core.isAlive()&&core.getHealth()==1&&nearby.getHealth()==1000,"Fall damage must be ignored, and canceling core death must leave the core alive without submitting a detonation");h.succeed();
    }
    public static void flareGunCoreDeathWithoutEntityAttackerDoesNotDetonate(GameTestHelper h) {
        var core=spawnCore(h,8);var nearby=cow(h,core.position().add(1,0,0),new long[]{0});var damage=new DamageSource(h.getLevel().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS));core.hurt(damage,2);
        h.assertTrue(core.isDeadOrDying()&&nearby.getHealth()==1000,"A core killed by an entity-less damage source must not damage nearby entities with a detonation");h.succeed();
    }
    public static void flareGunCorePlayerKillDetonatesSevenWithOriginalAttacker(GameTestHelper h) {
        var attacker=player(h,3);var core=spawnCore(h,8);var ticks=new long[]{0};var nearby=cow(h,core.position().add(1,0,0),ticks);var damage=new DamageSource(h.getLevel().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS),attacker);core.hurt(damage,2);
        h.assertTrue(core.isRemoved()&&nearby.hits.size()==1&&nearby.hits.get(0).amount==7&&attacker.getUUID().equals(nearby.hits.get(0).attacker),"An entity-attributed lethal hit must remove the core and deliver one seven-damage burst attributed to the original attacker");PlayerInteractionTests.release(h,attacker);h.succeed();
    }
    public static void flareGunCoreChainProcessesEachNearbyCoreOnce(GameTestHelper h) {
        var attacker=player(h,3);var first=spawnCore(h,8);var second=spawnCore(h,9);var victim=cow(h,first.position().add(0,0,1),new long[]{0});var damage=new DamageSource(h.getLevel().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageTypes.THERM_IFRAME_BYPASS),attacker);first.hurt(damage,2);
        h.assertTrue(first.isRemoved()&&second.isRemoved()&&victim.hits.size()==2&&victim.hits.stream().allMatch(hit->hit.amount==7&&attacker.getUUID().equals(hit.attacker)),"One core detonation must queue the neighboring core's committed death and produce exactly two attributed seven-damage bursts");PlayerInteractionTests.release(h,attacker);h.succeed();
    }
    public static void flareGunCoreExpiresOnItsOneHundredTwentiethEndTick(GameTestHelper h) {
        var tickingPlayer=player(h,3);var ticks=new long[]{0};var remaining=new ArrayList<Integer>();Long[] removedAt={null};var coreRef=new FlareGunCoreEntity[1];Consumer<ServerTickEvent.Post> clock=e->{if(e.getServer()==h.getLevel().getServer())ticks[0]++;};Consumer<ServerTickEvent.Post> observer=e->{if(e.getServer()==h.getLevel().getServer()&&coreRef[0]!=null){remaining.add(fieldInt(coreRef[0],"remaining"));if(coreRef[0].isRemoved()&&removedAt[0]==null)removedAt[0]=ticks[0];}};NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST,ServerTickEvent.Post.class,clock);NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,ServerTickEvent.Post.class,observer);long submitted=ticks[0];coreRef[0]=spawnCore(h,8);
        h.runAfterDelay(122,()->{NeoForge.EVENT_BUS.unregister(observer);NeoForge.EVENT_BUS.unregister(clock);h.assertTrue(remaining.size()>=120&&remaining.subList(0,120).equals(intRange(119,0))&&removedAt[0]!=null&&removedAt[0]-submitted==120,"Spawn-initialized FlareGunCore must decrement its local lifetime once per END tick and expire at offset 120; observed="+remaining+", removed="+removedAt[0]);PlayerInteractionTests.release(h,tickingPlayer);h.succeed();});
    }
    public static void liteprismDiskUseStartsWithoutFiringOrWearingTheItem(GameTestHelper h) {
        var p=player(h,4);var item=WeaponContent.LITEPRISM_DISK.get();var stack=new ItemStack(item);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);var result=item.use(h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(result.consumesAction()&&p.isUsingItem()&&countOwner(h,LiteprismDiskProjectileEntity.class,p)==0&&stack.getDamageValue()==0,"Disk use must enter its real use state without firing or applying durability before onUseTick");PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void liteprismDiskOnUseTickFiresOnceWearsOneAndStopsUse(GameTestHelper h) {
        var p=player(h,4);var item=WeaponContent.LITEPRISM_DISK.get();var stack=new ItemStack(item);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);item.use(h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND);item.onUseTick(h.getLevel(),p,stack,71999);var disks=owned(h,LiteprismDiskProjectileEntity.class,p);
        h.assertTrue(disks.size()==1&&disks.get(0).getOwner()==p&&pickup(disks.get(0)).equals("DISALLOWED")&&stack.getDamageValue()==1&&!p.isUsingItem(),"One onUseTick must fire one owner-bound non-pickup disk, consume exactly one durability, and release item use");PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void liteprismFirstHitCreatesBuildUpLayerZero(GameTestHelper h) {
        var p=player(h,4);var target=cow(h,p.position().add(1,0,0),new long[]{0});hitDisk(h,p,target);var effect=target.getEffect(WeaponContent.LITEPRISM_BUILDUP);
        h.assertTrue(effect!=null&&effect.getAmplifier()==0&&effect.getDuration()==20,"A first disk hit must add buildup layer one as amplifier zero with a 20-tick duration");PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void liteprismSixthHitStopsAtLayerFiveWithoutDischarging(GameTestHelper h) {
        var p=player(h,4);var target=cow(h,p.position().add(1,0,0),new long[]{0});for(int i=0;i<6;i++)hitDisk(h,p,target);var effect=target.getEffect(WeaponContent.LITEPRISM_BUILDUP);
        h.assertTrue(effect!=null&&effect.getAmplifier()==5&&target.hits.stream().noneMatch(hit->hit.bypass),"The sixth disk hit must leave amplifier five active and must not issue the bypass discharge");PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void liteprismSeventhHitDischargesTenAndClearsBuildUp(GameTestHelper h) {
        var p=player(h,4);var target=cow(h,p.position().add(1,0,0),new long[]{0});for(int i=0;i<7;i++)hitDisk(h,p,target);var bypass=target.hits.stream().filter(hit->hit.bypass).toList();
        h.assertTrue(target.getEffect(WeaponContent.LITEPRISM_BUILDUP)==null&&bypass.size()==1&&bypass.get(0).amount==10,"The seventh disk hit must remove buildup before applying exactly one ten-damage bypass discharge");PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void liteprismBuildUpExpiresAfterTwentyTicksWithoutDischarge(GameTestHelper h) {
        var p=player(h,4);var target=cow(h,p.position().add(1,0,0),new long[]{0});hitDisk(h,p,target);float health=target.getHealth();h.runAfterDelay(21,()->{h.assertTrue(target.getEffect(WeaponContent.LITEPRISM_BUILDUP)==null&&target.getHealth()==health&&target.hits.stream().noneMatch(hit->hit.bypass),"A lone buildup layer must expire naturally without a delayed discharge or extra damage");PlayerInteractionTests.release(h,p);h.succeed();});
    }
    public static void liteprismHitOnFlareCoreSubmitsResonanceWithOriginalOwner(GameTestHelper h) {
        var p=player(h,3);var core=spawnCore(h,9);var fired=LiteprismDiskProjectileEntity.fire(h.getLevel(),p);var disk=latestOwned(h,LiteprismDiskProjectileEntity.class,p);disk.onHitEntity(new EntityHitResult(core));var tasks=h.getLevel().getEntitiesOfClass(DiskResonanceEntity.class,area(h,core.position(),8));
        h.assertTrue(fired&&core.isRemoved()&&tasks.size()==1&&p.getUUID().equals(fieldUuid(tasks.get(0),"owner"))&&disk.getOwner()==p&&pickup(disk).equals("DISALLOWED"),"A direct synthetic disk entity hit on a flare core must remove the one-health core and commit one non-pickup resonance task with the disk's original owner UUID");PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void diskResonanceBurstsAtSubmitThreeAndSixWithOwnerAndRadius(GameTestHelper h) {
        var owner=player(h,13);var center=h.absolutePos(new BlockPos(4,2,4)).getCenter();var ticks=new long[]{0};var inside=cow(h,center.add(2,0,0),ticks);var outside=cow(h,center.add(4,0,0),ticks);Consumer<ServerTickEvent.Post> clock=e->{if(e.getServer()==h.getLevel().getServer())ticks[0]++;};NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST,ServerTickEvent.Post.class,clock);long submit=ticks[0];boolean accepted=DiskResonanceEntity.submit(h.getLevel(),center,owner);
        h.runAfterDelay(8,()->{NeoForge.EVENT_BUS.unregister(clock);var hits=inside.hits;h.assertTrue(accepted&&hits.size()==3&&hits.stream().map(hit->hit.tick-submit).toList().equals(List.of(0L,3L,6L))&&hits.stream().allMatch(hit->hit.amount==4&&owner.getUUID().equals(hit.attacker))&&outside.getHealth()==1000,"Disk resonance must burst immediately and at END+3/+6 for four damage, preserve owner attribution, and exclude targets outside its three-block AABB; ticks="+hits.stream().map(hit->hit.tick-submit).toList());PlayerInteractionTests.release(h,owner);h.succeed();});
    }
    public static void carborGlUseWithoutHeavyRoundFailsWithoutStartingOrFiring(GameTestHelper h) {
        var p=player(h,4);var item=WeaponContent.CARBOR_GL.get();var stack=new ItemStack(item);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);var result=item.use(h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(result==InteractionResult.FAIL&&!p.isUsingItem()&&countOwner(h,GrenadeBulletEntity.class,p)==0&&!p.getCooldowns().isOnCooldown(p.getMainHandItem())&&stack.getDamageValue()==0,"Carbor GL use without heavy ammunition must fail without beginning use, firing, adding cooldown, or wearing the launcher");PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void carborGlUseFiresOwnerBoundThreeDamageGrenadeAndConsumesOneRound(GameTestHelper h) {
        var p=player(h,4);var item=WeaponContent.CARBOR_GL.get();var stack=new ItemStack(item);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);p.getInventory().setItem(9,new ItemStack(ammo("heavy_round"),2));var result=item.use(h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND);var shots=owned(h,GrenadeBulletEntity.class,p);
        h.assertTrue(result.consumesAction()&&p.isUsingItem()&&shots.size()==1&&baseDamage(shots.get(0))==3&&pickup(shots.get(0)).equals("DISALLOWED")&&countAmmo(p,ammo("heavy_round"))==1&&p.getCooldowns().isOnCooldown(p.getMainHandItem())&&stack.getDamageValue()==0,"Carbor GL use must fire one owner-bound three-damage non-pickup grenade, consume one heavy round, start cooldown, and preserve launcher durability");PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void carborGlUseTickHonorsCooldownAndReleaseStopsFurtherShots(GameTestHelper h) {
        var p=player(h,4);var item=WeaponContent.CARBOR_GL.get();var stack=new ItemStack(item);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);p.getInventory().setItem(9,new ItemStack(ammo("heavy_round"),3));item.use(h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND);item.onUseTick(h.getLevel(),p,stack,59);boolean blocked=p.getCooldowns().isOnCooldown(p.getMainHandItem())&&countOwner(h,GrenadeBulletEntity.class,p)==1&&countAmmo(p,ammo("heavy_round"))==2;for(int i=0;i<6;i++)p.getCooldowns().tick();item.onUseTick(h.getLevel(),p,stack,53);int countAfterReady=countOwner(h,GrenadeBulletEntity.class,p);int ammoAfterReady=countAmmo(p,ammo("heavy_round"));p.releaseUsingItem();item.onUseTick(h.getLevel(),p,stack,52);
        h.assertTrue(blocked&&countAfterReady==2&&ammoAfterReady==1&&!p.isUsingItem()&&countOwner(h,GrenadeBulletEntity.class,p)==2&&stack.getDamageValue()==0,"Direct use/onUseTick calls must honor the six-native-tick cooldown, fire once when ready, preserve weapon durability, and stop firing after release");PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void carborEntityHitAttributesDirectAndBlastDamageSeparately(GameTestHelper h) {
        var owner=player(h,3);var target=cow(h,owner.position().add(2,0,0),new long[]{0});var bystander=cow(h,owner.position().add(2,0,1),new long[]{0});var bullet=grenade(h,owner);bullet.onHitEntity(new EntityHitResult(target));var attributed=target.hits.stream().anyMatch(hit->owner.getUUID().equals(hit.attacker));var blast=bystander.hits.stream().anyMatch(hit->hit.attacker==null);
        h.assertTrue(attributed&&blast&&target.getHealth()<1000&&bystander.getHealth()<1000,"A direct synthetic GrenadeBullet entity hit must retain the living owner on projectile damage while its null-exploder TNT blast independently damages a nearby bystander");PlayerInteractionTests.release(h,owner);h.succeed();
    }
    public static void carborEntityHitOnPlayerDoesNotCreateTheTntBlast(GameTestHelper h) {
        var owner=player(h,3);var target=player(h,5);var bystander=cow(h,target.position().add(1,0,0),new long[]{0});var bullet=grenade(h,owner);bullet.onHitEntity(new EntityHitResult(target));
        h.assertTrue(target.hurtCalls>0&&bystander.getHealth()==1000&&bystander.hits.isEmpty(),"A direct synthetic GrenadeBullet player hit may deal its native arrow damage but must skip the non-player TNT impact blast");PlayerInteractionTests.release(h,owner);PlayerInteractionTests.release(h,target);h.succeed();
    }
    public static void carborBlockHitCreatesItsTwoPowerTntBlast(GameTestHelper h) {
        var owner=player(h,3);var block=h.absolutePos(new BlockPos(8,2,4));h.getLevel().setBlock(block,Blocks.STONE.defaultBlockState(),3);var target=cow(h,block.getCenter().add(1,0,0),new long[]{0});var bullet=grenade(h,owner);bullet.onHitBlock(new BlockHitResult(block.getCenter(),Direction.UP,block,false));
        h.assertTrue(target.getHealth()<1000&&target.hits.stream().anyMatch(hit->hit.attacker==null),"A direct synthetic GrenadeBullet block hit must create the radius-two TNT blast with no living exploder");PlayerInteractionTests.release(h,owner);h.succeed();
    }

    private static TestPlayer player(GameTestHelper h,int x) { var p=new TestPlayer(h.getLevel());return PlayerInteractionTests.connect(h,p,GameType.SURVIVAL,x+.5,2.5,4.5); }
    private static FlareGunCoreEntity spawnCore(GameTestHelper h,int x) { var pos=h.absolutePos(new BlockPos(x,2,4));var core=WeaponContent.FLARE_GUN_CORE.get().spawn(h.getLevel(),pos,EntitySpawnReason.MOB_SUMMONED);if(core!=null){core.setNoAi(true);core.setNoGravity(true);core.setDeltaMovement(Vec3.ZERO);}return core; }
    private static TestCow cow(GameTestHelper h,Vec3 pos,long[] ticks) { var c=new TestCow(h.getLevel(),ticks);c.setPos(pos);c.setNoAi(true);c.setNoGravity(true);c.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1000);c.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);c.setHealth(1000);h.getLevel().addFreshEntity(c);return c; }
    private static Item ammo(String id) { return MaterialContent.items().get(id).get(); }
    private static int countAmmo(ServerPlayer p,Item item) { return p.getInventory().getNonEquipmentItems().stream().filter(s->s.is(item)).mapToInt(ItemStack::getCount).sum(); }
    private static int count(GameTestHelper h,Class<? extends Entity> type) { return h.getLevel().getEntitiesOfClass(type,area(h,h.absolutePos(new BlockPos(4,2,4)).getCenter(),8)).size(); }
    private static AABB area(GameTestHelper h,Vec3 pos,double radius) { return new AABB(pos,pos).inflate(radius); }
    private static <T extends Entity> List<T> owned(GameTestHelper h,Class<T> type,Entity owner) { return h.getLevel().getEntitiesOfClass(type,area(h,h.absolutePos(new BlockPos(4,2,4)).getCenter(),16),e->e instanceof net.minecraft.world.entity.projectile.Projectile projectile&&projectile.getOwner()==owner); }
    private static int countOwner(GameTestHelper h,Class<? extends Entity> type,Entity owner) { return h.getLevel().getEntitiesOfClass(type,area(h,h.absolutePos(new BlockPos(4,2,4)).getCenter(),16),e->e instanceof net.minecraft.world.entity.projectile.Projectile projectile&&projectile.getOwner()==owner).size(); }
    private static LiteprismDiskProjectileEntity latestOwned(GameTestHelper h,Class<LiteprismDiskProjectileEntity> type,Entity owner) { return owned(h,type,owner).stream().max(java.util.Comparator.comparingInt(Entity::getId)).orElseThrow(); }
    private static void hitDisk(GameTestHelper h,ServerPlayer owner,LivingEntity target) { if(!LiteprismDiskProjectileEntity.fire(h.getLevel(),owner))throw new IllegalStateException("Disk shot submission failed");latestOwned(h,LiteprismDiskProjectileEntity.class,owner).onHitEntity(new EntityHitResult(target)); }
    private static GrenadeBulletEntity grenade(GameTestHelper h,ServerPlayer owner) { var bullet=ProjectileContent.GRENADE_BULLET.get().create(h.getLevel(),EntitySpawnReason.COMMAND);if(bullet==null)throw new IllegalStateException("Grenade entity creation failed");bullet.setOwner(owner);bullet.setBaseDamage(3);bullet.setImpactKnockback(1);bullet.setPos(owner.position().add(0,1,0));h.getLevel().addFreshEntity(bullet);return bullet; }
    private static String pickup(AbstractArrow arrow) { try {Field field=AbstractArrow.class.getDeclaredField("pickup");field.setAccessible(true);return String.valueOf(field.get(arrow));}catch(ReflectiveOperationException e){throw new IllegalStateException(e);} }
    private static double baseDamage(AbstractArrow arrow) { try {Field field=AbstractArrow.class.getDeclaredField("baseDamage");field.setAccessible(true);return field.getDouble(arrow);}catch(ReflectiveOperationException e){throw new IllegalStateException(e);} }
    private static int fieldInt(Object target,String name) { try {Field field=target.getClass().getDeclaredField(name);field.setAccessible(true);return field.getInt(target);}catch(ReflectiveOperationException e){throw new IllegalStateException(e);} }
    private static boolean fieldBoolean(Object target,String name) { try {Field field=target.getClass().getDeclaredField(name);field.setAccessible(true);return field.getBoolean(target);}catch(ReflectiveOperationException e){throw new IllegalStateException(e);} }
    private static UUID fieldUuid(Object target,String name) { try {Field field=target.getClass().getDeclaredField(name);field.setAccessible(true);return (UUID)field.get(target);}catch(ReflectiveOperationException e){throw new IllegalStateException(e);} }
    private static List<Integer> intRange(int first,int last) { var values=new ArrayList<Integer>();int step=first<=last?1:-1;for(int value=first;;value+=step){values.add(value);if(value==last)break;}return values; }
    private static List<Long> longRange(long first,long last) { var values=new ArrayList<Long>();for(long value=first;value<=last;value++)values.add(value);return values; }
    private static final class CycleTrace { private final FlareGunCycleEntity entity;private final boolean alternate;private final long submittedTick;private final List<Integer> stages=new ArrayList<>();private final List<Long> tickOffsets=new ArrayList<>();private Long removedOffset;private CycleTrace(FlareGunCycleEntity entity,boolean alternate,long submittedTick){this.entity=entity;this.alternate=alternate;this.submittedTick=submittedTick;} }
    private record Hit(UUID attacker,float amount,boolean bypass,long tick) { }
    private static final class TestPlayer extends ServerPlayer { private int hurtCalls;TestPlayer(net.minecraft.server.level.ServerLevel level){super(level.getServer(),level,new GameProfile(UUID.randomUUID(),"weapon-core-disk"),net.minecraft.server.level.ClientInformation.createDefault());}@Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level,DamageSource source,float amount){hurtCalls++;return super.hurtServer(level,source,amount);} }
    private static final class TestCow extends Cow { private final long[] ticks;private final List<Hit> hits=new ArrayList<>();TestCow(net.minecraft.server.level.ServerLevel level,long[] ticks){super(EntityType.COW,level);this.ticks=ticks;}@Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level,DamageSource source,float amount){hits.add(new Hit(source.getEntity()==null?null:source.getEntity().getUUID(),amount,source.is(ModDamageTypes.THERM_IFRAME_BYPASS),ticks[0]));return super.hurtServer(level,source,amount);} }
    private WeaponCoreDiskServerTests() { }
}





