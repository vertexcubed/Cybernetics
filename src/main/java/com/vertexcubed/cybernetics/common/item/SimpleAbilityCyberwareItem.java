package com.vertexcubed.cybernetics.common.item;

import com.vertexcubed.cybernetics.common.ability.Ability;
import com.vertexcubed.cybernetics.common.ability.AbilityType;
import com.vertexcubed.cybernetics.common.util.AbilityHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class SimpleAbilityCyberwareItem<T extends Ability> extends CyberwareItem {

    private final Supplier<AbilityType<T>> abilityType;

    public SimpleAbilityCyberwareItem(Properties properties, Supplier<AbilityType<T>> abilityType) {
        super(properties);
        this.abilityType = abilityType;
    }

    @Override
    public void onEquip(ItemStack stack, int slot, Level level, LivingEntity entity) {
        if(!AbilityHelper.hasAbility(entity, abilityType.get())) {
            AbilityHelper.addAbility(entity, abilityType.get());
        }
    }

    @Override
    public void onUnequip(ItemStack stack, int slot, Level level, LivingEntity entity) {
        AbilityHelper.removeAbility(entity, abilityType.get());
    }
}
