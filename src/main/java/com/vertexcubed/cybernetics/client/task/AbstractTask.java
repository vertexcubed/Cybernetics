package com.vertexcubed.cybernetics.client.task;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;

public abstract class AbstractTask<T> {
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
    public static final NoResult NONE = new NoResult();

    public static class NoResult {
        private NoResult() {}
    }

    protected final Set<String> tags = new HashSet<>();

    protected @Nonnull Function<T, AbstractTask<?>> onCompleteFunc = (prev -> null);
    protected @Nonnull Function<T, AbstractTask<?>> onFailFunc = (prev -> null);
    protected @Nonnull Function<T, AbstractTask<?>> onInterruptFunc = (prev -> null);


    public abstract void update(TaskManager context, long gameTime, float partialTick);

    public abstract void init(TaskManager context);


    public TaskState getState() {
        return state;
    }

    public AbstractTask<T> withTag(String tag) {
        tags.add(tag);
        return this;
    }

    public AbstractTask<T> withTags(String... tag) {
        tags.addAll(Arrays.asList(tag));
        return this;
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public UUID uuid() {
        return uuid;
    }


    public <U> WrappedTask<T, U> then(Function<T, AbstractTask<U>> onComplete) {
        return then((s, r) -> {
            if(s != TaskState.COMPLETED) return null;
            return onComplete.apply(r);
        });
    }

    public <U> WrappedTask<T, U> then(Function<T, AbstractTask<U>> onComplete, Function<T, AbstractTask<U>> onFail) {
        return then((s, r) -> {
            switch(s) {
                case COMPLETED -> {
                    return onComplete.apply(r);
                }
                case FAILURE -> {
                    return onFail.apply(r);
                }
                default -> {
                    return null;
                }
            }
        });
    }

    public <U> WrappedTask<T, U> then(Function<T, AbstractTask<U>> onComplete, Function<T, AbstractTask<U>> onFail, Function<T, AbstractTask<U>> onInterrupt) {
        return then((s, r) -> {
            switch(s) {
                case COMPLETED -> {
                    return onComplete.apply(r);
                }
                case FAILURE -> {
                    return onFail.apply(r);
                }
                case INTERRUPTED -> {
                    return onInterrupt.apply(r);
                }
                default -> {
                    return null;
                }
            }
        });
    }

    public <U> WrappedTask<T, U> then(WrappedTask.ChildFactory<T, U> factory) {
        return new WrappedTask<>(this, factory);
    }

    /**
     * Note: result may not be accurate if getResult() is called before the task is finished.
     */
    public abstract T getResult();

    public abstract void interrupt();

    public AbstractTask<?> applyOnComplete() {
        return onCompleteFunc.apply(getResult());
    }
    public AbstractTask<?> applyOnFail() {
        return onFailFunc.apply(getResult());
    }

    public AbstractTask<?> applyOnInterrupt() {
        return onInterruptFunc.apply(getResult());
    }

}
