package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class WikiIconExporter {
    private static final int SIZE = 1280;
    private static final float GUI_TO_PIXEL = SIZE / 80F;
    private static boolean done;
    private static final AtomicInteger PENDING = new AtomicInteger();
    private static final AtomicInteger OK = new AtomicInteger();
    private static final AtomicInteger FAIL = new AtomicInteger();
    private static Path directory;

    @SubscribeEvent
    public static void onRender(ScreenEvent.Render.Post event) {
        String keys = System.getProperty("tsr.wikiIcons");
        if (done || keys == null || keys.isBlank() || !(event.getScreen() instanceof TitleScreen)) return;
        done = true;
        Minecraft mc = Minecraft.getInstance();
        directory = Path.of(System.getProperty("tsr.wikiIconDir", "D:/AI临时文件/icons_render"));
        try {
            Files.createDirectories(directory);
        } catch (Exception e) {
            ThermalShockReforged.LOGGER.error("Wiki icon export cannot create directory {}", directory, e);
            return;
        }
        PENDING.incrementAndGet();
        for (String key : keys.split(",")) {
            String path = key.trim();
            if (path.isEmpty()) continue;
            var item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path));
            ItemStack stack = new ItemStack(item);
            if (stack.isEmpty()) {
                ThermalShockReforged.LOGGER.warn("Wiki icon export skipped unknown item {}", path);
                FAIL.incrementAndGet();
                continue;
            }
            float rotY = 0;
            for (String rot : System.getProperty("tsr.wikiIconRotY", "").split(",")) {
                String[] pair = rot.split(":");
                if (pair.length == 2 && pair[0].trim().equals(path)) rotY = Float.parseFloat(pair[1].trim());
            }
            try {
                PENDING.incrementAndGet();
                render(mc, stack, rotY, directory.resolve(path + "_raw.png"));
            } catch (Throwable t) {
                PENDING.decrementAndGet();
                FAIL.incrementAndGet();
                ThermalShockReforged.LOGGER.error("Wiki icon export failed for {}", path, t);
            }
        }
        finish(mc);
    }

    private static void finish(Minecraft mc) {
        if (PENDING.decrementAndGet() > 0) return;
        ThermalShockReforged.LOGGER.info("Wiki icon export finished: {} images written, {} failures, directory {}", OK.get(), FAIL.get(), directory);
        if (Boolean.parseBoolean(System.getProperty("tsr.wikiIconsQuit", "true"))) mc.execute(mc::stop);
    }

    private static void render(Minecraft mc, ItemStack stack, float rotY, Path out) {
        TextureTarget target = new TextureTarget("tsr wiki icon", SIZE, SIZE, true);
        Projection projection = new Projection();
        ProjectionMatrixBuffer projectionBuffer = new ProjectionMatrixBuffer("tsr wiki icon");
        var oldColor = RenderSystem.outputColorTextureOverride;
        var oldDepth = RenderSystem.outputDepthTextureOverride;
        var oldProjection = RenderSystem.getProjectionMatrixBuffer();
        var oldType = RenderSystem.getProjectionType();
        var features = mc.gameRenderer.getFeatureRenderDispatcher();
        var buffers = mc.renderBuffers().bufferSource();
        try {
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(target.getColorTexture(), 0, target.getDepthTexture(), 1.0);
            RenderSystem.outputColorTextureOverride = target.getColorTextureView();
            RenderSystem.outputDepthTextureOverride = target.getDepthTextureView();
            projection.setupOrtho(-1000F, 1000F, SIZE, SIZE, true);
            RenderSystem.setProjectionMatrix(projectionBuffer.getBuffer(projection), ProjectionType.ORTHOGRAPHIC);
            ItemStackRenderState state = new ItemStackRenderState();
            mc.getItemModelResolver().updateForTopItem(state, stack, ItemDisplayContext.GUI, mc.level, null, 0);
            PoseStack pose = new PoseStack();
            pose.translate(SIZE / 2F, SIZE / 2F, 0);
            pose.scale(GUI_TO_PIXEL, GUI_TO_PIXEL, GUI_TO_PIXEL);
            pose.mulPose(Axis.YP.rotationDegrees(rotY));
            pose.scale(16, -16, 16);
            mc.gameRenderer.getLighting().setupFor(state.usesBlockLight() ? Lighting.Entry.ITEMS_3D : Lighting.Entry.ITEMS_FLAT);
            state.submit(pose, features.getSubmitNodeStorage(), 15728880, OverlayTexture.NO_OVERLAY, 0);
            features.renderAllFeatures();
            buffers.endBatch();
        } finally {
            features.clearSubmitNodes();
            RenderSystem.outputColorTextureOverride = oldColor;
            RenderSystem.outputDepthTextureOverride = oldDepth;
            if (oldProjection != null) RenderSystem.setProjectionMatrix(oldProjection, oldType);
        }
        var texture = target.getColorTexture();
        var device = RenderSystem.getDevice();
        var encoder = device.createCommandEncoder();
        GpuBuffer buffer = device.createBuffer(() -> "tsr wiki icon readback", 9, (long) SIZE * SIZE * texture.getFormat().pixelSize());
        encoder.copyTextureToBuffer(texture, buffer, 0L, () -> {
            try (GpuBuffer.MappedView read = encoder.mapBuffer(buffer, true, false); NativeImage image = new NativeImage(SIZE, SIZE, false)) {
                for (int y = 0; y < SIZE; y++) {
                    for (int x = 0; x < SIZE; x++) {
                        image.setPixelABGR(x, SIZE - y - 1, read.data().getInt((x + y * SIZE) * texture.getFormat().pixelSize()));
                    }
                }
                image.writeToFile(out);
                OK.incrementAndGet();
            } catch (Throwable t) {
                FAIL.incrementAndGet();
                ThermalShockReforged.LOGGER.error("Wiki icon export failed to write {}", out, t);
            } finally {
                buffer.close();
                target.destroyBuffers();
                projectionBuffer.close();
                finish(mc);
            }
        }, 0);
    }
}
