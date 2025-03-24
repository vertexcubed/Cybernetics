package com.vertexcubed.cybernetics.common.menu;

import com.mojang.datafixers.util.Pair;
import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.event.CyberwareEvent;
import com.vertexcubed.cybernetics.common.item.CyberwareItem;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.registry.CybDataComponents;
import com.vertexcubed.cybernetics.common.registry.CybMenus;
import com.vertexcubed.cybernetics.common.registry.CybTags;
import com.vertexcubed.cybernetics.common.storage.CyberwareInventory;
import com.vertexcubed.cybernetics.common.storage.CyberwareSectionType;
import com.vertexcubed.cybernetics.common.util.CyberwareHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CyberwareMenu extends AbstractContainerMenu {


    protected final IItemHandlerModifiable inventoryClone;
    protected final CyberwareInventory cyberwareInventoryClone;


    //Items to Add to cyberware
    protected final List<ItemStack> cybAddItems = new ArrayList<>();
    //Items to remove from cyberware
    protected final List<ItemStack> cybRemoveItems = new ArrayList<>();
    //Items to add to inventory
    protected final List<ItemStack> invAddItems = new ArrayList<>();
    //Items to remove from inventory
    protected final List<ItemStack> invRemoveItems = new ArrayList<>();

    private int currentInventoryPage;


    private final int inventorySlotId;
    public CyberwareMenu(int containerId, Inventory inventory, CyberwareInventory cyberwareInventory) {
        super(CybMenus.CYBERWARE_MENU.get(), containerId);
        this.inventoryClone = new ItemStackHandler(Inventory.INVENTORY_SIZE);
        int counter = 0;
        for(int i = 0 ; i < Inventory.INVENTORY_SIZE ; i++) {
            ItemStack stack = inventory.getItem(i).copy();
            if(!CyberwareHelper.getValidSections(stack, inventory.player.registryAccess()).isEmpty() || stack.is(CybTags.ANY_SECTION) || stack.has(CybDataComponents.CYBERWARE_PROPERTIES)) {
                this.inventoryClone.insertItem(counter++, inventory.getItem(i).copy(), false);
            }

        }

        currentInventoryPage = -1;

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
            addSlot(new CyberwareSlot(this, cyberwareInventoryClone, i, slotX + ((counter % rows) * 25) + 1, slotY + ((counter / rows) * 21) + 1, inventory.player));
            counter++;
        }
        maxCols = Mth.ceil((float) maxCols / rows);


        //=======================
        // Player inventory clone
        //=======================
        inventorySlotId = slots.size();
        int invX = 36, invY = 84;

        // # of pages total
        for(int j = 0; j < 3; j++) {
            // each slot
            for(int i = 0; i < 12; i++) {
//                addSlot(new InventorySlot(this.inventoryClone, i, invX + ((i % rows) * 25) + 1, invY + ((i / rows) * 21) + 1, 0));
                addSlot(new InventorySlot(this, this.inventoryClone, i + (j*12), invX + ((i % rows) * 25) + 1, invY + ((i / rows) * 21) + 1, j));
            }
        }

        //===========
        // Data Slots
        //===========
    }

    public void switchInventoryPage(int page) {
//        if(!canEdit) return;
        this.currentInventoryPage = page;
        for(int i = inventorySlotId; i < slots.size(); i++) {
            InventorySlot slot = (InventorySlot) getSlot(i);
            if(slot.getPage() == page) {
                slot.turnOn();
            }
            else {
                slot.turnOff();
            }
        }
    }

    public void switchCyberwareSlots(CyberwareSectionType type) {
        for(int i = 0; i < cyberwareInventoryClone.getSlots(); i++) {
            if (type != null && cyberwareInventoryClone.getSectionFromSlot(i).getType().equals(type)) {
                ((CyberwareSlot) getSlot(i)).turnOn();
            } else {
                ((CyberwareSlot) getSlot(i)).turnOff();
            }

        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if(clickType == ClickType.THROW) return;

        super.clicked(slotId, button, clickType, player);


        Cybernetics.LOGGER.debug("====================");
        Cybernetics.LOGGER.debug("cybAddItems: {}", cybAddItems);
        Cybernetics.LOGGER.debug("cybRemoveItems: {}", cybRemoveItems);
        Cybernetics.LOGGER.debug("invAddItems: {}", invAddItems);
        Cybernetics.LOGGER.debug("invRemoveItems: {}", invRemoveItems);
        Cybernetics.LOGGER.debug("====================");

    }

    public boolean hasModified() {
        return !invAddItems.isEmpty() || !invRemoveItems.isEmpty() || !cybAddItems.isEmpty() || !cybRemoveItems.isEmpty();
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

    public int getInventoryPage() {
        return currentInventoryPage;
    }

    public void applyChanges(Player player) {

        ItemStack itemstack = this.getCarried();
        if (!itemstack.isEmpty()) {
            if (in(itemstack, invRemoveItems)) {
                removeFrom(itemstack, invRemoveItems);
            } else {
                invAddItems.add(itemstack.copy());
            }
            this.setCarried(ItemStack.EMPTY);
        }

        CyberwareInventory cyberware = player.getData(CybAttachments.CYBERWARE_INVENTORY);
        List<Pair<Integer, ItemStack>> toAddReal = new ArrayList<>();
        List<Pair<Integer, ItemStack>> toRemoveReal = new ArrayList<>();
        for(int i = 0; i < cyberware.getSlots(); i++) {
            if(cyberware.getStackInSlot(i).isEmpty() && !cyberwareInventoryClone.getStackInSlot(i).isEmpty()) {
                ItemStack stack = cyberwareInventoryClone.getStackInSlot(i);
                if(in(stack, cybAddItems)) {
                    toAddReal.add(new Pair<>(i, stack));
                }
                else {
                    Cybernetics.LOGGER.error("Cannot apply changes: Fatal mismatch between cybAddItems list and actual inventory.");
                    return;
                }
            }
            else if(!cyberware.getStackInSlot(i).isEmpty() && cyberwareInventoryClone.getStackInSlot(i).isEmpty()) {
                ItemStack stack = cyberware.getStackInSlot(i);
                if(in(stack, cybRemoveItems)) {
                    toRemoveReal.add(new Pair<>(i, stack));
                }
                else {
                    Cybernetics.LOGGER.error("Cannot apply changes: Fatal mismatch between cybRemoveItems list and actual inventory.");
                    return;
                }
            }
        }
        toAddReal.forEach(pair -> {
            cyberware.insertItem(pair.getFirst(), pair.getSecond(), false);
            ItemStack inserted = pair.getSecond();
            NeoForge.EVENT_BUS.post(new CyberwareEvent.Equip(inserted, pair.getFirst(), player.level(), player));
            if(inserted.getItem() instanceof CyberwareItem cybItem) {
                cybItem.onEquip(inserted, pair.getFirst(), player.level(), player);
            }
        });
        toRemoveReal.forEach(pair -> {
            ItemStack extracted = cyberware.extractItem(pair.getFirst(), pair.getSecond().getCount(), false);
            NeoForge.EVENT_BUS.post(new CyberwareEvent.Unequip(extracted, pair.getFirst(), player.level(), player));
            if(extracted.getItem() instanceof CyberwareItem cybItem) {
                cybItem.onUnequip(extracted, pair.getFirst(), player.level(), player);
            }
        });


        Inventory inventory = player.getInventory();
        Cybernetics.LOGGER.debug("Applying changes!");
        invAddItems.forEach(item -> {
            if(!inventory.add(item)) {
                player.drop(item, true);
            }
        });
        invRemoveItems.forEach(item -> {
            for(int i = 35; i >= 0; i--) {
                if(ItemStack.matches(inventory.getItem(i), item)) {
                    inventory.removeItem(i, inventory.getItem(i).getCount());
                    return;
                }
            }
        });

    }

    private boolean in(ItemStack stack, List<ItemStack> list) {
        for(ItemStack s : list) {
            if(ItemStack.matches(s, stack)) return true;
        }
        return false;
    }
    private boolean removeFrom(ItemStack stack, List<ItemStack> list) {
        for(int i = 0; i < list.size(); i++) {
            if(ItemStack.matches(list.get(i), stack)) {
                list.remove(i);
                return true;
            }
        }
        return false;
    }

    public static class InventorySlot extends ToggleableSlot {

        private final CyberwareMenu parent;
        private final int page;
        public InventorySlot(CyberwareMenu parent, IItemHandler itemHandler, int index, int xPosition, int yPosition, int page) {
            super(itemHandler, index, xPosition, yPosition);
            this.parent = parent;
            this.page = page;
            this.turnOff();
        }

        @Override
        public void setByPlayer(ItemStack newStack, ItemStack oldStack) {
            if(!oldStack.isEmpty()) {
                if(parent.in(oldStack, parent.invAddItems)) {
                    parent.removeFrom(oldStack, parent.invAddItems);
                }
                else {
                    parent.invRemoveItems.add(oldStack.copy());
                }
            }

            if(!newStack.isEmpty()) {
                if(parent.in(newStack, parent.invRemoveItems)) {
                    parent.removeFrom(newStack, parent.invRemoveItems);
                }
                else {
                    parent.invAddItems.add(newStack.copy());
                }
            }
            super.setByPlayer(newStack, oldStack);
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
        private final CyberwareMenu parent;

        public CyberwareSlot(CyberwareMenu parent, CyberwareInventory cyberware, int index, int xPosition, int yPosition, Player player) {
            super(cyberware, index, xPosition, yPosition);
            this.parent = parent;
            this.player = player;
            this.canEdit = true;
            this.cyberware = cyberware;
            this.index = index;
            this.type = cyberware.getSectionFromSlot(index).getType();
            this.turnOff();
        }




        @Override
        public ItemStack safeTake(int count, int decrement, Player player) {
            ItemStack res = super.safeTake(count, decrement, player);
            if(!res.isEmpty()) {
            }
            return res;
        }

        @Override
        public void setByPlayer(ItemStack newStack, ItemStack oldStack) {
            if(!oldStack.isEmpty()) {
                if(parent.in(oldStack, parent.cybAddItems)) {
                    parent.removeFrom(oldStack, parent.cybAddItems);
                }
                else {
                    parent.cybRemoveItems.add(oldStack.copy());
                }
            }

            if(!newStack.isEmpty()) {
                if(parent.in(newStack, parent.cybRemoveItems)) {
                    parent.removeFrom(newStack, parent.cybRemoveItems);
                }
                else {
                    parent.cybAddItems.add(newStack.copy());
                }
            }
            super.setByPlayer(newStack, oldStack);
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
