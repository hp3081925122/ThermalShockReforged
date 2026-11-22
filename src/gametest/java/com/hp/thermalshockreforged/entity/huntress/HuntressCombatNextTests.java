package com.hp.thermalshockreforged.entity.huntress;
import net.minecraft.world.entity.EntitySpawnReason;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.phys.AABB;
public final class HuntressCombatNextTests {
    public static void slamWaitsForFinalStageThenHitsAndReleasesGravity(GameTestHelper h){var boss=boss(h,10,2,8);boss.getRandom().setSeed(seed());var target=cow(h,14,2,8,100);boss.beginAttack(target);var task=only(h,boss);h.assertTrue(data(task).getIntOr("HuntressSkill", 0)==2&&data(boss).getIntOr("HuntressCooldown", 0)==HuntressConfig.SLAM_COOLDOWN.get()&&boss.isNoGravity(),"Slam entry must commit skill, cooldown, and gravity lock");h.runAfterDelay(36,()->h.assertTrue(target.getHealth()==100&&!task.isRemoved(),"Slam must remain pending before its final stage"));h.runAfterDelay(50,()->{h.assertTrue(target.getHealth()<100&&task.isRemoved()&&!boss.isNoGravity(),"Slam must resolve once at its deadline and release gravity");h.succeed();});}
    public static void threeBlockBoundarySelectsRollbackAndOutsideStartsDelayedSkill(GameTestHelper h){var exact=boss(h,8,2,8);exact.beginAttack(cow(h,11,2,8,100));h.assertTrue(data(exact).getIntOr("HuntressCooldown", 0)==HuntressConfig.ROLLBACK_COOLDOWN.get()&&tasks(h,exact).isEmpty(),"Exactly three blocks must select rollback");var outside=boss(h,8,2,14);outside.getRandom().setSeed(seed());outside.beginAttack(cow(h,11.001,2,14,100));h.assertTrue(tasks(h,outside).size()==1&&data(outside).getIntOr("HuntressCooldown", 0)==HuntressConfig.SLAM_COOLDOWN.get(),"A target just outside three blocks must enter the seeded Slam branch");h.succeed();}
    public static void slamCooldownRejectsDuplicateSubmissionAndReset(GameTestHelper h){var boss=boss(h,8,2,8);boss.getRandom().setSeed(seed());var target=cow(h,12,2,8,100);boss.beginAttack(target);int cooldown=data(boss).getIntOr("HuntressCooldown", 0);boss.beginAttack(target);h.assertTrue(tasks(h,boss).size()==1&&data(boss).getIntOr("HuntressCooldown", 0)==cooldown,"Repeated entry must preserve one task and the original cooldown");h.succeed();}
    public static void slamUsesArmorAndTargetKnockbackResistance(GameTestHelper h){var boss=boss(h,8,2,8);boss.getRandom().setSeed(seed());var target=cow(h,12,2,8,100);target.getAttribute(Attributes.ARMOR).setBaseValue(8);target.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);boss.beginAttack(target);h.runAfterDelay(50,()->{h.assertTrue(target.getHealth()<100&&target.getHealth()>80,"Slam must apply configured attack damage through native armor handling");h.assertTrue(target.getDeltaMovement().y==0,"Full knockback resistance must suppress Slam knockback");h.succeed();});}
    public static void doubleStrikeAppliesSecondDelayedHitToTheSameLivingTarget(GameTestHelper h){var boss=boss(h,8,2,8);boss.getRandom().setSeed(tripleSeed());var trigger=cow(h,15,2,8,100);var witness=new HitCow(h);witness.setNoAi(true);witness.setNoGravity(true);witness.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);witness.setHealth(100);witness.setPos(h.absolutePos(new BlockPos(10,2,8)).getCenter());h.getLevel().addFreshEntity(witness);boss.beginAttack(trigger);var task=only(h,boss);int before=witness.hits;for(int i=0;i<19;i++)task.endTick(h.getLevel());h.assertTrue(data(task).getIntOr("HuntressSkill", 0)==1&&witness.hits-before==2&&witness.getHealth()<100&&task.isRemoved(),"Both delayed strike stages must hit the same target exactly once each");h.succeed();}
    private static final class HitCow extends Cow{int hits;HitCow(GameTestHelper h){super(EntityType.COW,h.getLevel());}@Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level,net.minecraft.world.damagesource.DamageSource s,float n){hits++;return super.hurtServer(level,s,n);}}
    private static HuntressBossEntity boss(GameTestHelper h,double x,double y,double z){var e=HuntressContent.BOSS.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);e.setNoAi(true);e.setPos(h.absolutePos(new BlockPos((int)x,(int)y,(int)z)).getCenter().add(x-(int)x,y-(int)y,z-(int)z));h.getLevel().addFreshEntity(e);return e;}
    private static Cow cow(GameTestHelper h,double x,double y,double z,double hp){var e=EntityType.COW.create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);e.setNoAi(true);e.setNoGravity(true);e.getAttribute(Attributes.MAX_HEALTH).setBaseValue(hp);e.setHealth((float)hp);e.setPos(h.absolutePos(new BlockPos((int)x,(int)y,(int)z)).getCenter().add(x-(int)x,y-(int)y,z-(int)z));h.getLevel().addFreshEntity(e);return e;}
    private static long seed(){for(long i=0;i<100000;i++)if(RandomSource.create(i).nextDouble()>=HuntressConfig.TRIPLE_CHANCE.get())return i;throw new IllegalStateException("No Slam seed");}
    private static long tripleSeed(){for(long i=0;i<100000;i++)if(RandomSource.create(i).nextDouble()<HuntressConfig.TRIPLE_CHANCE.get())return i;throw new IllegalStateException("No double-strike seed");}
    private static CompoundTag data(Entity e){return snapshot(e);}
    private static List<HuntressTaskEntity> tasks(GameTestHelper h,HuntressBossEntity e){return h.getLevel().getEntitiesOfClass(HuntressTaskEntity.class,new AABB(e.blockPosition()).inflate(64),t->uuid(data(t), "HuntressCaster") != null&&uuid(data(t), "HuntressCaster").equals(e.getUUID()));}
    private static HuntressTaskEntity only(GameTestHelper h,HuntressBossEntity e){var a=tasks(h,e);h.assertTrue(a.size()==1,"Caster must own one Huntress task");return a.getFirst();}


    private static CompoundTag snapshot(Entity entity) { var output=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private static void restore(GameTestHelper helper, Entity entity, CompoundTag tag) { entity.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,helper.getLevel().registryAccess(),tag)); }
    private static java.util.UUID uuid(CompoundTag tag, String key) { net.minecraft.nbt.Tag value=tag.get(key); return value==null?null:net.minecraft.core.UUIDUtil.LENIENT_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE,value).result().orElse(null); }
    public static void register() {
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_huntress_combat_next_tests_slam_waits_for_final_stage_then_hits_and_releases_gravity", HuntressCombatNextTests::slamWaitsForFinalStageThenHitsAndReleasesGravity, 60, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_huntress_combat_next_tests_three_block_boundary_selects_rollback_and_outside_starts_delayed_skill", HuntressCombatNextTests::threeBlockBoundarySelectsRollbackAndOutsideStartsDelayedSkill, 100, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_huntress_combat_next_tests_slam_cooldown_rejects_duplicate_submission_and_reset", HuntressCombatNextTests::slamCooldownRejectsDuplicateSubmissionAndReset, 100, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_huntress_combat_next_tests_slam_uses_armor_and_target_knockback_resistance", HuntressCombatNextTests::slamUsesArmorAndTargetKnockbackResistance, 60, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_huntress_combat_next_tests_double_strike_applies_second_delayed_hit_to_the_same_living_target", HuntressCombatNextTests::doubleStrikeAppliesSecondDelayedHitToTheSameLivingTarget, 60, "supernova_server");
    }
}



