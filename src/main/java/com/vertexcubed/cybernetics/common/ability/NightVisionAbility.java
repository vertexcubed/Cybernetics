package com.vertexcubed.cybernetics.common.ability;

import com.vertexcubed.cybernetics.common.util.Triple;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class NightVisionAbility extends Ability {

    public NightVisionAbility(AbilityType<?> type) {
        super(type);
    }

    @Override
    public void onEnable(LivingEntity parent) {

    }

    @Override
    public void abilityTick(LivingEntity parent) {
        if(parent.level().isClientSide) return;
        parent.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false, true));

    }

    @Override
    public void onDisable(LivingEntity parent) {
        if(parent.level().isClientSide) return;
        if(parent.hasEffect(MobEffects.NIGHT_VISION)) {
            parent.removeEffect(MobEffects.NIGHT_VISION);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {

    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {

    }
}
