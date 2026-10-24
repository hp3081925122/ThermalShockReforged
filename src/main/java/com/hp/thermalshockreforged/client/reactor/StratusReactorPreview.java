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
import java.util.function.BiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.core.BlockPos;
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
    private static final BlockModelRenderState GHOST = new BlockModelRenderState();
    private static final BlockDisplayContext GHOST_CONTEXT = BlockDisplayContext.create();
    private static net.minecraft.client.multiplayer.ClientLevel cachedLevel;
    private static long lastScan = Long.MIN_VALUE;

    private StratusReactorPreview() { }

    @SubscribeEvent
    public static void submit(SubmitCustomGeometryEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        var level = minecraft.level;
        var player = minecraft.player;
        if (level == null || player == null || minecraft.options.hideGui) return;
        updateCores(level, player.blockPosition());
        if (CORES.isEmpty()) return;
        Vec3 camera = minecraft.gameRenderer.getMainCamera().position();
        PoseStack pose = event.getPoseStack();
        forEachPart(camera, (pos, expected) -> {
            if (level.getBlockState(pos).getBlock() == expected.getBlock()) return;
            minecraft.getBlockModelResolver().update(GHOST, expected, GHOST_CONTEXT);
            pose.pushPose();
            pose.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);
            GHOST.submitMultiLayer(pose, event.getSubmitNodeCollector(), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
            pose.popPose();
        });
    }

    @SubscribeEvent
    public static void render(RenderLevelStageEvent.AfterTranslucentParticles event) {
        Minecraft minecraft = Minecraft.getInstance();
        var level = minecraft.level;
        if (level == null || minecraft.player == null || minecraft.options.hideGui || level != cachedLevel || CORES.isEmpty()) return;
        Vec3 camera = minecraft.gameRenderer.getMainCamera().position();
        PoseStack pose = new PoseStack();
        MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(BUFFER);
        VertexConsumer lines = buffers.getBuffer(RenderTypes.lines());
        float width = minecraft.getWindow().getAppropriateLineWidth();
        boolean[] rendered = { false };
        forEachPart(camera, (pos, expected) -> {
            boolean correct = level.getBlockState(pos).getBlock() == expected.getBlock();
            int color = correct ? ARGB.colorFromFloat(.8F, .25F, 1F, .35F) : ARGB.colorFromFloat(.8F, 1F, .2F, .15F);
            ShapeRenderer.renderShape(pose, lines, Shapes.block(), pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z, color, width);
            rendered[0] = true;
        });
        if (rendered[0]) buffers.endBatch();
    }

    private static void forEachPart(Vec3 camera, BiConsumer<BlockPos, BlockState> action) {
        for (BlockPos core : CORES) {
            if (camera.distanceToSqr(core.getX() + .5, core.getY() + .5, core.getZ() + .5) > MAX_DISTANCE * MAX_DISTANCE) continue;
            for (int x = -1; x <= 1; x++) for (int z = -1; z <= 1; z++) {
                boolean corner = x != 0 && z != 0;
                action.accept(core.offset(x, -1, z), block(corner ? "bordered_hazard_stripes" : "steelstem_block"));
                if (corner) action.accept(core.offset(x, 0, z), block("dark_frostrock_block"));
                else action.accept(core.offset(x, 1, z), block("dark_frostrock_block"));
            }
        }
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
}
