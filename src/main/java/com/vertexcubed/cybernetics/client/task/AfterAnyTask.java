package com.vertexcubed.cybernetics.client.task;

import java.util.List;
import java.util.UUID;


/**
 * Task that is instantly completed after any of its previous tasks are "finished".
 * It will <b>not</b> continue executing unfinished tasks!
 * Includes options for allowing failed/interrupted tasks to be considered finished.
 * Fails if all tasks end without being counted as "finished".
 */
public class AfterAnyTask extends AbstractTask {
    private final boolean allowInterrupted;
    private final boolean allowFailed;
    private final List<AbstractTask> tasks;
    private boolean isInterrupted = false;
    public AfterAnyTask(List<AbstractTask> tasks) {
        this(false, false, tasks);
    }

    public AfterAnyTask(boolean allowInterrupted, List<AbstractTask> tasks) {
        this(allowInterrupted, false, tasks);
    }

    public AfterAnyTask(boolean allowInterrupted, boolean allowFailed, List<AbstractTask> tasks) {
        super(UUID.randomUUID());
        this.allowInterrupted = allowInterrupted;
        this.allowFailed = allowFailed;
        this.tasks = tasks;
    }


    @Override
    public void init(TaskManager context) {
        for(AbstractTask t : tasks) {
            t.init(context);
        }
    }

    @Override
    public void update(TaskManager context, long gameTime, float partialTick) {
        if(isInterrupted) {
            state = TaskState.INTERRUPTED;
            return;
        }
        for(AbstractTask task : tasks) {
            task.update(context, gameTime, partialTick);
        }
        boolean anyPending = false;
        for(AbstractTask task : tasks) {
            switch(task.getState()) {
                case PENDING -> {
                    anyPending = true;
                }
                case INTERRUPTED -> {
                    if(allowInterrupted) {
                        state = TaskState.COMPLETED;
                        return;
                    }
                }
                case COMPLETED -> {
                    state = TaskState.COMPLETED;
                    return;
                }
                case FAILURE -> {
                    if(allowFailed) {
                        state = TaskState.COMPLETED;
                        return;
                    }
                }
            }
        }
        // If NOTHING is pending, and we haven't returned yet, then fail the task.
        if(!anyPending) {
            state = TaskState.FAILURE;
        }
    }

    @Override
    public void interrupt() {
        isInterrupted = true;
        for(AbstractTask t : tasks) {
            t.interrupt();
        }
    }
}
