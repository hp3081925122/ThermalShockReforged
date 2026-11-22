package com.hp.thermalshockreforged.entity.icesquad;

import com.hp.thermalshockreforged.gametest.TsrGameTests;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;

public final class BastionServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_bastion_current_configured_attributes_and_nbt_reload", BastionServerBehaviorTests::configuredAttributesAndNbtReload, 100, "supernova_server");
        TsrGameTests.add("history_bastion_current_gunner_ai_fires_twelve_configured_salvo_shards", BastionServerBehaviorTests::gunnerAiFiresTwelveConfiguredSalvoShards, 100, "supernova_server");
        TsrGameTests.add("history_bastion_current_lethal_damage_clears_pending_ai_salvo", BastionServerBehaviorTests::lethalDamageClearsPendingAiSalvo, 120, "supernova_server");
        TsrGameTests.add("history_bastion_current_burner_ai_vent_uses_attack_damage_and_applies_fire", BastionServerBehaviorTests::burnerAiVentUsesAttackDamageAndAppliesFire, 100, "supernova_server");
        TsrGameTests.add("history_bastion_current_boss_immunities_and_persistence_remain_specific", BastionServerBehaviorTests::bossImmunitiesAndPersistenceRemainSpecific, 100, "supernova_server");
        TsrGameTests.add("history_bastion_current_reactive_bullet_consumes_first_hit_and_captures_damage", BastionServerBehaviorTests::reactiveBulletConsumesFirstHitAndCapturesDamage, 100, "supernova_server");
        TsrGameTests.add("history_bastion_current_death_enrage_reaches_nearby_opposite_variant", BastionServerBehaviorTests::deathEnrageReachesNearbyOppositeVariant, 100, "supernova_server");
        TsrGameTests.add("history_bastion_current_player_kill_defers_experience_until_death_tick_twenty", BastionServerBehaviorTests::playerKillDefersExperienceUntilDeathTickTwenty, 100, "supernova_server");
    }

    public static void configuredAttributesAndNbtReload(GameTestHelper h) {
        for (int k = 0; k < 2; k++) {
            var boss = boss(h, k, true);
            var expected = new double[]{BastionCombatConfig.HEALTH[k].get(), BastionCombatConfig.ARMOR[k].get(), BastionCombatConfig.DAMAGE[k].get(), BastionCombatConfig.SPEED[k].get(), BastionCombatConfig.RESISTANCE[k].get()};
            var attributes = List.of(Attributes.MAX_HEALTH, Attributes.ARMOR, Attributes.ATTACK_DAMAGE, Attributes.MOVEMENT_SPEED, Attributes.KNOCKBACK_RESISTANCE);
            for (int i = 0; i < attributes.size(); i++) h.assertTrue(Math.abs(boss.getAttributeValue(attributes.get(i)) - expected[i]) < 1.0E-9, "Configured attribute must be applied to both Bastion variants: " + attributes.get(i));
            h.assertTrue(boss.texture().equals(k == 0 ? "icidrobastionblu" : "icidrobastionred"), "Each Bastion variant must keep its native default texture before an override is applied");
            boss.setTexture("test_texture_" + k);
            boss.setHealth(Math.min(31, boss.getMaxHealth() - 1));
            float health = boss.getHealth();
            var tag = save(boss);
            var restored = type(k).create(h.getLevel(), EntitySpawnReason.COMMAND);
            restored.load(input(h, tag));
            h.assertTrue(restored.texture().equals("test_texture_" + k) && restored.getHealth() == health && restored.isPersistenceRequired(), "Texture, injured health and persistence must survive entity NBT reload");
            boss.discard();
            restored.discard();
        }
        h.succeed();
    }

    public static void gunnerAiFiresTwelveConfiguredSalvoShards(GameTestHelper h) {
        var boss = boss(h, 0, false);
        boss.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(37);
        var target = com.hp.thermalshockreforged.gametest.PlayerInteractionTests.player(h, GameType.SURVIVAL, 15.5, 2.5, 8.5);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(10000);
        target.setHealth(10000);
        target.setNoGravity(true);
        target.setDeltaMovement(Vec3.ZERO);
        boss.setTarget(target);
        var shots = new ArrayList<Shot>();
        Consumer<EntityJoinLevelEvent> listener = event -> {
            if (event.getLevel() == h.getLevel() && event.getEntity() instanceof IceSquadProjectileEntity projectile && uuidEquals(save(projectile), "SquadProjectileCaster", boss.getUUID())) {
                shots.add(new Shot(h.getLevel().getGameTime(), projectile));
            }
        };
        NeoForge.EVENT_BUS.addListener(listener);
        long start = h.getLevel().getGameTime();
        h.runAfterDelay(32, () -> {
            if (boss.cast() != BastionEntity.SALVO) {
                NeoForge.EVENT_BUS.unregister(listener);
                shots.forEach(shot -> shot.projectile().discard());
                boss.discard();
                com.hp.thermalshockreforged.gametest.PlayerInteractionTests.release(h, target);
                h.assertTrue(false, "Gunner AI must select its configured salvo after its initial cooldown; cast=" + boss.cast() + ", targetAlive=" + target.isAlive() + ", distance=" + boss.distanceTo(target) + ", sight=" + boss.hasLineOfSight(target));
            }
        });
        h.runAfterDelay(42, () -> h.assertTrue(shots.size() == 2 && shots.get(0).time() - start == 40 && shots.get(1).time() - start == 42, "The first shard must fire when castTicks reaches ten and the next two ticks later; shots=" + shots.stream().map(shot -> shot.time() - start).toList() + ", cast=" + boss.cast() + ", castTicks=" + castTicks(boss) + ", gameTime=" + h.getLevel().getGameTime() + ", bossRemoved=" + boss.isRemoved() + ", bossAlive=" + boss.isAlive() + ", targetRemoved=" + target.isRemoved() + ", targetAlive=" + target.isAlive() + ", distance=" + boss.distanceTo(target) + ", sight=" + boss.hasLineOfSight(target)));
        h.runAfterDelay(70, () -> {
            try {
                h.assertTrue(shots.size() == 12, "The current Gunner salvo must emit twelve shards");
                for (int i = 0; i < shots.size(); i++) {
                    var shot = shots.get(i);
                    var projectile = shot.projectile();
                    h.assertTrue(shot.time() - start == 40 + i * 2, "Gunner salvo shot timing must remain spaced by two actual world ticks from cast tick ten");
                    h.assertTrue(projectile.getType() == IceSquadContent.GUNNER_BULLET.get() && projectile.getOwner() == null && projectile.pickup == AbstractArrow.Pickup.DISALLOWED && projectile.isSilent(), "Salvo shards must retain their registered projectile and ownerless safety flags");
                    h.assertTrue(Math.abs(doubleValue(save(projectile), "damage") - 37 * BastionCombatConfig.SHARD_MULTIPLIER.get() / 2) < 1.0E-9, "Every shard must use configured base-attack damage scaled by its speed compensation");
                }
                h.assertTrue(boss.cast() == BastionEntity.NONE, "Gunner must finish the current salvo at its native cast boundary");
                h.succeed();
            } finally {
                NeoForge.EVENT_BUS.unregister(listener);
                shots.forEach(shot -> shot.projectile().discard());
                boss.discard();
                com.hp.thermalshockreforged.gametest.PlayerInteractionTests.release(h, target);
            }
        });
    }

    public static void lethalDamageClearsPendingAiSalvo(GameTestHelper h) {
        var boss = boss(h, 0, false);
        var target = com.hp.thermalshockreforged.gametest.PlayerInteractionTests.player(h, GameType.SURVIVAL, 15.5, 2.5, 8.5);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(10000);
        target.setHealth(10000);
        target.setNoGravity(true);
        target.setDeltaMovement(Vec3.ZERO);
        boss.setTarget(target);
        var shots = new ArrayList<IceSquadProjectileEntity>();
        Consumer<EntityJoinLevelEvent> listener = event -> {
            if (event.getLevel() == h.getLevel() && event.getEntity() instanceof IceSquadProjectileEntity projectile && uuidEquals(save(projectile), "SquadProjectileCaster", boss.getUUID())) shots.add(projectile);
        };
        NeoForge.EVENT_BUS.addListener(listener);
        h.runAfterDelay(32, () -> {
            try {
                h.assertTrue(boss.cast() == BastionEntity.SALVO, "Gunner AI must commit its ordinary salvo before the lethal hit");
                float targetHealthAtDeath = target.getHealth();
                int launchedAtDeath = shots.size();
                shots.forEach(IceSquadProjectileEntity::discard);
                h.assertTrue(boss.hurtServer(h.getLevel(), h.getLevel().damageSources().generic(), 100000) && boss.isDeadOrDying(), "A real lethal damage event must start Bastion death processing");
                h.assertTrue(boss.cast() == BastionEntity.NONE, "Lethal damage must clear the pending cast immediately");
                h.runAfterDelay(48, () -> {
                    try {
                        h.assertTrue(shots.size() == launchedAtDeath && shots.stream().allMatch(IceSquadProjectileEntity::isRemoved), "A cleared pending salvo must not launch new shards or leave an old shard active after the lethal hit; before=" + launchedAtDeath + ", after=" + shots.size());
                        h.assertTrue(Math.abs(target.getHealth() - targetHealthAtDeath) < 1.0E-6, "A cleared pending salvo must not damage its live target after lethal damage; before=" + targetHealthAtDeath + ", after=" + target.getHealth());
                        h.succeed();
                    } finally {
                        NeoForge.EVENT_BUS.unregister(listener);
                        shots.forEach(IceSquadProjectileEntity::discard);
                        boss.discard();
                        com.hp.thermalshockreforged.gametest.PlayerInteractionTests.release(h, target);
                    }
                });
            } catch (RuntimeException | Error failure) {
                NeoForge.EVENT_BUS.unregister(listener);
                shots.forEach(IceSquadProjectileEntity::discard);
                boss.discard();
                com.hp.thermalshockreforged.gametest.PlayerInteractionTests.release(h, target);
                throw failure;
            }
        });
    }

    public static void burnerAiVentUsesAttackDamageAndAppliesFire(GameTestHelper h) {
        var boss = boss(h, 1, false);
        boss.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(20);
        var target = cow(h, boss.position().add(3, 0, 0), 100);
        boss.setTarget(target);
        h.runAfterDelay(2, () -> h.assertTrue(boss.cast() == BastionEntity.VENT, "Burner AI must choose its close-range vent"));
        h.runAfterDelay(18, () -> {
            h.assertTrue(target.getHealth() < 100 && target.getRemainingFireTicks() > 0, "Vent must damage and ignite a nearby target");
            h.assertTrue(Math.abs((100 - target.getHealth()) - 20 * BastionCombatConfig.VENT_MULTIPLIER.get()) < .01, "Vent damage must use current attack damage multiplied by the configured vent multiplier");
            boss.discard();
            target.discard();
            h.succeed();
        });
    }

    public static void bossImmunitiesAndPersistenceRemainSpecific(GameTestHelper h) {
        for (int k = 0; k < 2; k++) {
            var boss = boss(h, k, true);
            float before = boss.getHealth();
            h.assertTrue(!boss.hurtServer(h.getLevel(), h.getLevel().damageSources().drown(), 7) && !boss.hurtServer(h.getLevel(), h.getLevel().damageSources().lightningBolt(), 7) && boss.getHealth() == before, "Drowning and lightning must follow the boss-specific immunity rules");
            h.assertTrue(boss.hurtServer(h.getLevel(), h.getLevel().damageSources().generic(), 7) && boss.getHealth() < before, "Generic damage must remain available");
            h.assertTrue(!boss.removeWhenFarAway(Double.MAX_VALUE) && boss.isPersistenceRequired() && !boss.canTeleport(h.getLevel(), h.getLevel().getServer().getLevel(net.minecraft.world.level.Level.NETHER)), "Both variants must remain persistent and reject cross-dimensional travel");
            boss.discard();
        }
        h.succeed();
    }

    public static void reactiveBulletConsumesFirstHitAndCapturesDamage(GameTestHelper h) {
        var boss = boss(h, 0, true);
        boss.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(12);
        int castBeforeHit = boss.cast();
        var projectile = IceSquadContent.GUNNER_BULLET.get().create(h.getLevel(), EntitySpawnReason.COMMAND);
        projectile.configure(boss, false, BastionCombatConfig.SHARD_MULTIPLIER.get() / 2, 0);
        projectile.setPos(boss.position().add(3, 1, 0));
        projectile.setDeltaMovement(1, 0, 0);
        h.assertTrue(h.getLevel().addFreshEntity(projectile), "The reactive shard must join the server level before its collision callback");
        var target = cow(h, boss.position().add(4, 0, 0), 100);
        float before = target.getHealth();
        h.assertTrue(projectile.getOwner() == null && uuidEquals(save(projectile), "SquadProjectileCaster", boss.getUUID()), "Ownerless reactive shards must preserve caster identity");
        h.assertTrue(Math.abs(doubleValue(save(projectile), "damage") - 12 * BastionCombatConfig.SHARD_MULTIPLIER.get() / 2) < 1.0E-9, "The reactive shard must capture the configured attack damage");
        projectile.onHitEntity(new EntityHitResult(target));
        float after = target.getHealth();
        h.assertTrue(after < before && projectile.isRemoved() && booleanValue(save(projectile), "SquadProjectileHit") && boss.cast() == castBeforeHit, "The first entity hit must damage and consume the reactive shard without re-entering the boss cast bridge; before=" + before + ", after=" + after + ", removed=" + projectile.isRemoved() + ", hitConsumed=" + booleanValue(save(projectile), "SquadProjectileHit") + ", castBefore=" + castBeforeHit + ", castAfter=" + boss.cast() + ", capturedDamage=" + doubleValue(save(projectile), "damage"));
        target.invulnerableTime = 0;
        projectile.onHitEntity(new EntityHitResult(target));
        h.assertTrue(target.getHealth() == after, "A consumed shard must not deal damage a second time");
        target.discard();
        float bossHealth = boss.getHealth();
        var selfShot = IceSquadContent.GUNNER_BULLET.get().create(h.getLevel(), EntitySpawnReason.COMMAND);
        selfShot.configure(boss, false, BastionCombatConfig.SHARD_MULTIPLIER.get() / 2, 0);
        selfShot.setPos(boss.position().add(-2, 1, 0));
        selfShot.setDeltaMovement(1, 0, 0);
        h.assertTrue(h.getLevel().addFreshEntity(selfShot), "The caster-crossing projectile must join the server level");
        h.runAfterDelay(5, () -> {
            h.assertTrue(boss.getHealth() == bossHealth, "A real projectile trajectory through its caster must not damage or re-enter the caster");
            selfShot.discard();
            boss.discard();
            h.succeed();
        });
    }

    public static void deathEnrageReachesNearbyOppositeVariant(GameTestHelper h) {
        for (int k = 0; k < 2; k++) {
            var dead = boss(h, k, true);
            var same = boss(h, k, true);
            same.setPos(dead.position().add(4, 0, 0));
            var partner = boss(h, 1 - k, true);
            partner.setPos(dead.position().add(5, 0, 0));
            var corner = boss(h, 1 - k, true);
            corner.setPos(dead.position().add(20, 0, 20));
            var far = boss(h, 1 - k, true);
            far.setPos(dead.position().add(60, 0, 0));
            dead.hurtServer(h.getLevel(), h.getLevel().damageSources().generic(), 100000);
            h.assertTrue(partner.enraged() && corner.enraged(), "Boss death must enrage every opposite variant inside the current search box, including its corner");
            h.assertTrue(!same.enraged() && !far.enraged(), "The death enrage must exclude the same variant and entities outside its search box");
            for (var entity : List.of(dead, same, partner, corner, far)) entity.discard();
        }
        h.succeed();
    }

    public static void playerKillDefersExperienceUntilDeathTickTwenty(GameTestHelper h) {
        var level = h.getLevel();
        boolean loot = level.getGameRules().get(GameRules.MOB_DROPS);
        level.getGameRules().set(GameRules.MOB_DROPS, true, level.getServer());
        try {
            for (int k = 0; k < 2; k++) {
                var boss = boss(h, k, true);
                var records = new ArrayList<Integer>();
                var player = com.hp.thermalshockreforged.gametest.PlayerInteractionTests.player(h, GameType.SURVIVAL, 12.5, 2.5, 8.5);
                player.setNoGravity(true);
                List<Player> attackers = new ArrayList<>();
                Consumer<LivingExperienceDropEvent> listener = event -> { if (event.getEntity() == boss) { records.add(event.getDroppedExperience()); attackers.add(event.getAttackingPlayer()); } };
                NeoForge.EVENT_BUS.addListener(listener);
                try {
                    h.assertTrue(boss.hurtServer(level, level.damageSources().playerAttack(player), 100000) && boss.isDeadOrDying(), "Player-attributed lethal damage must begin native death processing");
                    h.assertTrue(records.isEmpty() && booleanValue(save(boss), "BastionExperiencePending"), "Experience must remain deferred immediately after the death callback");
                    for (int i = 0; i < 19; i++) boss.tickDeath();
                    h.assertTrue(!boss.isRemoved() && records.isEmpty(), "No experience or removal may occur before death tick twenty");
                    boss.tickDeath();
                    h.assertTrue(boss.isRemoved() && records.equals(List.of(400)) && attackers.equals(List.of(player)) && !booleanValue(save(boss), "BastionExperiencePending"), "Death tick twenty must emit one four-hundred XP reward attributed to the connected killer");
                    boss.tickDeath();
                    h.assertTrue(records.size() == 1, "An already removed Bastion must not emit its deferred XP again");
                } finally {
                    NeoForge.EVENT_BUS.unregister(listener);
                    boss.discard();
                    com.hp.thermalshockreforged.gametest.PlayerInteractionTests.release(h, player);
                }
            }
        } finally {
            level.getGameRules().set(GameRules.MOB_DROPS, loot, level.getServer());
        }
        h.succeed();
    }

    private record Shot(long time, IceSquadProjectileEntity projectile) { }
    private static int castTicks(BastionEntity entity) { try { var field = BastionEntity.class.getDeclaredField("castTicks"); field.setAccessible(true); return field.getInt(entity); } catch (ReflectiveOperationException exception) { throw new IllegalStateException(exception); } }
    private static BlockPos pos(GameTestHelper h) { return h.absolutePos(new BlockPos(10, 2, 8)); }
    private static EntityType<BastionEntity> type(int k) { return (k == 0 ? IceSquadContent.GUNNER : IceSquadContent.BURNER).get(); }
    private static BastionEntity boss(GameTestHelper h, int k, boolean noAi) { var entity = type(k).create(h.getLevel(), EntitySpawnReason.COMMAND); entity.setPos(Vec3.atCenterOf(pos(h))); entity.setNoAi(noAi); entity.setNoGravity(true); h.assertTrue(h.getLevel().addFreshEntity(entity), "Boss fixture must join the server level"); return entity; }
    private static Cow cow(GameTestHelper h, Vec3 position, double health) { var entity = EntityType.COW.create(h.getLevel(), EntitySpawnReason.COMMAND); entity.setNoAi(true); entity.setNoGravity(true); entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health); entity.setHealth((float) health); entity.setPos(position); h.getLevel().addFreshEntity(entity); return entity; }
    private static CompoundTag save(Entity entity) { var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private static net.minecraft.world.level.storage.ValueInput input(GameTestHelper h, CompoundTag tag) { return TagValueInput.create(ProblemReporter.DISCARDING, h.getLevel().registryAccess(), tag); }
    private static int intValue(CompoundTag tag, String key) { return ((net.minecraft.nbt.NumericTag) tag.get(key)).intValue(); }
    private static double doubleValue(CompoundTag tag, String key) { return ((net.minecraft.nbt.NumericTag) tag.get(key)).doubleValue(); }
    private static boolean booleanValue(CompoundTag tag, String key) { return ((net.minecraft.nbt.NumericTag) tag.get(key)).byteValue() != 0; }
    private static boolean uuidEquals(CompoundTag tag, String key, UUID expected) { return expected.toString().equals(tag.getString(key).orElse("")); }
    private BastionServerBehaviorTests() { }
}
