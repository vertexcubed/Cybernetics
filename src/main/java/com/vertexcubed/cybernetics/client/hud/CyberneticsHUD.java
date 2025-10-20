package com.vertexcubed.cybernetics.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class CyberneticsHUD implements LayeredDraw.Layer {
    public static final CyberneticsHUD INSTANCE = new CyberneticsHUD();
    public static CyberneticsHUD getInstance() {
        return INSTANCE;
    }

    private float xRot;
    private float yRot;
    private float xDiff;
    private float yDiff;
    private final List<HUDRenderable> renderables = new ArrayList<>();
    private boolean enabled = false;
    private CyberneticsHUD() {}


    public static void addElements(HUDRenderable... renderables) {
        for (HUDRenderable renderable : renderables) {
            INSTANCE.renderables.add(renderable);
            renderable.init();
        }
    }


    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if(!enabled) return;

        if(Minecraft.getInstance().options.hideGui) return;
        float xRotO = xRot;
        float yRotO = yRot;
        float xDiffO = xDiff;
        float yDiffO = yDiff;

        float partialTick = deltaTracker.getRealtimeDeltaTicks();

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        xRot = camera.getXRot();
        yRot = camera.getYRot();


        float scale = 0.3f;

        xDiff = (xRot - xRotO);
        yDiff = (yRot - yRotO);

        float c = 1.0f - (float) Math.pow(0.5, partialTick);
        xDiff = Mth.lerp(c, xDiffO, xDiff);
        yDiff = Mth.lerp(c, yDiffO, yDiff);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(-yDiff * scale, -xDiff * scale, 0.0f);

        RenderSystem.enableBlend();
        for(HUDRenderable renderable : renderables) {
            renderable.render(guiGraphics, deltaTracker);
        }

        poseStack.popPose();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<HUDRenderable> getElements() {
        return renderables;
    }
}
