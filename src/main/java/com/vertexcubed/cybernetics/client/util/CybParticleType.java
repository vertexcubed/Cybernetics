package com.vertexcubed.cybernetics.client.util;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

/**
 * Simple ParticleType wrapper for custom particle types.
 */
public class CybParticleType<T extends ParticleOptions> extends ParticleType<T> {

    private final MapCodec<T> codec;
    private final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;

    /**
     * Constructor
     * @param overrideLimiter   If true, ignores lower particle settings
     * @param codec             Map codec for serialization
     * @param streamCodec       Stream codec for serialization
     */
    public CybParticleType(boolean overrideLimiter, MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        super(overrideLimiter);
        this.codec = codec;
        this.streamCodec = streamCodec;
    }

    @Override
    public @NotNull MapCodec<T> codec() {
        return codec;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }
}
