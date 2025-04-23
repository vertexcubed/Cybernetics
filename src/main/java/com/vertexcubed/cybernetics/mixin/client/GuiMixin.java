package com.vertexcubed.cybernetics.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.vertexcubed.cybernetics.client.hud.CyberneticsHUD;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Gui.class)
public class GuiMixin {

    @WrapMethod(method = "renderEffects")
    private void cybernetics$renderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
        if(!CyberneticsHUD.getInstance().isEnabled()) {
            original.call(guiGraphics, deltaTracker);
        }
    }
}
