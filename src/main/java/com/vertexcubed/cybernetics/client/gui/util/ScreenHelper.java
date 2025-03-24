package com.vertexcubed.cybernetics.client.gui.util;

import com.vertexcubed.cybernetics.client.task.TaskManager;
import net.minecraft.client.gui.screens.Screen;

public class ScreenHelper {

    /**
     * Returns the associated TaskManager for this screen.
     * By default, all screens have a task manager associated with them.
     * Will only return null if this is called within the screen constructor (do not do!).
     */
    public static TaskManager getTaskManager(Screen screen) {
        return ((ICybScreen) screen).cybernetics$getTaskManager();
    }
}
