package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ThermalShockReforged.MOD_ID)
@PrefixGameTestTemplate(false)
public final class AxisMigrationTests {
    private record Expected(String id, int flammability) {
    }

    @GameTest(template = "migration_empty")
    public static void allPillarsRetainPlacementRotationLightAndFire(GameTestHelper helper) {
        Expected[] expected = {
            new Expected("vermilli_wood", 6),
            new Expected("vermilli_log", 6),
            new Expected("stripped_vermilli_log", 6),
            new Expected("cut_polished_permafrost", -1),
            new Expected("stripped_vermilli_wood", 6),
            new Expected("shiver_log", 6),
            new Expected("ochre_log", 6),
            new Expected("ochre_wood", 6),
            new Expected("dull_log", 6),
            new Expected("stripped_ochre_log", 6),
            new Expected("stripped_ochre_wood", 6),
            new Expected("shiverglitter_wood", 6),
            new Expected("stripped_shiverglitter_log", 6),
            new Expected("stripped_shiverglitter_wood", 6),
            new Expected("steelstem_block", -1),
            new Expected("kyaneos_wood", 5),
            new Expected("kyaneos_log", 5),
            new Expected("shale_pillar_block", -1),
            new Expected("cracked_shale_pillar_block", -1),
            new Expected("stripped_kyaneos_log", 5),
            new Expected("stripped_kyaneos_wood", 5),
            new Expected("spirewood_log", -1),
            new Expected("glowcap_stem", 6),
            new Expected("hollowed_log", -1),
            new Expected("darksteel_beam", -1),
            new Expected("mavros_log", 5),
            new Expected("sanctum_pillar", -1),
            new Expected("sea_carcass_mass", -1)
        };
        var position = helper.absolutePos(new BlockPos(3, 2, 3));
        for (var item : expected) {
            var block = MaterialContent.blocks().get(item.id()).get();
            var state = block.defaultBlockState();
            helper.assertTrue(state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y, "Default pillar axis must remain Y: " + item.id());
            helper.assertTrue(state.getLightBlock(helper.getLevel(), position) == 15, "Pillar light blocking must remain 15: " + item.id());
            if (item.flammability() >= 0) helper.assertTrue(block.getFlammability(state, helper.getLevel(), position, Direction.UP) == item.flammability(), "Pillar fire behavior must retain source value: " + item.id());
            for (Direction face : Direction.values()) {
                var context = new BlockPlaceContext(helper.getLevel(), null, InteractionHand.MAIN_HAND, block.asItem().getDefaultInstance(), new BlockHitResult(Vec3.atCenterOf(position), face, position, false));
                var placed = block.getStateForPlacement(context);
                helper.assertTrue(placed.getValue(RotatedPillarBlock.AXIS) == face.getAxis(), "Clicked face must determine the pillar axis: " + item.id());
            }
            for (Direction.Axis axis : Direction.Axis.values()) {
                var aligned = state.setValue(RotatedPillarBlock.AXIS, axis);
                var rotated = aligned.rotate(Rotation.CLOCKWISE_90).getValue(RotatedPillarBlock.AXIS);
                Direction.Axis target = axis == Direction.Axis.X ? Direction.Axis.Z : axis == Direction.Axis.Z ? Direction.Axis.X : Direction.Axis.Y;
                helper.assertTrue(rotated == target, "Quarter turn must preserve original axis behavior: " + item.id());
                helper.assertTrue(aligned.rotate(Rotation.CLOCKWISE_180).getValue(RotatedPillarBlock.AXIS) == axis, "Half turn must preserve pillar axis: " + item.id());
                helper.assertTrue(aligned.mirror(Mirror.LEFT_RIGHT).getValue(RotatedPillarBlock.AXIS) == axis, "Mirror must preserve the original axis-only state: " + item.id());
            }
        }
        helper.succeed();
    }
}
