package com.vertexcubed.cybernetics.server.network;

import com.vertexcubed.cybernetics.common.item.KineticDischargerItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public record C2SSpikePayload() implements CustomPacketPayload {

    public static final Type<C2SSpikePayload> TYPE = new Type<>(modLoc("spike"));

    public static final StreamCodec<ByteBuf, C2SSpikePayload> CODEC = StreamCodec.unit(new C2SSpikePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register(final PayloadRegistrar registrar) {
        registrar.playToServer(TYPE, CODEC, C2SSpikePayload::handle);
    }

    public static void handle(final C2SSpikePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            KineticDischargerItem.spike(player);
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Failed to handle payload " + TYPE.id() + ": " + e.getMessage()));
            return null;
        });
    }
}
