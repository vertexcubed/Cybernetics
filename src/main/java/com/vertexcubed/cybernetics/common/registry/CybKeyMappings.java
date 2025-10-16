package com.vertexcubed.cybernetics.common.registry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CybKeyMappings {

    public static final List<Lazy<KeyMapping>> KEY_MAPPINGS = new ArrayList<>();

    public static final Lazy<KeyMapping> OPEN_CYB_MENU = register(() -> new KeyMapping(
            "key.cybernetics.open_cyberware",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.cybernetics"));

    public static final Lazy<KeyMapping> PLAYER_ABILITIES_MENU = register(() -> new KeyMapping(
            "key.cybernetics.open_abilities",
            KeyConflictContext.UNIVERSAL,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.cybernetics"
    ));

    public static final Lazy<KeyMapping> DASH = register(() -> new KeyMapping(
            "key.cybernetics.dash",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT,
            "key.categories.cybernetics"
    ));


    private static Lazy<KeyMapping> register(Supplier<KeyMapping> sup) {
        return Lazy.of(sup);
    }

    public static void register(RegisterKeyMappingsEvent event) {
        KEY_MAPPINGS.forEach(k -> event.register(k.get()));
    }
}
