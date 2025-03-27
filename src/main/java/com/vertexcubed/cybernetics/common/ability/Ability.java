package com.vertexcubed.cybernetics.common.ability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;

public abstract class Ability implements INBTSerializable<CompoundTag> {

    private final AbilityType<?> type;
    private LivingEntity parent;
    private boolean enabled;
    private int runningTime;
    private int cooldown = -1;
    public Ability(AbilityType<?> type) {
        this.runningTime = 0;
        this.type = type;
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

    public abstract void saveAdditional(CompoundTag tag, HolderLookup.Provider provider);
    public abstract void loadAdditional(CompoundTag tag, HolderLookup.Provider provider);

    //TODO: this is extremely hacky. Figure something out!!!
    public Ability setParent(LivingEntity parent) {
        this.parent = parent;
        return this;
    }


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

    public int getCooldown() {
        return cooldown;
    }

    public int getRunningTime() {
        return runningTime;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
//        tag.putString("type", CybAbilities.ABILITY_TYPE_REGISTRY.getKey(type).toString());
        tag.putBoolean("enabled", enabled);
        tag.putInt("runningTime", runningTime);
        tag.putInt("cooldown", cooldown);
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.enabled = tag.getBoolean("enabled");
        this.runningTime = tag.getInt("runningTime");
        this.cooldown = tag.getInt("cooldown");
        loadAdditional(tag, provider);
    }
}
