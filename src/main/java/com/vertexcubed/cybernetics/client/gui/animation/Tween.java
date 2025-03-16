package com.vertexcubed.cybernetics.client.gui.animation;

import net.minecraft.util.Mth;
import team.lodestar.lodestone.systems.easing.Easing;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Tween implements Animation<Tween> {

    private final Consumer<Float> setter;
    private final Float oldValue;
    private final float newValue;
    private final long startTime;
    private final int duration;
    private final Set<String> tags = new HashSet<>();
    private final Easing easing;
    private boolean isDone = false;

    public Tween(Supplier<Float> getter, Consumer<Float> setter, float newValue, long startTime, int duration) {
        this(getter, setter, newValue, startTime, duration, Easing.LINEAR);
    }

    public Tween(Supplier<Float> getter, Consumer<Float> setter, float newValue, long startTime, int duration, Easing easing) {
        this.setter = setter;
        this.oldValue = getter.get();
        this.newValue = newValue;
        this.startTime = startTime;
        this.duration = duration;
        this.easing = easing;
    }

    @Override
    public Tween withTag(String tag) {
        tags.add(tag);
        return this;
    }

    @Override
    public Tween withTags(String... tag) {
        tags.addAll(Arrays.asList(tag));
        return this;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void update(float elapsedTime) {
        float percent = elapsedTime / duration;
        if(percent >= 1.0f) {
            isDone = true;
            return;
        }
        float easedPercent = easing.ease(percent, 0, 1);
        setter.accept(Mth.lerp(easedPercent, oldValue, newValue));
    }

    @Override
    public void interrupt() {
        setter.accept(newValue);
        isDone = true;
    }

    @Override
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }
}
