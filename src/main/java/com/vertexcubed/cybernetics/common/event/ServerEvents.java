package com.vertexcubed.cybernetics.common.event;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.server.network.S2CSyncAbilityStoragePayload;
import com.vertexcubed.cybernetics.server.network.S2CSyncCyberwarePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Cybernetics.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if(!player.hasData(CybAttachments.CYBERWARE_INVENTORY)) {
            player.getData(CybAttachments.CYBERWARE_INVENTORY).init(player.level().registryAccess());
        }
        if(!player.hasData(CybAttachments.ABILITY_STORAGE)) {
            player.getData(CybAttachments.ABILITY_STORAGE).init(player);
        }
        PacketDistributor.sendToPlayer(player, new S2CSyncAbilityStoragePayload(player.getData(CybAttachments.ABILITY_STORAGE), player));
        PacketDistributor.sendToPlayer(player, new S2CSyncCyberwarePayload(player.getData(CybAttachments.CYBERWARE_INVENTORY), player));
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        PacketDistributor.sendToPlayer(player, new S2CSyncCyberwarePayload(player.getData(CybAttachments.CYBERWARE_INVENTORY), player));

        player.getData(CybAttachments.ABILITY_STORAGE).init(player);
        PacketDistributor.sendToPlayer(player, new S2CSyncAbilityStoragePayload(player.getData(CybAttachments.ABILITY_STORAGE), player));
    }

    @SubscribeEvent
    public static void onEntityTracking(PlayerEvent.StartTracking event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) return;
        if(!(event.getTarget() instanceof LivingEntity entity) || !entity.hasData(CybAttachments.CYBERWARE_INVENTORY)) return;

        PacketDistributor.sendToPlayer(player, new S2CSyncCyberwarePayload(entity.getData(CybAttachments.CYBERWARE_INVENTORY), entity));
        if(entity.hasData(CybAttachments.ABILITY_STORAGE)) {
            entity.getData(CybAttachments.ABILITY_STORAGE).init(entity);
            PacketDistributor.sendToPlayer(player, new S2CSyncAbilityStoragePayload(entity.getData(CybAttachments.ABILITY_STORAGE), entity));
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if(!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide) return;
        if(entity.hasData(CybAttachments.CYBERWARE_INVENTORY)) {
            PacketDistributor.sendToAllPlayers(new S2CSyncCyberwarePayload(entity.getData(CybAttachments.CYBERWARE_INVENTORY), entity));
        }
        if(entity.hasData(CybAttachments.ABILITY_STORAGE)) {
            entity.getData(CybAttachments.ABILITY_STORAGE).init(entity);
            PacketDistributor.sendToAllPlayers(new S2CSyncAbilityStoragePayload(entity.getData(CybAttachments.ABILITY_STORAGE), entity));
        }
    }
}
