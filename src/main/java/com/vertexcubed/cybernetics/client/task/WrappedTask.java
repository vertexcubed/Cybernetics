package com.vertexcubed.cybernetics.client.task;

import java.util.UUID;

/**
 * Wraps a task into a parent and (lazily instantiated) child task. Once the parent task is
 * finished, the child task is created and begins executing. Generally don't creating this yourself
 * and use {@link AbstractTask#then} instead.
 */
public class WrappedTask extends AbstractTask {

    private AbstractTask child;
    private final AbstractTask parent;
    private final ChildFactory childFactory;

    public WrappedTask(AbstractTask parent, ChildFactory childFactory) {
        super(UUID.randomUUID());

        this.parent = parent;
        this.child = null;
        this.childFactory = childFactory;
    }

    @Override
    public void update(TaskManager context, long gameTime, float partialTick) {

        int i = 1;

        //Made child?
        if(child != null) {
            child.update(context, gameTime, partialTick);
            state = child.getState();
            return;
        }

        //Update parent. Maybe make child.
        parent.update(context, gameTime, partialTick);
        if(parent.getState() == TaskState.PENDING) {
            state = TaskState.PENDING;
            return;
        }

        //Make child. Did it not work?
        this.child = childFactory.create(parent.getState());
        if(this.child == null) {
            state = parent.getState();
            return;
        }
        this.child.init(context);

        //Update child.
        child.update(context, gameTime, partialTick);
        state = child.getState();

    }

    @Override
    public void init(TaskManager context) {
        parent.init(context);
    }


    @Override
    public void interrupt() {
        if(child == null) {
            parent.interrupt();
            return;
        }
        child.interrupt();
    }

    @Override
    public boolean hasTag(String tag) {
        boolean other = child == null ? parent.hasTag(tag) : child.hasTag(tag);
        return other || super.hasTag(tag);
    }

    @Override
    public UUID uuid() {
        return child == null ? parent.uuid() : child.uuid();
    }

    @FunctionalInterface
    public interface ChildFactory {
        AbstractTask create(TaskState parentResult);
    }
}
