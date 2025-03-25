package com.vertexcubed.cybernetics.common.util;

public class Triple<F, S, T> {

    private final F first;
    private final S second;
    private final T third;
    public Triple(final F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <F, S, T> Triple<F, S, T> of(F first, S second, T third) {
        return new Triple<>(first, second, third);
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public T getThird() {
        return third;
    }
}
