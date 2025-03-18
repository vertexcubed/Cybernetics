package com.vertexcubed.cybernetics.client.task;

import java.util.UUID;

public class InstantRunTask extends AbstractTask<AbstractTask.NoResult> {

    private final Runnable run;
    public InstantRunTask(Runnable run) {
        super(UUID.randomUUID());
        this.run = run;
    }

    @Override
    public void update(TaskManager context, long gameTime, float partialTick) {
        run.run();
        state = TaskState.COMPLETED;
    }

    @Override
    public void init(TaskManager context) {

    }

    @Override
    public NoResult getResult() {
        return NONE;
    }

    @Override
    public void interrupt() {

    }

}
