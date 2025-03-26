package com.vertexcubed.cybernetics.common.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

import java.util.ArrayList;
import java.util.List;

public class CombinedInvWrapperModifiable implements IItemHandlerModifiable {
    protected final List<IItemHandlerModifiable> itemHandlers;
    protected final List<Integer> handlerBaseIndices;
    protected int slotCount;

    public CombinedInvWrapperModifiable(IItemHandlerModifiable... itemHandler) {
        this.itemHandlers = new ArrayList<>(List.of(itemHandler));
        this.handlerBaseIndices = new ArrayList<>(itemHandler.length);
        int index = 0;

        for(int i = 0; i < itemHandler.length; i++) {
            index += itemHandler[i].getSlots();
            handlerBaseIndices.set(i, index);
        }

        this.slotCount = index;
    }

    protected int getIndexForSlot(int slot) {
        if (slot < 0) {
            return -1;
        } else {
            for(int i = 0; i < this.handlerBaseIndices.size(); ++i) {
                if (slot - this.handlerBaseIndices.get(i) < 0) {
                    return i;
                }
            }

            return -1;
        }
    }

    protected IItemHandlerModifiable getHandlerFromIndex(int index) {
        return index >= 0 && index < this.itemHandlers.size() ? this.itemHandlers.get(index) : (IItemHandlerModifiable) EmptyItemHandler.INSTANCE;
    }

    protected int getSlotFromIndex(int slot, int index) {
        return index > 0 && index < this.handlerBaseIndices.size() ? slot - this.handlerBaseIndices.get(index - 1) : slot;
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        slot = this.getSlotFromIndex(slot, index);
        handler.setStackInSlot(slot, stack);
    }

    public int getSlots() {
        return this.slotCount;
    }

    public ItemStack getStackInSlot(int slot) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        slot = this.getSlotFromIndex(slot, index);
        return handler.getStackInSlot(slot);
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        slot = this.getSlotFromIndex(slot, index);
        return handler.insertItem(slot, stack, simulate);
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        slot = this.getSlotFromIndex(slot, index);
        return handler.extractItem(slot, amount, simulate);
    }

    public int getSlotLimit(int slot) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        int localSlot = this.getSlotFromIndex(slot, index);
        return handler.getSlotLimit(localSlot);
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        int localSlot = this.getSlotFromIndex(slot, index);
        return handler.isItemValid(localSlot, stack);
    }

    public void clearHandlers() {
        itemHandlers.clear();
        handlerBaseIndices.clear();
        this.slotCount = 0;
    }

    public void addItemHandler(IItemHandlerModifiable newHandler) {
        itemHandlers.add(newHandler);
        handlerBaseIndices.add(this.slotCount + newHandler.getSlots());
        this.slotCount += newHandler.getSlots();
    }
}
