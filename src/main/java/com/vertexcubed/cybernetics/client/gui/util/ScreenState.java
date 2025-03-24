package com.vertexcubed.cybernetics.client.gui.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Consumer;

/**
 * A ScreenState represents some individual state a screen could be in. You can create one
 * by using the static {@link ScreenState#create} methods listed below, or by extending this
 * class.
 */
public abstract class ScreenState {

    public final Screen parent;
    protected long gameTime;
    public ScreenState(Screen parent) {
        this.parent = parent;
    }

    /**
     * Creates a basic screen state that doesn't need to do anything when ticked.
     * @param parent the parent Screen of this state.
     * @param onEnter A consumer representing what to do when this state is entered.
     * @param onExit A consumer representing what to do when this state is exited.
     * @return the new ScreenState.
     */
    public static ScreenState create(Screen parent, Consumer<ScreenState> onEnter, Consumer<ScreenState> onExit) {
        return create(parent, onEnter, onExit, (state, gameTime) -> {}, (state, graphics, partialTick) -> {});
    }

    /**
     * Creates a basic screen state that doesn't need to do anything when ticked.
     * @param parent the parent Screen of this state.
     * @param onEnter A consumer representing what to do when this state is entered.
     * @param onExit A consumer representing what to do when this state is exited.
     * @param onTick A function representing what to do when this state is ticked.
     * @return the new ScreenState.
     */
    public static ScreenState create(Screen parent, Consumer<ScreenState> onEnter, Consumer<ScreenState> onExit, Tick onTick) {
        return create(parent, onEnter, onExit, onTick, (state, graphics, partialTick) -> {});
    }

    /**
     * Creates a basic screen state that doesn't need to do anything when ticked.
     * @param parent the parent Screen of this state.
     * @param onEnter A consumer representing what to do when this state is entered.
     * @param onExit A consumer representing what to do when this state is exited.
     * @param onRender A function representing what to do when this state is rendered.
     * @return the new ScreenState.
     */
    public static ScreenState create(Screen parent, Consumer<ScreenState> onEnter, Consumer<ScreenState> onExit, Render onRender) {
        return create(parent, onEnter, onExit, (state, gameTime) -> {}, onRender);
    }

    /**
     * Creates a basic screen state that doesn't need to do anything when ticked.
     * @param parent the parent Screen of this state.
     * @param onEnter A consumer representing what to do when this state is entered.
     * @param onExit A consumer representing what to do when this state is exited.
     * @param onTick A function representing what to do when this state is ticked.
     * @param onRender A function representing what to do when this state is rendered.
     * @return the new ScreenState.
     */
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

    /**
     * Returns true if this state in this state machine is active.
     */
    public boolean isActive(ScreenStateMachine stateMachine) {
        return stateMachine.getActiveState() == this;
    }

    /**
     * Gets the current game time. Updated every tick.
     */
    public long getGameTime() {
        return gameTime;
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
