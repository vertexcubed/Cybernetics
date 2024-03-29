package com.vivi.cybernetics.common.util;

import com.vivi.cybernetics.Cybernetics;
import com.vivi.cybernetics.client.hud.AbilityHUD;
import com.vivi.cybernetics.client.hud.CyberneticsHUD;
import com.vivi.cybernetics.common.ability.Ability;
import com.vivi.cybernetics.common.ability.AbilityType;
import com.vivi.cybernetics.common.capability.PlayerAbilities;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

public class AbilityHelper {

    public static void addAbility(Player player, AbilityType type) {
        addAbility(player, type, false);
    }
    public static void addAbility(Player player, AbilityType type, boolean syncToClient) {
        getAbilities(player).ifPresent(abilities -> {
            if(!abilities.hasAbility(type)) {
                abilities.addAbility(new Ability(type), syncToClient);
            }
        });
        if(player.level().isClientSide) {
            CyberneticsHUD.getInstance().getElements().forEach(element -> {
                if(element instanceof AbilityHUD abilityHUD) {
                    abilityHUD.updateElementList();
                }
            });
        }
    }
    public static void removeAbility(Player player, AbilityType type) {
        removeAbility(player, type, false);
    }
    public static void removeAbility(Player player, AbilityType type, boolean syncToClient) {
        getAbilities(player).ifPresent(abilities -> {
            for (int i = 0; i < abilities.getAbilities().size(); i++) {
                Ability ability = abilities.getAbilities().get(i);
                if (ability.getType() == type) {
                    abilities.removeAbility(ability, syncToClient);
                    i--;
                }
            }
        });
        if(player.level().isClientSide) {
            CyberneticsHUD.getInstance().getElements().forEach(element -> {
                if(element instanceof AbilityHUD abilityHUD) {
                    abilityHUD.updateElementList();
                }
            });
        }
    }

    public static boolean isEnabled(Player player, AbilityType type) {
        PlayerAbilities abilities = getAbilities(player).orElse(null);
        if(abilities == null) return false;
        Ability ability = abilities.getAbility(type);
        return ability != null && ability.isEnabled();
    }

    public static boolean isEnabled(Player player, AbilityType... types) {
        PlayerAbilities abilities = getAbilities(player).orElse(null);
        if(abilities == null) return false;
        for (AbilityType type : types) {
            Ability ability = abilities.getAbility(type);
            if (ability != null && ability.isEnabled()) return true;
        }
        return false;
    }

    public static boolean isOnCooldown(Player player, AbilityType type) {
        PlayerAbilities abilities = getAbilities(player).orElse(null);
        if(abilities == null) return true;
        Ability ability = abilities.getAbility(type);
        return ability == null || ability.getCooldown() > -1;
    }

    public static LazyOptional<PlayerAbilities> getAbilities(Player player) {
        if(player == null) return LazyOptional.empty();
        return player.getCapability(Cybernetics.PLAYER_ABILITIES);
    }

    public static boolean enableAbility(Player player, AbilityType type) {
        return enableAbility(player, type, false);
    }
    public static boolean enableAbility(Player player, AbilityType type, boolean syncToClient) {
        PlayerAbilities abilities = getAbilities(player).orElse(null);
        if(abilities == null) return false;
        return abilities.enableAbility(type, syncToClient);
    }
    public static boolean disableAbility(Player player, AbilityType type) {
        return disableAbility(player, type, false);
    }
    public static boolean disableAbility(Player player, AbilityType type, boolean syncToClient) {
        PlayerAbilities abilities = getAbilities(player).orElse(null);
        if(abilities == null) return false;
        return abilities.disableAbility(type, syncToClient);
    }

}
