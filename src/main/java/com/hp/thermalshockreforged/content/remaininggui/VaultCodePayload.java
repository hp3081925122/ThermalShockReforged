package com.hp.thermalshockreforged.content.remaininggui;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public record VaultCodePayload(int containerId, String code) implements CustomPacketPayload {
    public static final Type<VaultCodePayload> TYPE = new Type<>(RemainingGuiContent.id("limbo_vault_code"));
    public static final StreamCodec<RegistryFriendlyByteBuf, VaultCodePayload> CODEC = new StreamCodec<>() {
        @Override public VaultCodePayload decode(RegistryFriendlyByteBuf buffer) { return new VaultCodePayload(buffer.readVarInt(), buffer.readUtf(Short.MAX_VALUE)); }
        @Override public void encode(RegistryFriendlyByteBuf buffer, VaultCodePayload value) { buffer.writeVarInt(value.containerId()); buffer.writeUtf(value.code(), Short.MAX_VALUE); }
    };
    @Override public Type<VaultCodePayload> type() { return TYPE; }
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(TYPE, CODEC, (payload, context) -> {
            var player = context.player();
            if (player.containerMenu instanceof RemainingGuiMenu menu && menu.containerId == payload.containerId()) menu.submitVaultCode(player, payload.code());
        });
    }
}
