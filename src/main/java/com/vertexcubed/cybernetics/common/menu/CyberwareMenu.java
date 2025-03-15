package com.vertexcubed.cybernetics.common.menu;

import com.vertexcubed.cybernetics.common.registry.CybMenus;
import com.vertexcubed.cybernetics.common.storage.CyberwareInventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CyberwareMenu extends AbstractContainerMenu {


    protected final IItemHandlerModifiable inventoryClone;
    protected final CyberwareInventory cyberwareInventoryClone;


    protected final List<ItemStack> itemsToAdd = new ArrayList<>();
    protected final List<ItemStack> itemsToRemove = new ArrayList<>();


    public CyberwareMenu(int containerId, Inventory inventory, CyberwareInventory cyberwareInventory) {
        super(CybMenus.CYBERWARE_MENU.get(), containerId);
        this.inventoryClone = new ItemStackHandler(Inventory.INVENTORY_SIZE);
        int counter = 0;
        for(int i = 0 ; i < Inventory.INVENTORY_SIZE ; i++) {
            //TODO: only cyberware can be inserted.
            this.inventoryClone.insertItem(counter++, inventory.getItem(i).copy(), false);
        }
        this.cyberwareInventoryClone = cyberwareInventory.copy();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }
}
