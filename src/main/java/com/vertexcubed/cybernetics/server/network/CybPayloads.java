package com.vertexcubed.cybernetics.server.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class CybPayloads {


    public static void regsiter(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar main = event.registrar("1");
        C2SOpenCyberwarePayload.register(main);
        S2CSyncCyberwarePayload.register(main);
        S2CSyncAbilityStoragePayload.register(main);
        C2SSwitchActiveSlotsPayload.register(main);
        C2SSwitchInventoryPagePayload.register(main);
        C2SApplyCyberwarePayload.register(main);
        BidirectionalCyberwareEventPayload.register(main);
        BidirectionalAbilityEventPayload.register(main);
    }
}
