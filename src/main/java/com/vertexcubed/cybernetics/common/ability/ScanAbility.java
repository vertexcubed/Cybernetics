package com.vertexcubed.cybernetics.common.ability;

import com.vertexcubed.cybernetics.client.render.ScannerRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class ScanAbility extends Ability {

    public ScanAbility(AbilityType<?> type) {
        super(type);
    }

    @Override
    public void onEnable(LivingEntity parent) {
        // only do this if I am the client player
        if(parent.level().isClientSide && parent.is(Minecraft.getInstance().player)) {
            ScannerRenderer.getInstance().setup(parent, ScannerRenderer.DURATION);
        }
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
