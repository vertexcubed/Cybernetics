package com.vertexcubed.cybernetics.common.event;


import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.item.CyberwareItem;
import com.vertexcubed.cybernetics.common.registry.CybAbilities;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.storage.AbilityStorage;
import com.vertexcubed.cybernetics.common.storage.CyberwareInventory;
import com.vertexcubed.cybernetics.common.util.AbilityHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;


@EventBusSubscriber(modid = Cybernetics.MOD_ID)
public class CommonEvents {

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

        if (event.getEntity().hasData(CybAttachments.CYBERWARE_INVENTORY)) {

            CyberwareInventory inv = event.getEntity().getData(CybAttachments.CYBERWARE_INVENTORY);
            for (int i = 0; i < inv.getSlots(); i++) {
                if (inv.getStackInSlot(i).isEmpty()) continue;

                ItemStack stack = inv.getStackInSlot(i);
                NeoForge.EVENT_BUS.post(new CyberwareEvent.Tick(stack, i, livingEntity.level(), livingEntity));
                if (stack.getItem() instanceof CyberwareItem item) {
                    item.cyberwareTick(stack, i, livingEntity.level(), livingEntity);
                }
            }
        }

        if(event.getEntity().hasData(CybAttachments.ABILITY_STORAGE)) {
            AbilityStorage storage = event.getEntity().getData(CybAttachments.ABILITY_STORAGE);
            storage.tick(livingEntity);
        }
    }

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if(!((entity instanceof Player player && player.isCreative()) || entity.isSpectator())) {
            if(AbilityHelper.enableAbility(entity, CybAbilities.EMERGENCY_DEFIBRILLATOR.get())) {
                event.setCanceled(true);
                return;
            }
        }

    }
}
