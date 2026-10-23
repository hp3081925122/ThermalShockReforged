package com.hp.thermalshockreforged.client.reactor;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.reactor.ReactorCoreBlockEntity;
import com.hp.thermalshockreforged.content.reactor.ReactorContent;
import com.hp.thermalshockreforged.content.reactor.StratusReactorCoreBlock;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.bus.api.SubscribeEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class StratusReactorPreview {
    private static final int MAX_DISTANCE = 32;
    private static final int MAX_CORES = 8;
    private static final ByteBufferBuilder BUFFER = new ByteBufferBuilder(8192);
    private static final List<BlockPos> CORES = new ArrayList<>();
    private static net.minecraft.client.multiplayer.ClientLevel cachedLevel;
    private static long lastScan = Long.MIN_VALUE;

    private StratusReactorPreview() { }

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        Minecraft minecraft = Minecraft.getInstance();
        var level = minecraft.level;
        var player = minecraft.player;
        if (level == null || player == null || minecraft.options.hideGui) return;
        updateCores(level, player.blockPosition());
        if (CORES.isEmpty()) return;
        Vec3 camera = event.getCamera().getPosition();
        PoseStack pose = new PoseStack();
        pose.translate(-camera.x, -camera.y, -camera.z);
        MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(BUFFER);
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());
        boolean rendered = false;
        for (BlockPos core : CORES) {
            if (camera.distanceToSqr(core.getX() + .5, core.getY() + .5, core.getZ() + .5) > MAX_DISTANCE * MAX_DISTANCE) continue;
            for (int x = -1; x <= 1; x++) for (int z = -1; z <= 1; z++) {
                boolean corner = x != 0 && z != 0;
                draw(pose, lines, level, core.offset(x, -1, z), block(corner ? "bordered_hazard_stripes" : "steelstem_block"));
                if (corner) draw(pose, lines, level, core.offset(x, 0, z), block("dark_frostrock_block"));
                else draw(pose, lines, level, core.offset(x, 1, z), block("dark_frostrock_block"));
                rendered = true;
            }
        }
        if (rendered) buffers.endBatch();
    }

    private static void updateCores(net.minecraft.client.multiplayer.ClientLevel level, BlockPos origin) {
        long gameTime = level.getGameTime();
        if (level == cachedLevel && gameTime - lastScan < 5) return;
        cachedLevel = level;
        lastScan = gameTime;
        CORES.clear();
        int chunkX = origin.getX() >> 4;
        int chunkZ = origin.getZ() >> 4;
        for (int x = -2; x <= 2; x++) for (int z = -2; z <= 2; z++) {
            LevelChunk chunk = level.getChunk(chunkX + x, chunkZ + z);
            for (var entity : chunk.getBlockEntities().values()) {
                if (!(entity instanceof ReactorCoreBlockEntity core)) continue;
                BlockState state = core.getBlockState();
                if (!state.is(ReactorContent.CORE.get()) || state.getValue(StratusReactorCoreBlock.BLOCKSTATE) != 0) continue;
                if (origin.distSqr(core.getBlockPos()) <= MAX_DISTANCE * MAX_DISTANCE) CORES.add(core.getBlockPos().immutable());
                if (CORES.size() >= MAX_CORES) return;
            }
        }
    }

    private static BlockState block(String id) {
        return MaterialContent.blocks().get(id).get().defaultBlockState();
    }

    private static void draw(PoseStack pose, VertexConsumer lines, BlockGetter level, BlockPos pos, BlockState expected) {
        Block actual = level.getBlockState(pos).getBlock();
        boolean correct = actual == expected.getBlock();
        if (!correct) {
            pose.pushPose();
            pose.translate(pos.getX(), pos.getY(), pos.getZ());
            pose.popPose();
        }
        float red = correct ? .25F : 1F;
        float green = correct ? 1F : .2F;
        float blue = correct ? .35F : .15F;
        LevelRenderer.renderLineBox(pose, lines, pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, red, green, blue, .8F);
    }
}
