package com.vertexcubed.cybernetics.server.network;

import com.vertexcubed.cybernetics.common.item.DashCyberwareItem;
import com.vertexcubed.cybernetics.common.item.KineticDischargerItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public record C2SDashPayload() implements CustomPacketPayload {

    public static final Type<C2SDashPayload> TYPE = new Type<>(modLoc("dash"));

    public static final StreamCodec<ByteBuf, C2SDashPayload> CODEC = StreamCodec.unit(new C2SDashPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register(final PayloadRegistrar registrar) {
        registrar.playToServer(TYPE, CODEC, C2SDashPayload::handle);
    }

    public static void handle(final C2SDashPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            DashCyberwareItem.dash(player);
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Failed to handle payload " + TYPE.id() + ": " + e.getMessage()));
            return null;
        });
    }
}
