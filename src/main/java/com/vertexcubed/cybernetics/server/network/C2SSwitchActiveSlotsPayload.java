package com.vertexcubed.cybernetics.server.network;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.menu.CyberwareMenu;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.registry.CybDPRegistries;
import com.vertexcubed.cybernetics.common.storage.CyberwareInventory;
import com.vertexcubed.cybernetics.common.storage.CyberwareSectionType;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class C2SSwitchActiveSlotsPayload implements CustomPacketPayload {


    public static final CustomPacketPayload.Type<C2SSwitchActiveSlotsPayload> TYPE =
            new CustomPacketPayload.Type<>(modLoc("switch_active_slots"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSwitchActiveSlotsPayload> CODEC =
            StreamCodec.ofMember(C2SSwitchActiveSlotsPayload::encode, C2SSwitchActiveSlotsPayload::new);


    private final CyberwareSectionType type;

    public C2SSwitchActiveSlotsPayload(CyberwareSectionType type) {
        this.type = type;
    }

    public C2SSwitchActiveSlotsPayload(RegistryFriendlyByteBuf buf) {
        boolean isNull = buf.readBoolean();
        if(isNull) {
            type = null;
        }
        else {
            type = buf.registryAccess().registryOrThrow(CybDPRegistries.CYBERWARE_SECTION_KEY).get(buf.readResourceLocation());
        }
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        if(type == null) {
            buf.writeBoolean(true);
        }
        else {
            ResourceLocation rl = buf.registryAccess().registryOrThrow(CybDPRegistries.CYBERWARE_SECTION_KEY).getKey(type);
            if(rl == null) {
                buf.writeBoolean(true);
                return;
            }
            buf.writeBoolean(false);
            buf.writeResourceLocation(rl);
        }
    }

    public CyberwareSectionType getType() {
        return type;
    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register(final PayloadRegistrar registrar) {
        registrar.playToServer(TYPE, CODEC, C2SSwitchActiveSlotsPayload::handle);
    }

    public static void handle(final C2SSwitchActiveSlotsPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Cybernetics.LOGGER.debug("Received switch slot paylod");
            ServerPlayer player = (ServerPlayer) context.player();
            CyberwareSectionType type = payload.getType();
            if(player.containerMenu instanceof CyberwareMenu menu) {
                Cybernetics.LOGGER.debug("Switching slots...");
                menu.switchActiveSlots(type);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Failed to handle payload " + TYPE.id() + ": " + e.getMessage()));
            return null;
        });
    }
}
