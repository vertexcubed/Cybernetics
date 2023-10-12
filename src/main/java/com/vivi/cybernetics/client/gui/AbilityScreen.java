package com.vivi.cybernetics.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.vivi.cybernetics.client.gui.util.CybAbstractWidget;
import com.vivi.cybernetics.util.MathHelper;
import com.vivi.cybernetics.util.client.RenderHelper;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class AbilityScreen extends Screen {

    private float centerX;
    private float centerY;
    public static final int SLICES = 128;
    public AbilityScreen() {
        super(Component.literal("Abilities"));
    }

    @Override
    protected void init() {
        super.init();
        centerX = minecraft.getWindow().getGuiScaledWidth() / 2.0f;
        centerY = minecraft.getWindow().getGuiScaledHeight() / 2.0f;
    }

    @Override
    public void render(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(poseStack, pMouseX, pMouseY, pPartialTick);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderHelper.resetShaderColor();

        float length = 60;
        for(int i = 0; i < 6; i++) {
            RenderSystem.setShaderColor((i + 1) / 6.0f, 0.0f, 0.0f, 1.0f);
            drawAnnulus(poseStack, 40 + (4 * i), 80 + (4 * i), length * i, length * (i+1));
        }

//        drawTorus(poseStack, 40, 80, 60, 120);


        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private void drawAnnulus(PoseStack poseStack, float innerRadius, float outerRadius) {
        drawAnnulus(poseStack, innerRadius, outerRadius, 0, 360);
    }
    private void drawAnnulus(PoseStack poseStack, float innerRadius, float outerRadius, float startAngle, float stopAngle) {



        float totalAngle = stopAngle - startAngle;

        //percent of total slices to use
        float slices = SLICES * (totalAngle / 360.0F);

        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 0.0f);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder vertexBuffer = tesselator.getBuilder();

        vertexBuffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION);
        Matrix4f matrix4f = poseStack.last().pose();

        float angle = Mth.DEG_TO_RAD * (totalAngle / slices);
        float startRad = Mth.DEG_TO_RAD * startAngle;
        for(int i = 0; i <= slices; i++) {

            float offset = i;
            if(i + 1 > slices) {
                offset = slices;
            }

            float cos = Mth.cos(-(startRad + angle * offset));
            float sin = Mth.sin(-(startRad + angle * offset));
            vertexBuffer.vertex(matrix4f, innerRadius * cos, innerRadius * sin, 0).endVertex();
            vertexBuffer.vertex(matrix4f, outerRadius * cos, outerRadius * sin, 0).endVertex();
        }



        //SHAPES DRAW IN COUNTER CLOCKWISE MOTION!

//        vertexBuffer.vertex(matrix4f, 1.0f, 200.0f, 0.0f).endVertex();
//        vertexBuffer.vertex(matrix4f, 100.0f, 200.0f, 0.0f).endVertex();
//        vertexBuffer.vertex(matrix4f, 100.0f, 1.0f, 0.0f).endVertex();
//        vertexBuffer.vertex(matrix4f, 1.0f, 1.0f, 0.0f).endVertex();

        tesselator.end();


        poseStack.popPose();
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public class AbilitySlice extends CybAbstractWidget {

        private float inner;
        private float outer;
        private float startAngle;
        private float totalAngle;
        public AbilitySlice(float inner, float outer, float startAngle, float totalAngle) {
            super((int) centerX, (int) centerY, 1, 1, Component.empty());
            this.playSound = false;
            this.inner = inner;
            this.outer = outer;
            this.startAngle = startAngle;
            this.totalAngle = totalAngle;
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

        }

        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            if(!this.visible) return;
            //converts mouse points to polar
            float mouseR = MathHelper.toRadius(pMouseX, pMouseY);
            float mouseT = MathHelper.toAngle(pMouseX, pMouseY);
            this.isHovered = (mouseR >= inner - 5) && (mouseT >= startAngle) && (mouseT < (startAngle + totalAngle));
            renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {

            RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 0.5f);
        }
    }
}
