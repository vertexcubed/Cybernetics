package com.vertexcubed.cybernetics.common.event;


import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.item.CyberwareItem;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.storage.CyberwareInventory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;


@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Cybernetics.MOD_ID)
public class CommonEvents {

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!event.getEntity().hasData(CybAttachments.CYBERWARE_INVENTORY)) return;
        if(!(event.getEntity() instanceof LivingEntity livingEntity)) return;

        CyberwareInventory inv = event.getEntity().getData(CybAttachments.CYBERWARE_INVENTORY);
        for(int i = 0; i < inv.getSlots(); i++) {
            if (inv.getStackInSlot(i).isEmpty()) continue;

            ItemStack stack = inv.getStackInSlot(i);
            NeoForge.EVENT_BUS.post(new CyberwareEvent.Tick(stack, i, livingEntity.level(), livingEntity));
            if(stack.getItem() instanceof CyberwareItem item) {
                item.cyberwareTick(stack, i, livingEntity.level(), livingEntity);
            }
        }
    }


}
