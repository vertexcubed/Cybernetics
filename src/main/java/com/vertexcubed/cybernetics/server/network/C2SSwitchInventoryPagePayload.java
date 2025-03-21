package com.vertexcubed.cybernetics.server.network;

import com.vertexcubed.cybernetics.common.menu.CyberwareMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public record C2SSwitchInventoryPagePayload(int page) implements CustomPacketPayload {

    public static final Type<C2SSwitchInventoryPagePayload> TYPE =
            new Type<>(modLoc("switch_inventory_page"));


    public static final StreamCodec<ByteBuf, C2SSwitchInventoryPagePayload> CODEC
            = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, C2SSwitchInventoryPagePayload::page,
            C2SSwitchInventoryPagePayload::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register(final PayloadRegistrar registrar) {
        registrar.playToServer(TYPE, CODEC, C2SSwitchInventoryPagePayload::handle);
    }

    public static void handle(final C2SSwitchInventoryPagePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if(player.containerMenu instanceof CyberwareMenu menu) {
                menu.switchInventoryPage(payload.page());
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Failed to handle payload " + TYPE.id() + ": " + e.getMessage()));
            return null;
        });
    }
}
