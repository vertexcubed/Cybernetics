package com.vertexcubed.cybernetics.common.ability;

import com.vertexcubed.cybernetics.client.hud.CyberneticsHUD;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class OpticsAbility extends Ability {
    public OpticsAbility(AbilityType<?> type) {
        super(type);
    }

    @Override
    public void onEnable(LivingEntity parent) {

    }

    @Override
    public void abilityTick(LivingEntity parent) {
        CyberneticsHUD.INSTANCE.setEnabled(true);
    }

    @Override
    public void onDisable(LivingEntity parent) {
        CyberneticsHUD.INSTANCE.setEnabled(false);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {

    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {

    }
}
