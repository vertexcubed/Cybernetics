package com.vertexcubed.cybernetics.client.gui.animation;

public interface Animation<T> {

    T withTag(String tag);
    T withTags(String... tag);
    long getStartTime();
    boolean isDone();
    void update(float elapsedTime);
    void interrupt();
    boolean hasTag(String tag);
}
