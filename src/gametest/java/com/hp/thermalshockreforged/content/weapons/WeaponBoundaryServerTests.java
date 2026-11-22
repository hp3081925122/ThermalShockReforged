package com.hp.thermalshockreforged.content.weapons;

import com.mojang.authlib.GameProfile;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.projectile.ProjectileContent;
import com.hp.thermalshockreforged.entity.projectile.ThermoblasterBulletEntity;
import com.hp.thermalshockreforged.entity.projectile.VolcanoBulletEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import com.hp.thermalshockreforged.gametest.TsrGameTests;
import com.hp.thermalshockreforged.gametest.PlayerInteractionTests;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class WeaponBoundaryServerTests {
            public static void register() {
        TsrGameTests.add("history_weapon_boundary_server_tests_volcano_can_fire_again_only_after_its_thirty_one_cooldown_ticks", WeaponBoundaryServerTests::volcanoCanFireAgainOnlyAfterItsThirtyOneCooldownTicks, 100, "supernova_server");
        TsrGameTests.add("history_weapon_boundary_server_tests_hi_volt_can_be_used_again_only_after_its_one_hundred_twenty_cooldown_ticks", WeaponBoundaryServerTests::hiVoltCanBeUsedAgainOnlyAfterItsOneHundredTwentyCooldownTicks, 100, "supernova_server");
        TsrGameTests.add("history_weapon_boundary_server_tests_buzzsaw_can_start_another_sweep_only_after_its_eleven_cooldown_ticks", WeaponBoundaryServerTests::buzzsawCanStartAnotherSweepOnlyAfterItsElevenCooldownTicks, 100, "supernova_server");
        TsrGameTests.add("history_weapon_boundary_server_tests_volcano_shots_keep_owner_uuid_and_appear_at_each_expected_end_tick", WeaponBoundaryServerTests::volcanoShotsKeepOwnerUuidAndAppearAtEachExpectedEndTick, 100, "supernova_server");
        TsrGameTests.add("history_weapon_boundary_server_tests_cobalt_water_mode_uses_real_player_eyes_and_fires_at_ticks_zero_two_and_four", WeaponBoundaryServerTests::cobaltWaterModeUsesRealPlayerEyesAndFiresAtTicksZeroTwoAndFour, 100, "supernova_server");
        TsrGameTests.add("history_weapon_boundary_server_tests_hi_volt_damage_events_land_immediately_and_at_every_even_end_tick_through_eighteen", WeaponBoundaryServerTests::hiVoltDamageEventsLandImmediatelyAndAtEveryEvenEndTickThroughEighteen, 100, "supernova_server");
        TsrGameTests.add("history_weapon_boundary_server_tests_buzzsaw_sweep_never_damages_asecond_server_player_inside_its_area", WeaponBoundaryServerTests::buzzsawSweepNeverDamagesASecondServerPlayerInsideItsArea, 100, "supernova_server");
        TsrGameTests.add("history_weapon_boundary_server_tests_volcano_burst_continues_after_owner_leaves_and_persists_the_original_uuid", WeaponBoundaryServerTests::volcanoBurstContinuesAfterOwnerLeavesAndPersistsTheOriginalUuid, 100, "supernova_server");
        TsrGameTests.add("history_weapon_boundary_server_tests_cobalt_burst_continues_after_owner_leaves_with_original_uuid_and_tick_schedule", WeaponBoundaryServerTests::cobaltBurstContinuesAfterOwnerLeavesWithOriginalUuidAndTickSchedule, 100, "supernova_server");
    }


public static void volcanoCanFireAgainOnlyAfterItsThirtyOneCooldownTicks(GameTestHelper h) {
        var p=player(h,4); var item=WeaponContent.VOLCANO.get(); p.getInventory().setItem(9,new ItemStack(ammo("heavy_round"),2)); p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(item)); item.onEntitySwing(p.getMainHandItem(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        for(int i=0;i<30;i++)p.getCooldowns().tick(); item.onEntitySwing(p.getMainHandItem(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(p.getCooldowns().isOnCooldown(p.getMainHandItem())&&count(h,VolcanoBurstEntity.class)==1&&countAmmo(p,ammo("heavy_round"))==1,"At thirty native cooldown ticks the retry must remain blocked without spending the second round");
        p.getCooldowns().tick(); item.onEntitySwing(p.getMainHandItem(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(p.getCooldowns().isOnCooldown(p.getMainHandItem())&&count(h,VolcanoBurstEntity.class)==2&&countAmmo(p,ammo("heavy_round"))==0,"At the thirty-first native cooldown tick the next swing must submit a second burst and spend exactly one round"); PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void hiVoltCanBeUsedAgainOnlyAfterItsOneHundredTwentyCooldownTicks(GameTestHelper h) {
        var p=player(h,4); var lance=WeaponContent.HI_VOLT_LANCE.get(); p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(lance)); lance.use(h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        for(int i=0;i<119;i++)p.getCooldowns().tick(); lance.use(h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(p.getCooldowns().isOnCooldown(p.getMainHandItem())&&count(h,HiVoltPulseEntity.class)==1,"At 119 native cooldown ticks a repeated right-click must remain blocked");
        p.getCooldowns().tick(); lance.use(h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(p.getCooldowns().isOnCooldown(p.getMainHandItem())&&count(h,HiVoltPulseEntity.class)==2,"At the 120th native cooldown tick right-click must submit a second electric pulse sequence"); PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void buzzsawCanStartAnotherSweepOnlyAfterItsElevenCooldownTicks(GameTestHelper h) {
        var p=player(h,4); var victim=cow(h,p.position().add(.5,0,0),new long[]{0}); var item=WeaponContent.BUZZSAW.get(); p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(item)); item.onEntitySwing(p.getMainHandItem(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        for(int i=0;i<10;i++)p.getCooldowns().tick(); item.onEntitySwing(p.getMainHandItem(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(p.getCooldowns().isOnCooldown(p.getMainHandItem())&&count(h,BuzzsawSweepEntity.class)==1&&victim.getHealth()==998,"At ten cooldown ticks Buzzsaw must block a second sweep and must not cut the target again");
        p.getCooldowns().tick(); item.onEntitySwing(p.getMainHandItem(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(p.getCooldowns().isOnCooldown(p.getMainHandItem())&&count(h,BuzzsawSweepEntity.class)==2&&victim.getHealth()==996,"At eleven cooldown ticks Buzzsaw must allow a second sweep and immediate two damage"); PlayerInteractionTests.release(h,p);h.succeed();
    }
    public static void volcanoShotsKeepOwnerUuidAndAppearAtEachExpectedEndTick(GameTestHelper h) {
        var first=player(h,3); var second=player(h,12); var firstId=first.getUUID(); var secondId=second.getUUID(); first.getInventory().setItem(9,new ItemStack(ammo("heavy_round"),1)); second.getInventory().setItem(9,new ItemStack(ammo("heavy_round"),1)); var ticks=new long[]{0}; Consumer<ServerTickEvent.Post> clock=e->{if(e.getServer()==h.getLevel().getServer())ticks[0]++;}; Map<UUID,List<Long>> arrivals=new HashMap<>(); Consumer<EntityJoinLevelEvent> joins=e->{if(e.getLevel()==h.getLevel()&&e.getEntity() instanceof VolcanoBulletEntity bullet){UUID id=ownerId(h,bullet);arrivals.computeIfAbsent(id,k->new ArrayList<>()).add(ticks[0]);}}; NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST,ServerTickEvent.Post.class,clock); NeoForge.EVENT_BUS.addListener(EntityJoinLevelEvent.class,joins); long firstSubmit=ticks[0]; WeaponContent.VOLCANO.get().onEntitySwing(new ItemStack(WeaponContent.VOLCANO.get()),first,net.minecraft.world.InteractionHand.MAIN_HAND); long secondSubmit=ticks[0]; WeaponContent.VOLCANO.get().onEntitySwing(new ItemStack(WeaponContent.VOLCANO.get()),second,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.runAfterDelay(7,()->{NeoForge.EVENT_BUS.unregister(joins);NeoForge.EVENT_BUS.unregister(clock);var a=offsets(arrivals.get(firstId),firstSubmit);var b=offsets(arrivals.get(secondId),secondSubmit);h.assertTrue(a.equals(List.of(0L,1L,2L,3L,4L))&&b.equals(List.of(0L,1L,2L,3L,4L)),"Each owner's Volcano bullets must retain its UUID and fire synchronously then once on each of the next four END ticks; first="+a+", second="+b);PlayerInteractionTests.release(h,first);PlayerInteractionTests.release(h,second);h.succeed();});
    }
    public static void cobaltWaterModeUsesRealPlayerEyesAndFiresAtTicksZeroTwoAndFour(GameTestHelper h) {
        var p=player(h,4); fillWaterAround(h,p); p.baseTick();p.baseTick();p.baseTick(); h.assertTrue(p.isUnderWater(),"ServerPlayer must detect water at its actual eyes after base ticks"); var owner=p.getUUID(); p.getInventory().setItem(9,new ItemStack(ammo("specialized_round"),1)); var ticks=new long[]{0}; Consumer<ServerTickEvent.Post> clock=e->{if(e.getServer()==h.getLevel().getServer())ticks[0]++;}; List<Long> arrivals=new ArrayList<>(); Consumer<EntityJoinLevelEvent> joins=e->{if(e.getLevel()==h.getLevel()&&e.getEntity() instanceof ThermoblasterBulletEntity bullet&&ownerId(h,bullet).equals(owner))arrivals.add(ticks[0]);}; NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST,ServerTickEvent.Post.class,clock);NeoForge.EVENT_BUS.addListener(EntityJoinLevelEvent.class,joins);long submit=ticks[0];var gun=WeaponContent.COBALT_THERMOBLASTER.get();p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(gun));gun.onEntitySwing(p.getMainHandItem(),p,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.runAfterDelay(7,()->{NeoForge.EVENT_BUS.unregister(joins);NeoForge.EVENT_BUS.unregister(clock);var deltas=offsets(arrivals,submit);h.assertTrue(deltas.equals(List.of(0L,2L,4L)),"Water-detected Cobalt left-click must make three owner-UUID-matched single shots at submit, END+2, END+4; observed="+deltas);PlayerInteractionTests.release(h,p);h.succeed();});
    }
    public static void hiVoltDamageEventsLandImmediatelyAndAtEveryEvenEndTickThroughEighteen(GameTestHelper h) {
        var p=player(h,4); var ticks=new long[]{0}; Consumer<ServerTickEvent.Post> clock=e->{if(e.getServer()==h.getLevel().getServer())ticks[0]++;}; NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST,ServerTickEvent.Post.class,clock); var victim=cow(h,p.position().add(1,0,0),ticks); victim.hitTicks.clear(); long submit=ticks[0]; HiVoltPulseEntity.submit(h.getLevel(),p,true);
        h.runAfterDelay(25,()->{NeoForge.EVENT_BUS.unregister(clock);var deltas=offsets(victim.hitTicks,submit);h.assertTrue(deltas.equals(List.of(0L,2L,4L,6L,8L,10L,12L,14L,16L,18L))&&victim.getHealth()==985,"Hi-Volt must damage at the immediate pulse and exactly every even END through eighteen for ten 1.5-point hits; ticks="+deltas+", health="+victim.getHealth());PlayerInteractionTests.release(h,p);h.succeed();});
    }
    public static void buzzsawSweepNeverDamagesASecondServerPlayerInsideItsArea(GameTestHelper h) {
        var owner=player(h,4);var other=player(h,5);var victim=cow(h,owner.position().add(.5,0,0),new long[]{0});float ownerHealth=owner.getHealth(),otherHealth=other.getHealth(),victimHealth=victim.getHealth();WeaponContent.BUZZSAW.get().onEntitySwing(new ItemStack(WeaponContent.BUZZSAW.get()),owner,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(owner.getHealth()==ownerHealth&&other.getHealth()==otherHealth&&other.hurtCalls==0&&victim.getHealth()==victimHealth-2,"Buzzsaw must exclude both owner and another ServerPlayer while still dealing two damage to a nearby non-player entity");PlayerInteractionTests.release(h,owner);PlayerInteractionTests.release(h,other);h.succeed();
    }
    public static void volcanoBurstContinuesAfterOwnerLeavesAndPersistsTheOriginalUuid(GameTestHelper h) {
        var p=player(h,4);var owner=p.getUUID();p.getInventory().setItem(9,new ItemStack(ammo("heavy_round"),1));var shots=new ArrayList<UUID>();var ticks=new long[]{0};Consumer<ServerTickEvent.Post> clock=e->{if(e.getServer()==h.getLevel().getServer())ticks[0]++;};Consumer<EntityJoinLevelEvent> joins=e->{if(e.getLevel()==h.getLevel()&&e.getEntity() instanceof VolcanoBulletEntity bullet&&owner.equals(ownerId(h,bullet)))shots.add(ownerId(h,bullet));};NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST,ServerTickEvent.Post.class,clock);NeoForge.EVENT_BUS.addListener(EntityJoinLevelEvent.class,joins);WeaponContent.VOLCANO.get().onEntitySwing(new ItemStack(WeaponContent.VOLCANO.get()),p,net.minecraft.world.InteractionHand.MAIN_HAND);PlayerInteractionTests.release(h,p);
        h.runAfterDelay(7,()->{NeoForge.EVENT_BUS.unregister(joins);NeoForge.EVENT_BUS.unregister(clock);h.assertTrue(shots.size()==5&&shots.stream().allMatch(owner::equals),"Volcano's five shots must continue after player removal and preserve the original owner UUID on every projectile; observed="+shots);h.succeed();});
    }
    public static void cobaltBurstContinuesAfterOwnerLeavesWithOriginalUuidAndTickSchedule(GameTestHelper h) {
        var p=player(h,4);fillWaterAround(h,p);p.baseTick();p.baseTick();p.baseTick();var owner=p.getUUID();p.getInventory().setItem(9,new ItemStack(ammo("specialized_round"),1));var ticks=new long[]{0};Consumer<ServerTickEvent.Post> clock=e->{if(e.getServer()==h.getLevel().getServer())ticks[0]++;};Map<UUID,List<Long>> arrivals=new HashMap<>();Consumer<EntityJoinLevelEvent> joins=e->{if(e.getLevel()==h.getLevel()&&e.getEntity() instanceof ThermoblasterBulletEntity bullet){UUID id=ownerId(h,bullet);arrivals.computeIfAbsent(id,k->new ArrayList<>()).add(ticks[0]);}};NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST,ServerTickEvent.Post.class,clock);NeoForge.EVENT_BUS.addListener(EntityJoinLevelEvent.class,joins);long submit=ticks[0];var gun=WeaponContent.COBALT_THERMOBLASTER.get();p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(gun));gun.onEntitySwing(p.getMainHandItem(),p,net.minecraft.world.InteractionHand.MAIN_HAND);PlayerInteractionTests.release(h,p);
        h.runAfterDelay(7,()->{NeoForge.EVENT_BUS.unregister(joins);NeoForge.EVENT_BUS.unregister(clock);var deltas=offsets(arrivals.get(owner),submit);h.assertTrue(deltas.equals(List.of(0L,2L,4L)),"Cobalt's three shots must continue after player removal at offsets 0/2/4 and keep the original owner UUID; observed="+deltas);PlayerInteractionTests.release(h,p);h.succeed();});
    }

    private static TestPlayer player(GameTestHelper h,int x) { var p=new TestPlayer(h.getLevel());return PlayerInteractionTests.connect(h,p,GameType.SURVIVAL,x+.5,2.5,4.5); }
    private static void fillWaterAround(GameTestHelper h,ServerPlayer player) { var feet=player.blockPosition();for(var pos:BlockPos.betweenClosed(feet.offset(-1,-1,-1),feet.offset(1,2,1)))h.getLevel().setBlock(pos,Blocks.WATER.defaultBlockState(),3); }
    private static TestCow cow(GameTestHelper h,Vec3 pos,long[] ticks) { var c=new TestCow(h.getLevel(),ticks);c.setPos(pos);c.setNoAi(true);c.setNoGravity(true);c.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1000);c.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);c.setHealth(1000);h.getLevel().addFreshEntity(c);return c; }
    private static UUID ownerId(GameTestHelper h,Projectile projectile) { return TsrGameTests.roundTrip(h,projectile).read("Owner",net.minecraft.core.UUIDUtil.CODEC).orElse(null); }
    private static List<Long> offsets(List<Long> ticks,long submit) { if(ticks==null)return List.of();return ticks.stream().map(t->t-submit).toList(); }
    private static int count(GameTestHelper h,Class<? extends Entity> type) { var pos=h.absolutePos(new BlockPos(4,2,4)).getCenter();return h.getLevel().getEntitiesOfClass(type,new AABB(pos,pos).inflate(8)).size(); }
    private static int countAmmo(ServerPlayer p,Item item) { return p.getInventory().getNonEquipmentItems().stream().filter(s->s.is(item)).mapToInt(ItemStack::getCount).sum(); }
    private static Item ammo(String id) { return MaterialContent.items().get(id).get(); }
    private static final class TestPlayer extends ServerPlayer { private int hurtCalls; TestPlayer(net.minecraft.server.level.ServerLevel level){super(level.getServer(),level,new GameProfile(UUID.randomUUID(),"weapon-boundary"),net.minecraft.server.level.ClientInformation.createDefault());} @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level,DamageSource source,float amount){hurtCalls++;return super.hurtServer(level,source,amount);} }
    private static final class TestCow extends Cow { private final List<Long> hitTicks=new ArrayList<>(); private final long[] ticks; TestCow(net.minecraft.server.level.ServerLevel level,long[] ticks){super(EntityType.COW,level);this.ticks=ticks;}@Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level,DamageSource source,float amount){hitTicks.add(ticks[0]);return super.hurtServer(level,source,amount);}}
    private WeaponBoundaryServerTests() { }
}






