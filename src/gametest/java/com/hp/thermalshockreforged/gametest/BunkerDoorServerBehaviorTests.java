package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.nativeblocks.NativeDecorContent;
import com.hp.thermalshockreforged.content.doors.DoorKind;
import com.hp.thermalshockreforged.content.doors.SpecialDoorBlock;
import com.hp.thermalshockreforged.content.doors.SpecialDoorsContent;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
public final class BunkerDoorServerBehaviorTests {
    public static void register() {
        TsrGameTests.add("history_bunker_door_server_behavior_tests_redstone_opens_door_and_clears_only_nearby_barriers", BunkerDoorServerBehaviorTests::redstoneOpensDoorAndClearsOnlyNearbyBarriers, 20, "supernova_server");
    }

    public static void redstoneOpensDoorAndClearsOnlyNearbyBarriers(GameTestHelper helper) {
        BlockPos doorPos = new BlockPos(2, 1, 2);
        BlockPos boundaryBarrier = doorPos.east(4);
        BlockPos outsideBarrier = doorPos.east(5);
        var barrier = NativeDecorContent.LIGHT_BLOCKING_BARRIER.get();
        var bunker = SpecialDoorsContent.block(DoorKind.BUNKER);

        helper.setBlock(boundaryBarrier, barrier);
        helper.setBlock(outsideBarrier, barrier);
        helper.setBlock(doorPos, bunker.defaultBlockState());
        helper.setBlock(doorPos.east(), Blocks.REDSTONE_BLOCK);

        var opened = helper.getBlockState(doorPos);
        helper.assertTrue(opened.getValue(SpecialDoorBlock.BLOCKSTATE) == 1, "Redstone power must open the bunker door");
        helper.assertTrue(opened.getValue(SpecialDoorBlock.ANIMATION) == 1, "Opening must set the door animation state");
        helper.assertTrue(helper.getBlockState(boundaryBarrier).isAir(), "A barrier at the four-block boundary must be removed");
        helper.assertTrue(helper.getBlockState(outsideBarrier).is(barrier), "A barrier beyond the four-block boundary must remain");
        helper.succeed();
    }

    private BunkerDoorServerBehaviorTests() { }
}
