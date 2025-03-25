package com.vertexcubed.cybernetics.common.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class CybTags {

    public static final TagKey<Item>
            OPTICS = itemTag("optics"),
            BERSERKS = itemTag("berserks"),
            SKELETONS = itemTag("skeletons"),
            DOUBLE_JUMP_ADDERS = itemTag("double_jump_adders"),
            DASH_ITEMS = itemTag("dash_items"),


            ANY_SECTION = itemTag("cyberware_section/any"),
            HEAD_SECTION = itemTag("cyberware_section/head"),
            EYES_SECTION = itemTag("cyberware_section/eyes"),
            UPPER_ORGANS_SECTION = itemTag("cyberware_section/upper_organs"),
            LOWER_ORGANS_SECTION = itemTag("cyberware_section/lower_organs"),
            SKELETON_SECTION = itemTag("cyberware_section/skeleton"),
            SKIN_SECTION = itemTag("cyberware_section/skin"),
            HANDS_SECTION = itemTag("cyberware_section/hands"),
            ARMS_SECTION = itemTag("cyberware_section/arms"),
            LEGS_SECTION = itemTag("cyberware_section/legs"),
            FEET_SECTION = itemTag("cyberware_section/feet")
                    ;


    private static TagKey<Item> itemTag(String name) {
        return ItemTags.create(modLoc(name));
    }
    private static TagKey<Item> forgeTag(String name) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
    }
}
