package com.hp.thermalshockreforged.client.squall;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.squall.SquallConfig;
import com.hp.thermalshockreforged.entity.squall.SquallEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class SquallAura {
    private static final float INNER = 6;
    private static final float FOG_FAR = 10;
    private static final float FOG_RED = .035F;
    private static final float FOG_GREEN = .02F;
    private static final float FOG_BLUE = .06F;
    private static float intensity;
    private static float previous;
    private static int messageCooldown;

    private SquallAura() { }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        previous = intensity;
        float target = 0;
        var player = minecraft.player;
        if (player != null && minecraft.level != null && !player.isSpectator()) {
            double radius = SquallConfig.AURA_RADIUS.get();
            double search = radius * 1.3;
            for (SquallEntity squall : minecraft.level.getEntitiesOfClass(SquallEntity.class, player.getBoundingBox().inflate(search), SquallEntity::isAlive)) {
                double reach = radius;
                if (squall.enraged()) reach = search;
                float strength = (float) Mth.clamp(1 - (squall.distanceTo(player) - INNER) / (reach - INNER), 0, 1);
                if (squall.lit()) strength *= .5F;
                target = Math.max(target, strength);
            }
        }
        intensity += (target - intensity) * .08F;
        if (intensity < .002F) intensity = 0;
        if (messageCooldown > 0) messageCooldown--;
        if (player != null && intensity > .6F && messageCooldown <= 0 && minecraft.level.getBrightness(LightLayer.BLOCK, player.blockPosition()) < SquallConfig.LIGHT_LEVEL.get()) {
            minecraft.gui.setOverlayMessage(Component.translatable("message.thermal_shock_reforged.squall_seek_light").withStyle(ChatFormatting.RED), false);
            messageCooldown = 80;
        }
    }

    private static float current(double partialTick) { return Mth.lerp((float) partialTick, previous, intensity); }

    @SubscribeEvent
    public static void fog(ViewportEvent.RenderFog event) {
        float strength = current(event.getPartialTick());
        if (strength <= 0) return;
        float far = Math.min(event.getFarPlaneDistance(), Mth.lerp(strength, event.getFarPlaneDistance(), FOG_FAR));
        event.setFarPlaneDistance(far);
        event.setNearPlaneDistance(Math.min(event.getNearPlaneDistance(), far * (1 - strength)));
    }

    @SubscribeEvent
    public static void fogColor(ViewportEvent.ComputeFogColor event) {
        float strength = current(event.getPartialTick());
        if (strength <= 0) return;
        event.setRed(Mth.lerp(strength, event.getRed(), FOG_RED));
        event.setGreen(Mth.lerp(strength, event.getGreen(), FOG_GREEN));
        event.setBlue(Mth.lerp(strength, event.getBlue(), FOG_BLUE));
    }
}
