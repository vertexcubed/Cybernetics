package com.vertexcubed.cybernetics.common.ability;

import net.minecraft.world.entity.LivingEntity;

public class AbilityType<T extends Ability> {


    private final AbilityFactory<T> factory;
    private final boolean multiEnable;
    private final int maxCooldown;
    private final int maxRuntime;
    public AbilityType(AbilityFactory<T> factory, boolean multiEnable, int maxCooldown, int maxRuntime) {
        this.factory = factory;
        this.multiEnable = multiEnable;
        this.maxCooldown = maxCooldown;
        this.maxRuntime = maxRuntime;
    }

    public int getMaxCooldown() {
        return maxCooldown;
    }

    public boolean isMultiEnable() {
        return multiEnable;
    }

    public int getMaxRuntime() {
        return maxRuntime;
    }

    public T createAbility(LivingEntity parent) {
        return factory.create(this, parent);
    }


    public static class Builder<T extends Ability> {
        private final AbilityFactory<T> factory;
        private boolean multiEnable = false;
        private int maxCooldown = 0;
        private int maxRuntime = 0;
        public Builder(AbilityFactory<T> factory) {
            this.factory = factory;
        }

        public Builder<T> multiEnable() {
            multiEnable = true;
            return this;
        }

        public Builder<T> maxCooldown(int cd) {
            maxCooldown = cd;
            return this;
        }

        public Builder<T> maxRuntime(int rt) {
            maxRuntime = rt;
            return this;
        }

        public AbilityType<T> build() {
            return new AbilityType<T>(factory, multiEnable, maxCooldown, maxRuntime);
        }
    }

    public interface AbilityFactory<T extends Ability> {
        T create(AbilityType<T> type, LivingEntity parent);
    }
}
