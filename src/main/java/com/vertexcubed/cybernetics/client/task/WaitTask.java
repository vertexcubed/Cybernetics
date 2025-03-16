package com.vertexcubed.cybernetics.client.task;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WaitTask<T> extends AbstractTask<T> {

    private final T value;
    private final long waitUntil;
    private boolean isInterrupted = false;
    public WaitTask(long waitUntil, T value) {
        super(UUID.randomUUID());
        this.value = value;
        this.waitUntil = waitUntil;
    }

    @Override
    public @NotNull TaskState update(long gameTime, float partialTick) {
        if(isInterrupted) return TaskState.INTERRUPTED;
        return gameTime >= waitUntil ? TaskState.COMPLETED : TaskState.PENDING;
    }

    @Override
    public T getResult() {
        return value;
    }

    @Override
    public void interrupt() {
        isInterrupted = true;
    }

}
