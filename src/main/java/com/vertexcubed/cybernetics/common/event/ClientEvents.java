package com.vertexcubed.cybernetics.common.event;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.client.gui.AbilityScreen;
import com.vertexcubed.cybernetics.client.gui.util.ScreenHelper;
import com.vertexcubed.cybernetics.client.util.InputHelper;
import com.vertexcubed.cybernetics.common.item.CyberwareItem;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.registry.CybDataComponents;
import com.vertexcubed.cybernetics.common.registry.CybKeyMappings;
import com.vertexcubed.cybernetics.server.network.C2SOpenCyberwarePayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Cybernetics.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void clientTickPre(ClientTickEvent.Pre event) {
        if(CybKeyMappings.OPEN_CYB_MENU.get().isDown()) {
            PacketDistributor.sendToServer(new C2SOpenCyberwarePayload());
        }

        if (InputHelper.isAbilityKeyHeld() && Minecraft.getInstance().screen == null && Minecraft.getInstance().player.hasData(CybAttachments.ABILITY_STORAGE)) {
            Minecraft.getInstance().setScreen(new AbilityScreen());
        }
    }

    @SubscribeEvent
    public static void renderScreenPre(ScreenEvent.Render.Pre event) {
        if(Minecraft.getInstance().level == null) {
            return;
        }
        ScreenHelper.getTaskManager(event.getScreen()).tickFrame(Minecraft.getInstance().level.getGameTime(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void addTooltip(ItemTooltipEvent event) {
        if(event.getItemStack().isEmpty() || event.getEntity() == null) return;


        CyberwareItem.addSectionTooltip(event);

        if(event.getItemStack().getComponents().has(CybDataComponents.CYBERWARE_PROPERTIES.get())) {
            CyberwareItem.addTooltip(event);
        }
    }


}
