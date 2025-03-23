package com.vertexcubed.cybernetics.common.registry;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.item.CyberwareProperties;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CybDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Cybernetics.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CyberwareProperties>> CYBERWARE_PROPERTIES = DATA_COMPONENT_TYPES.registerComponentType(
            "cyberware_properties",
            builder -> builder
                    .persistent(CyberwareProperties.CODEC)
                    .networkSynchronized(CyberwareProperties.STREAM_CODEC)
    );

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
