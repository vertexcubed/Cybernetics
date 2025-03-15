package com.vertexcubed.cybernetics.common.storage;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

public class CyberwareInventory extends CombinedInvWrapper implements INBTSerializable<CompoundTag> {

    private int capacity;
    private int maxCapacity;


    public CyberwareInventory(IItemHandlerModifiable... itemHandler) {
        super(itemHandler);
        this.capacity = 0;
        this.maxCapacity = 100; //TODO: Config entry.
    }

    public static CyberwareInventory create() {
        return new CyberwareInventory(new IItemHandlerModifiable[0]);
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
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, CompoundTag tag) {
        this.capacity = tag.getInt("capacity");
        this.maxCapacity = tag.getInt("maxCapacity");
    }
}
