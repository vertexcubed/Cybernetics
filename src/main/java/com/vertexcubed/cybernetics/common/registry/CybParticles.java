package com.vertexcubed.cybernetics.common.registry;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.client.particle.BlastWaveParticle;
import com.vertexcubed.cybernetics.client.particle.options.BlastWaveParticleOptions;
import com.vertexcubed.cybernetics.client.util.CybParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.lodestar.lodestone.systems.particle.world.type.LodestoneWorldParticleType;

import java.util.function.Supplier;

public class CybParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Cybernetics.MOD_ID);

    public static final Supplier<CybParticleType<BlastWaveParticleOptions>> BLAST_WAVE =
            PARTICLE_TYPES.register("blast_wave", () -> new CybParticleType<>(false, BlastWaveParticleOptions.CODEC, BlastWaveParticleOptions.STREAM_CODEC));

    public static final Supplier<LodestoneWorldParticleType> FALLING_PARTICLE =
            PARTICLE_TYPES.register("falling_particle", LodestoneWorldParticleType::new);



    public static void register(IEventBus bus) {
        PARTICLE_TYPES.register(bus);
    }
}
