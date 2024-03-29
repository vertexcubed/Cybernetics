package com.vivi.cybernetics.client.gui.cyberware;

import com.vivi.cybernetics.Cybernetics;
import com.vivi.cybernetics.client.gui.util.TextWidget;
import com.vivi.cybernetics.client.util.ScreenHelper;
import com.vivi.cybernetics.server.network.CybPackets;
import com.vivi.cybernetics.server.network.packet.c2s.C2SApplyCyberwareChangesPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.systems.easing.Easing;

public class CyberwareConfirmScreen extends Screen {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Cybernetics.MOD_ID, "textures/gui/cyberware/confirm_background.png");

    private int leftPos;
    private int topPos;
    private int imageWidth;
    private int imageHeight;
    private long time;

    private TextWidget textWidget;
    private int scissorY;
    protected CyberwareConfirmScreen(Component pTitle) {
        super(pTitle);
        imageWidth = 91;
        imageHeight = 65;
    }

    @Override
    protected void init() {
        super.init();
        time = 0;
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        textWidget = new TextWidget(this, leftPos + 8, topPos + 8, 85);

        addRenderableWidget(new ConfirmButton(leftPos + 9, topPos + 39, true));
        addRenderableWidget(new ConfirmButton(leftPos + 65, topPos + 39, false));

        scissorY = 0;
        ScreenHelper.addAnimation(this, () -> (float) scissorY, aFloat -> { scissorY = (int) (float) aFloat; }, imageHeight, 20, Easing.EXPO_IN_OUT);
        textWidget.setText(Component.translatable("tooltip.cybernetics.confirm"));
    }

    @Override
    public void tick() {
        textWidget.tick(time);
        time++;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        this.minecraft.popGuiLayer();
        this.minecraft.player.closeContainer();
        this.minecraft.popGuiLayer();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(guiGraphics);
        guiGraphics.enableScissor(leftPos, topPos, leftPos + imageWidth, topPos + scissorY);
        this.renderBg(guiGraphics, pPartialTick, pMouseX, pMouseY);

        //todo: fix depth issues
//        RenderSystem.disableDepthTest();
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.8888f, 0.8888f, 0);
        guiGraphics.pose().translate(0.125 * textWidget.getX(), 0.125 * textWidget.getY(), 0);
        textWidget.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        guiGraphics.pose().popPose();

        guiGraphics.disableScissor();
//        RenderSystem.enableDepthTest();
    }

    public void renderBg(GuiGraphics guiGraphics, float frameTimeDelta, int mouseX, int mouseY) {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderTexture(0, TEXTURE);
//        RenderHelper.resetShaderColor();
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 128, 128);
    }

    public class ConfirmButton extends AbstractButton {

        private final boolean isConfirm;
        public ConfirmButton(int pX, int pY, boolean isConfirm) {
            super(pX, pY, 17, 17, Component.empty());
            this.isConfirm = isConfirm;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            float color = this.isHoveredOrFocused() ? 1.0F : 0.65F;
            guiGraphics.setColor(color, color, color, alpha);
//            RenderSystem.setShaderColor(color, color, color, alpha);
            int v = isConfirm ? 30 : 12;
            guiGraphics.blit(TEXTURE, this.getX(), this.getY(), this.width, this.height, 93, v, this.width, this.height, 128, 128);
        }

        @Override
        public void onPress() {
            if(isConfirm) {
                CybPackets.sendToServer(new C2SApplyCyberwareChangesPacket());
            }
            CyberwareConfirmScreen.this.onClose();
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

        }
    }
}
