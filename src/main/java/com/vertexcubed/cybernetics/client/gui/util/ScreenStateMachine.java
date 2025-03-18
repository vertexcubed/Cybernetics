package com.vertexcubed.cybernetics.client.gui.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class  ScreenStateMachine {
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

    public boolean changeState(ScreenState state) {
        if(activeState == null) {
            return init(state);
        }
        if(!states.contains(state)) {
            return false;
        }
        activeState.exit();
        activeState = state;
        activeState.enter();
        return true;
    }

    public void tick(long gameTime) {
        if(activeState == null) return;
        activeState.tick(gameTime);
    }

    public void render(GuiGraphics guiGraphics, float partialTick) {
        if(activeState == null) return;
        activeState.render(guiGraphics, partialTick);
    }

    public ScreenState getActiveState() {
        return activeState;
    }
}
