package com.vertexcubed.cybernetics.common.item;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class StomachFilterItem extends CyberwareItem {

    private static final List<Holder<MobEffect>> BLACKLIST_EFFECTS = List.of(
            MobEffects.POISON,
            MobEffects.HUNGER,
            MobEffects.CONFUSION
    );



    public StomachFilterItem(Properties properties) {
        super(properties);
    }


    @Override
    public void cyberwareTick(ItemStack stack, int slot, Level level, LivingEntity entity) {
        BLACKLIST_EFFECTS.forEach(effect -> {
            if(entity.hasEffect(effect)) {
                entity.removeEffect(effect);
            }
        });
    }
}
