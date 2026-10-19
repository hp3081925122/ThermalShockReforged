package com.hp.thermalshockreforged.content.armor.fabricated;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class FabricatedArmorPayloads {
    public static Consumer<Visual> clientVisuals;

    public record Launch() implements CustomPacketPayload {
        public static final Launch INSTANCE = new Launch();
        public static final Type<Launch> TYPE = new Type<>(FabricatedArmorContent.id("propulsion_boots_launch"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Launch> CODEC = StreamCodec.unit(INSTANCE);
        @Override public Type<Launch> type() { return TYPE; }
    }

    public record Visual(int entityId, UUID uuid, int color, boolean active) implements CustomPacketPayload {
        public static final Type<Visual> TYPE = new Type<>(FabricatedArmorContent.id("propulsion_boots_visual"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Visual> CODEC = new StreamCodec<>() {
            @Override public Visual decode(RegistryFriendlyByteBuf buffer) { return new Visual(buffer.readVarInt(), buffer.readUUID(), buffer.readInt(), buffer.readBoolean()); }
            @Override public void encode(RegistryFriendlyByteBuf buffer, Visual value) { buffer.writeVarInt(value.entityId()); buffer.writeUUID(value.uuid()); buffer.writeInt(value.color()); buffer.writeBoolean(value.active()); }
        };
        @Override public Type<Visual> type() { return TYPE; }
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(Launch.TYPE, Launch.CODEC, (payload, context) -> FabricatedArmorEvents.launch(context.player()));
        registrar.playToClient(Visual.TYPE, Visual.CODEC, (payload, context) -> { if (clientVisuals != null) clientVisuals.accept(payload); });
    }
}
