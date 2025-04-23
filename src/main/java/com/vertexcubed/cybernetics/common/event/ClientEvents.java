package com.vertexcubed.cybernetics.common.event;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.client.gui.AbilityScreen;
import com.vertexcubed.cybernetics.client.gui.util.ScreenHelper;
import com.vertexcubed.cybernetics.client.util.InputHelper;
import com.vertexcubed.cybernetics.common.item.CyberwareItem;
import com.vertexcubed.cybernetics.common.item.DoubleJumpItem;
import com.vertexcubed.cybernetics.common.registry.*;
import com.vertexcubed.cybernetics.common.util.AbilityHelper;
import com.vertexcubed.cybernetics.common.util.CyberwareHelper;
import com.vertexcubed.cybernetics.server.network.C2SDoubleJumpPayload;
import com.vertexcubed.cybernetics.server.network.C2SOpenCyberwarePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Cybernetics.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    private static boolean releasedJump;
    private static boolean canDoubleJump;
    private static int currentJumps;
    private static boolean canSpike;
    private static boolean isSpiking;
    private static boolean canDash;
    private static boolean releasedDash;


    @SubscribeEvent
    public static void clientTickPre(ClientTickEvent.Pre event) {
        if(Minecraft.getInstance().player == null) return;
        if(CybKeyMappings.OPEN_CYB_MENU.get().isDown()) {
            PacketDistributor.sendToServer(new C2SOpenCyberwarePayload());
            Minecraft.getInstance().player.playSound(CybSoundEvents.CYBERWARE_OPEN.get());
        }

        if (AbilityHelper.hasAbility(Minecraft.getInstance().player, CybAbilities.OPTICS.get())
                && InputHelper.isAbilityKeyHeld()
                && Minecraft.getInstance().screen == null) {
            Minecraft.getInstance().setScreen(new AbilityScreen());
        }

        handleDoubleJump();
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




    private static void handleDoubleJump() {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null) return;

        if(player.onGround() || player.onClimbable() && !(player.isInWater())) {
            releasedJump = false;
            canDoubleJump = true;
            currentJumps = 1;
        }
        else if(!player.input.jumping) {
            //jump key is not down, and player is in the air
            releasedJump = true;
        }
        else if(!player.getAbilities().flying && canDoubleJump && releasedJump && currentJumps > 0) {
            //jump key pressed, player can double jump and has released the jump key
            currentJumps--;
            if(currentJumps == 0) {
                canDoubleJump = false;
            }
            releasedJump = false;
            if(CyberwareHelper.hasCyberware(CybItems.REINFORCED_TENDONS.get(), player)) {
                PacketDistributor.sendToServer(new C2SDoubleJumpPayload());
                DoubleJumpItem.doubleJump(player);

                if(isSpiking) {
                    canSpike = false;
                    isSpiking = false;
//                    if(AbilityHelper.isEnabled(player, CybAbilities.KINETIC_DISCHARGER.get())) {
//                        CybPackets.sendToServer(new C2SSpikePacket());
//                    }
                }
                canDash = true;
            }


        }
    }


}
