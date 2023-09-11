package com.vivi.cybernetics.menu;

import com.vivi.cybernetics.Cybernetics;
import com.vivi.cybernetics.block.entity.SurgicalChamberBlockEntity;
import com.vivi.cybernetics.capability.CyberwareInventory;
import com.vivi.cybernetics.capability.PlayerCyberwareProvider;
import com.vivi.cybernetics.registry.ModMenuTypes;
import com.vivi.cybernetics.util.ToggleableSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.compress.utils.Lists;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlayerCyberwareMenu extends AbstractContainerMenu {

    private Player player;
    private Inventory inventory;
    private CyberwareInventory cyberware;

    private final int invX = 8;
    private final int invY = 174;
    private final Map<String, List<Integer>> partSlotMap = new LinkedHashMap<>();
    private SurgicalChamberBlockEntity blockEntity;

    public PlayerCyberwareMenu(int pContainerId, Inventory inventory, Player player, FriendlyByteBuf buf) {
        this(pContainerId, inventory, player);
        boolean flag = buf.readBoolean();
        if(flag) {
        Cybernetics.LOGGER.info("Block pos: " + buf.readBlockPos());
        }
    }

    public PlayerCyberwareMenu(int pContainerId, Inventory inventory, Player player) {
        super(ModMenuTypes.PLAYER_CYBERWARE_MENU.get(), pContainerId);
        Cybernetics.LOGGER.info("On Client? " + player.level.isClientSide);
//        this.blockEntity = (SurgicalChamberBlockEntity) blockEntity;
        this.player = player;
        this.inventory = inventory;

        if(player != null) {
            player.getCapability(PlayerCyberwareProvider.PLAYER_CYBERWARE).ifPresent(cyberware -> {
                this.cyberware = cyberware;

                int x = 12, y = 135;

                int yOffset = 0, counter = 0;
                for (Map.Entry<String, ItemStackHandler> entry : cyberware.getParts().entrySet()) {
                    String key = entry.getKey();
                    ItemStackHandler handler = entry.getValue();
                    List<Integer> slots = Lists.newArrayList();
                    for (int i = 0; i < handler.getSlots(); i++) {
                        addSlot(new CyberwareSlot(handler, i, x + i * 19 - 1, y + yOffset + 1, player));
                        slots.add(counter++);
                    }
                    partSlotMap.put(key, slots);
                }

//                int[] numColsLeft = {2, 3, 3, 3, 3};
//                int[] numColsRight = {2, 1, 1, 1, 1};

                //2 head, 2 eye, 3 upper organs, 3 lower organs, 3 skeleton, 3 skin, 1 hands, 1 arms, 1 legs, 1 feet

                //temporary
//                int counter = 0;
//                for(int r = 0; r < 5; r++) {
//                    for(int c = 0; c < numColsLeft[r]; c++) {
//                        addSlot(new ToggleableSlot(handler, counter++, (50 - c*19) - 1, (14+r*23) + 1).turnOff());
//                    }
//                }
//                for(int r = 0; r < 5; r++) {
//                    for(int c = 0; c < numColsRight[r]; c++) {
//                        addSlot(new ToggleableSlot(handler, counter++, (127 + c*19) - 1, (14+r*23) + 1).turnOff());
//                    }
//                }

            });
        }

        addPlayerHotbar(inventory);
        addPlayerInventory(inventory);



    }



    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.isAlive();
    }

    public void switchSlots(String part) {
        partSlotMap.forEach((k, v) -> {
            if(k.equals(part)) {
                v.forEach(slot -> {
                    if(getSlot(slot) instanceof ToggleableSlot) {
                        ((ToggleableSlot)getSlot(slot)).turnOn();
                    }
                });
            }
            else {
                v.forEach(slot -> {
                    if(getSlot(slot) instanceof ToggleableSlot) {
                        ((ToggleableSlot)getSlot(slot)).turnOff();
                    }
                });
            }
        });
    }

    public List<String> getParts() {
        return partSlotMap.keySet().stream().toList();
    }

    private void addPlayerInventory(Inventory inv) {
        for(int r = 0; r < 3; r++) {
            for(int c = 0; c < 9; c++) {
                this.addSlot(new Slot(inv, c + r*9 + 9, invX + c*18, invY + r*18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for(int c = 0; c < 9; c++) {
            this.addSlot(new Slot(inv, c, invX + c*18, invY + 58));
        }

    }

    //todo replace this, broken

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 21;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        clearCyberware(pPlayer);
    }

    private void clearCyberware(Player pPlayer) {
        if(player == pPlayer && cyberware != null) {
            if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer)player).hasDisconnected()) {
                for(int j = 0; j < cyberware.getSize(); j++) {
                    player.drop(cyberware.removeItem(j), false);
                }

            } else {
                for(int i = 0; i < cyberware.getSize(); i++) {
                    Inventory inventory = player.getInventory();
                    if (inventory.player instanceof ServerPlayer) {
                        inventory.placeItemBackInInventory(cyberware.removeItem(i));
                    }
                }

            }
        }
    }
}
