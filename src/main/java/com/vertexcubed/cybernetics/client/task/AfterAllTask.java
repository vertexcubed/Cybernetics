package com.vertexcubed.cybernetics.client.task;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Task that is instantly completed after all of its previous tasks are completed.
 */
public class AfterAllTask<T> extends AbstractTask<List<T>> {
    private final boolean allowInterrupted;
    private final boolean allowFailed;
    private final AbstractTask<T>[] tasks;
    private boolean isInterrupted = false;
    @SafeVarargs
    public AfterAllTask(AbstractTask<T>... tasks) {
        this(false, false, tasks);
    }

    @SafeVarargs
    public AfterAllTask(boolean allowInterrupted, AbstractTask<T>... tasks) {
        this(allowInterrupted, false, tasks);
    }

    @SafeVarargs
    public AfterAllTask(boolean allowInterrupted, boolean allowFailed, AbstractTask<T>... tasks) {
        super(UUID.randomUUID());
        this.allowInterrupted = allowInterrupted;
        this.allowFailed = allowFailed;
        this.tasks = tasks;
    }

    @Override
    public void update(long gameTime, float partialTick) {
        if(isInterrupted) {
            state = TaskState.INTERRUPTED;
            return;
        }
        for(AbstractTask<?> task : tasks) {
            switch(task.getState()) {
                case PENDING -> {
                    state = TaskState.PENDING;
                    return;
                }
                case INTERRUPTED -> {
                    if(!allowInterrupted) {
                        state = TaskState.FAILURE;
                        return;
                    }
                }
                case COMPLETED -> { }
                case FAILURE -> {
                    if(!allowFailed) {
                        state = TaskState.FAILURE;
                        return;
                    }
                }
            }
        }
        //If we've made it this far, all tasks are finished. If dependencies can't finish, this task *fails*.
        state = TaskState.COMPLETED;
    }

    @Override
    public List<T> getResult() {
        return Arrays.stream(tasks).map(AbstractTask::getResult).toList();
    }

    @Override
    public void interrupt() {
        isInterrupted = true;
    }
}
