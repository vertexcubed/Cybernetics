package com.vertexcubed.cybernetics.common.item;

import com.vertexcubed.cybernetics.common.util.Triple;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MobEffectCyberwareItem extends CyberwareItem {

    private final Triple<Holder<MobEffect>, Integer, Integer>[] effects;

    //effect, duration, amplifier
    @SafeVarargs
    public MobEffectCyberwareItem(Properties pProperties, Triple<Holder<MobEffect>, Integer, Integer>... effects) {
        super(pProperties);
        this.effects = effects;
    }

    @Override
    public void cyberwareTick(ItemStack stack, int slot, Level level, LivingEntity entity) {
        if(level.isClientSide) return;
        for (Triple<Holder<MobEffect>, Integer, Integer> effect : effects) {
            entity.addEffect(new MobEffectInstance(effect.first(), effect.second(), effect.third(), false, false, true));
        }
    }

    @Override
    public void onUnequip(ItemStack stack, int slot, Level level, LivingEntity entity) {
        if(level.isClientSide) return;
        //if entity has higher level effect do not remove
        for (Triple<Holder<MobEffect>, Integer, Integer> effect : effects) {
            if (entity.hasEffect(effect.first()) && entity.getEffect(effect.first()).getAmplifier() == effect.third()) {
                entity.removeEffect(effect.first());
            }
        }

    }
}
