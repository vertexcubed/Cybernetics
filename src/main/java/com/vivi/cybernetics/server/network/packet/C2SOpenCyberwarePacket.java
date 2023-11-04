package com.vivi.cybernetics.server.network.packet;

import com.vivi.cybernetics.common.menu.deprecated.PlayerCyberwareMenuOld;
import com.vivi.cybernetics.common.util.CyberwareHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class C2SOpenCyberwarePacket extends Packet {

    public C2SOpenCyberwarePacket() {

    }

    public C2SOpenCyberwarePacket(FriendlyByteBuf buf) {

    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {

    }

    @Override
    public boolean handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            CyberwareHelper.getCyberware(player).ifPresent(cyberware -> {
                NetworkHooks.openScreen(player, new SimpleMenuProvider(((pContainerId, pPlayerInventory, pPlayer) -> new PlayerCyberwareMenuOld(pContainerId, pPlayerInventory, cyberware)), Component.literal(("Cyberware"))));
            });

        });

        return true;
    }
}
