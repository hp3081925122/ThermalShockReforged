package com.hp.thermalshockreforged.client.npc;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.npc.concierge.ConciergeEntity;
import com.hp.thermalshockreforged.content.npc.laut.LautEntity;
import com.hp.thermalshockreforged.content.npc.shutterbug.ShutterbugEntity;
import com.hp.thermalshockreforged.entity.labmachinist.LabMachinistEntity;
import com.hp.thermalshockreforged.entity.qo.DefectorMachinistEntity;
import com.hp.thermalshockreforged.world.ErrandMarkerPayload;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class ErrandMarkers {
    private static final double MAX_DISTANCE_SQUARED = 48 * 48;
    private static final int COLOR = 0xFFFF2A2A;
    private static final float SCALE = 0.06F;
    private static final ByteBufferBuilder BUFFER = new ByteBufferBuilder(1536);

    private ErrandMarkers() { }

    private static boolean pending(Entity entity) {
        if (entity instanceof ConciergeEntity) return ErrandMarkerPayload.pending(ErrandMarkerPayload.CONCIERGE);
        if (entity instanceof ShutterbugEntity) return ErrandMarkerPayload.pending(ErrandMarkerPayload.SHUTTERBUG);
        if (entity instanceof LabMachinistEntity) return ErrandMarkerPayload.pending(ErrandMarkerPayload.ICEBREAKER) || ErrandMarkerPayload.pending(ErrandMarkerPayload.ALLIANCE);
        if (entity instanceof DefectorMachinistEntity) return ErrandMarkerPayload.pending(ErrandMarkerPayload.ALLIANCE);
        if (entity instanceof LautEntity) return ErrandMarkerPayload.pending(ErrandMarkerPayload.LAUT);
        return false;
    }

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.options.hideGui) return;
        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        var camera = event.getCamera().getPosition();
        var buffers = MultiBufferSource.immediate(BUFFER);
        Font font = minecraft.font;
        float bob = Mth.sin((minecraft.level.getGameTime() + partial) * 0.1F) * 0.08F;
        boolean drawn = false;
        for (Entity entity : minecraft.level.entitiesForRendering()) {
            if (!entity.isAlive() || entity.isInvisible() || !pending(entity)) continue;
            double x = Mth.lerp(partial, entity.xOld, entity.getX());
            double y = Mth.lerp(partial, entity.yOld, entity.getY()) + entity.getBbHeight() + 0.8 + bob;
            double z = Mth.lerp(partial, entity.zOld, entity.getZ());
            if (camera.distanceToSqr(x, y, z) > MAX_DISTANCE_SQUARED) continue;
            PoseStack pose = new PoseStack();
            pose.translate(x - camera.x, y - camera.y, z - camera.z);
            pose.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
            pose.scale(SCALE, -SCALE, SCALE);
            font.drawInBatch("!", -font.width("!") / 2.0F, -font.lineHeight / 2.0F, COLOR, true, pose.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
            drawn = true;
        }
        if (drawn) buffers.endBatch();
    }

    @SubscribeEvent
    public static void loggingOut(ClientPlayerNetworkEvent.LoggingOut event) { ErrandMarkerPayload.resetClient(); }
}
