package com.vertexcubed.cybernetics.common.registry;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.ability.AbilityType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class CybAbilities {

    public static final ResourceKey<Registry<AbilityType<?>>> ABILITY_TYPE_KEY = ResourceKey.createRegistryKey(modLoc("ability_types"));
    public static final Registry<AbilityType<?>> ABILITY_TYPE_REGISTRY = new RegistryBuilder<>(ABILITY_TYPE_KEY)
            .sync(true)
            .create();

    public static final DeferredRegister<AbilityType<?>> ABILITY_TYPES = DeferredRegister.create(ABILITY_TYPE_REGISTRY, Cybernetics.MOD_ID);


    public static void register(IEventBus eventBus) {
        ABILITY_TYPES.register(eventBus);
    }
}
