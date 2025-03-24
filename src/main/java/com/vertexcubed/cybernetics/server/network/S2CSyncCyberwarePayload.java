package com.vertexcubed.cybernetics.server.network;

import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.storage.CyberwareInventory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class S2CSyncCyberwarePayload implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<S2CSyncCyberwarePayload> TYPE =
            new CustomPacketPayload.Type<>(modLoc("sync_cyberware"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncCyberwarePayload> CODEC =
            StreamCodec.ofMember(S2CSyncCyberwarePayload::encode, S2CSyncCyberwarePayload::new);


    private final CyberwareInventory inventory;
    public S2CSyncCyberwarePayload(CyberwareInventory inventory) {
        this.inventory = inventory;
    }

    public S2CSyncCyberwarePayload(RegistryFriendlyByteBuf byteBuf) {
        inventory = new CyberwareInventory();
        inventory.deserializeNBT(byteBuf.registryAccess(), byteBuf.readNbt());
    }

    public void encode(RegistryFriendlyByteBuf byteBuf) {
        byteBuf.writeNbt(inventory.serializeNBT(byteBuf.registryAccess()));
    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }



    public static void register(final PayloadRegistrar registrar) {
        registrar.playToClient(TYPE, CODEC, S2CSyncCyberwarePayload::handle);
    }




    public static void handle(final S2CSyncCyberwarePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            player.getData(CybAttachments.CYBERWARE_INVENTORY).copyFrom(payload.inventory);
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Failed to handle payload " + TYPE.id() + ": " + e.getMessage()));
            return null;
        });
    }
}
