package com.vertexcubed.cybernetics.server.network;

import com.vertexcubed.cybernetics.common.menu.CyberwareMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public record C2SApplyCyberwarePayload() implements CustomPacketPayload {

    public static final Type<C2SApplyCyberwarePayload> TYPE = new Type<>(modLoc("apply_cyberware"));

    public static final StreamCodec<ByteBuf, C2SApplyCyberwarePayload> CODEC = StreamCodec.unit(new C2SApplyCyberwarePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register(final PayloadRegistrar registrar) {
        registrar.playToServer(TYPE, CODEC, C2SApplyCyberwarePayload::handle);
    }

    public static void handle(final C2SApplyCyberwarePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if(player.containerMenu instanceof CyberwareMenu menu) {
                menu.applyChanges(player);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Failed to handle payload " + TYPE.id() + ": " + e.getMessage()));
            return null;
        });
    }
}
