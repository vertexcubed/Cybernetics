package com.vertexcubed.cybernetics.server.network;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.item.KineticDischargerItem;
import com.vertexcubed.cybernetics.common.registry.CybAbilities;
import com.vertexcubed.cybernetics.common.util.AbilityHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public record C2SSpikeShockwavePayload(int fallTime) implements CustomPacketPayload {

    public static final Type<C2SSpikeShockwavePayload> TYPE = new Type<>(modLoc("spike"));

    public static final StreamCodec<ByteBuf, C2SSpikeShockwavePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, C2SSpikeShockwavePayload::fallTime,
            C2SSpikeShockwavePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register(final PayloadRegistrar registrar) {
        registrar.playToServer(TYPE, CODEC, C2SSpikeShockwavePayload::handle);
    }

    public static void handle(final C2SSpikeShockwavePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if(!AbilityHelper.isEnabled(player, CybAbilities.KINETIC_DISCHARGER.get())) {
                Cybernetics.LOGGER.error("Could not create shockwave for player {}: ability not enabled", player.getDisplayName().getString());
                return;
            }
            KineticDischargerItem.shockwave(player, player.level(), payload.fallTime);
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Failed to handle payload " + TYPE.id() + ": " + e.getMessage()));
            return null;
        });
    }
}
