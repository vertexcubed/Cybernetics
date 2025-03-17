package com.vertexcubed.cybernetics.common.storage;

import com.vertexcubed.cybernetics.common.registry.CybTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class CyberwareSection extends ItemStackHandler {

    private final CyberwareSectionType type;
    private final ResourceLocation id;
    public CyberwareSection(CyberwareSectionType type, ResourceLocation id) {
        super(type.size());
        this.type = type;
        this.id = id;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return (stack.is(type.tag()) || stack.is(CybTags.ANY_SECTION)) && super.isItemValid(slot, stack);
    }

    public ResourceLocation getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CyberwareSection that = (CyberwareSection) o;

        if (!id.equals(that.id)) return false;
        return type.equals(that.type);
    }
}
