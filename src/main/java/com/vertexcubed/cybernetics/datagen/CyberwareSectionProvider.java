package com.vertexcubed.cybernetics.datagen;

import com.vertexcubed.cybernetics.common.registry.CybDPRegistries;
import com.vertexcubed.cybernetics.common.registry.CybTags;
import com.vertexcubed.cybernetics.common.storage.CyberwareSection;
import com.vertexcubed.cybernetics.common.storage.CyberwareSectionType;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class CyberwareSectionProvider {

    public static ResourceKey<CyberwareSectionType> createKey(String path) {
        return ResourceKey.create(CybDPRegistries.CYBERWARE_SECTION_KEY, modLoc(path));
    }

    public static void register(BootstrapContext<CyberwareSectionType> bootstrap) {
        bootstrap.register(createKey("head"), CyberwareSectionType.fromRaw(
                modLoc("textures/gui/cyberware/section/head.png"),
                59,
                24,
                130,
                20,
                CybTags.HEAD_SECTION,
                5
        ));

        bootstrap.register(createKey("upper_organs"), CyberwareSectionType.fromRaw(
                modLoc("textures/gui/cyberware/section/upper_organs.png"),
                37,
                46,
                130,
                -30,
                CybTags.UPPER_ORGANS_SECTION,
                7
        ));

        bootstrap.register(createKey("skeleton"), CyberwareSectionType.fromRaw(
                modLoc("textures/gui/cyberware/section/skeleton.png"),
                59,
                68,
                130,
                -30,
                CybTags.SKELETON_SECTION,
                4
        ));

        bootstrap.register(createKey("lower_organs"), CyberwareSectionType.fromRaw(
                modLoc("textures/gui/cyberware/section/lower_organs.png"),
                37,
                90,
                130,
                -70,
                CybTags.LOWER_ORGANS_SECTION,
                6
        ));

        bootstrap.register(createKey("legs"), CyberwareSectionType.fromRaw(
                modLoc("textures/gui/cyberware/section/legs.png"),
                59,
                112,
                130,
                -130,
                CybTags.LEGS_SECTION,
                3
        ));



        bootstrap.register(createKey("eyes"), CyberwareSectionType.fromRaw(
                modLoc("textures/gui/cyberware/section/eyes.png"),
                160,
                24,
                130,
                20,
                CybTags.HEAD_SECTION,
                4
        ));

        bootstrap.register(createKey("arms"), CyberwareSectionType.fromRaw(
                modLoc("textures/gui/cyberware/section/arms.png"),
                182,
                46,
                130,
                -40,
                CybTags.HEAD_SECTION,
                4
        ));

        bootstrap.register(createKey("skin"), CyberwareSectionType.fromRaw(
                modLoc("textures/gui/cyberware/section/skin.png"),
                160,
                68,
                130,
                -40,
                CybTags.HEAD_SECTION,
                3
        ));

        bootstrap.register(createKey("hands"), CyberwareSectionType.fromRaw(
                modLoc("textures/gui/cyberware/section/hands.png"),
                182,
                90,
                130,
                -70,
                CybTags.HEAD_SECTION,
                3
        ));

        bootstrap.register(createKey("feet"), CyberwareSectionType.fromRaw(
                modLoc("textures/gui/cyberware/section/feet.png"),
                160,
                112,
                130,
                -140,
                CybTags.HEAD_SECTION,
                4
        ));

    }
}
