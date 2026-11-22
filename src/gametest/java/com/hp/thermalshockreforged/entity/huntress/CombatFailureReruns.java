package com.hp.thermalshockreforged.entity.huntress;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.phys.AABB;
public final class CombatFailureReruns {
    public static void rerunDoubleStrikeSeedAndSecondHit(GameTestHelper h){var boss=boss(h,8,2,8);long seed=tripleSeed();boss.getRandom().setSeed(seed);var trigger=cow(h,15,2,8,100);var witness=new HitCow(h);witness.setNoAi(true);witness.setNoGravity(true);witness.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);witness.setHealth(100);witness.setPos(h.absolutePos(new BlockPos(10,2,8)).getCenter());h.getLevel().addFreshEntity(witness);boss.beginAttack(trigger);var task=only(h,boss);h.assertTrue(data(task).getIntOr("HuntressSkill", 0)==1,"Seed must select skill 1");int before=witness.hits;for(int i=0;i<19;i++)task.endTick(h.getLevel());String result="seed="+seed+" elapsed="+data(task).getIntOr("HuntressElapsed", 0)+" hits="+(witness.hits-before)+" health="+witness.getHealth()+" removed="+task.isRemoved();System.out.println("COMBAT_DOUBLE_RERUN "+result);h.assertTrue(witness.hits-before==2&&witness.getHealth()<100&&task.isRemoved(),result);h.succeed();}
    private static HuntressBossEntity boss(GameTestHelper h,int x,int y,int z){var e=HuntressContent.BOSS.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);e.setNoAi(true);e.setPos(h.absolutePos(new BlockPos(x,y,z)).getCenter());h.getLevel().addFreshEntity(e);return e;}
    private static Cow cow(GameTestHelper h,int x,int y,int z,double hp){var e=EntityType.COW.create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);e.setNoAi(true);e.setNoGravity(true);e.getAttribute(Attributes.MAX_HEALTH).setBaseValue(hp);e.setHealth((float)hp);e.setPos(h.absolutePos(new BlockPos(x,y,z)).getCenter());h.getLevel().addFreshEntity(e);return e;}
    private static long tripleSeed(){for(long i=0;i<100000;i++)if(RandomSource.create(i).nextDouble()<HuntressConfig.TRIPLE_CHANCE.get())return i;throw new IllegalStateException("No double-strike seed");}
    private static long slamSeed(){for(long i=0;i<100000;i++)if(RandomSource.create(i).nextDouble()>=HuntressConfig.TRIPLE_CHANCE.get())return i;throw new IllegalStateException("No Slam seed");}
    private static CompoundTag data(Entity e){return snapshot(e);}
    private static HuntressTaskEntity only(GameTestHelper h,HuntressBossEntity e){var t=h.getLevel().getEntitiesOfClass(HuntressTaskEntity.class,new AABB(e.blockPosition()).inflate(64),x->uuid(data(x), "HuntressCaster") != null&&uuid(data(x), "HuntressCaster").equals(e.getUUID()));h.assertTrue(t.size()==1,"Caster must own one task");return t.getFirst();}
    private static final class HitCow extends Cow{int hits;HitCow(GameTestHelper h){super(EntityType.COW,h.getLevel());}@Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level,net.minecraft.world.damagesource.DamageSource s,float n){hits++;return super.hurtServer(level,s,n);}}


    private static CompoundTag snapshot(Entity entity) { var output=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private static void restore(GameTestHelper helper, Entity entity, CompoundTag tag) { entity.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,helper.getLevel().registryAccess(),tag)); }
    private static java.util.UUID uuid(CompoundTag tag, String key) { net.minecraft.nbt.Tag value=tag.get(key); return value==null?null:net.minecraft.core.UUIDUtil.LENIENT_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE,value).result().orElse(null); }
    public static void register() {
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_combat_failure_reruns_rerun_double_strike_seed_and_second_hit", CombatFailureReruns::rerunDoubleStrikeSeedAndSecondHit, 100, "supernova_server");
    }
}

