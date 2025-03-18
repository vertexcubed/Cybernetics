package com.vertexcubed.cybernetics.client.task;

import com.vertexcubed.cybernetics.client.gui.util.ScreenHelper;
import net.minecraft.client.gui.screens.Screen;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;

/**
 * <p>
 *    A TaskManager manages, well, tasks. It processes tasks in the queue,
 *  decides what to do with them based on their status after updating,
 *  and handles adding and interrupting tasks. You can have
 *  multiple task managers that exist at the same time!
 * </p>
 * <p>
 *    To make a new TaskManager, just call its no argument constructor
 *  and go nuts. By default, all Screens have a TaskManager associated
 *  with them, which can be obtained by calling {@link ScreenHelper#getTaskManager(Screen)}
 * </p>
 *
 */
public class TaskManager {

    private Queue<AbstractTask> currentFrameTasks = new LinkedList<>();
    private Queue<AbstractTask> nextFrameTasks = new LinkedList<>();
    private Queue<AbstractTask> currentTickTasks = new LinkedList<>();
    private Queue<AbstractTask> nextTickTasks = new LinkedList<>();

    private long gameTime;


    public TaskManager(long gameTime) {
        this.gameTime = gameTime;
    }


    /**
     * Make sure you call this at the START of a tick, or whatever the "start" is in your context.
     */
    public void tick(long gameTime) {
        this.gameTime = gameTime;
        while(!currentTickTasks.isEmpty()) {
            AbstractTask task = currentTickTasks.poll();

            //In case a subclass does state = INTERRUPTED in the interrupt() method. Shouldnt do this but oh well.
            if(task.getState() != AbstractTask.TaskState.INTERRUPTED) {
                //Run the next task
                task.update(this, gameTime, 0);
            }

            processTaskResult(gameTime, task, currentTickTasks, nextTickTasks);
        }

        //Swap
        currentTickTasks = nextTickTasks;
        nextTickTasks = new LinkedList<>();
    }

    /**
     * Make sure you call this at the START of a frame, or whatever the "start" is in your context.
     */
    public void tickFrame(long gameTime, float partialTick) {
        this.gameTime = gameTime;
        while(!currentFrameTasks.isEmpty()) {
            AbstractTask task = currentFrameTasks.poll();

            //In case a subclass does state = INTERRUPTED in the interrupt() method. Shouldnt do this but oh well.
            if(task.getState() != AbstractTask.TaskState.INTERRUPTED) {
                //Run the next task
                task.update(this, gameTime, partialTick);
            }

            processTaskResult(gameTime, task, currentFrameTasks, nextFrameTasks);
        }

        //Swap
        currentFrameTasks = nextFrameTasks;
        nextFrameTasks = new LinkedList<>();
    }

    public Optional<Queue<AbstractTask>> findQueueWith(AbstractTask task) {
        if(queueHas(currentFrameTasks, task)) return Optional.of(currentFrameTasks);
        if(queueHas(nextFrameTasks, task)) return Optional.of(nextFrameTasks);
        return Optional.empty();
    }

    private boolean queueHas(Queue<AbstractTask> queue, AbstractTask task) {
        for(AbstractTask t : queue) {
            if(t == task) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a "tick" task to this task manager. Tick tasks are updated every tick.
     * If you want a task that's updated every frame instead, use {@link TaskManager#addFrameTask}
     * @param task the task you want to add.
     * @return the task you just added. Make sure to NOT modify it at this point! (i.e. do not call
     * {@link AbstractTask#then} after returning.)
     */
    public  AbstractTask addTickTask(AbstractTask task) {
        nextTickTasks.add(task);
        task.init(this);
        return task;
    }

    /**
     * Adds a "frame" task to this task manager. Frame tasks are updated every frame.
     * If you want a task that's updated every tick instead, use {@link TaskManager#addTickTask}
     * @param task the task you want to add.
     * @return the task you just added. Make sure to NOT modify it at this point! (i.e. do not call
     * {@link AbstractTask#then} after returning.)
     */
    public  AbstractTask addFrameTask(AbstractTask task) {
        nextFrameTasks.add(task);
        task.init(this);
        return task;
    }

    /**
     * Interrupts a frame task by its uuid. UUIDs can be obtained by calling
     * {@link AbstractTask#uuid()}
     * @return whether a task was found to interrupt.
     */
    public boolean interruptFrameTask(UUID uuid) {
        return interruptTaskInternal(uuid, nextFrameTasks);
    }

    /**
     * Interrupts a tick task by its uuid. UUIDs can be obtained by calling
     * {@link AbstractTask#uuid()}
     * @return whether a task was found to interrupt.
     */
    public boolean interruptTickTask(UUID uuid) {
        return interruptTaskInternal(uuid, nextTickTasks);
    }

    /**
     * Interrupts all frame tasks containing this tag. Tags can be set by calling
     * {@link AbstractTask#withTag} when constructing the task.
     * @return whether a task was found to interrupt.
     */
    public boolean interruptFrameTask(String tag) {
        return interruptTaskInternal(tag, nextFrameTasks);
    }

    /**
     * Interrupts all tick tasks containing this tag. Tags can be set by calling
     * {@link AbstractTask#withTag} when constructing the task.
     * @return whether a task was found to interrupt.
     */
    public boolean interruptTickTask(String tag) {
        return interruptTaskInternal(tag, nextTickTasks);
    }

    /**
     * Interrupts all tasks in the task queues.
     */
    public void interruptAll() {
        nextFrameTasks.forEach(AbstractTask::interrupt);
        nextTickTasks.forEach(AbstractTask::interrupt);
    }

    /**
     * Clears all tasks in the task queues. Generally, you should interrupt them all first
     * by calling {@link TaskManager#interruptAll}.
     */
    public void clear() {
        nextTickTasks.clear();
        nextFrameTasks.clear();
    }

    public long gameTime() {
        return gameTime;
    }

    private boolean interruptTaskInternal(UUID uuid, Queue<AbstractTask> queue) {
        boolean ret = false;
        for (AbstractTask task : queue) {
            if (task.uuid().equals(uuid)) {
                task.interrupt();
                ret = true;
            }
        }
        return ret;
    }

    private boolean interruptTaskInternal(String tag, Queue<AbstractTask> queue) {
        boolean ret = false;
        for (AbstractTask task : queue) {
            if (task.hasTag(tag)) {
                task.interrupt();
                ret = true;
            }
        }
        return ret;
    }




    // Processes the result of calling a task. Do stuff on complete, on fail, etc.
    private void processTaskResult(long gameTime, AbstractTask task, Queue<AbstractTask> currentQueue, Queue<AbstractTask> nextQueue) {
        AbstractTask.TaskState state = task.getState();
        switch(state) {
            case PENDING -> {
                nextQueue.add(task);
            }
            case INTERRUPTED -> {
            }
            case COMPLETED -> {
            }
            case FAILURE -> {
            }
        }
    }
}
