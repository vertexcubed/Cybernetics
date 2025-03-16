package com.vertexcubed.cybernetics.mixin.client;

import com.vertexcubed.cybernetics.client.gui.animation.ScreenAnimController;
import com.vertexcubed.cybernetics.client.gui.util.ICybScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin implements ICybScreen {

    @Unique
    private final ScreenAnimController cybernetics$controller = new ScreenAnimController(cybernetics$self());;


    @Unique
    private Screen cybernetics$self() {
        return (Screen) (Object) this;
    }

    @Override
    public ScreenAnimController cybernetics$getScreenAnimController() {
        return cybernetics$controller;
    }

    @Inject(method = "removed", at=@At("TAIL"))
    public void cybernetics$onRemoved(CallbackInfo ci) {
        cybernetics$controller.onRemoved();
    }

    @Inject(method = "init()V", at=@At("TAIL"))
    public void cybernetics$onInit(CallbackInfo ci) {
        cybernetics$controller.onInit();
    }
}
