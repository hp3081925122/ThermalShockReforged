package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class WikiIconExporter {
    private static boolean done;

    @SubscribeEvent
    public static void onRender(ScreenEvent.Render.Post event) {
        String keys = System.getProperty("tsr.wikiIcons");
        if (done || keys == null || keys.isBlank() || !(event.getScreen() instanceof TitleScreen)) return;
        done = true;
        Minecraft mc = Minecraft.getInstance();
        Path dir = Path.of(System.getProperty("tsr.wikiIconDir", "D:/AI临时文件/icons_render"));
        int ok = 0;
        int fail = 0;
        try {
            Files.createDirectories(dir);
        } catch (Exception e) {
            ThermalShockReforged.LOGGER.error("Wiki icon export cannot create directory {}", dir, e);
            return;
        }
        for (String key : keys.split(",")) {
            String path = key.trim();
            if (path.isEmpty()) continue;
            var item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path));
            ItemStack stack = new ItemStack(item);
            if (stack.isEmpty()) {
                ThermalShockReforged.LOGGER.warn("Wiki icon export skipped unknown item {}", path);
                fail++;
                continue;
            }
            float rotY = 0;
            for (String rot : System.getProperty("tsr.wikiIconRotY", "").split(",")) {
                String[] pair = rot.split(":");
                if (pair.length == 2 && pair[0].trim().equals(path)) rotY = Float.parseFloat(pair[1].trim());
            }
            try {
                render(mc, stack, rotY, dir.resolve(path + "_raw.png"));
                ok++;
            } catch (Throwable t) {
                fail++;
                ThermalShockReforged.LOGGER.error("Wiki icon export failed for {}", path, t);
            }
        }
        ThermalShockReforged.LOGGER.info("Wiki icon export finished: {} images written, {} failures, directory {}", ok, fail, dir);
        if (Boolean.parseBoolean(System.getProperty("tsr.wikiIconsQuit", "true"))) mc.stop();
    }

    private static void render(Minecraft mc, ItemStack stack, float rotY, Path out) throws Exception {
        int size = 1280;
        TextureTarget target = new TextureTarget(size, size, true, Minecraft.ON_OSX);
        target.setClearColor(0, 0, 0, 0);
        target.clear(Minecraft.ON_OSX);
        target.bindWrite(true);
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(-32, 48, 48, -32, 1000, 21000), VertexSorting.ORTHOGRAPHIC_Z);
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.translation(0, 0, -11000);
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
        GuiGraphics graphics = new GuiGraphics(mc, mc.renderBuffers().bufferSource());
        graphics.pose().pushPose();
        graphics.pose().translate(8, 8, 150);
        graphics.pose().mulPose(Axis.YP.rotationDegrees(rotY));
        graphics.pose().translate(-8, -8, -150);
        graphics.renderItem(stack, 0, 0);
        graphics.pose().popPose();
        graphics.flush();
        modelView.popMatrix();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.restoreProjectionMatrix();
        NativeImage image = new NativeImage(size, size, false);
        RenderSystem.bindTexture(target.getColorTextureId());
        image.downloadTexture(0, false);
        image.flipY();
        image.writeToFile(out);
        image.close();
        target.destroyBuffers();
        mc.getMainRenderTarget().bindWrite(true);
    }
}
