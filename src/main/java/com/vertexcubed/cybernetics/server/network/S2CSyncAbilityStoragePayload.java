package com.vertexcubed.cybernetics.server.network;

import com.vertexcubed.cybernetics.client.hud.AbilityHUD;
import com.vertexcubed.cybernetics.client.hud.CyberneticsHUD;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.storage.AbilityStorage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class S2CSyncAbilityStoragePayload implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<S2CSyncAbilityStoragePayload> TYPE =
            new CustomPacketPayload.Type<>(modLoc("sync_abilities"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncAbilityStoragePayload> CODEC =
            StreamCodec.ofMember(S2CSyncAbilityStoragePayload::encode, S2CSyncAbilityStoragePayload::new);




    private final AbilityStorage abilities;
    private final int entity;
    public S2CSyncAbilityStoragePayload(AbilityStorage abilities, LivingEntity entity) {

        this.abilities = abilities;
        this.entity = entity.getId();
    }

    public S2CSyncAbilityStoragePayload(RegistryFriendlyByteBuf buf) {
        this.entity = buf.readVarInt();
        abilities = new AbilityStorage();
        abilities.deserializeNBT(buf.registryAccess(), buf.readNbt());
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(entity);
        buf.writeNbt(abilities.serializeNBT(buf.registryAccess()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register(final PayloadRegistrar registrar) {
        registrar.playToClient(TYPE, CODEC, S2CSyncAbilityStoragePayload::handle);
    }

    public static void handle(final S2CSyncAbilityStoragePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            LivingEntity entity = (LivingEntity) player.level().getEntity(payload.entity);
            entity.getData(CybAttachments.ABILITY_STORAGE).copyFrom(payload.abilities, entity);

            CyberneticsHUD.getInstance().getElements().forEach(element -> {
                if(element instanceof AbilityHUD abilityHUD) {
                    abilityHUD.updateElementList();
                }
            });
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Failed to handle payload " + TYPE.id() + ": " + e.getMessage()));
            return null;
        });
    }
}
