package com.hp.thermalshockreforged.entity.huntress;
import net.minecraft.world.entity.EntitySpawnReason;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
public final class HuntressTailContractTests {
    public static void committedSlamCompletesAtLastCasterSnapshotAfterRemoval(GameTestHelper h){var boss=HuntressContent.BOSS.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);boss.setNoAi(true);boss.setPos(pos(h,8,2,8));h.getLevel().addFreshEntity(boss);boss.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(13);boss.getRandom().setSeed(slamSeed());var originalTarget=cow(h,12,2,8);var unrelated=cow(h,30,2,8);boss.beginAttack(originalTarget);var tasks=h.getLevel().getEntitiesOfClass(HuntressTaskEntity.class,new AABB(boss.blockPosition()).inflate(64),task->boss.getUUID().equals(uuid(snapshot(task),"HuntressCaster")));h.assertTrue(tasks.size()==1,"Committed Slam must own one delayed task");var committed=tasks.getFirst();var initial=new Vec3(boss.getX(),boss.getY(),boss.getZ());boss.setPos(pos(h,20,2,8));var last=new Vec3(boss.getX(),boss.getY(),boss.getZ());float expected=(float)Math.max(0,boss.getAttributeValue(Attributes.ATTACK_DAMAGE)*HuntressConfig.SLAM_MULTIPLIER.get());boss.discard();h.assertTrue(committed.position().distanceToSqr(last)<1E-10,"Removal must capture the actual last caster position");h.runAfterDelay(48,()->{String evidence="initial="+initial+" last="+last+" taskRemoved="+committed.isRemoved()+" committedTargetHp="+originalTarget.getHealth()+" unrelatedHp="+unrelated.getHealth()+" expectedDamage="+expected+" unrelatedHits="+unrelated.hits;h.assertTrue(committed.isRemoved()&&Math.abs(originalTarget.getHealth()-(100-expected))<.01&&unrelated.getHealth()==100,evidence);h.succeed();});}
    private static Vec3 pos(GameTestHelper h,double x,double y,double z){return h.absolutePos(new BlockPos((int)x,(int)y,(int)z)).getCenter().add(x-(int)x,128+y-(int)y,z-(int)z);}
    private static ProbeCow cow(GameTestHelper h,double x,double y,double z){var e=new ProbeCow(h.getLevel());e.setNoAi(true);e.setNoGravity(true);e.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);e.setHealth(100);e.setPos(pos(h,x,y,z));h.getLevel().addFreshEntity(e);return e;}
    private static final class ProbeCow extends Cow {
        private final List<String> hits = new ArrayList<>();
        private ProbeCow(net.minecraft.server.level.ServerLevel level) { super(EntityType.COW, level); }
        @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
            hits.add(source.getMsgId() + ":" + amount);
            return super.hurtServer(level, source, amount);
        }
    }
    private static long slamSeed(){for(long i=0;i<100000;i++)if(RandomSource.create(i).nextDouble()>=HuntressConfig.TRIPLE_CHANCE.get())return i;throw new IllegalStateException("No Slam seed");}


    private static CompoundTag snapshot(Entity entity) { var output=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private static void restore(GameTestHelper helper, Entity entity, CompoundTag tag) { entity.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,helper.getLevel().registryAccess(),tag)); }
    private static java.util.UUID uuid(CompoundTag tag, String key) { net.minecraft.nbt.Tag value=tag.get(key); return value==null?null:net.minecraft.core.UUIDUtil.LENIENT_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE,value).result().orElse(null); }
    public static void register() {
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_huntress_tail_contract_tests_committed_slam_completes_at_last_caster_snapshot_after_removal", HuntressTailContractTests::committedSlamCompletesAtLastCasterSnapshotAfterRemoval, 60, "supernova_server");
    }
}
