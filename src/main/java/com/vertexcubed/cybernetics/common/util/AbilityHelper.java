package com.vertexcubed.cybernetics.common.util;

import com.vertexcubed.cybernetics.client.hud.AbilityHUD;
import com.vertexcubed.cybernetics.client.hud.CyberneticsHUD;
import com.vertexcubed.cybernetics.common.ability.Ability;
import com.vertexcubed.cybernetics.common.ability.AbilityType;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.storage.AbilityStorage;
import com.vertexcubed.cybernetics.server.network.BidirectionalAbilityEventPayload;
import com.vertexcubed.cybernetics.server.network.S2CSyncAbilityStoragePayload;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;

public class AbilityHelper {


    public static void addAbility(LivingEntity entity, AbilityType<?> type) {
        addAbility(entity, type, false);
    }

    public static void addAbility(LivingEntity entity, AbilityType<?> type, boolean syncToClient) {
        if(!entity.hasData(CybAttachments.ABILITY_STORAGE)) return;
        AbilityStorage storage = entity.getData(CybAttachments.ABILITY_STORAGE);
        storage.add(type.createAbility().setParent(entity));

        if(entity.level().isClientSide) {
            CyberneticsHUD.getInstance().getElements().forEach(element -> {
                if(element instanceof AbilityHUD abilityHUD) {
                    abilityHUD.updateElementList();
                }
            });
        }

        if(syncToClient && !entity.level().isClientSide()) {
            PacketDistributor.sendToAllPlayers(new S2CSyncAbilityStoragePayload(storage, entity));
        }
    }


    public static boolean removeAbility(LivingEntity entity, AbilityType<?> type) {
        return removeAbility(entity, type, false);
    }

    public static boolean removeAbility(LivingEntity entity, AbilityType<?> type, boolean syncToClient) {
        if(!entity.hasData(CybAttachments.ABILITY_STORAGE)) return false;
        AbilityStorage storage = entity.getData(CybAttachments.ABILITY_STORAGE);
        boolean removedAnything = false;
        for(Ability a : storage.getAbilities()) {
            if(a.getType() == type) {
                storage.remove(a);
                removedAnything = true;
            }
        }

        if(entity.level().isClientSide) {
            CyberneticsHUD.getInstance().getElements().forEach(element -> {
                if(element instanceof AbilityHUD abilityHUD) {
                    abilityHUD.updateElementList();
                }
            });
        }

        if(syncToClient && !entity.level().isClientSide() && removedAnything) {
            PacketDistributor.sendToAllPlayers(new S2CSyncAbilityStoragePayload(storage, entity));
        }
        return removedAnything;
    }

    public static boolean hasAbility(LivingEntity entity, AbilityType<?> type) {
        if(!entity.hasData(CybAttachments.ABILITY_STORAGE)) return false;
        AbilityStorage storage = entity.getData(CybAttachments.ABILITY_STORAGE);
        for(Ability a : storage.getAbilities()) {
            if(a.getType() == type) {
                return true;
            }
        }
        return false;
    }

    public static boolean enableAbility(LivingEntity entity, AbilityType<?> type) {
        return enableAbility(entity, type, false);
    }

    public static boolean enableAbility(LivingEntity entity, AbilityType<?> type, boolean syncToClient) {
        if(!hasAbility(entity, type)) return false;
        AbilityStorage storage = entity.getData(CybAttachments.ABILITY_STORAGE);
        boolean enabledAnything = false;
        for(Ability a : storage.getAbilities()) {
            if(a.getType() == type) {
                a.enable();
                enabledAnything = true;
            }
        }
        if(syncToClient && !entity.level().isClientSide() && enabledAnything) {
            PacketDistributor.sendToAllPlayers(new BidirectionalAbilityEventPayload(BidirectionalAbilityEventPayload.Mode.ENABLE, type, entity.getId()));
        }
        return enabledAnything;
    }

    public static boolean disableAbility(LivingEntity entity, AbilityType<?> type) {
        return disableAbility(entity, type, false);
    }

    public static boolean disableAbility(LivingEntity entity, AbilityType<?> type, boolean syncToClient) {
        if(!hasAbility(entity, type)) return false;
        AbilityStorage storage = entity.getData(CybAttachments.ABILITY_STORAGE);
        boolean disabledAnything = false;
        for(Ability a : storage.getAbilities()) {
            if(a.getType() == type) {
                a.disable();
                disabledAnything = true;
            }
        }
        if(syncToClient && !entity.level().isClientSide() && disabledAnything) {
            PacketDistributor.sendToAllPlayers(new BidirectionalAbilityEventPayload(BidirectionalAbilityEventPayload.Mode.DISABLE, type, entity.getId()));
        }
        return disabledAnything;
    }

    public static boolean isEnabled(LivingEntity entity, AbilityType<?> type) {
        if(!entity.hasData(CybAttachments.ABILITY_STORAGE)) return false;
        AbilityStorage storage = entity.getData(CybAttachments.ABILITY_STORAGE);
        for(Ability a : storage.getAbilities()) {
            if(a.getType() == type && a.isEnabled()) {
                return true;
            }
        }
        return false;
    }
}
