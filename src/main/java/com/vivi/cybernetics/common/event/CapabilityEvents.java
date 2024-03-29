package com.vivi.cybernetics.common.event;

import com.vivi.cybernetics.Cybernetics;
import com.vivi.cybernetics.common.capability.provider.PlayerAbilityProvider;
import com.vivi.cybernetics.common.capability.provider.PlayerCyberwareProvider;
import com.vivi.cybernetics.common.capability.provider.PlayerEnergyProvider;
import com.vivi.cybernetics.common.capability.provider.PlayerSpikeProvider;
import com.vivi.cybernetics.common.util.AbilityHelper;
import com.vivi.cybernetics.common.util.CyberwareHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Cybernetics.MOD_ID)
public class CapabilityEvents {

    @SubscribeEvent
    public static <T> void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player player) {
            if(!player.getCapability(Cybernetics.CYBERWARE).isPresent()) {
                event.addCapability(new ResourceLocation(Cybernetics.MOD_ID, "cyberware"), new PlayerCyberwareProvider(player));
            }
            if(!player.getCapability(Cybernetics.PLAYER_ENERGY).isPresent()) {
                event.addCapability(new ResourceLocation(Cybernetics.MOD_ID, "energy"), new PlayerEnergyProvider());
            }
            if(!player.getCapability(Cybernetics.PLAYER_ABILITIES).isPresent()) {
                event.addCapability(new ResourceLocation(Cybernetics.MOD_ID, "abilities"), new PlayerAbilityProvider(player));
            }
            if(!player.getCapability(Cybernetics.PLAYER_SPIKE).isPresent()) {
                event.addCapability(new ResourceLocation(Cybernetics.MOD_ID, "spike_data"), new PlayerSpikeProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClonedEvent(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        if(event.isWasDeath()) {
            event.getOriginal().getCapability(Cybernetics.CYBERWARE).ifPresent(oldStore -> {
                event.getEntity().getCapability(Cybernetics.CYBERWARE).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore, false);
                });
            });
            event.getOriginal().getCapability(Cybernetics.PLAYER_ENERGY).ifPresent(oldStore -> {
                event.getEntity().getCapability(Cybernetics.PLAYER_ENERGY).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
            event.getOriginal().getCapability(Cybernetics.PLAYER_ABILITIES).ifPresent(oldStore -> {
                event.getEntity().getCapability(Cybernetics.PLAYER_ABILITIES).ifPresent(newStore -> {
//                    Cybernetics.LOGGER.info("Old id: " + event.getOriginal().getId() + ", new id: " + event.getEntity().getId());
                    newStore.copyFrom(oldStore);
                });
            });
            event.getOriginal().getCapability(Cybernetics.PLAYER_SPIKE).ifPresent(oldStore -> {
                event.getEntity().getCapability(Cybernetics.PLAYER_SPIKE).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
        event.getOriginal().invalidateCaps();
    }



    //SYNC EVENTS


    @SubscribeEvent
    public static void onStartTrackingPlayer(PlayerEvent.StartTracking event) {
        if(event.getEntity().level().isClientSide) return;
        Entity target = event.getTarget();
        if(target instanceof Player player) {
//            CyberwareInventory cyberware = CyberwareHelper.getCyberware(player).orElse(null);
//            if(cyberware == null) return;
//            CybPackets.sendToClient(new S2CSyncCyberwarePacket(player, cyberware, false), (ServerPlayer) event.getEntity());
            CyberwareHelper.getCyberware(player).ifPresent(cyberware -> {
                cyberware.syncToClient((ServerPlayer) event.getEntity());
            });
            AbilityHelper.getAbilities((Player)target).ifPresent(abilities -> {
                abilities.syncToClient((ServerPlayer) event.getEntity());
            });
        }
    }

    @SubscribeEvent
    public static void onLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if(player.level().isClientSide) return;
        CyberwareHelper.getCyberware(player).ifPresent(cyberware -> {
            cyberware.syncToClient((ServerPlayer) player);
        });

        AbilityHelper.getAbilities(player).ifPresent(abilities -> {
            abilities.syncToClient((ServerPlayer) player);
        });
    }

    @SubscribeEvent
    public static void onEntityJoinLevelEvent(EntityJoinLevelEvent event) {
        if(!(event.getEntity() instanceof Player player) || event.getLevel().isClientSide) return;

        CyberwareHelper.getCyberware(player).ifPresent(cyberware -> {
            cyberware.syncToClient((ServerPlayer) player);
        });
        AbilityHelper.getAbilities(player).ifPresent(abilities -> {
            abilities.syncToClient((ServerPlayer) player);
        });
    }

    @SubscribeEvent
    public static void onChangeDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if(player.level().isClientSide) return;
        CyberwareHelper.getCyberware(player).ifPresent(cyberware -> {
            cyberware.syncToClient((ServerPlayer) player);
        });

        AbilityHelper.getAbilities(player).ifPresent(abilities -> {
            abilities.syncToClient((ServerPlayer) player);
        });
    }
}
