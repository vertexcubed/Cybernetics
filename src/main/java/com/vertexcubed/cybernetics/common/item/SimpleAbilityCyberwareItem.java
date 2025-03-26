package com.vertexcubed.cybernetics.common.item;

import com.vertexcubed.cybernetics.common.ability.AbilityType;
import com.vertexcubed.cybernetics.common.util.AbilityHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SimpleAbilityCyberwareItem extends CyberwareItem {

    private final AbilityType<?> abilityType;

    public SimpleAbilityCyberwareItem(Properties properties, AbilityType<?> abilityType) {
        super(properties);
        this.abilityType = abilityType;
    }

    @Override
    public void onEquip(ItemStack stack, int slot, Level level, LivingEntity entity) {
        if(!AbilityHelper.hasAbility(entity, abilityType)) {
            AbilityHelper.addAbility(entity, abilityType);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, int slot, Level level, LivingEntity entity) {
        AbilityHelper.removeAbility(entity, abilityType);
    }
}
