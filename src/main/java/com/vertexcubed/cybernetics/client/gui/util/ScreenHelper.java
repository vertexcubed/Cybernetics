package com.vertexcubed.cybernetics.client.gui.util;

import com.vertexcubed.cybernetics.client.task.TaskManager;
import net.minecraft.client.gui.screens.Screen;

public class ScreenHelper {

    /**
     * Returns the associated task manager for this screen. ALL screens have a task manager, so this should never be null unless I royally fucked something up.
     */
    public static TaskManager getTaskManager(Screen screen) {
        return ((ICybScreen) screen).cybernetics$getTaskManager();
    }
}
