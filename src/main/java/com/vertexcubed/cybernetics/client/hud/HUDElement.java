package com.vertexcubed.cybernetics.client.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public abstract class HUDElement implements HUDRenderable {
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public HUDElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    public void init() {
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        renderElement(guiGraphics, deltaTracker);
        guiGraphics.pose().popPose();
    }
    public abstract void renderElement(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

}
