package com.vertexcubed.cybernetics.client.task;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class InstantTask extends AbstractTask<AbstractTask.NoResult> {

    private final Runnable run;
    public InstantTask(Runnable run) {
        super(UUID.randomUUID());
        this.run = run;
    }

    @Override
    public @NotNull TaskState update(long gameTime, float partialTick) {
        run.run();
        return TaskState.COMPLETED;
    }

    @Override
    public NoResult getResult() {
        return NONE;
    }

    @Override
    public void interrupt() {

    }

}
