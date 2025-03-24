package com.vertexcubed.cybernetics.client.task;

import java.util.UUID;

/**
 * Simple task that waits a given amount of time in ticks, then is completed.
 */
public class WaitTask extends AbstractTask {

    private final long duration;
    private long startTime;
    private boolean isInterrupted = false;
    public WaitTask(long duration) {
        super(UUID.randomUUID());
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
    public void interrupt() {
        isInterrupted = true;
    }

}
