package com.vertexcubed.cybernetics.client.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.StringRepresentable;

public interface HUDRenderable extends StringRepresentable {

    void init();
    void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
}
