package com.vertexcubed.cybernetics.client.task;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class TaskManager {

    private Queue<AbstractTask<?>> currentFrameTasks = new LinkedList<>();
    private Queue<AbstractTask<?>> nextFrameTasks = new LinkedList<>();
    private Queue<AbstractTask<?>> currentTickTasks = new LinkedList<>();
    private Queue<AbstractTask<?>> nextTickTasks = new LinkedList<>();


    /**
     * Make sure you call this at the START of a tick, or whatever the "start" is in your context.
     */
    public void tick(long gameTime) {
        while(!currentTickTasks.isEmpty()) {
            AbstractTask<?> task = currentTickTasks.poll();

            //In case a subclass does state = INTERRUPTED in the interrupt() method. Shouldnt do this but oh well.
            if(task.getState() != AbstractTask.TaskState.INTERRUPTED) {
                //Run the next task
                task.update(gameTime, 0);
            }

            processTaskResult(task, currentTickTasks, nextTickTasks);
        }

        //Swap
        currentTickTasks = nextTickTasks;
        nextTickTasks = new LinkedList<>();
    }

    /**
     * Make sure you call this at the START of a frame, or whatever the "start" is in your context.
     */
    public void tickFrame(long gameTime, float partialTick) {
        while(!currentFrameTasks.isEmpty()) {
            AbstractTask<?> task = currentFrameTasks.poll();

            //In case a subclass does state = INTERRUPTED in the interrupt() method. Shouldnt do this but oh well.
            if(task.getState() != AbstractTask.TaskState.INTERRUPTED) {
                //Run the next task
                task.update(gameTime, partialTick);
            }

            processTaskResult(task, currentFrameTasks, nextFrameTasks);
        }

        //Swap
        currentFrameTasks = nextFrameTasks;
        nextFrameTasks = new LinkedList<>();
    }

    public void addFrameTask(AbstractTask<?> task) {
        nextFrameTasks.add(task);
    }

    public void addTickTask(AbstractTask<?> task) {
        nextTickTasks.add(task);
    }

    public boolean interruptFrameTask(UUID uuid) {
        return interruptTaskInternal(uuid, nextFrameTasks);
    }

    public boolean interruptTickTask(UUID uuid) {
        return interruptTaskInternal(uuid, nextTickTasks);
    }

    public boolean interruptFrameTask(String tag) {
        return interruptTaskInternal(tag, nextFrameTasks);
    }

    public boolean interruptTickTask(String tag) {
        return interruptTaskInternal(tag, nextTickTasks);
    }

    public void interruptAll() {
        nextFrameTasks.forEach(AbstractTask::interrupt);
        nextTickTasks.forEach(AbstractTask::interrupt);
    }

    public void clear() {
        nextTickTasks.clear();
        nextFrameTasks.clear();
    }

    private boolean interruptTaskInternal(UUID uuid, Queue<AbstractTask<?>> queue) {
        boolean ret = false;
        for (AbstractTask<?> task : queue) {
            if (task.uuid().equals(uuid)) {
                task.interrupt();
                ret = true;
            }
        }
        return ret;
    }

    private boolean interruptTaskInternal(String tag, Queue<AbstractTask<?>> queue) {
        boolean ret = false;
        for (AbstractTask<?> task : queue) {
            if (task.hasTag(tag)) {
                task.interrupt();
                ret = true;
            }
        }
        return ret;
    }




    // Processes the result of calling a task. Do stuff on complete, on fail, etc.
    private void processTaskResult(AbstractTask<?> task, Queue<AbstractTask<?>> currentQueue, Queue<AbstractTask<?>> nextQueue) {
        AbstractTask.TaskState state = task.getState();
        switch(state) {
            case PENDING -> {
                nextQueue.add(task);
            }
            case INTERRUPTED -> {
                AbstractTask<?> onInterrupted = task.applyOnInterrupt();
                if(onInterrupted != null) {
                    currentQueue.add(onInterrupted);
                }
            }
            case COMPLETED -> {
                AbstractTask<?> onCompleted = task.applyOnComplete();
                if(onCompleted != null) {
                    currentQueue.add(onCompleted);
                }
            }
            case FAILURE -> {
                AbstractTask<?> onFailed = task.applyOnFail();
                if(onFailed != null) {
                    currentQueue.add(onFailed);
                }
            }
        }
    }
}
