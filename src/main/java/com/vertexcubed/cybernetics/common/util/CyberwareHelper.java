package com.vertexcubed.cybernetics.common.util;

import com.vertexcubed.cybernetics.common.registry.CybDPRegistries;
import com.vertexcubed.cybernetics.common.storage.CyberwareSectionType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public class CyberwareHelper {


    public static List<Map.Entry<ResourceKey<CyberwareSectionType>, CyberwareSectionType>> getValidSections(ItemStack stack, RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(CybDPRegistries.CYBERWARE_SECTION_KEY).entrySet().stream().filter(type -> stack.is(type.getValue().tag())).toList();
    }
}
