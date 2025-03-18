package com.vertexcubed.cybernetics.client.task;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Task that is instantly completed after any of its previous tasks are completed. Uncompleted tasks will not continue to process.
 */
public class AfterAnyTask<T> extends AbstractTask<List<T>> {
    private final boolean allowInterrupted;
    private final boolean allowFailed;
    private final List<AbstractTask<T>> tasks;
    private boolean isInterrupted = false;
    public AfterAnyTask(List<AbstractTask<T>> tasks) {
        this(false, false, tasks);
    }

    public AfterAnyTask(boolean allowInterrupted, List<AbstractTask<T>> tasks) {
        this(allowInterrupted, false, tasks);
    }

    public AfterAnyTask(boolean allowInterrupted, boolean allowFailed, List<AbstractTask<T>> tasks) {
        super(UUID.randomUUID());
        this.allowInterrupted = allowInterrupted;
        this.allowFailed = allowFailed;
        this.tasks = tasks;
    }


    @Override
    public void init(TaskManager context) {
        for(AbstractTask<?> t : tasks) {
            t.init(context);
        }
    }

    @Override
    public void update(TaskManager context, long gameTime, float partialTick) {
        if(isInterrupted) {
            state = TaskState.INTERRUPTED;
            return;
        }
        for(AbstractTask<?> task : tasks) {
            task.update(context, gameTime, partialTick);
        }
        boolean anyPending = false;
        for(AbstractTask<?> task : tasks) {
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
    public List<T> getResult() {
        return tasks.stream().map(AbstractTask::getResult).toList();
    }

    @Override
    public void interrupt() {
        isInterrupted = true;
        for(AbstractTask<?> t : tasks) {
            t.interrupt();
        }
    }
}
