package com.vertexcubed.cybernetics.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;

public class FakeLocalPlayer extends LocalPlayer {
    private final Player parent;
    public FakeLocalPlayer(Minecraft pMinecraft, ClientLevel pClientLevel, LocalPlayer parent) {
        super(pMinecraft, pClientLevel, parent.connection, parent.getStats(), parent.getRecipeBook(), false, false);
        this.parent = parent;
    }

    /**
     * Hacky workaround to get skin layers to work
     */
    @Override
    public boolean isModelPartShown(PlayerModelPart pPart) {
        return parent.isModelPartShown(pPart);
    }
}
