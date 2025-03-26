package com.vertexcubed.cybernetics.server.network;

import com.vertexcubed.cybernetics.common.event.CyberwareEvent;
import com.vertexcubed.cybernetics.common.item.CyberwareItem;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.storage.CyberwareInventory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

//equip/unequip a cyberware
public record BidirectionalCyberwareEventPayload(Mode mode, ItemStack stack, int slot, int entity) implements CustomPacketPayload {

    public static final Type<BidirectionalCyberwareEventPayload> TYPE = new Type<>(modLoc("cyberware_event"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BidirectionalCyberwareEventPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(i -> Mode.values()[i], Mode::ordinal), BidirectionalCyberwareEventPayload::mode,
            ItemStack.STREAM_CODEC, BidirectionalCyberwareEventPayload::stack,
            ByteBufCodecs.VAR_INT, BidirectionalCyberwareEventPayload::slot,
            ByteBufCodecs.VAR_INT, BidirectionalCyberwareEventPayload::entity,
            BidirectionalCyberwareEventPayload::new
    );


    public static void register(final PayloadRegistrar registrar) {
        registrar.playBidirectional(TYPE, CODEC, BidirectionalCyberwareEventPayload::handle);
    }

    public static void handle(BidirectionalCyberwareEventPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            LivingEntity entity = (LivingEntity) player.level().getEntity(payload.entity);
            if(!entity.hasData(CybAttachments.CYBERWARE_INVENTORY)) throw new IllegalArgumentException("Entity " + entity + " does not have cyberware!");
            CyberwareInventory inv = entity.getData(CybAttachments.CYBERWARE_INVENTORY);
            if(ItemStack.matches(inv.getStackInSlot(payload.slot), payload.stack)) {
                if(payload.stack.getItem() instanceof CyberwareItem item) {
                    switch (payload.mode) {
                        case EQUIP -> {
                            item.onEquip(payload.stack, payload.slot, entity.level(), entity);
                        }
                        case UNEQUIP -> {
                            item.onUnequip(payload.stack, payload.slot, entity.level(), entity);
                        }
                    }
                }
                switch (payload.mode) {
                    case EQUIP -> {
                        NeoForge.EVENT_BUS.post(new CyberwareEvent.Equip(payload.stack, payload.slot, entity.level(), entity));
                    }
                    case UNEQUIP -> {
                        NeoForge.EVENT_BUS.post(new CyberwareEvent.Unequip(payload.stack, payload.slot, entity.level(), entity));
                    }
                }
            }

        }).exceptionally(e -> {
            context.disconnect(Component.literal("Failed to handle payload " + TYPE.id() + ": " + e.getMessage()));
            return null;
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Mode {
        EQUIP,
        UNEQUIP
    }
}
