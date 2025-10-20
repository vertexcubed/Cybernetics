package com.vertexcubed.cybernetics.server.network;

import com.vertexcubed.cybernetics.common.ability.AbilityType;
import com.vertexcubed.cybernetics.common.registry.CybAbilities;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.storage.AbilityStorage;
import com.vertexcubed.cybernetics.common.util.AbilityHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;


//enable/disable an ability
public record BidirectionalAbilityEventPayload(Mode mode, AbilityType<?> abilityType, int entity) implements CustomPacketPayload {

    public static final Type<BidirectionalAbilityEventPayload> TYPE = new Type<>(modLoc("ability_event"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BidirectionalAbilityEventPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(i -> Mode.values()[i], Mode::ordinal), BidirectionalAbilityEventPayload::mode,
            ByteBufCodecs.registry(CybAbilities.ABILITY_TYPE_KEY), BidirectionalAbilityEventPayload::abilityType,
            ByteBufCodecs.VAR_INT, BidirectionalAbilityEventPayload::entity,
            BidirectionalAbilityEventPayload::new
    );


    public static void register(final PayloadRegistrar registrar) {
        registrar.playBidirectional(TYPE, CODEC, BidirectionalAbilityEventPayload::handle);
    }

    public static void handle(BidirectionalAbilityEventPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            LivingEntity entity = (LivingEntity) player.level().getEntity(payload.entity);
            if(!entity.hasData(CybAttachments.ABILITY_STORAGE)) throw new IllegalArgumentException("Entity " + entity + " does not have cyberware!");
            AbilityStorage storage = entity.getData(CybAttachments.ABILITY_STORAGE);
            switch (payload.mode) {
                case ENABLE -> {
                    AbilityHelper.enableAbility(entity, payload.abilityType);
                }
                case DISABLE -> {
                    AbilityHelper.disableAbility(entity, payload.abilityType);
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
        ENABLE,
        DISABLE
    }
}