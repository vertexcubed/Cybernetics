package com.vertexcubed.cybernetics.client.gui.cyberware;

import com.vertexcubed.cybernetics.common.menu.CyberwareMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CyberwareScreen extends AbstractContainerScreen<CyberwareMenu> {
    public CyberwareScreen(CyberwareMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }
}
