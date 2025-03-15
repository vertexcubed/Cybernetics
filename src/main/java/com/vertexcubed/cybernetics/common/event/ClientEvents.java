package com.vertexcubed.cybernetics.common.event;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.registry.CybKeyMappings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Cybernetics.MOD_ID)
public class ClientEvents {

    @SubscribeEvent
    public static void clientTickPre(ClientTickEvent.Pre event) {
        if(CybKeyMappings.OPEN_CYB_MENU.get().isDown()) {

        }
    }


}
