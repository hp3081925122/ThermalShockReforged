package com.hp.thermalshockreforged.entity.sanctum;

import com.hp.thermalshockreforged.gametest.TsrGameTests;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.phys.AABB;

public final class SanctumRippleTimingFocusedTests {
    public static void register() {
        TsrGameTests.add("history_sanctum_ripple_timing_focused_tests_ripple_cast_consumes_its_twenty_ninth_end_tick_exactly", SanctumRippleTimingFocusedTests::rippleCastConsumesItsTwentyNinthEndTickExactly, 36, "supernova_server");
    }

    public static void rippleCastConsumesItsTwentyNinthEndTickExactly(GameTestHelper h) {
        RippleEntity owner = h.spawnWithNoFreeWill(SanctumContent.RIPPLE.get(), 2, 2, 2);
        Cow target = EntityType.COW.create(h.getLevel(), EntitySpawnReason.COMMAND);
        target.setNoAi(true);
        target.setNoGravity(true);
        target.snapTo(h.absolutePos(new BlockPos(4, 2, 2)), 0, 0);
        if (!h.getLevel().addFreshEntity(target)) throw new IllegalStateException("GameTest server rejected cow entity");
        owner.beginAttack(target);
        RippleCastEntity task = h.getLevel().getEntitiesOfClass(RippleCastEntity.class, new AABB(owner.blockPosition()).inflate(24)).stream().findFirst().orElseThrow();
        h.runAfterDelay(28, () -> h.assertTrue(!task.isRemoved() && com.hp.thermalshockreforged.gametest.TsrGameTests.roundTrip(h, task).getIntOr("RippleRemaining", -1) == 1, "The task must remain pending with one tick before its documented twenty-ninth END stage"));
        h.runAfterDelay(29, () -> {
            int fields = h.getLevel().getEntitiesOfClass(ErrFieldEntity.class, new AABB(owner.blockPosition()).inflate(24)).size();
            h.assertTrue(task.isRemoved() && fields == 1, "The twenty-ninth END stage must consume exactly one cast and create one ERR field");
            h.succeed();
        });
    }

    private SanctumRippleTimingFocusedTests() { }
}
