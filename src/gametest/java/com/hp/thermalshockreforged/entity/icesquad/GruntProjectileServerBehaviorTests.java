package com.hp.thermalshockreforged.entity.icesquad;

import com.hp.thermalshockreforged.gametest.TsrGameTests;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class GruntProjectileServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_cryonic_native_damage_still_passes_through_target_armor", GruntProjectileServerBehaviorTests::cryonicNativeDamageStillPassesThroughTargetArmor, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_projectile_reload_clamps_negative_and_excess_lifetime_to_type_bounds", GruntProjectileServerBehaviorTests::projectileReloadClampsNegativeAndExcessLifetimeToTypeBounds, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_projectile_owner_reassignment_never_enables_pickup", GruntProjectileServerBehaviorTests::projectileOwnerReassignmentNeverEnablesPickup, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_strider_and_reactive_variants_retain_their_distinct_lifetime_and_display_item", GruntProjectileServerBehaviorTests::striderAndReactiveVariantsRetainTheirDistinctLifetimeAndDisplayItem, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_cryonic_entity_hit_uses_native_arrow_damage_and_consumes_projectile", GruntProjectileServerBehaviorTests::cryonicEntityHitUsesNativeArrowDamageAndConsumesProjectile, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_cryonic_reload_retains_remaining_lifetime_and_caster_identity", GruntProjectileServerBehaviorTests::cryonicReloadRetainsRemainingLifetimeAndCasterIdentity, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_cryonic_projectile_lifetime_starts_on_world_tick_and_expires_at_eighty_end_ticks", GruntProjectileServerBehaviorTests::cryonicProjectileLifetimeStartsOnWorldTickAndExpiresAtEightyEndTicks, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_current_gunner_bullet_uses_registered_orb_and_no_pickup", GruntProjectileServerBehaviorTests::gunnerBulletUsesRegisteredOrbAndNoPickup, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_cryonic_configuration_clamps_nonfinite_damage_and_knockback", GruntProjectileServerBehaviorTests::cryonicConfigurationClampsNonfiniteDamageAndKnockback, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_cryonic_projectiles_can_be_unowned_while_retaining_caster_uuid", GruntProjectileServerBehaviorTests::cryonicProjectilesCanBeUnownedWhileRetainingCasterUuid, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_cryonic_projectiles_capture_owner_damage_and_pickup_policy", GruntProjectileServerBehaviorTests::cryonicProjectilesCaptureOwnerDamageAndPickupPolicy, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_grunt_never_selects_passive_cow_as_target", GruntProjectileServerBehaviorTests::gruntNeverSelectsPassiveCowAsTarget, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_grunt_environmental_death_defers_experience_without_invented_attacker", GruntProjectileServerBehaviorTests::gruntEnvironmentalDeathDefersExperienceWithoutInventedAttacker, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_grunt_death_defers_five_experience_until_tick_twenty", GruntProjectileServerBehaviorTests::gruntDeathDefersFiveExperienceUntilTickTwenty, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_grunt_native_melee_applies_configured_damage_and_attack_knockback", GruntProjectileServerBehaviorTests::gruntNativeMeleeAppliesConfiguredDamageAndAttackKnockback, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_grunt_texture_and_injured_health_survive_entity_nbt_reload", GruntProjectileServerBehaviorTests::gruntTextureAndInjuredHealthSurviveEntityNbtReload, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_grunt_rejects_only_drowning_and_accepts_generic_damage", GruntProjectileServerBehaviorTests::gruntRejectsOnlyDrowningAndAcceptsGenericDamage, 100, "supernova_server");
        TsrGameTests.add("history_grunt_projectile_server_behavior_tests_grunt_registers_exact_vanilla_combat_attributes_and_scaled_dimensions", GruntProjectileServerBehaviorTests::gruntRegistersExactVanillaCombatAttributesAndScaledDimensions, 100, "supernova_server");
    }
    public static void gruntRegistersExactVanillaCombatAttributesAndScaledDimensions(GameTestHelper h) {
        IcidrogruntEntity grunt = grunt(h, 4, 2, 4);
        h.assertTrue(grunt.getAttributeValue(Attributes.MAX_HEALTH) == 8 && grunt.getHealth() == 8, "Grunt must retain eight configured maximum and current health");
        h.assertTrue(grunt.getAttributeValue(Attributes.ARMOR) == 1 && grunt.getAttributeValue(Attributes.ATTACK_DAMAGE) == 2, "Grunt must retain one armor and two melee attack damage");
        h.assertTrue(grunt.getAttributeValue(Attributes.MOVEMENT_SPEED) == .2 && grunt.getAttributeValue(Attributes.FOLLOW_RANGE) == 8, "Grunt must retain its native movement and target range attributes");
        h.assertTrue(Math.abs(grunt.getBbWidth() - .54) < .001 && Math.abs(grunt.getBbHeight() - 1.44) < .001, "Grunt dimensions must use the actual 0.9 entity scaling");
        grunt.discard(); h.succeed();
    }

    public static void gruntRejectsOnlyDrowningAndAcceptsGenericDamage(GameTestHelper h) {
        IcidrogruntEntity grunt = grunt(h, 4, 2, 4);
        float before = grunt.getHealth();
        h.assertTrue(!grunt.hurtServer(h.getLevel(), h.getLevel().damageSources().drown(), 3) && grunt.getHealth() == before, "Drowning must be rejected without reducing Grunt health");
        h.assertTrue(grunt.hurtServer(h.getLevel(), h.getLevel().damageSources().generic(), 3) && grunt.getHealth() == before - 3, "Generic damage must pass through the native hurt path");
        grunt.discard(); h.succeed();
    }

    public static void gruntTextureAndInjuredHealthSurviveEntityNbtReload(GameTestHelper h) {
        IcidrogruntEntity grunt = grunt(h, 4, 2, 4);
        grunt.setTexture("icidrogrunt_variant");
        grunt.setHealth(3);
        CompoundTag tag = save(grunt);
        IcidrogruntEntity restored = IceSquadContent.GRUNT.get().create(h.getLevel(), EntitySpawnReason.COMMAND);
        h.assertTrue(restored != null, "Registered Grunt type must create a reload instance");
        restored.load(input(h, tag));
        h.assertTrue(restored.texture().equals("icidrogrunt_variant") && restored.getHealth() == 3, "Entity NBT must retain its texture and injured health");
        grunt.discard(); restored.discard(); h.succeed();
    }

    public static void gruntNativeMeleeAppliesConfiguredDamageAndAttackKnockback(GameTestHelper h) {
        IcidrogruntEntity grunt = grunt(h, 4, 2, 4);
        Cow victim = cow(h, grunt.position().add(1, 0, 0));
        float before = victim.getHealth();
        h.assertTrue(grunt.doHurtTarget(h.getLevel(), victim), "Grunt melee attack must be accepted by the native target hurt path");
        h.assertTrue(before - victim.getHealth() == 2, "Grunt melee attack must apply its configured two damage");
        h.assertTrue(victim.getDeltaMovement().horizontalDistanceSqr() > 0, "Native melee attack knockback must move the victim");
        grunt.discard(); victim.discard(); h.succeed();
    }

    public static void gruntDeathDefersFiveExperienceUntilTickTwenty(GameTestHelper h) {
        IcidrogruntEntity grunt = grunt(h, 4, 2, 4);
        Player attacker = h.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        List<Integer> drops = new ArrayList<>();
        Consumer<LivingExperienceDropEvent> listener = event -> { if (event.getEntity() == grunt) drops.add(event.getDroppedExperience()); };
        NeoForge.EVENT_BUS.addListener(listener);
        try {
            h.assertTrue(grunt.hurtServer(h.getLevel(), h.getLevel().damageSources().playerAttack(attacker), 100) && grunt.isDeadOrDying(), "Player lethal damage must enter Grunt death processing");
            h.assertTrue(drops.isEmpty() && booleanValue(save(grunt), "GruntExperiencePending"), "Experience must remain pending immediately after death callback");
            for (int i = 0; i < 19; i++) grunt.tickDeath();
            h.assertTrue(!grunt.isRemoved() && drops.isEmpty(), "Grunt must retain its death animation and award no XP before tick twenty");
            grunt.tickDeath();
            h.assertTrue(grunt.isRemoved() && drops.equals(List.of(5)) && !booleanValue(save(grunt), "GruntExperiencePending"), "Death tick twenty must remove Grunt and award exactly five XP once");
            grunt.tickDeath();
            h.assertTrue(drops.equals(List.of(5)), "Repeated death processing must not duplicate Grunt XP");
        } finally { NeoForge.EVENT_BUS.unregister(listener); grunt.discard(); }
        h.succeed();
    }

    public static void gruntEnvironmentalDeathDefersExperienceWithoutInventedAttacker(GameTestHelper h) {
        IcidrogruntEntity grunt = grunt(h, 4, 2, 4);
        List<Integer> drops = new ArrayList<>();
        Consumer<LivingExperienceDropEvent> listener = event -> { if (event.getEntity() == grunt) drops.add(event.getDroppedExperience()); };
        NeoForge.EVENT_BUS.addListener(listener);
        try {
            h.assertTrue(grunt.hurtServer(h.getLevel(), h.getLevel().damageSources().generic(), 100) && booleanValue(save(grunt), "GruntExperiencePending"), "Environmental lethal damage must also schedule the vanilla XP path");
            for (int i = 0; i < 20; i++) grunt.tickDeath();
            h.assertTrue(grunt.isRemoved() && drops.isEmpty(), "Environmental death without a player attacker must finish its timer without granting vanilla XP");
        } finally { NeoForge.EVENT_BUS.unregister(listener); grunt.discard(); }
        h.succeed();
    }

    public static void gruntNeverSelectsPassiveCowAsTarget(GameTestHelper h) {
        IcidrogruntEntity grunt = IceSquadContent.GRUNT.get().create(h.getLevel(), EntitySpawnReason.COMMAND);
        h.assertTrue(grunt != null, "Registered Grunt type must create an AI test entity");
        grunt.setPos(h.absolutePos(new BlockPos(4, 2, 4)).getCenter());
        h.assertTrue(h.getLevel().addFreshEntity(grunt), "AI Grunt must join the server world");
        Cow cow = cow(h, grunt.position().add(2, 0, 0));
        for (int i = 0; i < 8; i++) grunt.tick();
        h.assertTrue(grunt.getTarget() != cow, "Grunt target selector must exclude passive cows rather than treating them as hostile targets");
        grunt.discard(); cow.discard(); h.succeed();
    }

    public static void cryonicProjectilesCaptureOwnerDamageAndPickupPolicy(GameTestHelper h) {
        IcidrogruntEntity source = grunt(h, 4, 2, 4);
        source.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(11);
        IceSquadProjectileEntity normal = projectile(h, IceSquadContent.CRYONIC_PROJECTILE.get(), source, true, 1.5, .4);
        IceSquadProjectileEntity alternate = projectile(h, IceSquadContent.ALT_CRYONIC_PROJECTILE.get(), source, true, 2, .6);
        h.assertTrue(normal.getOwner() == source && doubleValue(save(normal), "damage") == 16.5 && normal.pickup == AbstractArrow.Pickup.DISALLOWED && normal.isSilent() && !normal.isCritArrow(), "Cryonic shot must retain owner, exact configured damage, silent state and no pickup");
        h.assertTrue(alternate.getOwner() == source && doubleValue(save(alternate), "damage") == 22 && alternate.pickup == AbstractArrow.Pickup.DISALLOWED && alternate.isSilent() && !alternate.isCritArrow(), "Alternate cryonic shot must use its independent multiplier and the same native safety flags");
        source.discard(); normal.discard(); alternate.discard(); h.succeed();
    }

    public static void cryonicProjectilesCanBeUnownedWhileRetainingCasterUuid(GameTestHelper h) {
        IcidrogruntEntity source = grunt(h, 4, 2, 4);
        IceSquadProjectileEntity shot = IceSquadContent.CRYONIC_PROJECTILE.get().create(h.getLevel(), EntitySpawnReason.COMMAND);
        shot.configure(source, false, 3, .5);
        CompoundTag tag = save(shot);
        h.assertTrue(shot.getOwner() == null && uuidEquals(tag, "SquadProjectileCaster", source.getUUID()), "Unowned projectile must keep independent caster identity for self-hit filtering");
        h.assertTrue(doubleValue(save(shot), "damage") == 6 && doubleValue(tag, "SquadProjectileKnockback") == .5 && shot.pickup == AbstractArrow.Pickup.DISALLOWED, "Unowned projectile must still capture exact source-based damage and knockback");
        source.discard(); shot.discard(); h.succeed();
    }

    public static void cryonicConfigurationClampsNonfiniteDamageAndKnockback(GameTestHelper h) {
        IcidrogruntEntity source = grunt(h, 4, 2, 4);
        source.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0);
        IceSquadProjectileEntity shot = projectile(h, IceSquadContent.CRYONIC_PROJECTILE.get(), source, true, Double.POSITIVE_INFINITY, Double.NaN);
        h.assertTrue(Double.isFinite(doubleValue(save(shot), "damage")) && doubleValue(save(shot), "damage") == 0, "Nonfinite caster damage must be captured as finite zero damage; actual=" + doubleValue(save(shot), "damage") + ", attr=" + source.getAttributeValue(Attributes.ATTACK_DAMAGE));
        h.assertTrue(Double.isFinite(doubleValue(save(shot), "SquadProjectileKnockback")) && doubleValue(save(shot), "SquadProjectileKnockback") == 0, "Nonfinite impact knockback must be captured as zero");
        source.discard(); shot.discard(); h.succeed();
    }

    public static void gunnerBulletUsesRegisteredOrbAndNoPickup(GameTestHelper h) {
        IcidrogruntEntity source = grunt(h, 4, 2, 4);
        IceSquadProjectileEntity bullet = projectile(h, IceSquadContent.GUNNER_BULLET.get(), source, false, 1, 0);
        h.assertTrue(bullet.getRemainingFireTicks() <= 0 && bullet.getItem().is(com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent.item(com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind.BLUE_ORB)) && bullet.pickup == AbstractArrow.Pickup.DISALLOWED, "Current Gunner shards use the registered blue-orb display item and never enable pickup");
        source.discard(); bullet.discard(); h.succeed();
    }

    public static void cryonicProjectileLifetimeStartsOnWorldTickAndExpiresAtEightyEndTicks(GameTestHelper h) {
        IcidrogruntEntity source = grunt(h, 4, 2, 4);
        IceSquadProjectileEntity shot = projectile(h, IceSquadContent.CRYONIC_PROJECTILE.get(), source, true, 1, 0);
        shot.setNoPhysics(true);
        h.assertTrue(age(shot) == 0, "New Cryonic projectile must start at age zero");
        for (int i = 1; i < 79; i++) {
            advance(h, shot, 1);
            h.assertTrue(!shot.isRemoved() && age(shot) == i, "Cryonic projectile must remain alive through age " + i);
        }
        advance(h, shot, 1);
        h.assertTrue(!shot.isRemoved() && age(shot) == 79, "Cryonic projectile must survive at age 79");
        advance(h, shot, 1);
        h.assertTrue(shot.isRemoved() && age(shot) == 80, "Cryonic projectile must be removed on its eightieth entity tick");

        IceSquadProjectileEntity worldShot = projectile(h, IceSquadContent.CRYONIC_PROJECTILE.get(), source, true, 1, 0);
        worldShot.setNoPhysics(true);
        h.runAfterDelay(80, () -> {
            int worldAge = age(worldShot);
            h.assertTrue((worldAge == 79 && !worldShot.isRemoved()) || (worldAge == 80 && worldShot.isRemoved()), "Real world ticking must reach the age-79/80 lifetime boundary; age=" + worldAge + ", removed=" + worldShot.isRemoved());
            h.runAfterDelay(2, () -> {
                h.assertTrue(worldShot.isRemoved() && age(worldShot) == 80, "Real world ticking must remove Cryonic by the tick-81 observation");
                source.discard();
                h.succeed();
            });
        });
    }

    public static void cryonicReloadRetainsRemainingLifetimeAndCasterIdentity(GameTestHelper h) {
        IcidrogruntEntity source = grunt(h, 4, 2, 4);
        IceSquadProjectileEntity shot = projectile(h, IceSquadContent.CRYONIC_PROJECTILE.get(), source, false, 1, 0);
        shot.setNoPhysics(true);
        advance(h, shot, 17);
        CompoundTag tag = save(shot);
        int savedAge = age(shot);
        IceSquadProjectileEntity restored = IceSquadContent.CRYONIC_PROJECTILE.get().create(h.getLevel(), EntitySpawnReason.COMMAND);
        restored.load(input(h, tag));
        restored.setNoPhysics(true);
        h.assertTrue(savedAge == 17 && age(restored) == 17 && uuidEquals(save(restored), "SquadProjectileCaster", source.getUUID()), "Entity NBT reload must preserve age 17 and caster UUID; savedAge=" + savedAge + ", restoredAge=" + age(restored));
        advance(h, restored, 1);
        h.assertTrue(!restored.isRemoved() && age(restored) == 18, "Restored Cryonic must continue its remaining 62 ticks from age 17");
        shot.discard(); restored.discard(); source.discard(); h.succeed();
    }

    public static void cryonicEntityHitUsesNativeArrowDamageAndConsumesProjectile(GameTestHelper h) {
        IcidrogruntEntity source = grunt(h, 4, 2, 4);
        source.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4);
        Cow target = cow(h, source.position().add(2, 0, 0));
        IceSquadProjectileEntity shot = projectile(h, IceSquadContent.CRYONIC_PROJECTILE.get(), source, true, 1.5, .4);
        shot.setDeltaMovement(1, 0, 0);
        float before = target.getHealth();
        target.setArrowCount(1);
        shot.onHitEntity(new net.minecraft.world.phys.EntityHitResult(target));
        h.assertTrue(Math.abs(before - target.getHealth() - 6) < 1.0E-3, "Cryonic entity hit must use captured attack damage times the configured multiplier; dealt=" + (before - target.getHealth()) + ", base=" + doubleValue(save(shot), "damage"));
        h.assertTrue(shot.isRemoved() && target.getArrowCount() == 1, "Native arrow hit must consume the projectile while preserving the target existing arrow count; removed=" + shot.isRemoved() + ", arrows=" + target.getArrowCount());
        source.discard(); target.discard(); h.succeed();
    }

    public static void striderAndReactiveVariantsRetainTheirDistinctLifetimeAndDisplayItem(GameTestHelper h) {
        IcidrogruntEntity source = grunt(h, 4, 2, 4);
        IceSquadProjectileEntity strider = projectile(h, IceSquadContent.STRIDER_ATTACK.get(), source, true, 1, 0);
        IceSquadProjectileEntity gunner = projectile(h, IceSquadContent.GUNNER_BULLET.get(), source, false, 1, 0);
        h.assertTrue(strider.getItem().is(com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent.LIGHT_BLOCKING_BARRIER.get().asItem()) && gunner.getItem().is(com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent.item(com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind.BLUE_ORB)), "Strider and Gunner projectile items must preserve their registered display items");
        strider.setNoPhysics(true); gunner.setNoPhysics(true);
        advance(h, strider, 4);
        advance(h, gunner, 79);
        h.assertTrue(!strider.isRemoved() && age(strider) == 4, "Strider must survive through age four");
        h.assertTrue(!gunner.isRemoved() && age(gunner) == 79, "Gunner projectile must survive through age 79");
        advance(h, strider, 1); advance(h, gunner, 1);
        h.assertTrue(strider.isRemoved() && age(strider) == 5, "Strider must expire on its fifth entity tick");
        h.assertTrue(gunner.isRemoved() && age(gunner) == 80, "Gunner projectile must expire on its eightieth entity tick");
        strider.discard(); gunner.discard(); source.discard(); h.succeed();
    }

    public static void projectileOwnerReassignmentNeverEnablesPickup(GameTestHelper h) {
        IcidrogruntEntity first = grunt(h, 3, 2, 3);
        IcidrogruntEntity second = grunt(h, 6, 2, 6);
        IceSquadProjectileEntity shot = projectile(h, IceSquadContent.CRYONIC_PROJECTILE.get(), first, true, 1, 0);
        shot.setOwner(second);
        CompoundTag tag = save(shot);
        h.assertTrue(shot.getOwner() == second && shot.pickup == AbstractArrow.Pickup.DISALLOWED, "Changing the projectile owner must retain the native no-pickup rule");
        h.assertTrue(uuidEquals(tag, "SquadProjectileCaster", first.getUUID()), "Changing owner must not overwrite the original caster exclusion UUID");
        first.discard(); second.discard(); shot.discard(); h.succeed();
    }

    public static void projectileReloadClampsNegativeAndExcessLifetimeToTypeBounds(GameTestHelper h) {
        IcidrogruntEntity source = grunt(h, 4, 2, 4);
        IceSquadProjectileEntity shot = IceSquadContent.STRIDER_ATTACK.get().create(h.getLevel(), EntitySpawnReason.COMMAND);
        CompoundTag tag = new CompoundTag(); tag.putInt("SquadProjectileAge", 99); tag.put("SquadProjectileCaster", uuidTag(source.getUUID()));
        shot.load(input(h, tag));
        shot.setNoPhysics(true);
        h.assertTrue(age(shot) == 5, "Restored Strider age 99 must clamp to its five-tick lifetime");
        advance(h, shot, 1);
        h.assertTrue(shot.isRemoved() && age(shot) == 5, "Strider at clamped age five must be removed before gaining another tick");
        tag.putInt("SquadProjectileAge", -3);
        IceSquadProjectileEntity expired = IceSquadContent.STRIDER_ATTACK.get().create(h.getLevel(), EntitySpawnReason.COMMAND); expired.load(input(h, tag));
        expired.setNoPhysics(true);
        h.assertTrue(age(expired) == 0, "Restored Strider age -3 must clamp to age zero");
        advance(h, expired, 1);
        h.assertTrue(!expired.isRemoved() && age(expired) == 1, "Negative-age clamp must begin a full Strider lifetime");
        advance(h, expired, 3);
        h.assertTrue(!expired.isRemoved() && age(expired) == 4, "Strider restored from negative age must survive through age four");
        advance(h, expired, 1);
        h.assertTrue(expired.isRemoved() && age(expired) == 5, "Strider restored from negative age must then expire on its fifth entity tick");
        source.discard(); shot.discard(); expired.discard(); h.succeed();
    }

    public static void cryonicNativeDamageStillPassesThroughTargetArmor(GameTestHelper h) {
        IcidrogruntEntity source = grunt(h, 4, 2, 4);
        Cow target = cow(h, source.position().add(2, 0, 0)); target.getAttribute(Attributes.ARMOR).setBaseValue(10);
        IceSquadProjectileEntity shot = projectile(h, IceSquadContent.CRYONIC_PROJECTILE.get(), source, true, 1, 0);
        shot.setDeltaMovement(1, 0, 0);
        shot.onHitEntity(new net.minecraft.world.phys.EntityHitResult(target));
        double dealt = 10 - target.getHealth();
        h.assertTrue(Math.abs(dealt - 1.28) < .02, "Native projectile damage must follow the ten-armor reduction; dealt=" + dealt + ", health=" + target.getHealth());
        source.discard(); target.discard(); shot.discard(); h.succeed();
    }
    private static IcidrogruntEntity grunt(GameTestHelper h, int x, int y, int z) {
        IcidrogruntEntity grunt = h.spawnWithNoFreeWill(IceSquadContent.GRUNT.get(), x, y, z);
        grunt.setNoGravity(true);
        return grunt;
    }

    private static Cow cow(GameTestHelper h, Vec3 pos) {
        Cow cow = EntityType.COW.create(h.getLevel(), EntitySpawnReason.COMMAND);
        h.assertTrue(cow != null, "Cow fixture must be created");
        cow.setNoAi(true); cow.setNoGravity(true); cow.setPos(pos);
        h.assertTrue(h.getLevel().addFreshEntity(cow), "Cow fixture must join the server world");
        return cow;
    }

    private static IceSquadProjectileEntity projectile(GameTestHelper h, EntityType<IceSquadProjectileEntity> type, IcidrogruntEntity source, boolean owned, double multiplier, double knockback) {
        IceSquadProjectileEntity projectile = type.create(h.getLevel(), EntitySpawnReason.COMMAND);
        h.assertTrue(projectile != null, "Registered projectile type must create a fixture");
        projectile.configure(source, owned, multiplier, knockback);
        projectile.setNoGravity(true); projectile.setNoPhysics(true); projectile.setPos(source.position().add(3, 3, 3)); projectile.setDeltaMovement(0.1, 0, 0);
        h.assertTrue(h.getLevel().addFreshEntity(projectile), "Projectile fixture must join the server world");
        return projectile;
    }

    private static CompoundTag save(Entity entity) { TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess()); entity.saveWithoutId(output); return output.buildResult(); }
    private GruntProjectileServerBehaviorTests() { }

    private static net.minecraft.world.level.storage.ValueInput input(GameTestHelper h, CompoundTag tag) { return TagValueInput.create(ProblemReporter.DISCARDING, h.getLevel().registryAccess(), tag); }

    private static int intValue(CompoundTag tag, String key) { return ((net.minecraft.nbt.NumericTag)tag.get(key)).intValue(); }
    private static int age(IceSquadProjectileEntity projectile) { var tag = save(projectile); if (!booleanValue(tag, "SquadProjectileLifetimeStarted")) return 0; int life = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(projectile.getType()).getPath().equals("strider_attack") ? 5 : 80; return life - intValue(tag, "SquadProjectileRemainingEndTicks"); }
    private static void advance(GameTestHelper h, IceSquadProjectileEntity projectile, int ticks) { for (int i = 0; i < ticks && !projectile.isRemoved(); i++) { projectile.tick(); if (!projectile.isRemoved()) projectile.endTick(h.getLevel()); } }
    private static double doubleValue(CompoundTag tag, String key) { return ((net.minecraft.nbt.NumericTag)tag.get(key)).doubleValue(); }
    private static boolean booleanValue(CompoundTag tag, String key) { return ((net.minecraft.nbt.NumericTag)tag.get(key)).byteValue() != 0; }
    private static boolean uuidEquals(CompoundTag tag, String key, java.util.UUID expected) { return expected.toString().equals(tag.getString(key).orElse("")); }
    private static net.minecraft.nbt.Tag uuidTag(java.util.UUID value) { return net.minecraft.core.UUIDUtil.CODEC.encodeStart(NbtOps.INSTANCE, value).getOrThrow(); }
}






