package com.vivi.cybernetics.client.gui.cyberware;

import com.vivi.cybernetics.client.gui.util.ITransparentWidget;
import com.vivi.cybernetics.client.util.RenderHelper;
import com.vivi.cybernetics.common.cyberware.CyberwareSection;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;


public class SectionButton extends AbstractButton implements ITransparentWidget {

    private final CyberwareSection section;
    private final ResourceLocation texture;
    private final CyberwareScreen<?> screen;

    private int boxLeft;
    private int boxRight;
    private int boxTop;
    private int boxBottom;
    public SectionButton(CyberwareScreen<?> screen, int x, int y, CyberwareSection section) {
        super(x, y, 24, 24, Component.empty());
        this.screen = screen;
        this.section = section;
        this.texture = new ResourceLocation(section.getId().getNamespace(), "textures/gui/cyberware/section/" + section.getId().getPath() + ".png");
        alpha = 0.0f;
        visible = false;
        active = false;
    }

    public SectionButton setBox(int boxLeft, int boxRight, int boxTop, int boxBottom) {
        this.boxLeft = boxLeft;
        this.boxRight = boxRight;
        this.boxTop = boxTop;
        this.boxBottom = boxBottom;
        return this;
    }

    @Override
    public void onPress() {
        if(screen.getCurrentState() instanceof State.Main) {
            screen.setCurrentState(new State.Section(screen, section, section.getType().getXOffset(), section.getType().getYOffset()));
        }
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    public CyberwareSection getSection() {
        return section;
    }

    @Override
    public float getTransparency() {
        return alpha;
    }

    @Override
    public void setTransparency(float alpha) {
        this.alpha = alpha;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        visible = alpha > 0.0f;
        active = visible;
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        float color = this.isHoveredOrFocused() ? 1.0F : 0.65F;
//        RenderSystem.setShaderColor(color, color, color, alpha);
//            int u = this.isHoveredOrFocused() ? width : 0;
//        RenderSystem.enableBlend();
        guiGraphics.enableScissor(boxLeft, boxTop, boxRight, boxBottom);
        guiGraphics.setColor(color, color, color, alpha);
        guiGraphics.blit(texture, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
        guiGraphics.disableScissor();
//        RenderSystem.disableBlend();
        RenderHelper.resetShaderColor(guiGraphics);

    }
}
