package com.vertexcubed.cybernetics.common.util;

import com.mojang.datafixers.util.Pair;
import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.event.CyberwareEvent;
import com.vertexcubed.cybernetics.common.item.CyberwareItem;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.registry.CybDPRegistries;
import com.vertexcubed.cybernetics.common.storage.CyberwareInventory;
import com.vertexcubed.cybernetics.common.storage.CyberwareSectionType;
import com.vertexcubed.cybernetics.server.network.BidirectionalCyberwareEventPayload;
import com.vertexcubed.cybernetics.server.network.S2CSyncCyberwarePayload;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
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
     * Also calls {@link CyberwareItem#onEquip} and fires {@link CyberwareEvent.Equip}
     * @return Any stacks that were not successfully added
     */
    public static List<ItemStack> addToEntity(LivingEntity entity, ItemStack... stack) {
        return addToEntity(entity, false, stack);
    }

    /**
     * Adds a given cyberware to an entity. Will add to the first valid slot possible.
     * Also calls {@link CyberwareItem#onEquip} and fires {@link CyberwareEvent.Equip}
     * @return Any stacks that were not successfully added
     */
    public static List<ItemStack> addToEntity(LivingEntity entity, boolean syncToClients, ItemStack... stacks) {
        List<ItemStack> allStacks = new ArrayList<>(List.of(stacks));
        if(!entity.hasData(CybAttachments.CYBERWARE_INVENTORY)) return allStacks;
        CyberwareInventory inv = entity.getData(CybAttachments.CYBERWARE_INVENTORY);

        List<Pair<Integer, ItemStack>> syncEquip = new ArrayList<>();

        boolean addedAnything = false;
        for(int i = 0; i < inv.getSlots(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) continue;
            for (int j = 0; j < allStacks.size(); j++) {
                ItemStack stack = allStacks.get(j);
                ItemStack left = inv.insertItem(i, stack, true);
                if (left.isEmpty()) {
                    inv.insertItem(i, stack, false);
                    addedAnything = true;
                    syncEquip.add(Pair.of(i, stack));
                    if (stack.getItem() instanceof CyberwareItem item) {
                        item.onEquip(stack, i, entity.level(), entity);
                    }
                    NeoForge.EVENT_BUS.post(new CyberwareEvent.Equip(stack, i, entity.level(), entity));
                    allStacks.remove(j);
                    j--;
                }
            }
        }
        if(syncToClients && !entity.level().isClientSide() && addedAnything) {
            PacketDistributor.sendToAllPlayers(new S2CSyncCyberwarePayload(inv, entity));
            syncEquip.forEach(p -> {
                PacketDistributor.sendToAllPlayers(new BidirectionalCyberwareEventPayload(BidirectionalCyberwareEventPayload.Mode.EQUIP, p.getSecond(), p.getFirst(), entity.getId()));
            });
        }
        return allStacks;
    }

    /**
     * Removes a given cyberware to an entity.
     * Make sure to call this on both sides!
     * Also calls {@link CyberwareItem#onUnequip} and fires {@link CyberwareEvent.Unequip}
     * @return whether the removal was successful
     */
    public static boolean removeFromEntity(ItemStack stack, LivingEntity entity) {
        return removeFromEntity(stack, entity, false);
    }

    /**
     * Removes a given cyberware to an entity.
     * Make sure to call this on both sides!
     * Also calls {@link CyberwareItem#onUnequip} and fires {@link CyberwareEvent.Unequip}
     * @return whether the removal was successful
     */
    public static boolean removeFromEntity(ItemStack stack, LivingEntity entity, boolean syncToClients) {
        if(!entity.hasData(CybAttachments.CYBERWARE_INVENTORY)) return false;
        CyberwareInventory inv = entity.getData(CybAttachments.CYBERWARE_INVENTORY);
        for(int i = 0; i < inv.getSlots(); i++) {
            ItemStack other = inv.getStackInSlot(i);
            if(ItemStack.matches(stack, other)) {
                inv.extractItem(i, other.getCount(), false);
                //TODO: fire on client if syncToClients
                if (other.getItem() instanceof CyberwareItem item) {
                    item.onUnequip(other, i, entity.level(), entity);
                }
                NeoForge.EVENT_BUS.post(new CyberwareEvent.Unequip(other, i, entity.level(), entity));
                if(syncToClients && !entity.level().isClientSide()) {
                    PacketDistributor.sendToAllPlayers(new S2CSyncCyberwarePayload(inv, entity));
                    PacketDistributor.sendToAllPlayers(new BidirectionalCyberwareEventPayload(BidirectionalCyberwareEventPayload.Mode.UNEQUIP, other, i, entity.getId()));
                }
                return true;
            }
        }
        return false;
    }

    public static boolean hasCyberware(Item item, LivingEntity entity) {
        if(!entity.hasData(CybAttachments.CYBERWARE_INVENTORY)) return false;
        CyberwareInventory inv = entity.getData(CybAttachments.CYBERWARE_INVENTORY);
        for(int i = 0; i < inv.getSlots(); i++) {
            if(inv.getStackInSlot(i).getItem() == item) {
                return true;
            }
        }
        return false;
    }
}
