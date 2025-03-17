package com.vertexcubed.cybernetics.common.storage;

import com.vertexcubed.cybernetics.common.registry.CybDPRegistries;
import com.vertexcubed.cybernetics.common.util.CombinedInvWrapperModifiable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CyberwareInventory extends CombinedInvWrapperModifiable implements INBTSerializable<CompoundTag> {

    private int capacity;
    private int maxCapacity;


    public CyberwareInventory(IItemHandlerModifiable... itemHandler) {
        super(itemHandler);
        this.capacity = 0;
        this.maxCapacity = 100; //TODO: Config entry.
    }

    public static CyberwareInventory create() {

        return new CyberwareInventory();
    }


    public CyberwareInventory copy() {
        CyberwareInventory copy = new CyberwareInventory(new IItemHandlerModifiable[0]);
        //TODO: implement copy
        copy.capacity = capacity;
        copy.maxCapacity = maxCapacity;
        return copy;
    }





//  Bless Neoforge handling syncing for me.

    @Override
    public CompoundTag serializeNBT(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("capacity", this.capacity);
        tag.putInt("maxCapacity", this.maxCapacity);
        CompoundTag sections = new CompoundTag();
        for(IItemHandlerModifiable handler : this.itemHandlers) {
            CyberwareSection s = (CyberwareSection) handler;
            sections.put(s.getId().toString(), s.serializeNBT(provider));
        }
        tag.put("contents", sections);

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, CompoundTag tag) {

        List<CyberwareSection> sections = new ArrayList<>();
        provider.lookupOrThrow(CybDPRegistries.CYBERWARE_SECTION_KEY).listElements().forEach(holder -> {
            sections.add(new CyberwareSection(holder.value(), holder.key().location()));
        });
        clearHandlers();
        for(CyberwareSection section : sections) {
            addItemHandler(section);
        }

        CompoundTag contents = tag.getCompound("contents");
        for(IItemHandlerModifiable handler : this.itemHandlers) {
            CyberwareSection s = (CyberwareSection) handler;
            s.deserializeNBT(provider, contents.getCompound(s.getId().toString()));
        }

        this.capacity = tag.getInt("capacity");
        this.maxCapacity = tag.getInt("maxCapacity");
    }
}
