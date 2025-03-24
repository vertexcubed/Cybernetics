package com.vertexcubed.cybernetics.client.task;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * <p>
 *     Represents a task that can be added to a TaskManager. A task can be any arbitrary function,
 *     and can be called every tick or every frame.
 * </p>
 * <p>
 *     A task has four possible states: pending, interrupted, completed, or fail. While a task is
 *     being calculated, it is considered pending. An interrupted task is a task that has been
 *     forcibly ended <b>externally</b>, while a failed task is a task that has been ended
 *     <b>internally</b>.
 * </p>
 * <p>
 *     Tasks are <b>mutable</b> objects, thus their state changes over time.
 *     Make sure to not call update multiple times per execution step!
 * </p>
 */
public abstract class AbstractTask {
    public enum TaskState {
        PENDING,
        INTERRUPTED,
        COMPLETED,
        FAILURE
    }

    protected TaskState state;
    private final UUID uuid;

    /**
     * When overriding, do <b>not</b> perform any calculations in the constructor.
     */
    protected AbstractTask(@Nonnull final UUID uuid) {
        this.uuid = uuid;
        state = TaskState.PENDING;
    }

    protected final Set<String> tags = new HashSet<>();


    /**
     * Called by the TaskManager every time this task is to be updated. Each call is one "step"
     * in a task's execution.
     * @param context The parent TaskManager this task is apart of.
     * @param gameTime The current game time in ticks.
     * @param partialTick The current partial tick, from 0-1. If this task is being executed every tick, this will always be 0.
     */
    public abstract void update(TaskManager context, long gameTime, float partialTick);

    /**
     * Called when a task is first added to a TaskManager. Generally does stuff like setting the start time.
     * @param context The parent TaskManager this task is apart of.
     */
    public abstract void init(TaskManager context);


    /**
     * Called when a task is interrupted externally. May do stuff such as jumping to the end value or something
     * similar.
     */
    public abstract void interrupt();

    /**
     * Gets the state of the task.
     */
    public TaskState getState() {
        return state;
    }

    /**
     * Adds a tag to this task. Tags can be used to interrupt tasks en masse without having
     * to store references or UUIDs.
     * @param tag The tag to add.
     */
    public AbstractTask withTag(String tag) {
        tags.add(tag);
        return this;
    }

    /**
     * Adds multiple tags to this task. Tags can be used to interrupt tasks en masse without having
     * to store references or UUIDs.
     * @param tag The tags to add.
     */
    public AbstractTask withTags(String... tag) {
        tags.addAll(Arrays.asList(tag));
        return this;
    }

    /**
     * Returns true if this task has the given tag.
     */
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public UUID uuid() {
        return uuid;
    }

    /**
     * Chains a task after this to be executed when this task is completed.
     * @return A WrappedTask representing this task <b>and</b> the task superseding it.
     */
    public WrappedTask then(AbstractTask onComplete) {
        return then(() -> onComplete);
    }

    /**
     * Chains a task after this to be executed when this task is completed, or a task when it fails.
     * @return A WrappedTask representing this task <b>and</b> the task superseding it.
     */
    public WrappedTask then(AbstractTask onComplete, AbstractTask onFail) {
        return then(() -> onComplete, () -> onFail);
    }

    /**
     * Chains a task after this to be executed when this task is completed. Supplier to
     * allow you to make decisions on what the next task should be based on the running
     * state of your code.
     * @return A WrappedTask representing this task <b>and</b> the task superseding it.
     */
    public WrappedTask then(Supplier<AbstractTask> onComplete) {
        return then((s) -> {
            if(s != TaskState.COMPLETED) return null;
            return onComplete.get();
        });
    }

    /**
     * Chains a task after this to be executed when this task is completed, or when the task fails.
     * Supplier to allow you to make decisions on what the next task should be based on the running
     * state of your code.
     * @return A WrappedTask representing this task <b>and</b> the task superseding it.
     */
    public WrappedTask then(Supplier<AbstractTask> onComplete, Supplier<AbstractTask> onFail) {
        return then((s) -> {
            switch(s) {
                case COMPLETED -> {
                    return onComplete.get();
                }
                case FAILURE -> {
                    return onFail.get();
                }
                default -> {
                    return null;
                }
            }
        });
    }

    /**
     * Chains a task after this to be executed when this task is finished.
     * Multiple suppliers to allow you to make decisions on what the next task should be based on the running
     * state of your code, and the result of the previous task.
     * @return A WrappedTask representing this task <b>and</b> the task superseding it.
     */
    public WrappedTask then(Supplier<AbstractTask> onComplete, Supplier<AbstractTask> onFail, Supplier<AbstractTask> onInterrupt) {
        return then((s) -> {
            switch(s) {
                case COMPLETED -> {
                    return onComplete.get();
                }
                case FAILURE -> {
                    return onFail.get();
                }
                case INTERRUPTED -> {
                    return onInterrupt.get();
                }
                default -> {
                    return null;
                }
            }
        });
    }

    /**
     * Chains a task to be created after this, with the state of the previous task as an input.
     * @param factory A factory to create the next task, in the form of a function taking in a TaskState and returning an AbstractTask
     @return A WrappedTask representing this task <b>and</b> the task superseding it.
     */
    public WrappedTask then(WrappedTask.ChildFactory factory) {
        return new WrappedTask(this, factory);
    }

}
