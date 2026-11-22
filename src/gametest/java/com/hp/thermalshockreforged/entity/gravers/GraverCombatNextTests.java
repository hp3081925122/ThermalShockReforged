package com.hp.thermalshockreforged.entity.gravers;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
public final class GraverCombatNextTests {
    public static void assimilatorConvertsKilledGraverIntoOneAssimilatedForm(GameTestHelper h){var assimilator=assimilator(h,8,2,8);var victim=graver(h,10,2,8);assimilator.awardKillScore(victim,h.getLevel().damageSources().mobAttack(assimilator));var forms=entities(h,AssimilatedGraverEntity.class,assimilator);h.assertTrue(assimilator.isRemoved()&&forms.size()==1&&forms.getFirst().variant()==3,"A Graver kill-score callback must replace the Assimilator with one AssimilatedGraver");h.succeed();}
    public static void repeatedGraverKillCallbackCannotDuplicateConversion(GameTestHelper h){var assimilator=assimilator(h,8,2,8);var victim=graver(h,10,2,8);var source=h.getLevel().damageSources().mobAttack(assimilator);assimilator.awardKillScore(victim,source);assimilator.awardKillScore(victim,source);h.assertTrue(entities(h,AssimilatedGraverEntity.class,assimilator).size()==1,"Repeated conversion callback must leave exactly one replacement form");h.succeed();}
    public static void cancelledReplacementJoinKeepsAssimilatorAvailable(GameTestHelper h){var assimilator=assimilator(h,8,2,8);var victim=graver(h,10,2,8);Consumer<EntityJoinLevelEvent> veto=e->{if(e.getLevel()==h.getLevel()&&e.getEntity() instanceof AssimilatedGraverEntity)e.setCanceled(true);};NeoForge.EVENT_BUS.addListener(veto);try{assimilator.awardKillScore(victim,h.getLevel().damageSources().mobAttack(assimilator));h.assertTrue(!assimilator.isRemoved()&&entities(h,AssimilatedGraverEntity.class,assimilator).isEmpty(),"Rejected replacement join must retain the original Assimilator and create no orphan form");}finally{NeoForge.EVENT_BUS.unregister(veto);}h.succeed();}
    public static void nonGraverKillDoesNotTriggerAssimilatorConversion(GameTestHelper h){var assimilator=assimilator(h,8,2,8);var other=EntityType.COW.create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);other.setNoAi(true);other.setPos(h.absoluteVec(new net.minecraft.world.phys.Vec3(10,2,8)));h.getLevel().addFreshEntity(other);assimilator.awardKillScore(other,h.getLevel().damageSources().mobAttack(assimilator));h.assertTrue(!assimilator.isRemoved()&&entities(h,AssimilatedGraverEntity.class,assimilator).isEmpty(),"Only a Graver victim may trigger this conversion path");h.succeed();}
    public static void allGraverFamilyAttributesUseTheirConfiguredVariantValues(GameTestHelper h){GraverFamilyEntity[] family={spawn(h,GraverContent.GRAVER.get(),8,2,8),spawn(h,GraverContent.NEON.get(),10,2,8),spawn(h,GraverContent.ASSIMILATOR.get(),12,2,8),spawn(h,GraverContent.ASSIMILATED.get(),14,2,8),spawn(h,GraverContent.RAZORAPTOR.get(),16,2,8)};for(int i=0;i<family.length;i++){var e=family[i];h.assertTrue(Math.abs(e.getMaxHealth()-GraverConfig.HEALTH[i].get())<1E-4&&Math.abs(e.getAttributeValue(Attributes.ARMOR)-GraverConfig.ARMOR[i].get())<1E-4&&Math.abs(e.getAttributeValue(Attributes.ATTACK_DAMAGE)-GraverConfig.DAMAGE[i].get())<1E-4&&Math.abs(e.getAttributeValue(Attributes.MOVEMENT_SPEED)-GraverConfig.SPEED[i].get())<1E-4,"Variant "+i+" must apply configured health, armor, damage, and movement speed");}h.succeed();}
    private static AssimilatorEntity assimilator(GameTestHelper h,int x,int y,int z){var e=spawn(h,GraverContent.ASSIMILATOR.get(),x,y,z);return (AssimilatorEntity)e;}
    private static GraverEntity graver(GameTestHelper h,int x,int y,int z){return spawn(h,GraverContent.GRAVER.get(),x,y,z);}
    private static <T extends GraverFamilyEntity>T spawn(GameTestHelper h,net.minecraft.world.entity.EntityType<T> type,int x,int y,int z){T e=type.create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);e.setNoAi(true);e.setNoGravity(true);e.setPos(h.absolutePos(new BlockPos(x,y,z)).getCenter());h.getLevel().addFreshEntity(e);return e;}
    private static <T extends Entity>java.util.List<T> entities(GameTestHelper h,Class<T> type,Entity center){return h.getLevel().getEntitiesOfClass(type,new AABB(center.blockPosition()).inflate(8));}

    public static void register() {
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_graver_combat_next_tests_assimilator_converts_killed_graver_into_one_assimilated_form", GraverCombatNextTests::assimilatorConvertsKilledGraverIntoOneAssimilatedForm, 100, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_graver_combat_next_tests_repeated_graver_kill_callback_cannot_duplicate_conversion", GraverCombatNextTests::repeatedGraverKillCallbackCannotDuplicateConversion, 100, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_graver_combat_next_tests_cancelled_replacement_join_keeps_assimilator_available", GraverCombatNextTests::cancelledReplacementJoinKeepsAssimilatorAvailable, 100, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_graver_combat_next_tests_non_graver_kill_does_not_trigger_assimilator_conversion", GraverCombatNextTests::nonGraverKillDoesNotTriggerAssimilatorConversion, 100, "supernova_server");
        com.hp.thermalshockreforged.gametest.TsrGameTests.add("history_graver_combat_next_tests_all_graver_family_attributes_use_their_configured_variant_values", GraverCombatNextTests::allGraverFamilyAttributesUseTheirConfiguredVariantValues, 100, "supernova_server");
    }
}

