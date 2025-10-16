package com.vertexcubed.cybernetics.common.ability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class EmergencyDefibrillatorAbility extends Ability {

    public EmergencyDefibrillatorAbility(AbilityType<?> type) {
        super(type);
    }

    @Override
    public void onEnable(LivingEntity parent) {
        if(parent.level().isClientSide) {
            // Play sound


            return;
        }
        parent.setHealth(parent.getHealth() + parent.getMaxHealth() / 4.0f);
        parent.removeEffectsCuredBy(net.neoforged.neoforge.common.EffectCures.PROTECTED_BY_TOTEM);
        parent.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 2));
        parent.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));

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
