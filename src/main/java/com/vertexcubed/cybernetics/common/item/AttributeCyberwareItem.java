package com.vertexcubed.cybernetics.common.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AttributeCyberwareItem extends CyberwareItem {

    private final Pair<Holder<Attribute>, AttributeModifier>[] attributes;

    @SafeVarargs
    public AttributeCyberwareItem(Properties pProperties, Pair<Holder<Attribute>, AttributeModifier>... attributes) {
        super(pProperties);
        this.attributes = attributes;
    }

    @Override
    public void cyberwareTick(ItemStack stack, int slot, Level level, LivingEntity entity) {
        if(level.isClientSide) return;
        for (Pair<Holder<Attribute>, AttributeModifier> attribute : attributes) {
            AttributeInstance entityAttribute = entity.getAttribute(attribute.getFirst());
            //add if player dead as well
            if (entityAttribute != null && !entityAttribute.hasModifier(attribute.getSecond().id())) {
                entityAttribute.addPermanentModifier(attribute.getSecond());
            }
        }
    }

    @Override
    public void onUnequip(ItemStack stack, int slot, Level level, LivingEntity entity) {
        for (Pair<Holder<Attribute>, AttributeModifier> attribute : attributes) {
            AttributeInstance entityAttribute = entity.getAttribute(attribute.getFirst());
            if (entityAttribute != null && entityAttribute.hasModifier(attribute.getSecond().id())) {
                entityAttribute.removeModifier(attribute.getSecond());
            }
        }
    }

    public Pair<Holder<Attribute>, AttributeModifier>[] getAttributes() {
        return attributes;
    }
}
