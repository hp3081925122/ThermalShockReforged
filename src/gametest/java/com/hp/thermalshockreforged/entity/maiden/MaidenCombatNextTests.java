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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import com.hp.thermalshockreforged.entity.projectile.BloodBulletEntity;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
public final class MaidenCombatNextTests {
    private static final Map<UUID,List<BloodShot>> SHOTS=new ConcurrentHashMap<>();
    static{NeoForge.EVENT_BUS.addListener(MaidenCombatNextTests::joined);}
    public static void waveKeepsCommittedTargetIdentityAfterTargetMoves(GameTestHelper h){var owner=owner(h,8,2,8);owner.getRandom().setSeed(seed(true));var target=cow(h,11,2,8,100);owner.beginAttack(target);var task=only(h,owner);UUID targetId=target.getUUID();target.setPos(target.getX()+0.0,target.getY(),target.getZ()+8);h.runAfterDelay(38,()->{List<BloodShot> shots=shots(owner);h.assertTrue(uuid(data(task), "MaidenTarget").equals(targetId)&&shots.size()==8,"The committed volley must retain its original target identity and emit one single-ring wave after that target moves");h.assertTrue(shots.stream().allMatch(s->s.owner().equals(owner.getUUID())),"Launched arrows must preserve the Maiden owner");h.succeed();});}
    public static void emittedBloodArrowUsesRealCollisionAndDamageSource(GameTestHelper h){var owner=owner(h,8,2,8);owner.getRandom().setSeed(seed(true));var target=cow(h,11,2,8,100);owner.beginAttack(target);h.runAfterDelay(48,()->{List<BloodShot> shots=shots(owner);h.assertTrue(shots.size()==8,"The committed volley must produce eight launched entities");h.assertTrue(target.getHealth()<100,"At least one naturally ticking launched arrow must collide with the target");h.assertTrue(shots.stream().allMatch(s->s.owner().equals(owner.getUUID())&&s.damage()>0),"Each recorded projectile must retain its owner and positive configured damage");h.succeed();});}
    public static void movingCasterChangesRealVolleyLaunchOrigin(GameTestHelper h){var owner=owner(h,8,2,8);owner.getRandom().setSeed(seed(true));var target=cow(h,11,2,8,100);owner.beginAttack(target);owner.setPos(owner.getX(),owner.getY(),owner.getZ()+5);h.runAfterDelay(38,()->{var shots=shots(owner);h.assertTrue(shots.size()==8&&shots.stream().allMatch(s->Math.abs(s.z()-owner.getZ())<1.1),"Each wave must launch around the caster's live position snapshot");h.succeed();});}
    private static MaidenEntity owner(GameTestHelper h,double x,double y,double z){var e=MaidenContent.MAIDEN.get().create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);e.setNoAi(true);e.setPos(h.absolutePos(new BlockPos((int)x,(int)y,(int)z)).getCenter());h.getLevel().addFreshEntity(e);return e;}
    private static Cow cow(GameTestHelper h,double x,double y,double z,double hp){var e=EntityType.COW.create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);e.setNoAi(true);e.setNoGravity(true);e.getAttribute(Attributes.MAX_HEALTH).setBaseValue(hp);e.setHealth((float)hp);e.setPos(h.absolutePos(new BlockPos((int)x,(int)y,(int)z)).getCenter());h.getLevel().addFreshEntity(e);return e;}
    private static long seed(boolean single){double chance=MaidenConfig.SINGLE_CHANCE.get();for(long i=0;i<100000;i++)if((RandomSource.create(i).nextDouble()<chance)==single)return i;throw new IllegalStateException("No Maiden seed");}
    private static CompoundTag data(Entity e){return snapshot(e);}
    private static List<MaidenVolleyEntity> tasks(GameTestHelper h,MaidenEntity owner){return h.getLevel().getEntitiesOfClass(MaidenVolleyEntity.class,new AABB(owner.blockPosition()).inflate(64),t->uuid(data(t), "MaidenCaster") != null&&uuid(data(t), "MaidenCaster").equals(owner.getUUID()));}
    private static MaidenVolleyEntity only(GameTestHelper h,MaidenEntity owner){var all=tasks(h,owner);h.assertTrue(all.size()==1,"Caster must own one volley task");return all.getFirst();}
    private static void joined(EntityJoinLevelEvent event){if(event.getEntity() instanceof BloodBulletEntity bullet&&bullet.getOwner() instanceof MaidenEntity owner){SHOTS.computeIfAbsent(owner.getUUID(),id->new CopyOnWriteArrayList<>()).add(new BloodShot(owner.getUUID(),bullet.getX(),bullet.getY(),bullet.getZ(),snapshot(bullet).getDoubleOr("damage", 2.0)));}}
    private static List<BloodShot> shots(MaidenEntity e){return List.copyOf(SHOTS.getOrDefault(e.getUUID(),List.of()));}
    private record BloodShot(UUID owner,double x,double y,double z,double damage){}


    private static CompoundTag snapshot(Entity entity) { var output=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private static void restore(GameTestHelper helper, Entity entity, CompoundTag tag) { entity.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,helper.getLevel().registryAccess(),tag)); }
    private static java.util.UUID uuid(CompoundTag tag, String key) { net.minecraft.nbt.Tag value=tag.get(key); return value==null?null:net.minecraft.core.UUIDUtil.LENIENT_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE,value).result().orElse(null); }
    public static void register() {
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_maiden_combat_next_tests_wave_keeps_committed_target_identity_after_target_moves", MaidenCombatNextTests::waveKeepsCommittedTargetIdentityAfterTargetMoves, 60, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_maiden_combat_next_tests_emitted_blood_arrow_uses_real_collision_and_damage_source", MaidenCombatNextTests::emittedBloodArrowUsesRealCollisionAndDamageSource, 60, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_maiden_combat_next_tests_moving_caster_changes_real_volley_launch_origin", MaidenCombatNextTests::movingCasterChangesRealVolleyLaunchOrigin, 60, "supernova_server");
    }
}



