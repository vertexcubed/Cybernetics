package com.vertexcubed.cybernetics.common.item;

import com.vertexcubed.cybernetics.common.ability.OpticsAbility;
import com.vertexcubed.cybernetics.common.registry.CybAbilities;
import com.vertexcubed.cybernetics.common.util.AbilityHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class OpticsItem extends SimpleAbilityCyberwareItem<OpticsAbility> {
    private boolean canScan;
    public OpticsItem(Properties properties, boolean canScan) {
        super(properties, CybAbilities.OPTICS);
        this.canScan = canScan;
    }

    @Override
    public void onEquip(ItemStack stack, int slot, Level level, LivingEntity entity) {
        super.onEquip(stack, slot, level, entity);
        AbilityHelper.enableAbility(entity, CybAbilities.OPTICS.get()); //this should be enabled by default!
        if(canScan) {
            if(!AbilityHelper.hasAbility(entity, CybAbilities.SCAN.get())) {
                AbilityHelper.addAbility(entity, CybAbilities.SCAN.get());
            }
        }
    }

    @Override
    public void onUnequip(ItemStack stack, int slot, Level level, LivingEntity entity) {
        super.onUnequip(stack, slot, level, entity);
        if(canScan) {
            AbilityHelper.removeAbility(entity, CybAbilities.SCAN.get());
        }
    }


}
