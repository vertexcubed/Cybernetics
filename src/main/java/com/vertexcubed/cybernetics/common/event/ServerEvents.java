package com.vertexcubed.cybernetics.common.event;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.registry.CybItems;
import com.vertexcubed.cybernetics.common.registry.CybTags;
import com.vertexcubed.cybernetics.common.util.CyberwareHelper;
import com.vertexcubed.cybernetics.server.network.S2CSyncAbilityStoragePayload;
import com.vertexcubed.cybernetics.server.network.S2CSyncCyberwarePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Cybernetics.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if(!player.hasData(CybAttachments.CYBERWARE_INVENTORY)) {
            player.getData(CybAttachments.CYBERWARE_INVENTORY).init(player.level().registryAccess());
        }
        PacketDistributor.sendToPlayer(player, new S2CSyncAbilityStoragePayload(player.getData(CybAttachments.ABILITY_STORAGE), player));
        PacketDistributor.sendToPlayer(player, new S2CSyncCyberwarePayload(player.getData(CybAttachments.CYBERWARE_INVENTORY), player));
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        PacketDistributor.sendToPlayer(player, new S2CSyncCyberwarePayload(player.getData(CybAttachments.CYBERWARE_INVENTORY), player));

        PacketDistributor.sendToPlayer(player, new S2CSyncAbilityStoragePayload(player.getData(CybAttachments.ABILITY_STORAGE), player));
    }

    @SubscribeEvent
    public static void onEntityTracking(PlayerEvent.StartTracking event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) return;
        if(!(event.getTarget() instanceof LivingEntity entity) || !entity.hasData(CybAttachments.CYBERWARE_INVENTORY)) return;

        PacketDistributor.sendToPlayer(player, new S2CSyncCyberwarePayload(entity.getData(CybAttachments.CYBERWARE_INVENTORY), entity));
        if(entity.hasData(CybAttachments.ABILITY_STORAGE)) {
            PacketDistributor.sendToPlayer(player, new S2CSyncAbilityStoragePayload(entity.getData(CybAttachments.ABILITY_STORAGE), entity));
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if(!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide) return;
        if(entity.hasData(CybAttachments.CYBERWARE_INVENTORY)) {
            PacketDistributor.sendToAllPlayers(new S2CSyncCyberwarePayload(entity.getData(CybAttachments.CYBERWARE_INVENTORY), entity));
        }
        if(entity.hasData(CybAttachments.ABILITY_STORAGE)) {
            PacketDistributor.sendToAllPlayers(new S2CSyncAbilityStoragePayload(entity.getData(CybAttachments.ABILITY_STORAGE), entity));
        }
    }



    @SubscribeEvent
    public static void onLivingAttackEvent(LivingIncomingDamageEvent event) {
        if(event.getEntity().level().isClientSide) return;

        if(CyberwareHelper.hasCyberware(CybItems.PROJECTILE_DEFLECTOR.get(), event.getEntity()) && !event.getSource().isDirect()) {
            EntityType<?> entityType = event.getSource().getDirectEntity().getType();
            if(!entityType.is(CybTags.PROJECTILES_ALWAYS_HIT) && event.getEntity().getRandom().nextInt(10) < 4) {
                event.setCanceled(true);
            }
        }
    }


}

