package com.vertexcubed.cybernetics.client.task;

import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import team.lodestar.lodestone.systems.easing.Easing;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TweenTask extends AbstractTask<AbstractTask.NoResult> {


    private final Consumer<Float> setter;
    private final Easing easing;
    private final Float oldValue;
    private final float newValue;
    private final int duration;
    private final long startTime;
    private boolean isInterrupted = false;

    public TweenTask(Supplier<Float> getter, Consumer<Float> setter, float newValue, long startTime, int duration) {
        this(getter, setter, newValue, startTime, duration, Easing.LINEAR);
    }

    public TweenTask(Supplier<Float> getter, Consumer<Float> setter, float newValue, long startTime, int duration, Easing easing) {
        super(UUID.randomUUID());
        this.setter = setter;
        this.oldValue = getter.get();
        this.newValue = newValue;
        this.startTime = startTime;
        this.duration = duration;
        this.easing = easing;
    }


    @Override
    public @NotNull TaskState update(long gameTime, float partialTick) {
        if(gameTime < startTime) {
            return TaskState.PENDING;
        }
        if(isInterrupted) {
            return TaskState.INTERRUPTED;
        }
        float normalizedTime = (gameTime - startTime) + partialTick;
        float percent = normalizedTime / duration;
        if(percent >= 1.0f) {
            return TaskState.COMPLETED;
        }
        float easedPercent = easing.ease(percent, 0, 1);
        setter.accept(Mth.lerp(easedPercent, oldValue, newValue));
        return TaskState.PENDING;
    }

    @Override
    public NoResult getResult() {
        return NONE;
    }

    @Override
    public void interrupt() {
        setter.accept(newValue);
        isInterrupted = true;
    }

}
