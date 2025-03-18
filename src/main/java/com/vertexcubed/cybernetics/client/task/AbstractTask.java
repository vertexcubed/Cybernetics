package com.vertexcubed.cybernetics.client.task;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractTask {
    public enum TaskState {
        PENDING,
        INTERRUPTED,
        COMPLETED,
        FAILURE
    }

    protected TaskState state;
    private final UUID uuid;
    protected AbstractTask(@Nonnull final UUID uuid) {
        this.uuid = uuid;
        state = TaskState.PENDING;
    }

    /**
     * Use this value to represent a task that does not return a result, or just an empty result in general. Don't use null.
     */
//    public static final NoResult NONE = new NoResult();
//
//    public static class NoResult {
//        private NoResult() {}
//    }

    protected final Set<String> tags = new HashSet<>();


    public abstract void update(TaskManager context, long gameTime, float partialTick);

    public abstract void init(TaskManager context);


    public TaskState getState() {
        return state;
    }

    public AbstractTask withTag(String tag) {
        tags.add(tag);
        return this;
    }

    public AbstractTask withTags(String... tag) {
        tags.addAll(Arrays.asList(tag));
        return this;
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public UUID uuid() {
        return uuid;
    }


    public WrappedTask then(Supplier<AbstractTask> onComplete) {
        return then((s) -> {
            if(s != TaskState.COMPLETED) return null;
            return onComplete.get();
        });
    }

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

    public WrappedTask then(WrappedTask.ChildFactory factory) {
        return new WrappedTask(this, factory);
    }

    public abstract void interrupt();

}
