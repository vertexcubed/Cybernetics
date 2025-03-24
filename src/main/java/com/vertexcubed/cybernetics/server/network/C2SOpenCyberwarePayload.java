package com.vertexcubed.cybernetics.server.network;

import com.vertexcubed.cybernetics.common.menu.CyberwareMenu;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.storage.CyberwareInventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public record C2SOpenCyberwarePayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<C2SOpenCyberwarePayload> TYPE =
            new CustomPacketPayload.Type<>(modLoc("open_cyberware"));

    public static final StreamCodec<ByteBuf, C2SOpenCyberwarePayload> CODEC = StreamCodec.unit(new C2SOpenCyberwarePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register(final PayloadRegistrar registrar) {
        registrar.playToServer(TYPE, CODEC, C2SOpenCyberwarePayload::handle);
    }

    public static void handle(final C2SOpenCyberwarePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            CyberwareInventory cyberware = player.getData(CybAttachments.CYBERWARE_INVENTORY);
            player.openMenu(new SimpleMenuProvider((id, inv, p) -> new CyberwareMenu(id, inv, cyberware), Component.translatable("menu.cybernetics.cyberware")));
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Failed to handle payload " + TYPE.id() + ": " + e.getMessage()));
            return null;
        });
    }
}
