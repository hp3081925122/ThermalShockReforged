package com.hp.thermalshock.gametest;

import com.hp.thermalshock.ThermalShock;
import com.hp.thermalshock.content.MaterialContent;
import com.hp.thermalshock.entity.ModEntities;
import com.hp.thermalshock.entity.ForecastEntity;
import com.hp.thermalshock.entity.ZapWarningEntity;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Tool;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ThermalShock.MOD_ID)
@PrefixGameTestTemplate(false)
public final class MigrationGameTests {
    @GameTest(template = "migration_empty")
    public static void modularToolDoesNotRequireVanillaClass(GameTestHelper helper) {
        var ruby = MaterialContent.blocks().get("ruby_block").get().defaultBlockState();
        helper.assertTrue(ruby.is(BlockTags.MINEABLE_WITH_PICKAXE), "Ruby block must retain the pickaxe mining tag");
        ItemStack modular = new ItemStack(Items.STICK);
        modular.set(DataComponents.TOOL, new Tool(List.of(Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, 8)), 1, 1));
        helper.assertTrue(modular.isCorrectToolForDrops(ruby), "A component-based non-pickaxe item must harvest ruby blocks");
        helper.succeed();
    }

    @GameTest(template = "migration_empty")
    public static void migratedRecipeAndWolfFoodLoad(GameTestHelper helper) {
        helper.assertTrue(helper.getLevel().getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, "rough_ruby_block_recipe")).isPresent(), "Migrated material recipe must be decoded by the server");
        helper.assertTrue(MaterialContent.items().get("raw_pondler").get().getDefaultInstance().is(net.minecraft.tags.ItemTags.WOLF_FOOD), "Pondler meat must remain valid wolf food after food component migration");
        helper.succeed();
    }

    @GameTest(template = "migration_empty", timeoutTicks = 60)
    public static void zapUsesOwnerDamageAndMultiplier(GameTestHelper helper) {
        ForecastEntity caster = helper.spawnWithNoFreeWill(ModEntities.FORECAST.get(), 1, 2, 1);
        caster.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(7);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 4, 2, 4);
        target.setNoGravity(true);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
        target.setHealth(100);
        ZapWarningEntity warning = ModEntities.ZAP.get().create(helper.getLevel());
        warning.moveTo(target.position());
        warning.arm(caster, 2);
        helper.getLevel().addFreshEntity(warning);
        helper.runAfterDelay(21, () -> {
            helper.assertTrue(Math.abs(target.getHealth() - 86) < 0.001, "Zap must deal base attack 7 times multiplier 2");
            helper.assertTrue(warning.isRemoved(), "Zap entity must remove itself after detonation");
            helper.succeed();
        });
    }

    @GameTest(template = "migration_empty", timeoutTicks = 60)
    public static void removedOwnerCancelsDelayedDamage(GameTestHelper helper) {
        ForecastEntity caster = helper.spawnWithNoFreeWill(ModEntities.FORECAST.get(), 1, 2, 1);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 4, 2, 4);
        target.setNoGravity(true);
        float health = target.getHealth();
        ZapWarningEntity warning = ModEntities.ZAP.get().create(helper.getLevel());
        warning.moveTo(target.position());
        warning.arm(caster, 1);
        helper.getLevel().addFreshEntity(warning);
        caster.discard();
        helper.runAfterDelay(21, () -> {
            helper.assertTrue(target.getHealth() == health, "Removed caster must not damage targets from a stale delayed task");
            helper.assertTrue(warning.isRemoved(), "Cancelled warning must be removed");
            helper.succeed();
        });
    }

    @GameTest(template = "migration_empty")
    public static void forecastCooldownDoesNotUsePotionEffects(GameTestHelper helper) {
        ForecastEntity caster = helper.spawnWithNoFreeWill(ModEntities.FORECAST.get(), 1, 2, 1);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, 4, 2, 4);
        helper.assertTrue(caster.doHurtTarget(target), "First zap must start");
        helper.assertTrue(!caster.doHurtTarget(target), "Second zap must obey the entity cooldown");
        helper.assertTrue(!caster.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), "Internal cooldown must not apply slowness");
        helper.assertTrue(!caster.hasEffect(MobEffects.WEAKNESS), "Internal cooldown must not apply weakness");
        helper.assertTrue(!caster.hasEffect(MobEffects.DAMAGE_RESISTANCE), "Internal cooldown must not apply resistance");
        helper.succeed();
    }
    @GameTest(template = "migration_empty")
    public static void mapColorsAndMiningTiersRemainDefined(GameTestHelper helper) {
        for (var entry : MaterialContent.blocks().entrySet()) {
            var state = entry.getValue().get().defaultBlockState();
            helper.assertTrue(state.getMapColor(helper.getLevel(), helper.absolutePos(new net.minecraft.core.BlockPos(1, 1, 1))).col != 0, "Migrated map color must not be empty: " + entry.getKey());
        }
        var ruby = MaterialContent.blocks().get("ruby_block").get().defaultBlockState();
        helper.assertTrue(!Items.IRON_PICKAXE.getDefaultInstance().isCorrectToolForDrops(ruby), "Iron must not bypass the original diamond tier requirement");
        helper.assertTrue(Items.DIAMOND_PICKAXE.getDefaultInstance().isCorrectToolForDrops(ruby), "Diamond must harvest ruby blocks using merged mining tags");
        helper.succeed();
    }

    @GameTest(template = "migration_empty")
    public static void koiRetainsAquaticSemanticsAndSpawnRule(GameTestHelper helper) {
        var fish = helper.spawnWithNoFreeWill(ModEntities.CRYSTAL_KOI.get(), 3, 2, 3);
        helper.assertTrue(fish.canBreatheUnderwater(), "Koi breathing must be preserved through the target entity tag");
        helper.assertTrue(fish.getType().is(net.minecraft.tags.EntityTypeTags.SENSITIVE_TO_IMPALING), "Koi must retain the old WATER mob-type impaling behavior");
        helper.assertTrue(!fish.isPushedByFluid(), "Koi must not be pushed by fluid flow");
        helper.assertTrue(!fish.hurt(helper.getLevel().damageSources().drown(), 1), "Koi must remain immune to drowning damage");
        var position = helper.absolutePos(new net.minecraft.core.BlockPos(3, 2, 3));
        helper.getLevel().setBlock(position, net.minecraft.world.level.block.Blocks.WATER.defaultBlockState(), 3);
        helper.getLevel().setBlock(position.above(), net.minecraft.world.level.block.Blocks.WATER.defaultBlockState(), 3);
        helper.assertTrue(net.minecraft.world.entity.SpawnPlacements.checkSpawnRules(ModEntities.CRYSTAL_KOI.get(), helper.getLevel(), net.minecraft.world.entity.MobSpawnType.NATURAL, position, helper.getLevel().random), "Koi must accept two water blocks");
        helper.getLevel().setBlock(position.above(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        helper.assertTrue(!net.minecraft.world.entity.SpawnPlacements.checkSpawnRules(ModEntities.CRYSTAL_KOI.get(), helper.getLevel(), net.minecraft.world.entity.MobSpawnType.NATURAL, position, helper.getLevel().random), "Koi must reject a spawn without water above");
        helper.succeed();
    }

    @GameTest(template = "migration_empty")
    public static void koiDropsOneFishAndPreservesSavedTexture(GameTestHelper helper) {
        var fish = helper.spawnWithNoFreeWill(ModEntities.CRYSTAL_KOI.get(), 3, 2, 3);
        fish.setTexture("crystalkoi");
        var saved = new net.minecraft.nbt.CompoundTag();
        fish.addAdditionalSaveData(saved);
        var restored = ModEntities.CRYSTAL_KOI.get().create(helper.getLevel());
        restored.readAdditionalSaveData(saved);
        helper.assertTrue(restored.texture().equals(fish.texture()), "Koi texture must survive save and load");
        fish.hurt(helper.getLevel().damageSources().genericKill(), 100);
        var drops = helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, fish.getBoundingBox().inflate(1));
        int count = drops.stream().filter(item -> item.getItem().is(MaterialContent.items().get("raw_crystal_koi").get())).mapToInt(item -> item.getItem().getCount()).sum();
        helper.assertTrue(count == 1, "Koi must drop exactly one raw fish, without a duplicate loot table");
        for (String recipe : List.of("crystal_koi_smelt", "crystal_koi_smoke", "koi_campfire_smelt")) {
            helper.assertTrue(helper.getLevel().getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, recipe)).isPresent(), "Koi cooking recipe must load: " + recipe);
        }
        helper.succeed();
    }

    @GameTest(template = "migration_empty")
    public static void decorativeBlocksKeepShapeFireAndTooltipBehavior(GameTestHelper helper) {
        var position = helper.absolutePos(new net.minecraft.core.BlockPos(3, 1, 3));
        var stairs = MaterialContent.blocks().get("sanctum_brick_stairs").get();
        helper.assertTrue(stairs.getExplosionResistance() == 100, "Sanctum stairs must retain the explicit source blast resistance");
        helper.assertTrue(stairs.defaultBlockState().getDestroySpeed(helper.getLevel(), position) == -1, "Unbreakable sanctum stairs must remain unbreakable");
        var wet = stairs.defaultBlockState().setValue(net.minecraft.world.level.block.StairBlock.WATERLOGGED, true);
        helper.assertTrue(wet.getFluidState().is(net.minecraft.tags.FluidTags.WATER), "Stair waterlogging must use the inherited vanilla state");
        helper.assertTrue(wet.getShape(helper.getLevel(), position).toAabbs().size() >= 2, "Stair shape must retain its stepped outline");
        var gate = MaterialContent.blocks().get("vermilli_fence_gate").get();
        var closed = gate.defaultBlockState();
        helper.assertTrue(!closed.getCollisionShape(helper.getLevel(), position).isEmpty(), "Closed gate must block passage");
        helper.assertTrue(closed.setValue(net.minecraft.world.level.block.FenceGateBlock.OPEN, true).getCollisionShape(helper.getLevel(), position).isEmpty(), "Open gate must permit passage");
        helper.assertTrue(gate.getFlammability(closed, helper.getLevel(), position, net.minecraft.core.Direction.UP) == 6, "Vermilli gate must retain source flammability");
        var pane = MaterialContent.blocks().get("marine_seaglass_pane").get();
        helper.assertTrue(pane.shouldDisplayFluidOverlay(pane.defaultBlockState(), helper.getLevel(), position, net.minecraft.world.level.material.Fluids.WATER.defaultFluidState()), "Seaglass pane must retain its fluid overlay");
        var pureMuck = MaterialContent.blocks().get("pure_muck_stairs").get();
        java.util.List<net.minecraft.network.chat.Component> tooltip = new java.util.ArrayList<>();
        pureMuck.appendHoverText(pureMuck.asItem().getDefaultInstance(), net.minecraft.world.item.Item.TooltipContext.EMPTY, tooltip, net.minecraft.world.item.TooltipFlag.NORMAL);
        helper.assertTrue(tooltip.stream().anyMatch(line -> line.getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents contents && contents.getKey().equals("block.thermal_shock.pure_muck_stairs.description_0")), "Muck movement warning tooltip must survive refactoring");
        helper.succeed();
    }

}
