package com.vertexcubed.cybernetics.client.gui.util;

import net.minecraft.client.gui.GuiGraphics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple Finite State Machine implementation dedicated for screens. All states are
 * instantiated and known when this is created and are stateless.
 */
public class ScreenStateMachine {
    private final Set<ScreenState> states;
    private ScreenState activeState;
    public ScreenStateMachine(List<ScreenState> states) {
        this.states = new HashSet<>(states);
    }

    public ScreenStateMachine(ScreenState... states) {
        this.states = new HashSet<>(List.of(states));
    }

    public boolean init(ScreenState initial) {
        if(!states.contains(initial)) {
            return false;
        }
        activeState = initial;
        activeState.enter();
        return true;
    }

    public Set<ScreenState> getStates() {
        return Set.copyOf(states);
    }

    /**
     * Changes the state to the given state. Returns false if unable to change to this state.
     * In the future, you will be able to define states that this state can change to, but
     * this is currently NYI.
     */
    public boolean changeState(ScreenState state) {
        if(state == null || !states.contains(state)) {
            return false;
        }
        if(activeState == null) {
            return init(state);
        }
        activeState.exit();
        activeState = state;
        activeState.enter();
        return true;
    }

    /**
     * Ticks this state.
     */
    public void tick(long gameTime) {
        if(activeState == null) return;
        activeState.tick(gameTime);
    }

    /**
     * Called every frame. Do custom rendering here.
     */
    public void render(GuiGraphics guiGraphics, float partialTick) {
        if(activeState == null) return;
        activeState.render(guiGraphics, partialTick);
    }

    /**
     * Returns the current active state.
     */
    public ScreenState getActiveState() {
        return activeState;
    }
}
