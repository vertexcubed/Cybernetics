package com.vertexcubed.cybernetics.common.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;

public abstract class CyberwareEvent extends Event {
    private final ItemStack stack;
    private final int slot;
    private final Level level;
    private final Entity entity;
    public CyberwareEvent(ItemStack stack, int slot, Level level, Entity entity) {
        this.stack = stack;
        this.slot = slot;
        this.level = level;
        this.entity = entity;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getSlot() {
        return slot;
    }

    public Level getLevel() {
        return level;
    }

    public Entity getEntity() {
        return entity;
    }

    public static class Equip extends CyberwareEvent {

        public Equip(ItemStack stack, int slot, Level level, Entity entity) {
            super(stack, slot, level, entity);
        }
    }

    public static class Unequip extends CyberwareEvent {
        public Unequip(ItemStack stack, int slot, Level level, Entity entity) {
            super(stack, slot, level, entity);
        }
    }

    public static class Tick extends CyberwareEvent {
        public Tick(ItemStack stack, int slot, Level level, Entity entity) {
            super(stack, slot, level, entity);
        }
    }
}
