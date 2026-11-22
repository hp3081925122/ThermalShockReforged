package com.hp.thermalshockreforged.gametest;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.armor.fabricated.FabricatedArmorPayloads;
import com.hp.thermalshockreforged.content.remaininggui.VaultCodePayload;
import com.hp.thermalshockreforged.entity.icesquad.StriderFirePayload;
import com.hp.thermalshockreforged.world.AltarProgressPayload;
import com.hp.thermalshockreforged.world.ErrandMarkerPayload;
import com.hp.thermalshockreforged.world.OreChoicePayload;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class NetworkCodecServerTests {
    private NetworkCodecServerTests() {
    }

    public static <T extends CustomPacketPayload> T roundTrip(RegistryAccess registries, StreamCodec<RegistryFriendlyByteBuf, T> codec, T payload) {
        var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), registries);
        try {
            codec.encode(buffer, payload);
            int bytes = buffer.readableBytes();
            T received = codec.decode(buffer);
            if (buffer.isReadable() || !received.equals(payload) || !received.type().id().equals(payload.type().id())) {
                throw new IllegalStateException("Payload byte round trip changed fields or left unread bytes: " + payload.type().id());
            }
            ThermalShockReforged.LOGGER.debug("Payload byte round trip passed: type={}, bytes={}", payload.type().id(), bytes);
            return received;
        } finally {
            buffer.release();
        }
    }

    public static void allPayloadsPreserveFields(GameTestHelper helper) {
        var registries = helper.getLevel().registryAccess();
        roundTrip(registries, FabricatedArmorPayloads.Launch.CODEC, FabricatedArmorPayloads.Launch.INSTANCE);
        roundTrip(registries, StriderFirePayload.CODEC, StriderFirePayload.INSTANCE);
        for (boolean unlocked : new boolean[] {false, true}) {
            roundTrip(registries, AltarProgressPayload.CODEC, new AltarProgressPayload(unlocked));
        }
        for (int first = 0; first <= 2; first++) {
            for (int second = 0; second <= 2; second++) {
                roundTrip(registries, OreChoicePayload.CODEC, new OreChoicePayload(first, second));
            }
        }
        for (int done = 0; done < 32; done++) {
            roundTrip(registries, ErrandMarkerPayload.CODEC, new ErrandMarkerPayload(done));
        }
        for (var visual : List.of(
                new FabricatedArmorPayloads.Visual(0, new UUID(0, 0), 0, false),
                new FabricatedArmorPayloads.Visual(127, new UUID(-1, -1), 0x00ffffff, true),
                new FabricatedArmorPayloads.Visual(128, new UUID(Long.MIN_VALUE, Long.MAX_VALUE), 0x80123456, false),
                new FabricatedArmorPayloads.Visual(Integer.MAX_VALUE, new UUID(123, 456), -1, true))) {
            roundTrip(registries, FabricatedArmorPayloads.Visual.CODEC, visual);
        }
        for (String code : List.of("", "1234", "钥匙🔑", "x".repeat(64), "中".repeat(64))) {
            roundTrip(registries, VaultCodePayload.CODEC, new VaultCodePayload(Integer.MAX_VALUE, code));
        }
        ThermalShockReforged.LOGGER.info("Server payload byte codecs passed: types=7, cases=54");
        helper.succeed();
    }

    public static void malformedPayloadsAreRejected(GameTestHelper helper) {
        var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        try {
            expectFailure(EncoderException.class, () -> VaultCodePayload.CODEC.encode(buffer, new VaultCodePayload(1, "x".repeat(65))));
            buffer.clear();
            buffer.writeVarInt(1);
            buffer.writeVarInt(64 * 3 + 1);
            expectFailure(DecoderException.class, () -> VaultCodePayload.CODEC.decode(buffer));
            buffer.clear();
            VaultCodePayload.CODEC.encode(buffer, new VaultCodePayload(1, "1234"));
            buffer.writerIndex(buffer.writerIndex() - 1);
            expectFailure(DecoderException.class, () -> VaultCodePayload.CODEC.decode(buffer));
            buffer.clear();
            FabricatedArmorPayloads.Visual.CODEC.encode(buffer, new FabricatedArmorPayloads.Visual(128, new UUID(1, 2), 0xff123456, true));
            buffer.writerIndex(buffer.writerIndex() - 1);
            expectFailure(IndexOutOfBoundsException.class, () -> FabricatedArmorPayloads.Visual.CODEC.decode(buffer));
        } finally {
            buffer.release();
        }
        ThermalShockReforged.LOGGER.info("Server malformed payload rejection passed: cases=4");
        helper.succeed();
    }

    private static void expectFailure(Class<? extends Throwable> expected, Runnable action) {
        try {
            action.run();
        } catch (Throwable failure) {
            if (expected.isInstance(failure)) return;
            throw new IllegalStateException("Unexpected payload decode failure type: " + expected.getName(), failure);
        }
        throw new IllegalStateException("Malformed payload was accepted: " + expected.getName());
    }
}
