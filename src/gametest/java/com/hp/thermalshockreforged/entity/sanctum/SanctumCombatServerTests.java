package com.hp.thermalshockreforged.entity.sanctum;

import com.hp.thermalshockreforged.gametest.TsrGameTests;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.entity.ice.IceBulletEntity;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.gametest.framework.GameTestHelper;
import com.hp.thermalshockreforged.gametest.PlayerInteractionTests;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
public final class SanctumCombatServerTests {
    private static final ConcurrentHashMap<UUID, CopyOnWriteArrayList<CompoundTag>> GUNNER_SHOTS = new ConcurrentHashMap<>();

    static {
        NeoForge.EVENT_BUS.addListener(SanctumCombatServerTests::onEntityJoin);
    }
    public static void errantDeterministicParrySetsItsTwentyTickStatusAndFortyFiveTickCooldown(GameTestHelper h) {
        ErrantEntity owner = errant(h, 2, 2, 2);
        Cow target = cow(h, 4, 2, 2);
        owner.getRandom().setSeed(seedFor(0));
        owner.beginAttack(target);
        ValueInput data = save(h, owner);
        h.assertTrue(owner.hasEffect(StatusContent.PARRYING) && owner.getEffect(StatusContent.PARRYING).getDuration() == 20 && owner.getEffect(StatusContent.PARRYING).getAmplifier() == 1, "The deterministic 42-percent parry branch must apply PARRYING for twenty ticks at amplifier one");
        h.assertTrue(data.getIntOr("ErrantCooldown", 0) == 45 && data.getIntOr("ErrantMovementLock", 0) == 30 && data.getIntOr("ErrantStrikeRemaining", 0) == 0, "Parry must start its forty-five-tick cooldown and thirty-tick movement lock without a strike task");
        h.succeed();
    }
    public static void errantParryHealsFourBeforeResolvingIncomingDamage(GameTestHelper h) {
        ErrantEntity owner = errant(h, 2, 2, 2);
        Cow target = cow(h, 4, 2, 2);
        owner.getRandom().setSeed(seedFor(0));
        owner.beginAttack(target);
        owner.getAttribute(Attributes.ARMOR).setBaseValue(0);
        owner.setHealth(50);
        boolean accepted = owner.hurtServer(h.getLevel(), h.getLevel().damageSources().generic(), 10);
        h.assertTrue(accepted && Math.abs(owner.getHealth() - 40) < 1.0E-5 && owner.hasEffect(MobEffects.INSTANT_HEALTH), "Errant must queue the one-tick heal effect before the incoming hit is resolved");
        h.succeed();
    }
    public static void errantDeterministicDodgeUsesReverseLookVectorAndTenTickCooldown(GameTestHelper h) {
        ErrantEntity owner = errant(h, 2, 2, 2);
        Cow target = cow(h, 4, 2, 2);
        owner.setYRot(90);
        owner.getRandom().setSeed(seedFor(1));
        Vec3 expected = owner.getLookAngle().scale(-2);
        owner.beginAttack(target);
        ValueInput data = save(h, owner);
        Vec3 actual = owner.getDeltaMovement();
        h.assertTrue(data.getIntOr("ErrantCooldown", 0) == 10 && data.getIntOr("ErrantMovementLock", 0) == 0 && data.getIntOr("ErrantStrikeRemaining", 0) == 0, "The deterministic 28-percent dodge branch must use a ten-tick cooldown without locking movement");
        h.assertTrue(actual.distanceTo(expected) < 1.0E-6 && !owner.hasEffect(StatusContent.PARRYING), "Dodge must apply the exact reverse look vector at magnitude two without parry status");
        h.succeed();
    }
    public static void errantStrikeHitsOnlyOnItsTwelfthEndTick(GameTestHelper h) {
        ErrantEntity owner = errant(h, 2, 2, 2);
        Cow target = cow(h, 4, 2, 2);
        owner.getRandom().setSeed(seedFor(2));
        owner.beginAttack(target);
        float before = target.getHealth();
        h.assertTrue(save(h, owner).getIntOr("ErrantStrikeRemaining", 0) == 12, "The deterministic strike branch must commit twelve END ticks");
        h.runAfterDelay(11, () -> h.assertTrue(target.getHealth() == before, "No strike damage may occur during the first eleven END ticks"));
        h.runAfterDelay(12, () -> {
            double expected = owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * SanctumConfig.ERRANT.strikeMultiplier().get();
            h.assertTrue(Math.abs(target.getHealth() - (before - expected)) < 1.0E-4, "The twelfth END tick must apply the configured base-attack multiplier exactly once");
            h.assertTrue(save(h, owner).getIntOr("ErrantStrikeRemaining", 0) == 0 && save(h, owner).getIntOr("ErrantCooldown", 0) > 0, "The resolved strike must clear its pending stage and retain cooldown");
            h.succeed();
        });
    }
    public static void errantStrikeDoesNotFollowATargetPastThreeBlocks(GameTestHelper h) {
        ErrantEntity owner = errant(h, 2, 2, 2);
        Cow target = cow(h, 4, 2, 2);
        owner.getRandom().setSeed(seedFor(2));
        owner.beginAttack(target);
        float before = target.getHealth();
        h.runAfterDelay(2, () -> { BlockPos moved = h.absolutePos(new BlockPos(7, 2, 2)); target.snapTo(moved.getX(), moved.getY(), moved.getZ()); });
        h.runAfterDelay(14, () -> {
            h.assertTrue(target.getHealth() == before && save(h, owner).getIntOr("ErrantStrikeRemaining", 0) == 0, "The committed strike must expire without pursuing a target that moved outside three blocks");
            h.succeed();
        });
    }
    public static void errantStrikeKeepsItsOriginalTargetUuidAfterThatTargetIsRemoved(GameTestHelper h) {
        ErrantEntity owner = errant(h, 2, 2, 2);
        Cow target = cow(h, 4, 2, 2);
        Cow nearby = cow(h, 4, 2, 4);
        owner.getRandom().setSeed(seedFor(2));
        owner.beginAttack(target);
        float nearbyBefore = nearby.getHealth();
        target.discard();
        h.runAfterDelay(14, () -> {
            h.assertTrue(nearby.getHealth() == nearbyBefore && save(h, owner).getIntOr("ErrantStrikeRemaining", 0) == 0, "Removing the selected target must not redirect the strike to another nearby entity");
            h.succeed();
        });
    }
    public static void removedErrantCannotResolveItsPendingStrikeAgainstTheTarget(GameTestHelper h) {
        ErrantEntity owner = errant(h, 2, 2, 2);
        Cow target = cow(h, 4, 2, 2);
        owner.getRandom().setSeed(seedFor(2));
        owner.beginAttack(target);
        float before = target.getHealth();
        owner.discard();
        h.runAfterDelay(14, () -> {
            h.assertTrue(target.getHealth() == before && owner.isRemoved(), "A removed Errant must not resolve its scheduled strike");
            h.succeed();
        });
    }
    public static void errantDefersThirtyExperienceUntilItsTwentiethDeathTick(GameTestHelper h) {
        ErrantEntity owner = errant(h, 2, 2, 2);
        var player = PlayerInteractionTests.player(h, net.minecraft.world.level.GameType.SURVIVAL, 2.5, 2, 8.5);
        owner.hurtServer(h.getLevel(), h.getLevel().damageSources().playerAttack(player), 1000);
        AABB area = new AABB(owner.blockPosition()).inflate(12);
        h.runAfterDelay(10, () -> h.assertTrue(xp(h, area) == 0, "Pending Errant experience must remain withheld before the twentieth death tick"));
        awaitErrantDeathXp(h, owner, area, player);

    }
    public static void errantSavedStrikeReanchorsAndResolvesExactlyOnce(GameTestHelper h) {
        ErrantEntity original = errant(h, 2, 2, 2);
        Cow target = cow(h, 4, 2, 2);
        original.getRandom().setSeed(seedFor(2));
        original.beginAttack(target);
        for (int i = 0; i < 4; i++) original.endTick(h.getLevel());
        CompoundTag data = saveTag(h.getLevel(), original);
        putUuid(data, "UUID", UUID.randomUUID());
        original.discard();
        ErrantEntity restored = SanctumContent.ERRANT.get().create(h.getLevel(), EntitySpawnReason.LOAD);
        restored.load(readTag(h, data));
        if (!h.getLevel().addFreshEntity(restored)) throw new IllegalStateException("GameTest server rejected the restored Errant");
        float before = target.getHealth();
        h.assertTrue(save(h, restored).getIntOr("ErrantStrikeRemaining", 0) == 8, "NBT reload must preserve the strike's eight remaining END ticks");
        h.runAfterDelay(7, () -> h.assertTrue(target.getHealth() == before, "The reanchored strike must not resolve one tick early"));
        h.runAfterDelay(9, () -> {
            double expected = restored.getAttributeValue(Attributes.ATTACK_DAMAGE) * SanctumConfig.ERRANT.strikeMultiplier().get();
            h.assertTrue(Math.abs(target.getHealth() - (before - expected)) < 1.0E-4, "The restored strike must resolve once using the restored Errant");
            h.succeed();
        });
    }
    public static void rippleAttackCreatesOneCastAndStartsItsCooldownAndMovementLock(GameTestHelper h) {
        RippleEntity owner = ripple(h, 2, 2, 2);
        Cow target = cow(h, 4, 2, 2);
        owner.beginAttack(target);
        ValueInput data = save(h, owner);
        h.assertTrue(data.getIntOr("RippleCooldown", 0) == 180 && data.getIntOr("RippleMovementLock", 0) == 40, "A successful Ripple cast must start its 180-tick cooldown and 40-tick movement lock");
        h.assertTrue(casts(h, owner).size() == 1, "The first cast must create exactly one locally owned Ripple task");
        owner.beginAttack(target);
        h.assertTrue(casts(h, owner).size() == 1 && save(h, owner).getIntOr("RippleCooldown", 0) == 180, "A repeated attack during cooldown must not duplicate or reset the cast");
        h.succeed();
    }
    public static void rippleCastWaitsTwentyNineEndTicksThenCreatesOneErrField(GameTestHelper h) {
        RippleEntity owner = ripple(h, 2, 2, 2);
        Cow target = cow(h, 4, 2, 2);
        owner.beginAttack(target);
        RippleCastEntity task = casts(h, owner).getFirst();
        h.runAfterDelay(28, () -> h.assertTrue(errFields(h, owner).isEmpty() && save(h, task).getIntOr("RippleRemaining", 0) == 1, "The cast must retain exactly one remaining tick before its twenty-ninth END tick"));
        h.runAfterDelay(29, () -> {
            h.assertTrue(errFields(h, owner).size() == 1 && task.isRemoved() && casts(h, owner).isEmpty(), "The twenty-ninth END tick must consume the cast and create exactly one ERR field");
            h.succeed();
        });
    }
    public static void rippleElectrifiedStatusCancelsTheCommittedCast(GameTestHelper h) {
        verifyRippleInterrupted(h, StatusContent.ELECTRIFIED);
    }
    public static void rippleWeaknessStatusCancelsTheCommittedCast(GameTestHelper h) {
        verifyRippleInterrupted(h, MobEffects.WEAKNESS);
    }
    public static void rippleInfectedStatusCancelsTheCommittedCast(GameTestHelper h) {
        verifyRippleInterrupted(h, StatusContent.INFECTED);
    }
    public static void rippleCasterRemovalCancelsTheCommittedCast(GameTestHelper h) {
        RippleEntity owner = ripple(h, 2, 2, 2);
        Cow target = cow(h, 4, 2, 2);
        owner.beginAttack(target);
        RippleCastEntity task = casts(h, owner).getFirst();
        owner.discard();
        h.runAfterDelay(31, () -> {
            h.assertTrue(task.isRemoved() && errFields(h, owner).isEmpty(), "A cast whose Ripple caster was removed must terminate without an ERR field");
            h.succeed();
        });
    }
    public static void rippleRetainsTheLastAimAfterItsTargetIsRemoved(GameTestHelper h) {
        RippleEntity owner = ripple(h, 2, 2, 2);
        Cow target = cow(h, 4, 2, 2);
        owner.beginAttack(target);
        target.discard();
        h.runAfterDelay(31, () -> {
            h.assertTrue(errFields(h, owner).size() == 1, "A removed target must not cancel Ripple's already committed cast or erase its snapshotted aim");
            h.succeed();
        });
    }
    public static void gunnerVolleyFiresItsThreeBulletsAtEndTicksElevenSixteenAndTwentyOne(GameTestHelper h) {
        FrostilyteGunnerEntity owner = gunner(h, 2, 2, 2);
        Cow target = cow(h, 6, 2, 2);
        GUNNER_SHOTS.put(owner.getUUID(), new CopyOnWriteArrayList<>());
        owner.beginAttack(target);
        h.assertTrue(save(h, owner).getIntOr("GunnerCooldown", 0) == 60 && save(h, owner).getIntOr("GunnerMovementLock", 0) == 29, "A successful Gunner volley must start its sixty-tick cooldown and twenty-nine-tick movement lock");
        h.runAfterDelay(10, () -> h.assertTrue(shots(owner).size() == 0, "The first Gunner bullet must not fire before END tick eleven"));
        h.runAfterDelay(11, () -> h.assertTrue(shots(owner).size() == 1, "The first Gunner bullet must fire on END tick eleven"));
        h.runAfterDelay(16, () -> h.assertTrue(shots(owner).size() == 2, "The second Gunner bullet must fire on END tick sixteen"));
        h.runAfterDelay(21, () -> {
            List<CompoundTag> shots = shots(owner);
            h.assertTrue(shots.size() == 3 && shots.stream().allMatch(tag -> hasUuid(tag, "IceCaster") && owner.getUUID().equals(uuid(tag, "IceCaster")) && Math.abs(tag.getDoubleOr("damage", 0) - 5.0) < 1.0E-6), "The third bullet must fire on END tick twenty-one with the caster UUID and the captured five-point base damage");
            h.assertTrue(volleys(h, owner).isEmpty(), "The volley task must remove itself after the third shot");
            GUNNER_SHOTS.remove(owner.getUUID());
            h.succeed();
        });
    }
    public static void gunnerVolleyFinishesItsCommittedThirdShotAfterCasterDeath(GameTestHelper h) {
        FrostilyteGunnerEntity owner = gunner(h, 2, 2, 2);
        Cow target = cow(h, 6, 2, 2);
        GUNNER_SHOTS.put(owner.getUUID(), new CopyOnWriteArrayList<>());
        owner.beginAttack(target);
        h.runAfterDelay(16, () -> {
            h.assertTrue(shots(owner).size() == 2, "The caster must have committed exactly two bullets before the third stage");
            owner.hurtServer(h.getLevel(), h.getLevel().damageSources().generic(), 1000);
            h.assertTrue(owner.isDeadOrDying(), "The caster must enter its real death state before the committed third stage");
        });
        h.runAfterDelay(21, () -> {
            h.assertTrue(shots(owner).size() == 3, "Caster death after shot two must not erase the already committed third shot");
            GUNNER_SHOTS.remove(owner.getUUID());
            h.succeed();
        });
    }
    public static void gunnerVolleyKeepsFiringAtItsSnapshottedAimAfterTargetRemoval(GameTestHelper h) {
        FrostilyteGunnerEntity owner = gunner(h, 2, 2, 2);
        Cow target = cow(h, 6, 2, 2);
        GUNNER_SHOTS.put(owner.getUUID(), new CopyOnWriteArrayList<>());
        owner.beginAttack(target);
        h.runAfterDelay(12, () -> {
            h.assertTrue(shots(owner).size() == 1, "One bullet must have fired before target removal");
            target.discard();
        });
        h.runAfterDelay(21, () -> {
            h.assertTrue(shots(owner).size() == 3, "Target removal must leave all three committed volley stages using the saved aim");
            GUNNER_SHOTS.remove(owner.getUUID());
            h.succeed();
        });
    }
    public static void gunnerCooldownRejectsRepeatedAttackWithoutDuplicatingVolley(GameTestHelper h) {
        FrostilyteGunnerEntity owner = gunner(h, 2, 2, 2);
        Cow first = cow(h, 4, 2, 2);
        Cow second = cow(h, 4, 2, 4);
        owner.beginAttack(first);
        owner.beginAttack(second);
        ValueInput data = save(h, owner);
        h.assertTrue(volleys(h, owner).size() == 1 && data.getIntOr("GunnerCooldown", 0) == 60 && data.getIntOr("GunnerMovementLock", 0) == 29, "A second attack during cooldown must not reset state or schedule a second volley");
        h.succeed();
    }

    private static void verifyRippleInterrupted(GameTestHelper h, Holder<MobEffect> effect) {
        RippleEntity owner = ripple(h, 2, 2, 2);
        Cow target = cow(h, 4, 2, 2);
        owner.beginAttack(target);
        owner.addEffect(new MobEffectInstance(effect, 100, 0));
        h.runAfterDelay(31, () -> {
            h.assertTrue(errFields(h, owner).isEmpty() && casts(h, owner).isEmpty(), "Electrified, Weakness, and Infected status each interrupt the committed Ripple cast without creating an ERR field: " + effect);
            h.succeed();
        });
    }

    private static void awaitErrantDeathXp(GameTestHelper h, ErrantEntity owner, AABB area, net.minecraft.server.level.ServerPlayer player) {
        if (owner.deathTime < 19) {
            h.runAfterDelay(1, () -> awaitErrantDeathXp(h, owner, area, player));
            return;
        }
        h.assertTrue(owner.deathTime == 19 && xp(h, area) == 0, "Errant must withhold all pending experience until the twentieth death tick");
        h.runAfterDelay(1, () -> {
            h.assertTrue(owner.isRemoved() && xp(h, area) == 30, "The twentieth death tick must remove Errant and emit its configured thirty experience exactly once");
            PlayerInteractionTests.release(h, player);
            h.succeed();
        });
    }

    private static ErrantEntity errant(GameTestHelper h, int x, int y, int z) {
        return h.spawnWithNoFreeWill(SanctumContent.ERRANT.get(), x, y, z);
    }

    private static RippleEntity ripple(GameTestHelper h, int x, int y, int z) {
        return h.spawnWithNoFreeWill(SanctumContent.RIPPLE.get(), x, y, z);
    }

    private static FrostilyteGunnerEntity gunner(GameTestHelper h, int x, int y, int z) {
        return h.spawnWithNoFreeWill(SanctumContent.FROSTILYTE_GUNNER.get(), x, y, z);
    }

    private static Cow cow(GameTestHelper h, int x, int y, int z) {
        Cow cow = EntityType.COW.create(h.getLevel(), EntitySpawnReason.COMMAND);
        if (cow == null) throw new IllegalStateException("Cow entity type returned null");
        cow.setNoAi(true);
        cow.setNoGravity(true);
        cow.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
        cow.setHealth(100);
        BlockPos spawn = h.absolutePos(new BlockPos(x, y, z));
        cow.snapTo(spawn.getX(), spawn.getY(), spawn.getZ());
        if (!h.getLevel().addFreshEntity(cow)) throw new IllegalStateException("GameTest server rejected cow entity");
        return cow;
    }

    private static long seedFor(int branch) {
        for (long seed = 0; seed < 100000; seed++) {
            RandomSource random = RandomSource.create(seed);
            boolean first = random.nextDouble() < .7;
            boolean second = random.nextDouble() < .6;
            if (branch == 0 && first && second || branch == 1 && first && !second || branch == 2 && !first) return seed;
        }
        throw new IllegalStateException("No deterministic Errant branch seed found");
    }

    private static List<RippleCastEntity> casts(GameTestHelper h, RippleEntity owner) {
        return h.getLevel().getEntitiesOfClass(RippleCastEntity.class, new AABB(owner.blockPosition()).inflate(24), task -> {
            ValueInput data = save(h, task);
            return hasUuid(data, "RippleCaster") && owner.getUUID().equals(uuid(data, "RippleCaster"));
        });
    }

    private static List<ErrFieldEntity> errFields(GameTestHelper h, Entity owner) {
        return h.getLevel().getEntitiesOfClass(ErrFieldEntity.class, new AABB(owner.blockPosition()).inflate(24));
    }

    private static List<GunnerVolleyEntity> volleys(GameTestHelper h, FrostilyteGunnerEntity owner) {
        return h.getLevel().getEntitiesOfClass(GunnerVolleyEntity.class, new AABB(owner.blockPosition()).inflate(24), task -> {
            ValueInput data = save(h, task);
            return hasUuid(data, "GunnerCaster") && owner.getUUID().equals(uuid(data, "GunnerCaster"));
        });
    }

    private static List<CompoundTag> shots(FrostilyteGunnerEntity owner) {
        return GUNNER_SHOTS.getOrDefault(owner.getUUID(), new CopyOnWriteArrayList<>()).stream().map(CompoundTag::copy).toList();
    }

    private static int xp(GameTestHelper h, AABB area) {
        return h.getLevel().getEntitiesOfClass(ExperienceOrb.class, area).stream().mapToInt(ExperienceOrb::getValue).sum();
    }

    private static ValueInput save(GameTestHelper h, Entity entity) { return TsrGameTests.roundTrip(h, entity); }

    private static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof IceBulletEntity bullet)) return;
        CompoundTag data = saveTag((ServerLevel) event.getLevel(), bullet);
        if (!hasUuid(data, "IceCaster")) return;
        CopyOnWriteArrayList<CompoundTag> shots = GUNNER_SHOTS.get(uuid(data, "IceCaster"));
        if (shots != null) shots.add(data.copy());
    }

    private static CompoundTag saveTag(ServerLevel level, Entity entity) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
        entity.saveWithoutId(output);
        return output.buildResult();
    }

    private static ValueInput readTag(GameTestHelper h, CompoundTag tag) { return TagValueInput.create(ProblemReporter.DISCARDING, h.getLevel().registryAccess(), tag); }
    private static boolean hasUuid(CompoundTag tag, String key) { return uuid(tag, key) != null; }
    private static boolean hasUuid(ValueInput input, String key) { return input.getString(key).isPresent(); }
    private static UUID uuid(CompoundTag tag, String key) { return tag.getString(key).map(UUID::fromString).orElse(null); }
    private static void putUuid(CompoundTag tag, String key, UUID value) { tag.put(key, UUIDUtil.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, value).result().orElseThrow()); }
    private static UUID uuid(ValueInput input, String key) { return input.getString(key).map(UUID::fromString).orElse(null); }

    public static void register() {
        TsrGameTests.add("history_sanctum_combat_server_tests_errant_deterministic_parry_sets_its_twenty_tick_status_and_forty_five_tick_cooldown", SanctumCombatServerTests::errantDeterministicParrySetsItsTwentyTickStatusAndFortyFiveTickCooldown, 30, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_errant_parry_heals_four_before_resolving_incoming_damage", SanctumCombatServerTests::errantParryHealsFourBeforeResolvingIncomingDamage, 20, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_errant_deterministic_dodge_uses_reverse_look_vector_and_ten_tick_cooldown", SanctumCombatServerTests::errantDeterministicDodgeUsesReverseLookVectorAndTenTickCooldown, 20, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_errant_strike_hits_only_on_its_twelfth_end_tick", SanctumCombatServerTests::errantStrikeHitsOnlyOnItsTwelfthEndTick, 30, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_errant_strike_does_not_follow_a_target_past_three_blocks", SanctumCombatServerTests::errantStrikeDoesNotFollowATargetPastThreeBlocks, 30, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_errant_strike_keeps_its_original_target_uuid_after_that_target_is_removed", SanctumCombatServerTests::errantStrikeKeepsItsOriginalTargetUuidAfterThatTargetIsRemoved, 30, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_removed_errant_cannot_resolve_its_pending_strike_against_the_target", SanctumCombatServerTests::removedErrantCannotResolveItsPendingStrikeAgainstTheTarget, 30, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_errant_defers_thirty_experience_until_its_twentieth_death_tick", SanctumCombatServerTests::errantDefersThirtyExperienceUntilItsTwentiethDeathTick, 60, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_errant_saved_strike_reanchors_and_resolves_exactly_once", SanctumCombatServerTests::errantSavedStrikeReanchorsAndResolvesExactlyOnce, 35, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_ripple_attack_creates_one_cast_and_starts_its_cooldown_and_movement_lock", SanctumCombatServerTests::rippleAttackCreatesOneCastAndStartsItsCooldownAndMovementLock, 40, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_ripple_cast_waits_twenty_nine_end_ticks_then_creates_one_err_field", SanctumCombatServerTests::rippleCastWaitsTwentyNineEndTicksThenCreatesOneErrField, 40, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_ripple_electrified_status_cancels_the_committed_cast", SanctumCombatServerTests::rippleElectrifiedStatusCancelsTheCommittedCast, 40, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_ripple_weakness_status_cancels_the_committed_cast", SanctumCombatServerTests::rippleWeaknessStatusCancelsTheCommittedCast, 40, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_ripple_infected_status_cancels_the_committed_cast", SanctumCombatServerTests::rippleInfectedStatusCancelsTheCommittedCast, 40, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_ripple_caster_removal_cancels_the_committed_cast", SanctumCombatServerTests::rippleCasterRemovalCancelsTheCommittedCast, 40, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_ripple_retains_the_last_aim_after_its_target_is_removed", SanctumCombatServerTests::rippleRetainsTheLastAimAfterItsTargetIsRemoved, 40, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_gunner_volley_fires_its_three_bullets_at_end_ticks_eleven_sixteen_and_twenty_one", SanctumCombatServerTests::gunnerVolleyFiresItsThreeBulletsAtEndTicksElevenSixteenAndTwentyOne, 35, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_gunner_volley_finishes_its_committed_third_shot_after_caster_death", SanctumCombatServerTests::gunnerVolleyFinishesItsCommittedThirdShotAfterCasterDeath, 35, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_gunner_volley_keeps_firing_at_its_snapshotted_aim_after_target_removal", SanctumCombatServerTests::gunnerVolleyKeepsFiringAtItsSnapshottedAimAfterTargetRemoval, 35, "supernova_server");
        TsrGameTests.add("history_sanctum_combat_server_tests_gunner_cooldown_rejects_repeated_attack_without_duplicating_volley", SanctumCombatServerTests::gunnerCooldownRejectsRepeatedAttackWithoutDuplicatingVolley, 25, "supernova_server");
    }
    private SanctumCombatServerTests() { }
}
