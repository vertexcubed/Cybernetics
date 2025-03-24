package com.vertexcubed.cybernetics.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * A flexible, modular widget, allowing you to create complex screen members
 * without having to create subclasses for each.
 */
public class BasicWidget extends CybAbstractWidget {

    private final Screen parent;
    private final WidgetRenderer renderer;
    private ClickHandler clickHandler = null;
    private boolean lightOnHover;
    private float scale;
    private int zOffset;

    private BasicWidget(Screen parent, int x, int y, int width, int height, WidgetRenderer renderer) {
        super(x, y, width, height);
        this.renderer = renderer;
        this.scale = 1.0f;
        this.parent = parent;
        this.zOffset = 0;
        this.alpha = 1.0f;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        float color = 1.0f;
        if(lightOnHover) {
            color = (this.isHovered) ? 1.0f : 0.65f;
        }
        RenderSystem.enableBlend();
        guiGraphics.setColor(color, color, color, alpha);
        renderer.render(this, guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.setColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    /**
     * Creates a simple widget that renders a given texture. If your widget doesn't need any fancy
     * rendering, use this.
     */
    public static BasicWidget texture(Screen parent, int x, int y, int width, int height, int uOffset, int vOffset, int textureWidth, int textureHeight, ResourceLocation texture) {
        return create(parent, x, y, width, height, (context, graphics, mouseX, mouseY, partialTick) -> {
            graphics.pose().pushPose();
            graphics.pose().scale(context.getScale(), context.getScale(), context.getScale());
            graphics.blit(texture, context.getX(), context.getY(), context.zOffset, uOffset, vOffset, width, height, textureWidth, textureHeight);
            graphics.pose().popPose();
        });
    }

    /**
     * Creates a simple widget that renders a given texture. If your widget doesn't need any fancy
     * rendering, use this.
     */
    public static BasicWidget texture(Screen parent, int x, int y, int width, int height, ResourceLocation texture) {
        return create(parent, x, y, width, height, (context, graphics, mouseX, mouseY, partialTick) -> {
            graphics.pose().pushPose();
            graphics.pose().scale(context.getScale(), context.getScale(), context.getScale());
            graphics.blit(texture, context.getX(), context.getY(), context.zOffset, 0, 0, width, height, width, height);
            graphics.pose().popPose();
        });
    }

    /**
     * Creates a simple widget. Use to define a custom render function.
     */
    public static BasicWidget create(Screen parent, int x, int y, int width, int height, WidgetRenderer renderer) {
        return new BasicWidget(parent, x, y, width, height, renderer);
    }

    /**
     * Should this widget play a sound when clicked? Defaults to false.
     */
    public BasicWidget playSoundOnClick(boolean b) {
        this.playSound = b;
        return this;
    }

    /**
     * Should this widget play light up when hovered over? Defaults to false.
     */
    public BasicWidget lightOnHover(boolean b) {
        this.lightOnHover = b;
        return this;
    }

    /**
     * Defines a custom click function to be called any time this widget is clicked.
     */
    public BasicWidget click(ClickHandler clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    /**
     * Sets the initial z offset of this widget.
     */
    public BasicWidget zOffset(int offset) {
        this.zOffset = offset;
        return this;
    }

    /**
     * Sets the initial alpha value of this widget.
     */
    public BasicWidget alpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    /**
     * Sets whether this widget is visible or not. Widgets do not render if
     * visible is set to false, but they can still be interacted with.
     */
    public BasicWidget visible(boolean v) {
        this.visible = v;
        return this;
    }

    /**
     * Sets whether this widget is visible or not. Widgets cannot be interacted
     * with if active is set to false, but they can still render.
     */
    public BasicWidget active(boolean a) {
        this.active = a;
        return this;
    }

    /**
     * Gets the parent screen of this widget.
     */
    public Screen getParent() {
        return parent;
    }

    public float getAlpha() {
        return alpha;
    }

    public float getScale() {
        return scale;
    }

    public int getZOffset() {
        return zOffset;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if(this.clickHandler != null) {
            clickHandler.onClick(this, mouseX, mouseY, button);
        }
    }

    @FunctionalInterface
    public interface WidgetRenderer {
        void render(BasicWidget context, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);

    }

    @FunctionalInterface
    public interface ClickHandler {
        void onClick(BasicWidget context, double mouseX, double mouseY, int button);
    }

}
