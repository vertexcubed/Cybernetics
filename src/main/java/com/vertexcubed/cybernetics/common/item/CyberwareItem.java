package com.vertexcubed.cybernetics.common.item;

import com.vertexcubed.cybernetics.client.util.TooltipHelper;
import com.vertexcubed.cybernetics.common.registry.CybDataComponents;
import com.vertexcubed.cybernetics.common.registry.CybTags;
import com.vertexcubed.cybernetics.common.storage.CyberwareSectionType;
import com.vertexcubed.cybernetics.common.util.CyberwareHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;
import java.util.Map;

public class CyberwareItem extends Item {
    public CyberwareItem(Properties properties) {
        super(properties);
    }


    /**
     * Called every tick when a cyberware item is in an active cyberware inventory.
     */
    public void cyberwareTick(ItemStack stack, int slot, Level level, LivingEntity entity) {

    }

    /**
     * Called When a cyberware item is equipped.
     */
    public void onEquip(ItemStack stack, int slot, Level level, LivingEntity entity) {

    }

    /**
     * Called When a cyberware item is unequipped.
     */
    public void onUnequip(ItemStack stack, int slot, Level level, LivingEntity entity) {

    }





    public static void addTooltip(ItemTooltipEvent event) {
        CyberwareProperties properties = event.getItemStack().get(CybDataComponents.CYBERWARE_PROPERTIES);
        List<Component> tooltip = event.getToolTip();
        ItemStack stack = event.getItemStack();
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());

        tooltip.add(Component.translatable("tooltip.cybernetics.capacity").append(": ").append(Component.literal(String.valueOf(properties.capacity()))).withStyle(ChatFormatting.GOLD));

        if(properties.allowDuplicates()) {
            tooltip.add(Component.translatable("tooltip.cybernetics.allows_duplicates").withStyle(ChatFormatting.RED));
        }
        if(properties.description().showDescription()) {
            if(Screen.hasShiftDown()) {
                tooltip.addAll(TooltipHelper.processTooltip(Component.translatable("tooltip." + id.getNamespace() + "." + id.getPath() + ".description"), ChatFormatting.GRAY, ChatFormatting.RED, 40));
            }
            else {
                tooltip.addAll(TooltipHelper.processTooltip(Component.translatable("tooltip.cybernetics.description.shift"), ChatFormatting.GRAY, ChatFormatting.RED, 40));
            }
        }

        if((!properties.requirements().isEmpty() && properties.description().showRequirements()) || (!properties.incompatibilities().isEmpty() && properties.description().showIncompatibilities())) {
            if(Screen.hasAltDown()) {
                if(properties.description().showRequirements() && !properties.requirements().isEmpty()) {
                    tooltip.add(Component.translatable("tooltip.cybernetics.requirements").append(": ").withStyle(ChatFormatting.GRAY));
                    addIngredientTooltip(stack, properties.requirements(), tooltip, ChatFormatting.AQUA);
                }
                if(properties.description().showIncompatibilities() && !properties.incompatibilities().isEmpty()) {
                    tooltip.add(Component.translatable("tooltip.cybernetics.incompatibilities").append(": ").withStyle(ChatFormatting.GRAY));
                    addIngredientTooltip(stack, properties.incompatibilities(), tooltip, ChatFormatting.RED);
                }

            }
            else {
                tooltip.addAll(TooltipHelper.processTooltip(Component.translatable("tooltip.cybernetics.description.alt"), ChatFormatting.GRAY, ChatFormatting.RED, 40));
            }
        }
    }

    private static void addIngredientTooltip(ItemStack stack, List<Ingredient> ingredients, List<Component> tooltip, ChatFormatting color) {

        ingredients.forEach(ingredient -> {
            ItemStack[] stacks = ingredient.getItems();
            Component text = stacks.length == 1 ? stacks[0].getHoverName() : TooltipHelper.getDisplayNameList(stacks);
            tooltip.add(Component.literal("    ").append(text.copy().withStyle(color)));
        });
    }

    public static void addSectionTooltip(ItemTooltipEvent event) {
        MutableComponent sectionTooltip = Component.translatable("tooltip.cybernetics.section").append(": ").withStyle(ChatFormatting.GRAY);
        ItemStack stack = event.getItemStack();

        boolean addTooltip = false;

        if(stack.is(CybTags.ANY_SECTION)) {
            sectionTooltip.append(Component.translatable("tooltip.cybernetics.section.any"));
            addTooltip = true;
        }
        else {
            RegistryAccess registryAccess = event.getEntity().registryAccess();
            List<Map.Entry<ResourceKey<CyberwareSectionType>, CyberwareSectionType>> sections = CyberwareHelper.getValidSections(stack, registryAccess);

            for(int i = 0; i < sections.size(); i++) {
                ResourceLocation id = sections.get(i).getKey().location();
                sectionTooltip.append(Component.translatable("tooltip." + id.getNamespace() + ".section." + id.getPath()).withStyle(ChatFormatting.RED));
                if(i < sections.size() - 1) sectionTooltip.append(", ").withStyle(ChatFormatting.GRAY);
                addTooltip = true;
            }

        }

        if(addTooltip) event.getToolTip().add(sectionTooltip);

    }
}
