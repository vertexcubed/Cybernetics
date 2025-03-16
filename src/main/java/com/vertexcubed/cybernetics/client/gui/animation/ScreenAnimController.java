package com.vertexcubed.cybernetics.client.gui.animation;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A controller class that's created when a screen is opened to manage all of that screen's animations and such.
 */
public class ScreenAnimController {

    private final Screen parent;
    private final List<Animation<?>> animations = new ArrayList<>();
    private float partialTick;
    private long gameTime;

    public ScreenAnimController(Screen parent) {
        this.parent = parent;
    }

    public void onInit() {

    }

    public void renderTick(GuiGraphics guiGraphics, long gameTime, float partialTick) {
        this.gameTime = gameTime;
        this.partialTick = partialTick;

        for(int i = 0; i < animations.size(); i++) {
            Animation<?> animation = animations.get(i);
            if(gameTime >= animation.getStartTime()) {
                animation.update(gameTime + partialTick);
            }
            if(animation.isDone()) {
                animations.remove(i);
                i--;
            }
        }
    }

    public void onRemoved() {
        animations.forEach(Animation::interrupt);
        animations.clear();
    }

    public void addAnimation(Animation<?> animation) {
        animations.add(animation);
    }

    public void interrupt(Animation<?> animation) {
        animations.get(animations.indexOf(animation)).interrupt();
    }

    public List<Animation<?>> getAnimations() {
        return animations;
    }

    public Stream<Animation<?>> getWithTag(String tag) {
        return animations.stream().filter(a -> a.hasTag(tag));
    }

    public void interruptWithTag(String tag) {
        animations.forEach(a -> {
            if(a.hasTag(tag)) {
                a.interrupt();
            }
        });
    }
}
