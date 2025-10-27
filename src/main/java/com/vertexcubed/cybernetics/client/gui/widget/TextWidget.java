package com.vertexcubed.cybernetics.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import vertexcubed.vrtex.client.screen.widget.VrTeXAbstractWidget;

public class TextWidget extends VrTeXAbstractWidget {


    private Component realText = Component.empty();
    private Font font;
    private int maxWidth;
    private int color;


    private MutableComponent displayText = Component.empty();
    private boolean drawUnderscore = false;
    private Mode mode = Mode.NONE;
    private int lastChar = 0;

    public TextWidget(int x, int y) {
        this(x, y, -1);
    }


    public TextWidget(int x, int y, int maxWidth) {
        super(x, y, 1, 1);
        this.playSound = false;
        this.maxWidth = maxWidth;
        this.font = Minecraft.getInstance().font;
        this.color = 0xFFFFFFFF;
    }

    public void tick(long gameTime) {
        if (gameTime % 10 == 0) {
            drawUnderscore = !drawUnderscore;
        }

        if(realText.getString().isEmpty()) {
            return;
        }

        switch (mode) {
            case WRITE -> {
                this.lastChar++;
                this.displayText = Component.literal(realText.getString(lastChar)).withStyle(realText.getStyle());
                if(this.displayText.getString().equals(realText.getString())) {
                    this.mode = Mode.NONE;
                }
            }
            case DELETE -> {
                this.lastChar--;
                this.displayText = Component.literal(displayText.getString(lastChar)).withStyle(displayText.getStyle());
                if(displayText.getString().isEmpty()) {
                    this.mode = Mode.WRITE;
                }
            }
        }

    }

    public void setText(Component text) {
        setText(text, false);
    }

    public void setText(Component text, boolean instant) {
        this.realText = text;
        if(instant) {
            this.displayText = text.copy();
        }
        else {
            this.mode = displayText.getString().isEmpty() ? Mode.WRITE : Mode.DELETE;
            this.lastChar = displayText.getString().isEmpty() ? 0 : displayText.getString().length() - 1;
        }
    }

    public void setMaxWidth(int width) {
        this.maxWidth = width;
    }

    public int getTextWidth() {
        return font.width(displayText);
    }
    public int getTextHeight() {
        return font.lineHeight;
    }

    /**
     * Color formated as ARGB hex
     */
    public void setColor(int color) {
        this.color = color;
        //Blue: 0xff00fff7
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        MutableComponent drawText = drawUnderscore ? displayText.copy().append("_") : displayText.copy();
        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        if(maxWidth != -1) {
            int yOffset = 0;
            for(FormattedCharSequence fcq : font.split(drawText, maxWidth)) {
                guiGraphics.drawString(font, fcq, getX(), getY() + yOffset, color);
                yOffset += 9;
            }
        }
        else {
            guiGraphics.drawString(font, drawText, getX(), getY(), 0xff00fff7);
        }
    }


    public enum Mode {
        NONE,
        WRITE,
        DELETE
    }
}
