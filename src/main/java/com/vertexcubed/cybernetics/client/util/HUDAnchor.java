package com.vertexcubed.cybernetics.client.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.StringRepresentable;
import org.joml.Vector2i;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Adapted from <a href=https://www.curseforge.com/minecraft/mc-mods/dirtbagutil>DirtbagUtil</a> under LGPL v3
 */
@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public enum HUDAnchor implements StringRepresentable {

    TOP_LEFT("top_left", (w, h, x, y) -> new Vector2i(x, y)),
    TOP_MIDDLE("top_middle", (w, h, x, y) -> new Vector2i((w / 2) + x, y)),
    TOP_RIGHT("top_right", (w, h, x, y) -> new Vector2i(w + x, y)),
    MIDDLE_LEFT("middle_left", (w, h, x, y) -> new Vector2i(x, (h / 2) + y)),
    MIDDLE("middle", (w, h, x, y) -> new Vector2i((w / 2) + x, (h / 2) + y)),
    MIDDLE_RIGHT("middle_right", (w, h, x, y) -> new Vector2i(w + x, (h / 2) + y)),
    BOTTOM_LEFT("bottom_left", (w, h, x, y) -> new Vector2i(x, h + y)),
    BOTTOM_MIDDLE("bottom_middle", (w, h, x, y) -> new Vector2i((w / 2) + x, h + y)),
    BOTTOM_RIGHT("bottom_right", (w, h, x, y) -> new Vector2i(w + x, h + y));


    private static final Map<String, HUDAnchor> ID_MAP = ImmutableMap.copyOf(Arrays.stream(values())
            .collect(Collectors.<HUDAnchor, String, HUDAnchor>toMap(HUDAnchor::getSerializedName, Function.identity())));
    private final String name;
    private final IWithOffset function;

    HUDAnchor(final String name, final IWithOffset function) {
        this.name = name;
        this.function = function;
    }

    public static HUDAnchor getByName(final String name) {
        return ID_MAP.getOrDefault(name, TOP_LEFT);
    }

    public Vector2i getWithOffset(final int screenWidth, final int screenHeight, final int offsetX, final int offsetY) {
        return function.apply(screenWidth, screenHeight, offsetX, offsetY);
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }


    @FunctionalInterface
    public interface IWithOffset {
        Vector2i apply(final int screenWidth, final int screenHeight, final int offsetX, final int offsetY);
    }

}