package com.hp.thermalshockreforged.content.armor.fabricated;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.EffectParticleModificationEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class FabricatedArmorEvents {
    private static final String REMAINING = "thermal_shock_reforged_propulsion_remaining";
    private static final String COLOR = "thermal_shock_reforged_propulsion_color";
    private static final String SENT = "thermal_shock_reforged_propulsion_visual_sent";
    private static final net.minecraft.tags.TagKey<net.minecraft.world.item.Item> ZENITH = ItemTags.create(FabricatedArmorContent.id("zenith"));

    public static boolean silencedSet(Player player) {
        return player.isAlive() && player.isShiftKeyDown() && eligible(player)
                && player.getItemBySlot(EquipmentSlot.HEAD).is(FabricatedArmorContent.SILENCED_HELMET.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(FabricatedArmorContent.SILENCED_CHESTPLATE.get());
    }

    private static boolean eligible(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ZENITH) || player.getItemBySlot(EquipmentSlot.FEET).is(ZENITH);
    }

    public static void launch(Player player) {
        if (!(player instanceof ServerPlayer server) || !player.isAlive() || !player.isSprinting()
                || !player.level().hasChunkAt(player.blockPosition()) || remaining(player) > 0
                || !player.getItemBySlot(EquipmentSlot.FEET).is(FabricatedArmorContent.PROPULSION_BOOTS.get())) return;
        player.getPersistentData().putInt(REMAINING, 135);
        player.getPersistentData().remove(SENT);
        visualMarker(player);
        Vec3 look = player.getLookAngle();
        player.setDeltaMovement(look.x * 3, 1, look.z * 3);
        player.hurtMarked = true;
        server.connection.send(new ClientboundSetEntityMotionPacket(player));
        server.serverLevel().sendParticles(ParticleTypes.POOF, player.getX(), player.getY() + 1, player.getZ(), 12, .8, .1, .8, 1);
        player.level().playSound(null, player.blockPosition(), BuiltInRegistries.SOUND_EVENT.get(FabricatedArmorContent.id("propulsionbootsactivate")), SoundSource.NEUTRAL, 1, 1);
        syncVisual(server);
    }

    private static int remaining(Player player) { return Math.clamp(player.getPersistentData().getInt(REMAINING), 0, 135); }

    private static void visualMarker(Player player) {
        if (!player.hasEffect(FabricatedArmorContent.COOLDOWN_VISUAL)) player.addEffect(new MobEffectInstance(FabricatedArmorContent.COOLDOWN_VISUAL, -1, 1, false, true));
    }

    static void tick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.isAlive()) { clear(player); return; }
        int ticks = remaining(player);
        if (ticks > 0) {
            if (--ticks == 0) {
                clear(player);
                player.level().playSound(null, player.blockPosition(), BuiltInRegistries.SOUND_EVENT.get(FabricatedArmorContent.id("cooldownend")), SoundSource.PLAYERS, .45F, 1);
            } else {
                player.getPersistentData().putInt(REMAINING, ticks);
                visualMarker(player);
                syncVisual(player);
            }
        } else if (player.hasEffect(FabricatedArmorContent.COOLDOWN_VISUAL) || player.getPersistentData().getBoolean(SENT)) clear(player);
        if (eligible(player) && player.getItemBySlot(EquipmentSlot.HEAD).is(FabricatedArmorContent.SILENCED_HELMET.get())) {
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, true, false));
        }
    }

    private static int color(Player player) {
        float red = 2 * (153 / 255F), green = red, blue = red;
        int weight = 2;
        for (var effect : player.getActiveEffects()) {
            if (!effect.isVisible() || effect.is(FabricatedArmorContent.COOLDOWN_VISUAL)) continue;
            int color = effect.getEffect().value().getColor();
            int amount = effect.getAmplifier() + 1;
            red += amount * ((color >> 16) & 255) / 255F;
            green += amount * ((color >> 8) & 255) / 255F;
            blue += amount * (color & 255) / 255F;
            weight += amount;
        }
        return 0xFF000000 | ((int) (red / weight * 255) << 16) | ((int) (green / weight * 255) << 8) | (int) (blue / weight * 255);
    }

    private static void syncVisual(ServerPlayer player) {
        int color = color(player);
        if (player.getPersistentData().getBoolean(SENT) && player.getPersistentData().getInt(COLOR) == color) return;
        player.getPersistentData().putInt(COLOR, color);
        player.getPersistentData().putBoolean(SENT, true);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new FabricatedArmorPayloads.Visual(player.getId(), player.getUUID(), color, true));
    }

    static void particles(EffectParticleModificationEvent event) {
        if (event.getEntity() instanceof Player player && player.hasEffect(FabricatedArmorContent.COOLDOWN_VISUAL)) event.setVisible(false);
    }

    static void login(PlayerEvent.PlayerLoggedInEvent event) { refreshSession(event.getEntity()); }
    static void dimension(PlayerEvent.PlayerChangedDimensionEvent event) { refreshSession(event.getEntity()); }

    private static void refreshSession(Player player) {
        player.getPersistentData().remove(SENT);
        if (player instanceof ServerPlayer server && remaining(player) > 0) {
            visualMarker(player);
            syncVisual(server);
        }
    }

    static void tracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer observer && event.getTarget() instanceof Player target && remaining(target) > 0) {
            PacketDistributor.sendToPlayer(observer, new FabricatedArmorPayloads.Visual(target.getId(), target.getUUID(), color(target), true));
        }
    }

    private static void clear(ServerPlayer player) {
        boolean sent = player.getPersistentData().getBoolean(SENT);
        player.getPersistentData().remove(REMAINING);
        player.getPersistentData().remove(COLOR);
        player.getPersistentData().remove(SENT);
        player.removeEffect(FabricatedArmorContent.COOLDOWN_VISUAL);
        if (sent) PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new FabricatedArmorPayloads.Visual(player.getId(), player.getUUID(), 0, false));
    }

    static void death(LivingDeathEvent event) { if (event.getEntity() instanceof ServerPlayer player) clear(player); }

    static void clonePlayer(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            int ticks = remaining(event.getOriginal());
            if (ticks > 0) event.getEntity().getPersistentData().putInt(REMAINING, ticks);
        }
        event.getEntity().getPersistentData().remove(SENT);
    }
}
