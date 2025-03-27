package com.vertexcubed.cybernetics.client.util;

import com.mojang.blaze3d.platform.InputConstants;
import com.vertexcubed.cybernetics.common.registry.CybKeyMappings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;


public class InputHelper {

    /**
     * Stolen from mekanism's code, keybinds are the bane of my existance
     */
    public static boolean isAbilityKeyHeld() {
        KeyMapping key = CybKeyMappings.PLAYER_ABILITIES_MENU.get();
        if(key.isDown()) return true;

        IKeyConflictContext conflictContext = key.getKeyConflictContext();
        if (!conflictContext.isActive()) {
            conflictContext = KeyConflictContext.GUI;
        }

        if(conflictContext.isActive() && (key.getKeyModifier() == KeyModifier.NONE || key.getKeyModifier().isActive(conflictContext))) {
            return isKeyDown(key);
        }
        return KeyModifier.isKeyCodeModifier(key.getKey()) && isKeyDown(key);

    }

    /**
     * Stolen from mekanism's code as well
     */
    public static boolean isKeyDown(KeyMapping keyMapping) {
        InputConstants.Key key = keyMapping.getKey();
        int keyCode = key.getValue();
        if (keyCode != InputConstants.UNKNOWN.getValue()) {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();
            try {
                if (key.getType() == InputConstants.Type.KEYSYM) {
                    return InputConstants.isKeyDown(windowHandle, keyCode);
                } else if (key.getType() == InputConstants.Type.MOUSE) {
                    return GLFW.glfwGetMouseButton(windowHandle, keyCode) == GLFW.GLFW_PRESS;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }
}
