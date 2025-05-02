package com.vertexcubed.cybernetics.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vertexcubed.cybernetics.client.render.ScannerRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow @Final private RenderBuffers renderBuffers;

    // makes entities that are being rendered by the scanner red
    @Inject(method = "renderEntity", at = @At("HEAD"))
    private void cybernetics$renderEntity(Entity entity, double pCamX, double pCamY, double pCamZ, float partialTick, PoseStack pPoseStack, MultiBufferSource multiBufferSource, CallbackInfo ci) {
        if(ScannerRenderer.getInstance().shouldRenderGlowingEntiy(entity)) {
            OutlineBufferSource outlinebuffersource = renderBuffers.outlineBufferSource();
            int color = 0xd1221f;
            int r = color >> 16 & 255;
            int g = color >> 8 & 255;
            int b = color & 255;
            outlinebuffersource.setColor(r, g, b, 255);
        }
    }


    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OutlineBufferSource;endOutlineBatch()V", shift = At.Shift.BEFORE))
    private void cybernetics$renderScan(CallbackInfo ci, @Local(argsOnly = true) DeltaTracker deltaTracker, @Local PoseStack poseStack, @Local(argsOnly = true) Camera camera) {
        ScannerRenderer.getInstance().renderScan(poseStack, deltaTracker.getGameTimeDeltaPartialTick(false), camera, Minecraft.getInstance().getMainRenderTarget());
    }
}
