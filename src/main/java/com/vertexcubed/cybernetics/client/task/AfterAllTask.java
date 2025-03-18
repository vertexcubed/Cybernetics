package com.vertexcubed.cybernetics.client.task;

import java.util.List;
import java.util.UUID;


/**
 * Task that is instantly completed after all of its previous tasks are completed.
 */
public class AfterAllTask extends AbstractTask {
    private final boolean allowInterrupted;
    private final boolean allowFailed;
    private final List<AbstractTask> tasks;
    private boolean isInterrupted = false;
    public AfterAllTask(List<AbstractTask> tasks) {
        this(false, false, tasks);
    }

    public AfterAllTask(boolean allowInterrupted, List<AbstractTask> tasks) {
        this(allowInterrupted, false, tasks);
    }

    public AfterAllTask(boolean allowInterrupted, boolean allowFailed, List<AbstractTask> tasks) {
        super(UUID.randomUUID());
        this.allowInterrupted = allowInterrupted;
        this.allowFailed = allowFailed;
        this.tasks = tasks;
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
        for(AbstractTask task : tasks) {
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
    public void init(TaskManager context) {
        for(AbstractTask t : tasks) {
            t.init(context);
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
