package com.vertexcubed.cybernetics.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.vertexcubed.cybernetics.client.render.ScannerRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public class MinecraftMixin {


    // makes entities in the scan glow
    @WrapMethod(method = "shouldEntityAppearGlowing")
    public boolean cybernetics$shouldEntityAppearGlowing(Entity entity, Operation<Boolean> original) {
        if(ScannerRenderer.getInstance().shouldRenderGlowingEntiy(entity)) {
            return true;
        }
        return original.call(entity);
    }

}
