package com.vertexcubed.cybernetics.client.particle.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vertexcubed.cybernetics.common.registry.CybParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record BlastWaveParticleOptions(int argb, int duration, float radius) implements ParticleOptions {

    public BlastWaveParticleOptions() {
        this(0xFFFFFFFF, 20, 8.0f);
    }
    public BlastWaveParticleOptions(int argb) {
        this(argb, 20, 8.0f);
    }
    public BlastWaveParticleOptions(int argb, int duration) {
        this(argb, duration, 8.0f);
    }


    public static final MapCodec<BlastWaveParticleOptions> CODEC =
        RecordCodecBuilder.mapCodec(builder -> builder.group(
                Codec.INT.fieldOf("argb").forGetter(BlastWaveParticleOptions::argb),
                Codec.INT.fieldOf("duration").forGetter(BlastWaveParticleOptions::duration),
                Codec.FLOAT.fieldOf("radius").forGetter(BlastWaveParticleOptions::radius)
        ).apply(builder, BlastWaveParticleOptions::new));

    public static final StreamCodec<FriendlyByteBuf, BlastWaveParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, BlastWaveParticleOptions::argb,
            ByteBufCodecs.VAR_INT, BlastWaveParticleOptions::duration,
            ByteBufCodecs.FLOAT, BlastWaveParticleOptions::radius,
            BlastWaveParticleOptions::new
    );

    @Override
    public @NotNull ParticleType<?> getType() {
        return CybParticles.BLAST_WAVE.get();
    }
}
