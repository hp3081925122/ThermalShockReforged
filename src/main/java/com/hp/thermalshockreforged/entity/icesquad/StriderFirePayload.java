package com.hp.thermalshockreforged.entity.icesquad;

import com.hp.thermalshockreforged.ThermalShockReforged;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public record StriderFirePayload() implements CustomPacketPayload {
    public static final StriderFirePayload INSTANCE = new StriderFirePayload();
    public static final Type<StriderFirePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "strider_fire"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StriderFirePayload> CODEC = StreamCodec.unit(INSTANCE);
    @Override public Type<StriderFirePayload> type() { return TYPE; }
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(TYPE, CODEC, (payload, context) -> {
            var player = context.player();
            if (player.getVehicle() instanceof IcidrostriderEntity strider) strider.requestFire(player);
        });
    }
}
