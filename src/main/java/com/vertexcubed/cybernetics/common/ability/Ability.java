package com.vertexcubed.cybernetics.common.ability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;

public abstract class Ability implements INBTSerializable<CompoundTag> {

    private final AbilityType<?> type;
    private final LivingEntity parent;
    private boolean enabled;
    private int runningTime;
    private int cooldown;
    public Ability(AbilityType<?> type, LivingEntity parent) {
        this.runningTime = 0;
        this.type = type;
        this.parent = parent;
    }

    public void tick() {
        if(enabled) {
            this.runningTime++;
            this.abilityTick(parent);
            if(type.getMaxRuntime() > -1 && this.runningTime >= type.getMaxRuntime()) {
                this.disable();
                return;
            }
        }
        else {
            if(cooldown > -1) {
                cooldown--;
            }
        }
    }

    public abstract void onEnable(LivingEntity parent);
    public abstract void abilityTick(LivingEntity parent);

    public abstract void onDisable(LivingEntity parent);



    public AbilityType<?> getType() {
        return type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean enable() {
        if(cooldown > -1) return false;
        if(!type.isMultiEnable() && enabled) return false;
        this.enabled = true;
        this.runningTime = 0;
        this.cooldown = type.getMaxCooldown();
        onEnable(parent);
        return true;
    }

    public boolean disable() {
        if(!enabled) return false;
        this.enabled = false;
        onDisable(parent);
        return true;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
//        tag.putString("type", CybAbilities.ABILITY_TYPE_REGISTRY.getKey(type).toString());
        tag.putBoolean("enabled", enabled);
        tag.putInt("runningTime", runningTime);
        tag.putInt("cooldown", cooldown);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.enabled = tag.getBoolean("enabled");
        this.runningTime = tag.getInt("runningTime");
        this.cooldown = tag.getInt("cooldown");
    }
}
