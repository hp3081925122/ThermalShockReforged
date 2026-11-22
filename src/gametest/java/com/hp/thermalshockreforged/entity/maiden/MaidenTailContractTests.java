package com.hp.thermalshockreforged.entity.maiden;
import net.minecraft.world.entity.EntitySpawnReason;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.phys.Vec3;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
public final class MaidenTailContractTests {
    private static final Map<UUID,List<Shot>> SHOTS=new ConcurrentHashMap<>();
    static{NeoForge.EVENT_BUS.addListener(MaidenTailContractTests::joined);}
    public static void removedOwnerCompletesSingleRingUsingSavedUuid(GameTestHelper h){run(h,true);}
    public static void removedOwnerCompletesAllTripleRingsUsingSavedUuid(GameTestHelper h){run(h,false);}
    private static void run(GameTestHelper h,boolean single){var owner=MaidenContent.MAIDEN.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);owner.setNoAi(true);owner.setPos(pos(h,8,2,8));h.getLevel().addFreshEntity(owner);owner.getRandom().setSeed(seed(single));var target=EntityType.COW.create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);target.setNoAi(true);target.setNoGravity(true);target.setPos(pos(h,11,2,8));h.getLevel().addFreshEntity(target);owner.beginAttack(target);UUID id=owner.getUUID();Vec3 start=owner.position();owner.setPos(pos(h,10,2,13));Vec3 last=owner.position();double eye=target.getEyeHeight();owner.discard();h.runAfterDelay(50,()->{List<Shot> shots=List.copyOf(SHOTS.getOrDefault(id,List.of()));var ticks=shots.stream().collect(java.util.stream.Collectors.groupingBy(Shot::tick,TreeMap::new,java.util.stream.Collectors.toList()));int expected=single?8:24;boolean times=single?ticks.size()==1&&ticks.firstEntry().getValue().size()==8:ticks.size()==3&&ticks.values().stream().allMatch(v->v.size()==8)&&new ArrayList<>(ticks.keySet()).get(1)-new ArrayList<>(ticks.keySet()).get(0)==5&&new ArrayList<>(ticks.keySet()).get(2)-new ArrayList<>(ticks.keySet()).get(1)==5;boolean ownerSaved=shots.stream().allMatch(s->s.owner().equals(id));boolean liveOrigin=shots.stream().allMatch(s->Math.abs(s.x()-last.x())<.001&&Math.abs(s.z()-last.z())<.001&&Math.abs(s.y()-(last.y()+eye))<.001);boolean damage=single?shots.stream().allMatch(s->s.damage()==10):shots.size()==24&&shots.stream().collect(java.util.stream.Collectors.groupingBy(Shot::tick)).values().stream().allMatch(r->r.size()==8&&r.stream().filter(s->Math.abs(s.damage()-7)<.001).count()==7&&r.stream().filter(s->Math.abs(s.damage()-5)<.001).count()==1);boolean direction=shots.stream().collect(java.util.stream.Collectors.groupingBy(Shot::tick)).values().stream().allMatch(r->r.size()==8&&vectorsMatch(r,single?.3:.4));String summary="mode="+(single?"single":"triple")+" owner="+id+" initial="+start+" last="+last+" eye="+eye+" total="+shots.size()+" ticks="+ticks.entrySet().stream().map(e->e.getKey()+":"+e.getValue().size()).toList()+" owners="+ownerSaved+" origins="+liveOrigin+" damage="+damage+" directions="+direction;System.out.println("MAIDEN_TAIL_CONTRACT "+summary);h.assertTrue(shots.size()==expected&&times&&ownerSaved&&liveOrigin&&damage&&direction,summary);h.succeed();});}
    private static boolean vectorsMatch(List<Shot> shots,double speed){int[][] dirs={{1,0},{1,1},{0,1},{-1,0},{-1,-1},{0,-1},{1,-1},{-1,1}};for(int i=0;i<dirs.length;i++){double length=Math.sqrt(dirs[i][0]*dirs[i][0]+.17*.17+dirs[i][1]*dirs[i][1]);double x=dirs[i][0]/length*speed,y=.17/length*speed,z=dirs[i][1]/length*speed;final double ex=x,ey=y,ez=z;if(shots.stream().noneMatch(s->Math.abs(s.vx()-ex)<.005&&Math.abs(s.vy()-ey)<.005&&Math.abs(s.vz()-ez)<.005))return false;}return true;}
    private static void joined(EntityJoinLevelEvent e){if(e.getEntity() instanceof com.hp.thermalshockreforged.entity.projectile.BloodBulletEntity bullet&&e.getLevel() instanceof net.minecraft.server.level.ServerLevel level){CompoundTag tag=snapshot(bullet);if(uuid(tag, "Owner") != null){UUID id=uuid(tag, "Owner");SHOTS.computeIfAbsent(id,k->new CopyOnWriteArrayList<>()).add(new Shot(id,level.getGameTime(),bullet.getX(),bullet.getY(),bullet.getZ(),bullet.getDeltaMovement().x,bullet.getDeltaMovement().y,bullet.getDeltaMovement().z,snapshot(bullet).getDoubleOr("damage", 2.0)));}}}
    private static long seed(boolean single){double c=MaidenConfig.SINGLE_CHANCE.get();for(long i=0;i<100000;i++)if((RandomSource.create(i).nextDouble()<c)==single)return i;throw new IllegalStateException("No mode seed");}
    private static Vec3 pos(GameTestHelper h,int x,int y,int z){return h.absolutePos(new BlockPos(x,y,z)).getCenter();}
    private record Shot(UUID owner,long tick,double x,double y,double z,double vx,double vy,double vz,double damage){}


    private static CompoundTag snapshot(Entity entity) { var output=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private static void restore(GameTestHelper helper, Entity entity, CompoundTag tag) { entity.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,helper.getLevel().registryAccess(),tag)); }
    private static java.util.UUID uuid(CompoundTag tag, String key) { net.minecraft.nbt.Tag value=tag.get(key); return value==null?null:net.minecraft.core.UUIDUtil.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE,value).result().orElse(null); }
    public static void register() {
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_maiden_tail_contract_tests_removed_owner_completes_single_ring_using_saved_uuid", MaidenTailContractTests::removedOwnerCompletesSingleRingUsingSavedUuid, 70, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_maiden_tail_contract_tests_removed_owner_completes_all_triple_rings_using_saved_uuid", MaidenTailContractTests::removedOwnerCompletesAllTripleRingsUsingSavedUuid, 70, "supernova_server");
    }
}
