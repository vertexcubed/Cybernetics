package com.vertexcubed.cybernetics.client.gui.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class ScreenState {

    public final Screen parent;
    protected long gameTime;
    public ScreenState(Screen parent) {
        this.parent = parent;
    }

    public static ScreenState create(Screen parent, Consumer<ScreenState> onEnter, Consumer<ScreenState> onExit) {
        return create(parent, onEnter, onExit, (state, gameTime) -> {}, (state, graphics, partialTick) -> {});
    }

    public static ScreenState create(Screen parent, Consumer<ScreenState> onEnter, Consumer<ScreenState> onExit, Tick onTick) {
        return create(parent, onEnter, onExit, onTick, (state, graphics, partialTick) -> {});
    }

    public static ScreenState create(Screen parent, Consumer<ScreenState> onEnter, Consumer<ScreenState> onExit, Render onRender) {
        return create(parent, onEnter, onExit, (state, gameTime) -> {}, onRender);
    }

    public static ScreenState create(Screen parent, Consumer<ScreenState> onEnter, Consumer<ScreenState> onExit, Tick onTick, Render onRender) {
        return new ScreenState(parent) {
            @Override
            public void enter() {
                onEnter.accept(this);
            }

            @Override
            public void exit() {
                onExit.accept(this);
            }

            @Override
            public void tick(long gameTime) {
                super.tick(gameTime);
                onTick.tick(this, gameTime);
            }

            @Override
            public void render(GuiGraphics guiGraphics, float partialTick) {
                super.render(guiGraphics, partialTick);
                onRender.render(this, guiGraphics, partialTick);
            }
        };
    }


    public abstract void enter();

    public abstract void exit();

    public void tick(long gameTime) {
        this.gameTime = gameTime;
    }

    public void render(GuiGraphics guiGraphics, float partialTick) {

    }

    public boolean isActive(ScreenStateMachine stateMachine) {
        return stateMachine.getActiveState() == this;
    }

    @FunctionalInterface
    public interface Tick {
        void tick(ScreenState state, long gameTime);
    }

    @FunctionalInterface
    public interface Render {
        void render(ScreenState state, GuiGraphics guiGraphics, float partialTick);
    }
}
