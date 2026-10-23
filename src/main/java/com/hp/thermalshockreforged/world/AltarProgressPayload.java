package com.hp.thermalshockreforged.world;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public record AltarProgressPayload(boolean unlocked) implements CustomPacketPayload {
    private static volatile boolean clientUnlocked;
    public static final Type<AltarProgressPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "altar_progress"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarProgressPayload> CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, AltarProgressPayload::unlocked, AltarProgressPayload::new);
    @Override public Type<AltarProgressPayload> type() { return TYPE; }
    public static boolean unlocked(Level level) { return level instanceof ServerLevel server ? WorldProgressData.get(server).altarUnlocked() : clientUnlocked; }
    public static void resetClient() { clientUnlocked = false; }
    public static void broadcast(ServerLevel level) {
        var payload = new AltarProgressPayload(WorldProgressData.get(level).altarUnlocked());
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) PacketDistributor.sendToPlayer(player, payload);
    }
    private static void send(PlayerEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) PacketDistributor.sendToPlayer(player, new AltarProgressPayload(WorldProgressData.get(((net.minecraft.server.level.ServerLevel) player.level())).altarUnlocked()));
    }
    private static void registerPayload(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(TYPE, CODEC, (payload, context) -> clientUnlocked = payload.unlocked());
    }
    public static void register(IEventBus bus) {
        bus.addListener(AltarProgressPayload::registerPayload);
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> send(event));
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerRespawnEvent event) -> send(event));
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerChangedDimensionEvent event) -> send(event));
    }
}
