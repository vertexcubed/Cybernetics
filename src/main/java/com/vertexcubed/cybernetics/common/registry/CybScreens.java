package com.vertexcubed.cybernetics.common.registry;

import com.vertexcubed.cybernetics.client.gui.cyberware.CyberwareScreen;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class CybScreens {


    public static void register(RegisterMenuScreensEvent event) {
        event.register(CybMenus.CYBERWARE_MENU.get(), CyberwareScreen::new);
    }
}
