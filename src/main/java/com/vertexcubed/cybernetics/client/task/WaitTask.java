package com.vertexcubed.cybernetics.client.task;

import java.util.UUID;

public class WaitTask<T> extends AbstractTask<T> {

    private final T value;
    private final long duration;
    private long startTime;
    private boolean isInterrupted = false;
    public WaitTask(long duration, T value) {
        super(UUID.randomUUID());
        this.value = value;
        this.duration = duration;
    }

    @Override
    public void update(TaskManager context, long gameTime, float partialTick) {
        if(isInterrupted) {
            state = TaskState.INTERRUPTED;
            return;
        }
        state = gameTime >= (startTime + duration) ? TaskState.COMPLETED : TaskState.PENDING;
    }

    @Override
    public void init(TaskManager context) {
        this.startTime = context.gameTime();
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
