package com.hp.thermalshockreforged.world;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public record OreChoicePayload(int tier1, int tier2) implements CustomPacketPayload {
    private static volatile int clientTier1;
    private static volatile int clientTier2;
    public static final Type<OreChoicePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "ore_choice"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OreChoicePayload> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, OreChoicePayload::tier1, ByteBufCodecs.VAR_INT, OreChoicePayload::tier2, OreChoicePayload::new);
    @Override public Type<OreChoicePayload> type() { return TYPE; }
    public static int tier1(Level level) {
        if (level instanceof ServerLevel server) return WorldProgressData.get(server).oreTier1();
        return clientTier1;
    }
    public static int tier2(Level level) {
        if (level instanceof ServerLevel server) return WorldProgressData.get(server).oreTier2();
        return clientTier2;
    }
    public static void resetClient() {
        clientTier1 = 0;
        clientTier2 = 0;
    }
    private static OreChoicePayload of(ServerLevel level) {
        var data = WorldProgressData.get(level);
        return new OreChoicePayload(data.oreTier1(), data.oreTier2());
    }
    public static void broadcast(ServerLevel level) {
        var payload = of(level);
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) PacketDistributor.sendToPlayer(player, payload);
    }
    private static void send(PlayerEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) PacketDistributor.sendToPlayer(player, of(((net.minecraft.server.level.ServerLevel) player.level())));
    }
    private static void registerPayload(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(TYPE, CODEC, (payload, context) -> {
            clientTier1 = payload.tier1();
            clientTier2 = payload.tier2();
        });
    }
    public static void register(IEventBus bus) {
        bus.addListener(OreChoicePayload::registerPayload);
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> send(event));
    }
}
