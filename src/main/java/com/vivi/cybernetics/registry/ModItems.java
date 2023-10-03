package com.vivi.cybernetics.registry;

import com.vivi.cybernetics.Cybernetics;
import com.vivi.cybernetics.item.*;
import com.vivi.cybernetics.util.CyberwareHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.UUID;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Cybernetics.MOD_ID);

    public static final RegistryObject<Item>
            MK_1_OPTICS = ITEMS.register("mk1_optics", () -> new OpticsItem(cyberwareProps())),
            MK_2_OPTICS = ITEMS.register("mk2_optics", () -> new OpticsItem(cyberwareProps())),
            MK_3_OPTICS = ITEMS.register("mk3_optics", () -> new OpticsItem(cyberwareProps())),
            NIGHT_VISION_EYES = ITEMS.register("night_vision_eyes", () -> new MobEffectCyberwareItem(cyberwareProps(), new ImmutableTriple<>(MobEffects.NIGHT_VISION, 319, 0))),

            REINFORCED_SKELETON = ITEMS.register("reinforced_skeleton", () -> new AttributeCyberwareItem(cyberwareProps(), new ImmutablePair<>(Attributes.ARMOR, new AttributeModifier(UUID.fromString("1a9c9f62-d28f-48fb-8c9e-b688046f7099"), "Skeleton Armor", 2.0, AttributeModifier.Operation.ADDITION)))),
            TITANIUM_SKELETON = ITEMS.register("titanium_skeleton", () -> new AttributeCyberwareItem(cyberwareProps(),
                    new ImmutablePair<>(Attributes.ARMOR, new AttributeModifier(UUID.fromString("d4490a2f-e05e-4ddf-beea-66ec7ca6e5f1"), "Skeleton Armor", 5.0, AttributeModifier.Operation.ADDITION)),
                    new ImmutablePair<>(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(UUID.fromString("d4490a2f-e05e-4ddf-beea-66ec7ca6e5f1"), "Seketon Armor Toughness", 2.0, AttributeModifier.Operation.ADDITION))
            )),

            FIRE_RESISTANCE_SKIN = ITEMS.register("fire_resistance_skin", () -> new MobEffectCyberwareItem(cyberwareProps(), new ImmutableTriple<>(MobEffects.FIRE_RESISTANCE, 319, 0))),

            EMERGENCY_DEFIBRILLATOR = ITEMS.register("emergency_defibrillator", () -> new CyberwareItem(cyberwareProps()))
    ;


    private static Item.Properties cyberwareProps() {
        return new Item.Properties().stacksTo(1).tab(Cybernetics.TAB);
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static void cyberwareIncompatReq() {
        CyberwareHelper.addIncompatibilities(MK_1_OPTICS.get(), Ingredient.of(ModTags.OPTICS));
        CyberwareHelper.addIncompatibilities(MK_2_OPTICS.get(), Ingredient.of(ModTags.OPTICS));
        CyberwareHelper.addIncompatibilities(MK_3_OPTICS.get(), Ingredient.of(ModTags.OPTICS));

        CyberwareHelper.addIncompatibilities(REINFORCED_SKELETON.get(), Ingredient.of(ModTags.SKELETON));
        CyberwareHelper.addIncompatibilities(TITANIUM_SKELETON.get(), Ingredient.of(ModTags.SKELETON));



        CyberwareHelper.addRequirements(NIGHT_VISION_EYES.get(), Ingredient.of(ModTags.OPTICS));
    }

}
