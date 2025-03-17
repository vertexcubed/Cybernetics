package com.vertexcubed.cybernetics.common.registry;

import com.vertexcubed.cybernetics.common.storage.CyberwareSectionType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class CybDPRegistries {

    public static final ResourceKey<Registry<CyberwareSectionType>> CYBERWARE_SECTION_KEY = ResourceKey.createRegistryKey(modLoc("cyberware_sections"));





    public static void register(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(CYBERWARE_SECTION_KEY, CyberwareSectionType.CODEC, CyberwareSectionType.CODEC);
    }
}
