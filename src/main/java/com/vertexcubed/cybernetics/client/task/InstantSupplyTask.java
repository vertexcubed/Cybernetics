package com.vertexcubed.cybernetics.client.task;

import java.util.UUID;
import java.util.function.Supplier;

public class InstantSupplyTask<T> extends AbstractTask<T> {

    private final Supplier<T> sup;
    private T value = null;
    public InstantSupplyTask(Supplier<T> sup) {
        super(UUID.randomUUID());
        this.sup = sup;
    }

    @Override
    public void update(long gameTime, float partialTick) {
        value = sup.get();
        state = TaskState.COMPLETED;
    }

    @Override
    public void init(TaskManager context, long gameTime) {

    }

    @Override
    public T getResult() {
        return value;
    }

    @Override
    public void interrupt() {

    }
}
