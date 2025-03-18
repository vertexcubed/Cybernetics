package com.vertexcubed.cybernetics.common.event;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.server.network.S2CSyncCyberwarePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
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
        PacketDistributor.sendToPlayer(player, new S2CSyncCyberwarePayload(player.getData(CybAttachments.CYBERWARE_INVENTORY)));
    }
}
