package com.vertexcubed.cybernetics.client.gui.util;

import com.vertexcubed.cybernetics.client.task.TaskManager;
import net.minecraft.client.gui.screens.Screen;

public class ScreenHelper {

    public static TaskManager getTaskManager(Screen screen) {
        return ((ICybScreen) screen).cybernetics$getTaskManager();
    }
}
