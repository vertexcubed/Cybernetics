package com.vivi.cybernetics.menu;

import com.vivi.cybernetics.Cybernetics;
import com.vivi.cybernetics.block.entity.SurgicalChamberBlockEntity;
import com.vivi.cybernetics.cyberware.CyberwareInventory;
import com.vivi.cybernetics.item.CyberwareItem;
import com.vivi.cybernetics.registry.ModCyberware;
import com.vivi.cybernetics.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

public class SurgicalChamberCyberwareMenu extends CyberwareMenu  {

    private final SurgicalChamberBlockEntity blockEntity;

    public SurgicalChamberCyberwareMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, (SurgicalChamberBlockEntity) inv.player.level.getBlockEntity(buf.readBlockPos()));
    }
    public SurgicalChamberCyberwareMenu(int id, Inventory inv, SurgicalChamberBlockEntity be) {
        this(id, inv, be.getCapability(ModCyberware.CYBERWARE).orElse(null), be);
    }
    public SurgicalChamberCyberwareMenu(int pContainerId, Inventory inventory, CyberwareInventory cyberware, SurgicalChamberBlockEntity be) {
        super(ModMenuTypes.SURGICAL_CHAMBER_CYBERWARE_MENU.get(), pContainerId, inventory, cyberware);
        this.blockEntity = be.getMainBlockEntity();

        addSlotListener(new ContainerListener() {
            @Override
            public void slotChanged(AbstractContainerMenu menu, int slot, ItemStack stack) {
                Cybernetics.LOGGER.info("Slot changed, is client: " + inventory.player.level.isClientSide);
                if(!(menu instanceof CyberwareMenu cyberwareMenu)) return;
                if(!(cyberwareMenu.getSlot(slot) instanceof CyberwareSlot)) return;

                if(cyberwareMenu.getCarried().is(stack.getItem())) return;

//                if(pStack.is(cyberwareMenu.getSlot(slot).getItem().getItem())) return;
//                Cybernetics.LOGGER.info("Stack: " + stack + ", Stack in slot: " + cyberwareMenu.getSlot(slot).getItem());
//                Cybernetics.LOGGER.info("Is pickup: " + stack.is(cyberwareMenu.getSlot(slot).getItem().getItem()));
                Cybernetics.LOGGER.info("Removing cyberware depending on " + cyberwareMenu.getCarried());
                for(int i = 0; i < cyberwareMenu.getCyberware().getSlots(); i++) {
                    if (!(cyberwareMenu.getCyberware().getStackInSlot(i).getItem() instanceof CyberwareItem item) || !(cyberwareMenu.getCarried().getItem() instanceof CyberwareItem)) {
                        continue;
                    }
                    Cybernetics.LOGGER.info("Found cyberware");
                    for (Ingredient req : item.getRequirements()) {
                        if (req.test(cyberwareMenu.getCarried())) {
                            Cybernetics.LOGGER.info("Moving cyberware...");
                            SurgicalChamberCyberwareMenu.this.moveItemStackTo(cyberwareMenu.getCyberware().getStackInSlot(i), cyberwareMenu.getCyberware().getSlots(), cyberwareMenu.slots.size(), false);
                            break;
                        }
                    }
                }
            }

            @Override
            public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {

            }
        });
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if(player instanceof ServerPlayer) {
            CyberwareInventory playerCyberware = player.getCapability(ModCyberware.CYBERWARE).orElse(null);
            if(!playerCyberware.equals(cyberware)) {
                playerCyberware.copyFrom(cyberware, player, false);
            }
            cyberware.clear();
            if(blockEntity != null) {
                blockEntity.setInUse(false);
            }
        }
    }


}
