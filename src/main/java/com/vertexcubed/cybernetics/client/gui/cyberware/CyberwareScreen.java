package com.vertexcubed.cybernetics.client.gui.cyberware;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.client.gui.util.ScreenHelper;
import com.vertexcubed.cybernetics.client.task.AbstractTask;
import com.vertexcubed.cybernetics.client.task.InstantRunTask;
import com.vertexcubed.cybernetics.client.task.WaitTask;
import com.vertexcubed.cybernetics.common.menu.CyberwareMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class CyberwareScreen extends AbstractContainerScreen<CyberwareMenu> {

    public static final ResourceLocation TEXTURE = modLoc("textures/gui/cyberware/background.png");
    public static final ResourceLocation SLOT_TEXTURE = modLoc("textures/gui/cyberware/slots.png");



    public CyberwareScreen(CyberwareMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 226;
        this.imageHeight = 154;
    }


    @Override
    protected void init() {
        super.init();

        Cybernetics.LOGGER.info("Waiting 40 ticks...");
        ScreenHelper.getTaskManager(this)
                .addTickTask(
                        new WaitTask<>(Minecraft.getInstance().level.getGameTime() + 40, AbstractTask.NONE)
                                .onComplete(prev ->
                                        new InstantRunTask(() -> Cybernetics.LOGGER.info("Task completed!"))
                                ));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);


    }
}
