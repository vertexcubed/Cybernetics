package com.vertexcubed.cybernetics.common.menu;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.registry.CybMenus;
import com.vertexcubed.cybernetics.common.storage.CyberwareInventory;
import com.vertexcubed.cybernetics.common.storage.CyberwareSectionType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CyberwareMenu extends AbstractContainerMenu {


    protected final IItemHandlerModifiable inventoryClone;
    protected final CyberwareInventory cyberwareInventoryClone;


    protected final List<ItemStack> itemsToAdd = new ArrayList<>();
    protected final List<ItemStack> itemsToRemove = new ArrayList<>();


    private final int inventorySlotId;
    public CyberwareMenu(int containerId, Inventory inventory, CyberwareInventory cyberwareInventory) {
        super(CybMenus.CYBERWARE_MENU.get(), containerId);
        this.inventoryClone = new ItemStackHandler(Inventory.INVENTORY_SIZE);
        int counter = 0;
        for(int i = 0 ; i < Inventory.INVENTORY_SIZE ; i++) {
            //TODO: only cyberware can be inserted.
            this.inventoryClone.insertItem(counter++, inventory.getItem(i).copy(), false);
        }

        // ===================
        // Cyberware Inventory
        // ===================
        this.cyberwareInventoryClone = cyberwareInventory.copy();

        int slotX = 36, slotY = 20;

        counter = 0;
        int rows = 4;
        int maxCols = 0;
        for(int i = 0; i < cyberwareInventoryClone.getSlots(); i++) {
            CyberwareSectionType last = i > 0 ? cyberwareInventoryClone.getSectionFromSlot(i - 1).getType() : null;
            if(last != null && !last.equals(cyberwareInventoryClone.getSectionFromSlot(i).getType())) {
                if(counter > maxCols) {
                    maxCols = counter;
                }
                counter = 0;
            }
            addSlot(new CyberwareSlot(cyberwareInventoryClone, i, slotX + ((counter % rows) * 25) + 1, slotY + ((counter / rows) * 21) + 1, inventory.player));
            counter++;
        }
        maxCols = Mth.ceil((float) maxCols / rows);


        //=======================
        // Player inventory clone
        //=======================
        inventorySlotId = slots.size();
        int invX = 36, invY = 84;
        for(int j = 0; j < 3; j++) {
            for(int i = 0; i < 12; i++) {
                addSlot(new InventorySlot(this.inventoryClone, i + (j*12), invX + ((i % rows) * 25) + 1, invY + ((i / rows) * 21) + 1, j));
            }
        }

        //===========
        // Data Slots
        //===========
    }


    public void switchActiveSlots(CyberwareSectionType type) {
        for(int i = 0; i < cyberwareInventoryClone.getSlots(); i++) {
            if (type != null && cyberwareInventoryClone.getSectionFromSlot(i).getType().equals(type)) {
                ((CyberwareSlot) getSlot(i)).turnOn();
            } else {
                ((CyberwareSlot) getSlot(i)).turnOff();
            }

        }
    }

    public CyberwareInventory getCyberware() {
        return cyberwareInventoryClone;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public static class InventorySlot extends ToggleableSlot {

        private final int page;
        public InventorySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, int page) {
            super(itemHandler, index, xPosition, yPosition);
            this.page = page;
            this.turnOff();
        }

        public int getPage() {
            return page;
        }
    }

    public static class CyberwareSlot extends ToggleableSlot {

        private Player player;
        private boolean canEdit;
        private final CyberwareInventory cyberware;
        private final int index;
        public final CyberwareSectionType type;

        public CyberwareSlot(CyberwareInventory cyberware, int index, int xPosition, int yPosition, Player player) {
            super(cyberware, index, xPosition, yPosition);
            this.player = player;
            this.canEdit = true;
            this.cyberware = cyberware;
            this.index = index;
            this.type = cyberware.getSectionFromSlot(index).getType();
            this.turnOff();
        }

        @Override
        public boolean mayPickup(Player playerIn) {
            return canEdit && super.mayPickup(playerIn);
        }


        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return canEdit && super.mayPlace(stack);
        }

        public void setCanEdit(boolean canEdit) {
            this.canEdit = canEdit;
        }

        public boolean canEdit() {
            return canEdit;
        }
    }
}
