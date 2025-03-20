package com.vertexcubed.cybernetics.common.storage;

import com.vertexcubed.cybernetics.common.registry.CybDPRegistries;
import com.vertexcubed.cybernetics.common.util.CombinedInvWrapperModifiable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
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

    public void init(HolderLookup.Provider provider) {
        List<CyberwareSection> sections = new ArrayList<>();
        provider.lookupOrThrow(CybDPRegistries.CYBERWARE_SECTION_KEY).listElements().forEach(holder -> {
            sections.add(new CyberwareSection(holder.value(), holder.key().location()));
        });
        clearHandlers();
        for(CyberwareSection section : sections) {
            addItemHandler(section);
        }
    }


    public CyberwareInventory copy() {
        CyberwareInventory copy = new CyberwareInventory(new IItemHandlerModifiable[0]);
        //TODO: implement copy
        copy.capacity = capacity;
        copy.maxCapacity = maxCapacity;
        copy.clearHandlers();
        for(IItemHandlerModifiable handler : this.itemHandlers) {
            CyberwareSection s = (CyberwareSection) handler;
            copy.addItemHandler(s.copy());
        }
        return copy;
    }

    public void copyFrom(CyberwareInventory other) {
        this.capacity = other.capacity;
        this.maxCapacity = other.maxCapacity;
        this.clearHandlers();
        for(IItemHandlerModifiable handler : other.itemHandlers) {
            CyberwareSection s = (CyberwareSection) handler;
            this.addItemHandler(s.copy());
        }
    }


    public CyberwareSection getSectionFromSlot(int slot) {
        int index = getIndexForSlot(slot);
        return ((CyberwareSection) getHandlerFromIndex(index));
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
        init(provider);

        CompoundTag contents = tag.getCompound("contents");
        for(IItemHandlerModifiable handler : this.itemHandlers) {
            CyberwareSection s = (CyberwareSection) handler;
            s.deserializeNBT(provider, contents.getCompound(s.getId().toString()));
        }

        this.capacity = tag.getInt("capacity");
        this.maxCapacity = tag.getInt("maxCapacity");
    }

    public List<CyberwareSection> getSections() {
        List<CyberwareSection> output = new ArrayList<>();
        for (IItemHandlerModifiable itemHandler : itemHandlers) {
            output.add(((CyberwareSection) itemHandler).copy());
        }
        return output;
    }
}
