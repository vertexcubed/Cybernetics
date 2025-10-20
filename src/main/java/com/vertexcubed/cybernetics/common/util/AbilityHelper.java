package com.vertexcubed.cybernetics.common.util;

import com.vertexcubed.cybernetics.client.hud.AbilityHUD;
import com.vertexcubed.cybernetics.client.hud.CyberneticsHUD;
import com.vertexcubed.cybernetics.common.ability.Ability;
import com.vertexcubed.cybernetics.common.ability.AbilityType;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.storage.AbilityStorage;
import net.minecraft.world.entity.LivingEntity;

public class AbilityHelper {


    /**
     * Adds an ability to the current entity. This must be called on BOTH sides!
     * @param entity    The entity to add an ability to
     * @param type      The ability to add
     */
    public static void addAbility(LivingEntity entity, AbilityType<?> type) {
        if(!entity.hasData(CybAttachments.ABILITY_STORAGE)) return;
        AbilityStorage storage = entity.getData(CybAttachments.ABILITY_STORAGE);
        storage.add(type.createAbility());

        if(entity.level().isClientSide) {
            CyberneticsHUD.getInstance().getElements().forEach(element -> {
                if(element instanceof AbilityHUD abilityHUD) {
                    abilityHUD.updateElementList();
                }
            });
        }
    }


    /**
     * Removes an ability from the current entity. This must be called on BOTH sides!
     * @param entity    The entity to test on
     * @param type      The ability type to check
     * @return          Whether the removal was successful. Returns false if the ability was not found.
     */
    public static boolean removeAbility(LivingEntity entity, AbilityType<?> type) {
        if(!entity.hasData(CybAttachments.ABILITY_STORAGE)) return false;
        AbilityStorage storage = entity.getData(CybAttachments.ABILITY_STORAGE);
        boolean removedAnything = false;
        for(Ability a : storage.getAbilities()) {
            if(a.getType() == type) {
                if(storage.remove(entity, a)) {
                    removedAnything = true;
                }
            }
        }

        if(entity.level().isClientSide) {
            CyberneticsHUD.getInstance().getElements().forEach(element -> {
                if(element instanceof AbilityHUD abilityHUD) {
                    abilityHUD.updateElementList();
                }
            });
        }
        return removedAnything;
    }

    /**
     * Returns true if the specified entity has this ability.
     * @param entity    The entity to test on
     * @param type      The ability type to check
     * @return          Whether the entity has this ability.
     */
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

    /**
     * Enables a specified ability for an entity.
     * @param entity    The entity to test on
     * @param type      The ability type to check
     * @return          Whether enabling was successful. Returns false if it couldn't be enabled (on cooldown, not found, etc.)
     */
    public static boolean enableAbility(LivingEntity entity, AbilityType<?> type) {
        if(!hasAbility(entity, type)) return false;
        AbilityStorage storage = entity.getData(CybAttachments.ABILITY_STORAGE);
        boolean enabledAnything = false;
        for(Ability a : storage.getAbilities()) {
            if(a.getType() == type) {
                if(a.enable(entity)) {
                    enabledAnything = true;
                }
            }
        }
        return enabledAnything;
    }

    /**
     * Disables a specified ability for an entity.
     * @param entity    The entity to test on
     * @param type      The ability type to check
     * @return          Whether disabling was successful. Returns false if it couldn't be disabled (already disabled, not found, etc.)
     */
    public static boolean disableAbility(LivingEntity entity, AbilityType<?> type) {
        if(!hasAbility(entity, type)) return false;
        AbilityStorage storage = entity.getData(CybAttachments.ABILITY_STORAGE);
        boolean disabledAnything = false;
        for(Ability a : storage.getAbilities()) {
            if(a.getType() == type) {
                if(a.disable(entity)) {
                    disabledAnything = true;
                }
            }
        }
        return disabledAnything;
    }

    /**
     * Returns whether this ability is enabled on this entity.
     * @param entity    The entity to test on
     * @param type      The ability type to check
     * @return          Whether the ability was enabled. Returns false if the ability was not found.
     */
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

    /**
     * Returns the cooldown of this ability, or -1 if not found / not on cooldown.
     * @param entity    The entity to test on
     * @param type      The ability type to check
     * @return          The cooldown of the FIRST instance of this ability
     */
    public static int getCooldown(LivingEntity entity, AbilityType<?> type) {
        if(!entity.hasData(CybAttachments.ABILITY_STORAGE)) return -1;
        AbilityStorage storage = entity.getData(CybAttachments.ABILITY_STORAGE);
        for(Ability a : storage.getAbilities()) {
            if(a.getType() == type) {
                return a.getCooldown();
            }
        }
        return -1;
    }

    /**
     * Returns whether the specified ability is on cooldown.
     * @param entity    The entity to test on
     * @param type      The ability type to check
     * @return          Whether the FIRST instance of this ability is on cooldown
     */
    public static boolean isOnCooldown(LivingEntity entity, AbilityType<?> type) {
        return getCooldown(entity, type) > -1;
    }
}
