package com.vertexcubed.cybernetics.common.ability;

import com.vertexcubed.cybernetics.common.item.KineticDischargerItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class KineticDischargerAbility extends Ability {

    public KineticDischargerAbility(AbilityType<?> type) {
        super(type);
    }

    @Override
    public void onEnable(LivingEntity parent) {
        KineticDischargerItem.spike(parent);
    }

    @Override
    public void abilityTick(LivingEntity parent) {

    }

    @Override
    public void onDisable(LivingEntity parent) {

    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {

    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {

    }
}
