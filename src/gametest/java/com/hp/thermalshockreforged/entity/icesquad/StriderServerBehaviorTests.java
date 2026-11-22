package com.hp.thermalshockreforged.entity.icesquad;

import net.minecraft.world.entity.EntitySpawnReason;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.storage.TagValueOutput;

import net.minecraft.world.level.storage.TagValueInput;

import net.minecraft.util.ProblemReporter;

import com.hp.thermalshockreforged.gametest.TsrGameTests;
import com.hp.thermalshockreforged.gametest.PlayerInteractionTests;
import net.minecraft.world.level.GameType;

import com.mojang.authlib.GameProfile;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import net.minecraft.gametest.framework.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
public final class StriderServerBehaviorTests {
    public static void constructorConsumesItselfAndCommitsOneActualStrider(GameTestHelper h){
        var pos=pos(h);var state=IceSquadContent.STRIDER_CONSTRUCTOR.get().defaultBlockState();h.getLevel().setBlock(pos,state,3);
        var list=striders(h);h.assertTrue(h.getLevel().getBlockState(pos).isAir()&&list.size()==1,"Constructor must consume its actual block and commit exactly one real strider");
        h.assertTrue(h.getLevel().getEntity(list.getFirst().getUUID())==list.getFirst(),"Constructor result must actually be accepted by the world");h.succeed();
    }
    public static void sameBlockPlacementCallbackDoesNotSpawnOrConsumeAnotherConstructor(GameTestHelper h){
        var pos=pos(h);var state=IceSquadContent.STRIDER_CONSTRUCTOR.get().defaultBlockState();h.getLevel().setBlock(pos,state,3);striders(h).forEach(Entity::discard);
        IceSquadContent.STRIDER_CONSTRUCTOR.get().onPlace(state,h.getLevel(),pos,state,false);
        h.assertTrue(striders(h).isEmpty()&&h.getLevel().getBlockState(pos).isAir(),"A repeated same-block onPlace callback must not create another entity");h.succeed();
    }
    public static void cancelledConstructorEntityJoinKeepsOriginalSingleConsumptionSemantics(GameTestHelper h){
        var pos=pos(h);Consumer<EntityJoinLevelEvent> veto=e->{if(e.getLevel()==h.getLevel()&&e.getEntity() instanceof IcidrostriderEntity&&e.getEntity().distanceToSqr(Vec3.atCenterOf(pos))<4)e.setCanceled(true);};NeoForge.EVENT_BUS.addListener(veto);
        try{h.getLevel().setBlock(pos,IceSquadContent.STRIDER_CONSTRUCTOR.get().defaultBlockState(),3);h.assertTrue(h.getLevel().getBlockState(pos).isAir()&&striders(h).isEmpty(),"Rejected spawn must retain documented one-time consumed constructor semantics, without infinite retry");}finally{NeoForge.EVENT_BUS.unregister(veto);}h.succeed();
    }
    public static void realRightClickMountsTheActualControllingPassenger(GameTestHelper h){
        var owner=strider(h);var rider=player(h);h.assertTrue(owner.mobInteract(rider,InteractionHand.MAIN_HAND).consumesAction()&&rider.getVehicle()==owner&&owner.getControllingPassenger()==rider,"Real native interaction must actually mount the rider and select it as controller");
        rider.stopRiding();h.assertTrue(owner.getControllingPassenger()==null&&!rider.isPassenger(),"Dismount must remove controller authority");owner.setHealth(0);h.assertTrue(owner.mobInteract(rider,InteractionHand.MAIN_HAND)==net.minecraft.world.InteractionResult.PASS&&!rider.isPassenger(),"A dead strider must reject interaction");PlayerInteractionTests.release(h,rider);h.succeed();
    }
    public static void riddenInputAndNativeRotationPreserveBothAxesAndHalfPitch(GameTestHelper h){
        var owner=strider(h);var rider=player(h);rider.xxa=.65F;rider.zza=-.4F;rider.setYRot(73);rider.setXRot(42);
        Vec3 input=owner.getRiddenInput(rider,new Vec3(9,8,7));h.assertTrue(Math.abs(input.x-.65)<1E-6&&input.y==0&&Math.abs(input.z+.4)<1E-6,"Native ridden input must preserve both rider axes without a jump component");owner.tickRidden(rider,Vec3.ZERO);
        h.assertTrue(owner.getYRot()==73&&owner.yRotO==73&&owner.yBodyRot==73&&owner.yHeadRot==73&&owner.getXRot()==21&&owner.getRiddenSpeed(rider)==(float)owner.getAttributeValue(Attributes.MOVEMENT_SPEED),"Native ridden rotation and configured movement speed must match the actual rider");PlayerInteractionTests.release(h,rider);h.succeed();
    }
    public static void unmountedAndNoncontrollingPassengersCannotFire(GameTestHelper h){
        var owner=strider(h);var rider=player(h);var outsider=player(h);owner.requestFire(outsider);h.assertTrue(arrows(h).isEmpty(),"A nearby unmounted player must have no authority to fire");
        h.assertTrue(rider.startRiding(owner,true,true)&&outsider.startRiding(owner,true,true),"Fixture must actually mount both players to exercise controller filtering");h.assertTrue(owner.getControllingPassenger()==rider,"First rider must remain controller");owner.requestFire(outsider);h.assertTrue(arrows(h).isEmpty()&&save(owner).getIntOr("StriderFireCooldown", 0)==0,"A noncontrolling passenger must not fire or consume cooldown");rider.stopRiding();outsider.stopRiding();PlayerInteractionTests.release(h,rider);PlayerInteractionTests.release(h,outsider);h.succeed();
    }
    public static void actualFireUsesRiderOwnershipConfiguredBaseAttackAndNativePiercingBridge(GameTestHelper h){
        var owner=strider(h);var rider=mount(h,owner);rider.setYRot(-90);rider.setXRot(37);owner.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8);owner.requestFire(rider);var arrow=onlyArrow(h);
        h.assertTrue(arrow.getOwner()==rider&&save(arrow).getDoubleOr("damage", 0.0)==28&&arrow.getPierceLevel()==5&&arrow.isCritArrow()&&arrow.isSilent()&&arrow.pickup==AbstractArrow.Pickup.DISALLOWED,"Native projectile must retain rider owner, base attack times 3.5, genuine pierce5, crit, silent and no pickup");
        h.assertTrue(arrow.position().equals(rider.position())&&arrow.getDeltaMovement().y==0&&Math.abs(arrow.getDeltaMovement().length()-1)<1E-6&&save(arrow).getDoubleOr("SquadProjectileKnockback", 0.0)==0,"Fire must start at rider feet with speed one and no Y motion or impact knockback");
        h.assertTrue(!arrow.canHitEntity(owner)&&!arrow.canHitEntity(rider),"Native projectile eligibility must exclude the caster and its owner");rider.stopRiding();PlayerInteractionTests.release(h,rider);h.succeed();
    }
    public static void cooldownRejectsSameTickAndNineTicksThenAcceptsAtTen(GameTestHelper h){
        var owner=strider(h);var rider=mount(h,owner);long start=h.getLevel().getGameTime();owner.requestFire(rider);h.assertTrue(arrows(h).size()==1,"Initial fire must commit one projectile");clearArrows(h);owner.requestFire(rider);h.assertTrue(arrows(h).isEmpty(),"Same-tick repeat must be rejected");
        h.runAfterDelay(9,()->{h.assertTrue(h.getLevel().getGameTime()-start==9,"Cooldown fixture must reach exactly nine world ticks");owner.requestFire(rider);h.assertTrue(arrows(h).isEmpty(),"Ninth world tick must still be on cooldown");});
        h.runAfterDelay(10,()->{h.assertTrue(h.getLevel().getGameTime()-start==10,"Cooldown fixture must reach exactly ten world ticks");owner.requestFire(rider);h.assertTrue(arrows(h).size()==1,"Tenth world tick must permit one new projectile");rider.stopRiding();PlayerInteractionTests.release(h,rider);h.succeed();});
    }
    public static void reloadRetainsRemainingCooldownAndCannotResetByRemounting(GameTestHelper h){
        var owner=strider(h);var rider=mount(h,owner);owner.requestFire(rider);clearArrows(h);var tag=save(owner);h.assertTrue(tag.getIntOr("StriderFireCooldown", 0)==10,"Saved fire cooldown must be ten remaining ticks");putUuid(tag, "UUID", UUID.randomUUID());rider.stopRiding();owner.discard();var restored=IceSquadContent.STRIDER.get().create(h.getLevel(), EntitySpawnReason.COMMAND);load(restored, tag);restored.setNoAi(true);restored.setNoGravity(true);h.getLevel().addFreshEntity(restored);h.assertTrue(rider.startRiding(restored,true,true),"Rider must actually remount the restored entity");restored.requestFire(rider);h.assertTrue(arrows(h).isEmpty(),"NBT reload and remount must not reset remaining cooldown");
        h.runAfterDelay(10,()->{restored.requestFire(rider);h.assertTrue(arrows(h).size()==1&&restored.isPersistenceRequired(),"The reanchored remaining deadline must permit firing and retain persistence");rider.stopRiding();PlayerInteractionTests.release(h,rider);h.succeed();});
    }
    public static void projectileLivesFourTicksThenExpiresAtFiveAndCannotBePickedUp(GameTestHelper h){
        var arrow=IceSquadContent.STRIDER_ATTACK.get().create(h.getLevel(), EntitySpawnReason.COMMAND);arrow.setPos(Vec3.atCenterOf(pos(h).above(2)));h.getLevel().addFreshEntity(arrow);
        for(int i=0;i<4;i++){arrow.tick();arrow.endTick(h.getLevel());h.assertTrue(!arrow.isRemoved()&&arrow.isNoGravity(),"Strider projectile must survive before its fifth server END tick and retain native no-gravity state");}
        arrow.tick();arrow.endTick(h.getLevel());h.assertTrue(arrow.isRemoved()&&save(arrow).getIntOr("SquadProjectileRemainingEndTicks", -1)==0,"The actual projectile must expire at its fifth server END tick");
        var restored=IceSquadContent.STRIDER_ATTACK.get().create(h.getLevel(), EntitySpawnReason.COMMAND);var tag=save(arrow);tag.putByte("pickup",(byte)1);tag.putInt("SquadProjectileAge",4);load(restored, tag);h.assertTrue(restored.pickup==AbstractArrow.Pickup.DISALLOWED,"NBT reload must override illicit pickup eligibility");restored.tick();restored.endTick(h.getLevel());h.assertTrue(restored.isRemoved(),"An age-four restored projectile must consume only the remaining final tick");h.succeed();
    }
    public static void genuinePierceFiveNativeHitChainHurtsSixTargetsAndStopsBeforeSeventh(GameTestHelper h){
        var owner=strider(h);var rider=mount(h,owner);owner.requestFire(rider);var arrow=onlyArrow(h);var victims=new ArrayList<TestCow>();
        for(int i=0;i<7;i++){var c=cow(h,Vec3.atCenterOf(pos(h)).add(3,0,0));victims.add(c);arrow.onHitEntity(new EntityHitResult(c));}
        h.assertTrue(victims.subList(0,6).stream().allMatch(c->c.hits==1&&c.getHealth()<1000&&c.getArrowCount()==0)&&victims.get(6).hits==0&&victims.get(6).getHealth()==1000&&arrow.isRemoved(),"The actual native pierce5 hit chain must hurt six distinct targets, remove embedded-arrow counts and stop before target seven");rider.stopRiding();PlayerInteractionTests.release(h,rider);h.succeed();
    }
    public static void allOriginalDamageImmunitiesRemainSpecificAndOrdinaryDamageStillWorks(GameTestHelper h){
        var owner=strider(h);float health=owner.getHealth();var level=h.getLevel();var arrow=IceSquadContent.STRIDER_ATTACK.get().create(level, EntitySpawnReason.COMMAND);var potion=new ThrownSplashPotion(level,0,0,0,new ItemStack(Items.SPLASH_POTION));var cloud=new AreaEffectCloud(level,0,0,0);
        DamageSource[] immune={level.damageSources().arrow(arrow,null),level.damageSources().thrown(potion,null),level.damageSources().indirectMagic(cloud,null),level.damageSources().fall(),level.damageSources().cactus(),level.damageSources().drown(),level.damageSources().dragonBreath()};
        for(var source:immune)h.assertTrue(!owner.hurtServer((ServerLevel)owner.level(), source,4)&&owner.getHealth()==health,"Each original direct-source/type immunity must preserve health: "+source.getMsgId());h.assertTrue(owner.hurtServer((ServerLevel)owner.level(), level.damageSources().generic(),4)&&owner.getHealth()<health,"Ordinary generic damage must remain valid");h.succeed();
    }
    public static void striderNeverUsesOrdinaryMeleeAndRemainsPersistentAcrossReload(GameTestHelper h){
        var owner=strider(h);var victim=cow(h,owner.position().add(2,0,0));h.assertTrue(!owner.doHurtTarget((ServerLevel)owner.level(),victim)&&victim.getHealth()==1000,"The strider itself must never perform ordinary melee damage");h.assertTrue(!owner.removeWhenFarAway(Double.MAX_VALUE)&&owner.isPersistenceRequired(),"Native distance despawn must remain disabled");var tag=save(owner);tag.putBoolean("PersistenceRequired",false);tag.putString("Texture","fixture_texture");var restored=IceSquadContent.STRIDER.get().create(h.getLevel(), EntitySpawnReason.COMMAND);load(restored, tag);h.assertTrue(restored.isPersistenceRequired()&&!restored.removeWhenFarAway(Double.MAX_VALUE)&&restored.texture().equals("fixture_texture"),"Reload must restore persistent behavior independently of saved persistence flag and keep texture state");h.succeed();
    }
    public static void deathReturnsOneConstructorOnlyWhenNativeMobLootRuleAllows(GameTestHelper h){
        var level=h.getLevel();boolean previous=level.getGameRules().get(GameRules.MOB_DROPS);
        try{for(boolean enabled:new boolean[]{true,false}){level.getGameRules().set(GameRules.MOB_DROPS,enabled,level.getServer());var owner=strider(h);h.assertTrue(owner.hurtServer((ServerLevel)owner.level(), level.damageSources().generic(),1000)&&owner.isDeadOrDying(),"Ordinary lethal damage must enter actual strider death flow");var drops=level.getEntitiesOfClass(ItemEntity.class,new AABB(pos(h)).inflate(4));int constructors=drops.stream().filter(e->e.getItem().is(IceSquadContent.STRIDER_CONSTRUCTOR_ITEM.get())).mapToInt(e->e.getItem().getCount()).sum();h.assertTrue(constructors==(enabled?1:0),"Death must recover exactly one constructor only under doMobLoot=true");owner.tickDeath();h.assertTrue(owner.isRemoved()&&owner.deathTime==1&&level.getEntitiesOfClass(ExperienceOrb.class,new AABB(pos(h)).inflate(4)).isEmpty(),"Original one-tick death removal and zero XP must remain intact");drops.forEach(Entity::discard);}}
        finally{level.getGameRules().set(GameRules.MOB_DROPS,previous,level.getServer());}h.succeed();
    }
    public static void cancelledProjectileJoinDoesNotPermitUnboundedSameTickRetries(GameTestHelper h){
        var owner=strider(h);var rider=mount(h,owner);Consumer<EntityJoinLevelEvent> veto=e->{if(e.getLevel()==h.getLevel()&&e.getEntity() instanceof IceSquadProjectileEntity arrow&&arrow.getOwner()==rider)e.setCanceled(true);};NeoForge.EVENT_BUS.addListener(veto);
        try{owner.requestFire(rider);h.assertTrue(arrows(h).isEmpty()&&save(owner).getIntOr("StriderFireCooldown", 0)==10,"Rejected projectile join must retain documented fire-attempt cooldown");owner.requestFire(rider);h.assertTrue(arrows(h).isEmpty(),"Repeated same-tick requests must not produce retry storms");}finally{NeoForge.EVENT_BUS.unregister(veto);rider.stopRiding();PlayerInteractionTests.release(h,rider);}h.succeed();
    }

    private static BlockPos pos(GameTestHelper h){return h.absolutePos(new BlockPos(10,2,8));}
    private static IcidrostriderEntity strider(GameTestHelper h){var e=IceSquadContent.STRIDER.get().create(h.getLevel(), EntitySpawnReason.COMMAND);e.setPos(Vec3.atCenterOf(pos(h)));e.setNoAi(true);e.setNoGravity(true);h.assertTrue(h.getLevel().addFreshEntity(e),"Actual strider fixture must join its level");return e;}
    private static List<IcidrostriderEntity> striders(GameTestHelper h){return h.getLevel().getEntitiesOfClass(IcidrostriderEntity.class,new AABB(pos(h)).inflate(4));}
    private static ServerPlayer player(GameTestHelper h){var p=PlayerInteractionTests.player(h,GameType.SURVIVAL,10.5,2.5,10.5);return p;}
    private static ServerPlayer mount(GameTestHelper h,IcidrostriderEntity owner){var rider=player(h);h.assertTrue(rider.startRiding(owner,true,true)&&owner.getControllingPassenger()==rider,"Fixture must use real native startRiding and controlling passenger state");return rider;}
    private static CompoundTag save(Entity entity){var output=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess());entity.saveWithoutId(output);return output.buildResult();}
    private static void load(Entity entity,CompoundTag tag){entity.load(TagValueInput.create(ProblemReporter.DISCARDING,((ServerLevel)entity.level()).registryAccess(),tag));}
    private static void putUuid(CompoundTag tag,String key,UUID value){tag.put(key,net.minecraft.core.UUIDUtil.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,value).result().orElseThrow());}
    private static List<IceSquadProjectileEntity> arrows(GameTestHelper h){return h.getLevel().getEntitiesOfClass(IceSquadProjectileEntity.class,new AABB(pos(h)).inflate(10),e->e.getType()==IceSquadContent.STRIDER_ATTACK.get());}
    private static IceSquadProjectileEntity onlyArrow(GameTestHelper h){var a=arrows(h);h.assertTrue(a.size()==1,"Exactly one actual strider projectile must be in the world");return a.getFirst();}
    private static void clearArrows(GameTestHelper h){arrows(h).forEach(Entity::discard);}
    private static TestCow cow(GameTestHelper h,Vec3 position){var c=new TestCow(h.getLevel());c.setNoAi(true);c.setNoGravity(true);c.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1000);c.setHealth(1000);c.setPos(position);h.getLevel().addFreshEntity(c);return c;}
    private static final class TestCow extends Cow{private int hits;TestCow(ServerLevel level){super(EntityType.COW,level);}@Override public boolean hurtServer(ServerLevel level,DamageSource source,float amount){hits++;return super.hurtServer(level,source,amount);}}
    private StriderServerBehaviorTests(){}
    public static void register() {
        TsrGameTests.add("history_strider_server_behavior_tests_constructor_consumes_itself_and_commits_one_actual_strider", StriderServerBehaviorTests::constructorConsumesItselfAndCommitsOneActualStrider, 100, "supernova_server");
        TsrGameTests.add("history_strider_server_behavior_tests_same_block_placement_callback_does_not_spawn_or_consume_another_constructor", StriderServerBehaviorTests::sameBlockPlacementCallbackDoesNotSpawnOrConsumeAnotherConstructor, 100, "supernova_server");
        TsrGameTests.add("history_strider_server_behavior_tests_cancelled_constructor_entity_join_keeps_original_single_consumption_semantics", StriderServerBehaviorTests::cancelledConstructorEntityJoinKeepsOriginalSingleConsumptionSemantics, 100, "supernova_server");
        TsrGameTests.add("history_strider_server_behavior_tests_real_right_click_mounts_the_actual_controlling_passenger", StriderServerBehaviorTests::realRightClickMountsTheActualControllingPassenger, 100, "supernova_server");
        TsrGameTests.add("history_strider_server_behavior_tests_ridden_input_and_native_rotation_preserve_both_axes_and_half_pitch", StriderServerBehaviorTests::riddenInputAndNativeRotationPreserveBothAxesAndHalfPitch, 100, "supernova_server");
        TsrGameTests.add("history_strider_server_behavior_tests_unmounted_and_noncontrolling_passengers_cannot_fire", StriderServerBehaviorTests::unmountedAndNoncontrollingPassengersCannotFire, 100, "supernova_server");
        TsrGameTests.add("history_strider_server_behavior_tests_actual_fire_uses_rider_ownership_configured_base_attack_and_native_piercing_bridge", StriderServerBehaviorTests::actualFireUsesRiderOwnershipConfiguredBaseAttackAndNativePiercingBridge, 100, "supernova_server");
        TsrGameTests.add("history_strider_server_behavior_tests_cooldown_rejects_same_tick_and_nine_ticks_then_accepts_at_ten", StriderServerBehaviorTests::cooldownRejectsSameTickAndNineTicksThenAcceptsAtTen, 20, "supernova_server");
        TsrGameTests.add("history_strider_server_behavior_tests_reload_retains_remaining_cooldown_and_cannot_reset_by_remounting", StriderServerBehaviorTests::reloadRetainsRemainingCooldownAndCannotResetByRemounting, 20, "supernova_server");
        TsrGameTests.add("history_strider_server_behavior_tests_projectile_lives_four_ticks_then_expires_at_five_and_cannot_be_picked_up", StriderServerBehaviorTests::projectileLivesFourTicksThenExpiresAtFiveAndCannotBePickedUp, 100, "supernova_server");
        TsrGameTests.add("history_strider_server_behavior_tests_genuine_pierce_five_native_hit_chain_hurts_six_targets_and_stops_before_seventh", StriderServerBehaviorTests::genuinePierceFiveNativeHitChainHurtsSixTargetsAndStopsBeforeSeventh, 100, "supernova_server");
        TsrGameTests.add("history_strider_server_behavior_tests_all_original_damage_immunities_remain_specific_and_ordinary_damage_still_works", StriderServerBehaviorTests::allOriginalDamageImmunitiesRemainSpecificAndOrdinaryDamageStillWorks, 100, "supernova_server");
        TsrGameTests.add("history_strider_server_behavior_tests_strider_never_uses_ordinary_melee_and_remains_persistent_across_reload", StriderServerBehaviorTests::striderNeverUsesOrdinaryMeleeAndRemainsPersistentAcrossReload, 100, "supernova_server");
        TsrGameTests.add("history_strider_server_behavior_tests_death_returns_one_constructor_only_when_native_mob_loot_rule_allows", StriderServerBehaviorTests::deathReturnsOneConstructorOnlyWhenNativeMobLootRuleAllows, 100, "supernova_server");
        TsrGameTests.add("history_strider_server_behavior_tests_cancelled_projectile_join_does_not_permit_unbounded_same_tick_retries", StriderServerBehaviorTests::cancelledProjectileJoinDoesNotPermitUnboundedSameTickRetries, 100, "supernova_server");
    }
}
