package com.vertexcubed.cybernetics.common.ability;

import com.vertexcubed.cybernetics.common.registry.CybAbilities;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class AbilityType<T extends Ability> {


    private final AbilityFactory<T> factory;
    private final boolean multiEnable;
    private final int maxCooldown;
    private final int maxRuntime;
    private final ResourceLocation texture;

    public AbilityType(AbilityFactory<T> factory, boolean multiEnable, int maxCooldown, int maxRuntime, ResourceLocation texture) {
        this.factory = factory;
        this.multiEnable = multiEnable;
        this.maxCooldown = maxCooldown;
        this.maxRuntime = maxRuntime;
        this.texture = texture;
    }

    public boolean is(TagKey<AbilityType<?>> tag) {
        return holder().is(tag);
    }

    public Holder<AbilityType<?>> holder() {
        return CybAbilities.ABILITY_TYPE_REGISTRY.getHolderOrThrow(CybAbilities.ABILITY_TYPE_REGISTRY.getResourceKey(this).orElseThrow());
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

    public T createAbility() {
        return factory.create(this);
    }

    public ResourceLocation getTexture() {
        return texture;
    }


    public static class Builder<T extends Ability> {
        private final AbilityFactory<T> factory;
        private boolean multiEnable = false;
        private int maxCooldown = -1;
        private int maxRuntime = -1;
        private ResourceLocation resourceLocation = null;
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

        public Builder<T> texture(ResourceLocation rl) {
            resourceLocation = rl;
            return this;
        }

        public AbilityType<T> build() {
            return new AbilityType<T>(factory, multiEnable, maxCooldown, maxRuntime, resourceLocation);
        }
    }

    public interface AbilityFactory<T extends Ability> {
        T create(AbilityType<T> type);
    }
}
