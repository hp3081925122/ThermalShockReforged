package com.hp.thermalshockreforged.world;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public record ErrandMarkerPayload(int done) implements CustomPacketPayload {
    public static final int CONCIERGE = 0;
    public static final int SHUTTERBUG = 1;
    public static final int ICEBREAKER = 2;
    public static final int ALLIANCE = 3;
    public static final int LAUT = 4;
    private static final List<String> FINALS = List.of("blasphemy_4", "leaf_it_to_me_3", "icebreaker_2", "uneasy_alliance_1", "babies_or_pets");
    private static volatile int clientDone;
    public static final Type<ErrandMarkerPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "errand_marker"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ErrandMarkerPayload> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ErrandMarkerPayload::done, ErrandMarkerPayload::new);
    @Override public Type<ErrandMarkerPayload> type() { return TYPE; }

    public static boolean pending(int errand) { return (clientDone & 1 << errand) == 0; }

    public static void resetClient() { clientDone = 0; }

    private static void send(ServerPlayer player) {
        int done = 0;
        for (int index = 0; index < FINALS.size(); index++) {
            var advancement = player.server.getAdvancements().get(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, FINALS.get(index)));
            if (advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone()) done |= 1 << index;
        }
        PacketDistributor.sendToPlayer(player, new ErrandMarkerPayload(done));
        ThermalShockReforged.LOGGER.debug("Errand markers synced: player={}, done={}", player.getUUID(), Integer.toBinaryString(done));
    }

    private static void progress(AdvancementEvent.AdvancementProgressEvent event) {
        var id = event.getAdvancement().id();
        if (event.getEntity() instanceof ServerPlayer player && id.getNamespace().equals(ThermalShockReforged.MOD_ID) && FINALS.contains(id.getPath())) send(player);
    }

    private static void registerPayload(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(TYPE, CODEC, (payload, context) -> clientDone = payload.done());
    }

    public static void register(IEventBus bus) {
        bus.addListener(ErrandMarkerPayload::registerPayload);
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> { if (event.getEntity() instanceof ServerPlayer player) send(player); });
        NeoForge.EVENT_BUS.addListener(ErrandMarkerPayload::progress);
    }
}
