package com.vertexcubed.cybernetics.common.ability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public abstract class Ability implements INBTSerializable<CompoundTag> {

    private final AbilityType<?> type;
    private boolean enabled;
    private int runningTime;
    private int cooldown = -1;
    public Ability(AbilityType<?> type) {
        this.runningTime = 0;
        this.type = type;
    }

    public void tick(LivingEntity entity) {
        if(enabled) {
            this.runningTime++;
            this.abilityTick(entity);
            if(type.getMaxRuntime() > -1 && this.runningTime >= type.getMaxRuntime()) {
                this.disable(entity);
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

    public AbilityType<?> getType() {
        return type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean enable(LivingEntity entity) {
        if(cooldown > -1) return false;
        if(!type.isMultiEnable() && enabled) return false;
        this.enabled = true;
        this.runningTime = 0;
        this.cooldown = type.getMaxCooldown();
        onEnable(entity);
        return true;
    }

    public boolean disable(LivingEntity entity) {
        if(!enabled) return false;
        this.enabled = false;
        onDisable(entity);
        return true;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getRunningTime() {
        return runningTime;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("enabled", enabled);
        tag.putInt("runningTime", runningTime);
        tag.putInt("cooldown", cooldown);
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        this.enabled = tag.getBoolean("enabled");
        this.runningTime = tag.getInt("runningTime");
        this.cooldown = tag.getInt("cooldown");
        loadAdditional(tag, provider);
    }
}
