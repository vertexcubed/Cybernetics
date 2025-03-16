package com.vertexcubed.cybernetics.client.gui.util;

import com.vertexcubed.cybernetics.client.gui.animation.ScreenAnimController;
import net.minecraft.client.gui.screens.Screen;

public class ScreenHelper {

    public static ScreenAnimController getAnimController(Screen screen) {
        return ((ICybScreen) screen).cybernetics$getScreenAnimController();
    }
}
