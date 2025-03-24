package com.vertexcubed.cybernetics.common.util;

import com.vertexcubed.cybernetics.common.event.CyberwareEvent;
import com.vertexcubed.cybernetics.common.item.CyberwareItem;
import com.vertexcubed.cybernetics.common.registry.CybDPRegistries;
import com.vertexcubed.cybernetics.common.storage.CyberwareSectionType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public class CyberwareHelper {

    /**
     * Returns a List of sections (formatted as a Map Entry of id : type) that this itemstack can be added to.
     * Requires RegistryAccess
     */
    public static List<Map.Entry<ResourceKey<CyberwareSectionType>, CyberwareSectionType>> getValidSections(ItemStack stack, RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(CybDPRegistries.CYBERWARE_SECTION_KEY).entrySet().stream().filter(type -> stack.is(type.getValue().tag())).toList();
    }


    /**
     * Adds a given cyberware to an entity. Will add to the first valid slot possible.
     * Make sure to call this on both sides!
     * Also calls {@link CyberwareItem#onEquip} and fires {@link CyberwareEvent.Equip}
     * @return whether the addition was successful
     */
    public static boolean addToEntity(ItemStack stack, Entity entity) {
        //TODO: implement
        return false;
    }

    /**
     * Adds a given cyberware to an entity. Will add to the first valid slot possible.
     * Make sure to call this on both sides!
     * Also calls {@link CyberwareItem#onUnequip} and fires {@link CyberwareEvent.Unequip}
     * @return whether the removal was successful
     */
    public static boolean removeFromEntity(ItemStack stack, Entity entity) {
        //TODO: implement
        return false;
    }
}
