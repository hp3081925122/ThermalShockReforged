package com.hp.thermalshock.gametest;

import com.hp.thermalshock.ThermalShock;
import com.hp.thermalshock.content.MaterialContent;
import com.hp.thermalshock.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ThermalShock.MOD_ID)
@PrefixGameTestTemplate(false)
public final class InteractiveMigrationTests {
    @GameTest(template = "migration_empty", timeoutTicks = 65)
    public static void buttonsRetainDistinctPressDurations(GameTestHelper helper) {
        BlockPos wood = new BlockPos(2, 1, 2);
        BlockPos stone = new BlockPos(5, 1, 2);
        for (var entry : java.util.Map.of(wood, "vermilli_button", stone, "shale_button").entrySet()) {
            helper.setBlock(entry.getKey(), MaterialContent.blocks().get(entry.getValue()).get().defaultBlockState().setValue(ButtonBlock.FACE, AttachFace.FLOOR));
            helper.pressButton(entry.getKey());
        }
        helper.runAfterDelay(21, () -> {
            helper.assertTrue(helper.getBlockState(wood).getValue(ButtonBlock.POWERED), "Wood button must remain pressed for 30 ticks");
            helper.assertTrue(!helper.getBlockState(stone).getValue(ButtonBlock.POWERED), "Stone button must release after 20 ticks");
        });
        helper.runAfterDelay(31, () -> {
            helper.assertTrue(!helper.getBlockState(wood).getValue(ButtonBlock.POWERED), "Wood button must release after 30 ticks");
            helper.succeed();
        });
    }

    @GameTest(template = "migration_empty")
    public static void onlyOriginalWoodButtonsRespondToArrows(GameTestHelper helper) {
        for (var entry : java.util.Map.of(new BlockPos(2, 1, 2), "vermilli_button", new BlockPos(5, 1, 2), "shale_button").entrySet()) {
            var position = helper.absolutePos(entry.getKey());
            BlockState state = MaterialContent.blocks().get(entry.getValue()).get().defaultBlockState().setValue(ButtonBlock.FACE, AttachFace.FLOOR);
            helper.setBlock(entry.getKey(), state);
            var arrow = EntityType.ARROW.create(helper.getLevel());
            arrow.setPos(state.getShape(helper.getLevel(), position).bounds().getCenter().add(Vec3.atLowerCornerOf(position)));
            arrow.setNoGravity(true);
            helper.getLevel().addFreshEntity(arrow);
            state.entityInside(helper.getLevel(), position, arrow);
            helper.assertTrue(helper.getBlockState(entry.getKey()).getValue(ButtonBlock.POWERED) == entry.getValue().equals("vermilli_button"), "Arrow trigger rule must retain source behavior: " + entry.getValue());
            arrow.discard();
        }
        helper.succeed();
    }

    @GameTest(template = "migration_empty")
    public static void ironSoundPressurePlateStillIgnoresItems(GameTestHelper helper) {
        BlockPos wood = new BlockPos(2, 1, 2);
        BlockPos mobs = new BlockPos(5, 1, 2);
        for (var entry : java.util.Map.of(wood, "vermilli_pressure_plate", mobs, "shale_pressure_plate").entrySet()) {
            helper.setBlock(entry.getKey(), MaterialContent.blocks().get(entry.getValue()).get());
            var item = helper.spawnItem(Items.STICK, entry.getKey());
            var position = helper.absolutePos(entry.getKey());
            item.setPos(position.getX() + 0.5, position.getY() + 0.05, position.getZ() + 0.5);
            item.setNoGravity(true);
            helper.getBlockState(entry.getKey()).entityInside(helper.getLevel(), position, item);
            helper.assertTrue(helper.getBlockState(entry.getKey()).getValue(PressurePlateBlock.POWERED) == entry.getKey().equals(wood), "Source MOBS sensitivity must not be replaced by the IRON set default");
            item.discard();
        }
        var cow = helper.spawnWithNoFreeWill(EntityType.COW, mobs);
        helper.getBlockState(mobs).entityInside(helper.getLevel(), helper.absolutePos(mobs), cow);
        helper.assertTrue(helper.getBlockState(mobs).getValue(PressurePlateBlock.POWERED), "MOBS pressure plate must respond to living entities");
        helper.assertTrue(helper.getBlockState(mobs).getBlock().getSoundType(helper.getBlockState(mobs), helper.getLevel(), helper.absolutePos(mobs), cow) == SoundType.METAL, "The explicit detection fix must retain iron pressure-plate sounds");
        helper.succeed();
    }

    @GameTest(template = "migration_empty")
    public static void doorsRetainHandOperationAndPairedRedstoneState(GameTestHelper helper) {
        for (var entry : java.util.Map.of(new BlockPos(2, 1, 2), "vermilli_door", new BlockPos(5, 1, 2), "shale_door").entrySet()) {
            var block = MaterialContent.blocks().get(entry.getValue()).get();
            helper.setBlock(entry.getKey(), block.defaultBlockState());
            helper.setBlock(entry.getKey().above(), block.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
            helper.useBlock(entry.getKey());
            helper.assertTrue(helper.getBlockState(entry.getKey()).getValue(DoorBlock.OPEN) && helper.getBlockState(entry.getKey().above()).getValue(DoorBlock.OPEN), "Original OAK and STONE door sets must both allow hand opening and synchronize both halves");
            helper.useBlock(entry.getKey());
            helper.setBlock(entry.getKey().east(), Blocks.REDSTONE_BLOCK);
            helper.assertTrue(helper.getBlockState(entry.getKey()).getValue(DoorBlock.POWERED) && helper.getBlockState(entry.getKey().above()).getValue(DoorBlock.OPEN), "Door power must propagate to both halves");
        }
        helper.succeed();
    }

    @GameTest(template = "migration_empty")
    public static void trapdoorsRetainWaterAndInteractionCategories(GameTestHelper helper) {
        BlockPos position = new BlockPos(3, 1, 3);
        var block = MaterialContent.blocks().get("vermilli_trapdoor").get();
        helper.setBlock(position, block.defaultBlockState().setValue(TrapDoorBlock.WATERLOGGED, true));
        helper.useBlock(position);
        var state = helper.getBlockState(position);
        helper.assertTrue(state.getValue(TrapDoorBlock.OPEN), "Original trapdoor must open by hand");
        helper.assertTrue(state.getFluidState().is(net.minecraft.tags.FluidTags.WATER), "Opening a trapdoor must not erase its waterlogged state");
        helper.assertTrue(state.is(BlockTags.TRAPDOORS), "Trapdoor classification must be visible to standard mechanics");
        helper.assertTrue(MaterialContent.blocks().get("shale_button").get().defaultBlockState().is(BlockTags.BUTTONS), "Buttons must be recognized through the standard tag");
        helper.succeed();
    }

    @GameTest(template = "migration_empty")
    public static void scarletRayKeepsDistinctAttributesSizeAndLoot(GameTestHelper helper) {
        var ray = helper.spawnWithNoFreeWill(ModEntities.SCARLET_RAY.get(), 3, 2, 3);
        helper.assertTrue(ray.getMaxHealth() == 6 && ray.getAttributeValue(Attributes.ARMOR) == 0, "Ray attributes must not copy Koi attributes");
        helper.assertTrue(Math.abs(ray.getBbWidth() - 1) < 0.001 && Math.abs(ray.getBbHeight() - 0.3) < 0.001, "Ray collision size must not copy its 1.25 visual scale");
        helper.assertTrue(ray.canBreatheUnderwater() && !ray.isPushedByFluid(), "Ray must retain aquatic behavior");
        ray.setTexture("scarletray");
        var saved = new net.minecraft.nbt.CompoundTag();
        ray.addAdditionalSaveData(saved);
        var loaded = ModEntities.SCARLET_RAY.get().create(helper.getLevel());
        loaded.readAdditionalSaveData(saved);
        helper.assertTrue(loaded.texture().equals(ray.texture()), "Ray texture must persist");
        ray.hurt(helper.getLevel().damageSources().genericKill(), 100);
        var marrow = MaterialContent.items().get("scarlet_marrow").get();
        int count = helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, ray.getBoundingBox().inflate(1)).stream().filter(item -> item.getItem().is(marrow)).mapToInt(item -> item.getItem().getCount()).sum();
        helper.assertTrue(count == 1 && marrow.getDefaultInstance().get(DataComponents.FOOD) == null, "Ray must drop one non-food marrow item");
        helper.succeed();
    }
}
