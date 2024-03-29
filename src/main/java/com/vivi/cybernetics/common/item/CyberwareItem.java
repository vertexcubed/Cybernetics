package com.vivi.cybernetics.common.item;

import com.vivi.cybernetics.client.util.TooltipHelper;
import com.vivi.cybernetics.common.cyberware.CyberwareSectionType;
import com.vivi.cybernetics.common.registry.CybCyberware;
import com.vivi.cybernetics.common.registry.CybTags;
import com.vivi.cybernetics.common.util.CyberwareHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CyberwareItem extends Item {


    public static final int DEFAULT_CAPACITY = 2;
    private final List<Ingredient> requirements = new ArrayList<>();
    private final List<Ingredient> incompatibilities = new ArrayList<>();

    private boolean showRequirements = true;
    private boolean showIncompatibilities = true;
    private boolean showDescription = true;
    private int capacity = DEFAULT_CAPACITY;

    public CyberwareItem(Properties pProperties) {
        super(pProperties);
    }

    public CyberwareItem addRequirements(Ingredient... requirements) {
        this.requirements.addAll(Arrays.asList(requirements));
        return this;
    }
    public CyberwareItem addIncompatibilities(Ingredient... incompatibilities) {
        this.incompatibilities.addAll(Arrays.asList(incompatibilities));
        return this;
    }

    public List<Ingredient> getRequirements() {
        return requirements;
    }

    public List<Ingredient> getIncompatibilities() {
        return incompatibilities;
    }

    public CyberwareItem hideRequirements() {
        showRequirements = false;
        return this;
    }
    public CyberwareItem hideIncompatibilities() {
        showIncompatibilities = false;
        return this;
    }
    public CyberwareItem hideDescription() {
        showDescription = false;
        return this;
    }

    public CyberwareItem setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public int getCapacity() {
        return capacity;
    }

    public void cyberwareTick(ItemStack stack, Level level, Player player) {

    }

    public void onEquip(ItemStack stack, Level level, Player player) {

    }

    public void onUnequip(ItemStack stack, Level level, Player player) {

    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(this);

        addSectionTooltip(stack, tooltip);
        MutableComponent capacityTooltip = Component.translatable("tooltip.cybernetics.capacity").append(": ").append(Component.literal(String.valueOf(capacity))).withStyle(ChatFormatting.GOLD);
        tooltip.add(capacityTooltip);
//        if(showRequirements && requirements.size() > 0) {
//            MutableComponent requirementsTooltip = Component.translatable("tooltip.cybernetics.requirements").append(": ").withStyle(ChatFormatting.GRAY)
//                    .append(Component.translatable("tooltip." + id.getNamespace() + "." + id.getPath() + ".requirements").withStyle(ChatFormatting.AQUA));
//            tooltip.add(requirementsTooltip);
//        }
//        if(showIncompatibilities && incompatibilities.size() > 1) {
//            MutableComponent incompatibilitiesTooltip = Component.translatable("tooltip.cybernetics.incompatibilities").append(": ").withStyle(ChatFormatting.GRAY)
//                    .append(Component.translatable("tooltip." + id.getNamespace() + "." + id.getPath() + ".incompatibilities").withStyle(ChatFormatting.RED));
//            tooltip.add(incompatibilitiesTooltip);
//        }
        if(showDescription) {
            if(Screen.hasShiftDown()) {
                tooltip.addAll(TooltipHelper.processTooltip(Component.translatable("tooltip." + id.getNamespace() + "." + id.getPath() + ".description"), ChatFormatting.GRAY, ChatFormatting.RED, 40));
            }
            else {
                tooltip.addAll(TooltipHelper.processTooltip(Component.translatable("tooltip.cybernetics.description.shift"), ChatFormatting.GRAY, ChatFormatting.RED, 40));
            }
        }

        if((requirements.size() > 0 && showRequirements) || (incompatibilities.size() > 0 && showIncompatibilities)) {
            if(Screen.hasAltDown()) {
                if(showRequirements && requirements.size() > 0) {
                    tooltip.add(Component.translatable("tooltip.cybernetics.requirements").append(": ").withStyle(ChatFormatting.GRAY));
                    addIngredientTooltip(stack, requirements, tooltip, ChatFormatting.AQUA);
                }
                if(showIncompatibilities && incompatibilities.size() > 0) {
                    tooltip.add(Component.translatable("tooltip.cybernetics.incompatibilities").append(": ").withStyle(ChatFormatting.GRAY));
                    addIngredientTooltip(stack, incompatibilities, tooltip, ChatFormatting.RED);
                }

            }
            else {
                tooltip.addAll(TooltipHelper.processTooltip(Component.translatable("tooltip.cybernetics.description.alt"), ChatFormatting.GRAY, ChatFormatting.RED, 40));
            }
        }
    }

    private void addIngredientTooltip(ItemStack stack, List<Ingredient> ingredients, List<Component> tooltip, ChatFormatting color) {

        ingredients.forEach(ingredient -> {
            ItemStack[] stacks = ingredient.getItems();
            Component text = stacks.length == 1 ? stacks[0].getHoverName() : TooltipHelper.getDisplayNameList(stacks);
            tooltip.add(Component.literal("    ").append(text.copy().withStyle(color)));
        });
    }




    private void addSectionTooltip(ItemStack stack, List<Component> pTooltipComponents) {

        if(!stack.isEmpty() && stack.getItem() instanceof CyberwareItem) {

            MutableComponent sectionTooltip = Component.translatable("tooltip.cybernetics.section").append(": ").withStyle(ChatFormatting.GRAY);

            if(stack.is(CybTags.ANY_SECTION)) {
                sectionTooltip.append(Component.translatable("tooltip.cybernetics.section.any"));
            }
            else {
                List<CyberwareSectionType> sections = CyberwareHelper.getValidCyberwareSections(stack);
                for (int i = 0; i < sections.size(); i++) {
                    ResourceLocation id = CybCyberware.CYBERWARE_SECTION_TYPE_REGISTRY.get().getKey(sections.get(i));
                    sectionTooltip.append(Component.translatable("tooltip." + id.getNamespace() + ".section." + id.getPath()).withStyle(ChatFormatting.RED));
                    if(i < sections.size() - 1) sectionTooltip.append(", ").withStyle(ChatFormatting.GRAY);
                }
            }

            pTooltipComponents.add(sectionTooltip);
        }
    }


}