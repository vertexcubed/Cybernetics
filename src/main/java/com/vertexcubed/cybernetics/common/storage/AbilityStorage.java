package com.vertexcubed.cybernetics.common.storage;

import com.vertexcubed.cybernetics.common.ability.Ability;
import com.vertexcubed.cybernetics.common.ability.AbilityType;
import com.vertexcubed.cybernetics.common.registry.CybAbilities;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class AbilityStorage implements INBTSerializable<CompoundTag> {

    private LivingEntity parent;
    private final List<Ability> abilities = new ArrayList<>();

    private CompoundTag tag;
    public AbilityStorage() {

    }

    public void init(LivingEntity parent) {
        this.parent = parent;
        abilities.forEach(ability -> ability.setParent(parent));
    }

    public void copyFrom(AbilityStorage other, LivingEntity parent) {
        this.abilities.clear();
        this.abilities.addAll(other.abilities);
        this.parent = parent;
        this.abilities.forEach(ability -> ability.setParent(parent));
    }

    public void tick() {
        abilities.forEach(Ability::tick);
    }


    public LivingEntity getParent() {
        return parent;
    }

    public void add(Ability ability) {
        this.abilities.add(ability);
    }

    public boolean remove(Ability ability) {
        return this.abilities.remove(ability);
    }

    public List<Ability> getAbilities() {
        return List.copyOf(abilities);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (Ability ability : abilities) {
            CompoundTag sub = ability.serializeNBT(provider);
            sub.putString("type", CybAbilities.ABILITY_TYPE_REGISTRY.getKey(ability.getType()).toString());
            list.add(sub);
        }
        tag.put("abilities", list);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        ListTag list = tag.getList("abilities", ListTag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag sub = list.getCompound(i);
            AbilityType<?> type = CybAbilities.ABILITY_TYPE_REGISTRY.getOptional(ResourceLocation.parse(sub.getString("type"))).orElseThrow();

            Ability ability = type.createAbility();
            ability.deserializeNBT(provider, sub);
            abilities.add(ability);
        }
    }
}
