package com.vertexcubed.cybernetics.common.registry;

import com.mojang.datafixers.util.Pair;
import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.item.*;
import com.vertexcubed.cybernetics.common.item.CyberwareProperties.Builder;
import com.vertexcubed.cybernetics.common.util.Triple;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class CybItems {


    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Cybernetics.MOD_ID);

    public static final Map<String, Supplier<Builder>> propMap = new HashMap<>();

    public static final DeferredHolder<Item, CyberwareItem>
        //HEAD
        OVERCAPACITY_HEAD = ITEMS.register("overcapacity_head", () -> new CyberwareItem(cyberwareProps("overcapacity_head"))),
        CARBON_FIBER_SKULL = ITEMS.register("carbon_fiber_skull", () -> new AttributeCyberwareItem(cyberwareProps("carbon_fiber_skull"), Pair.of(Attributes.ARMOR, new AttributeModifier(modLoc("carbon_fiber_skull"), 3.0, AttributeModifier.Operation.ADD_VALUE)))),
//        MK1_BERSERK = ITEMS.register("mk1_berserk", () -> new SimpleAbilityCyberwareItem(cyberwareProps("mk1_berserk"), CybAbilities.MK1_BERSERK)),
//        MK2_BERSERK = ITEMS.register("mk2_berserk", () -> new SimpleAbilityCyberwareItem(cyberwareProps("mk2_berserk"), CybAbilities.MK2_BERSERK)),
//        MK3_BERSERK = ITEMS.register("mk3_berserk", () -> new SimpleAbilityCyberwareItem(cyberwareProps("mk3_berserk"), CybAbilities.MK3_BERSERK)),
//        MK1_DASH = ITEMS.register("mk1_dash", () -> new DashCyberwareItem(cyberwareProps("mk1_dash"), CybAbilities.MK1_DASH)),
//        MK2_DASH = ITEMS.register("mk2_dash", () -> new DashCyberwareItem(cyberwareProps("mk2_dash"), CybAbilities.MK2_DASH)),


        //EYES
        MK1_OPTICS = ITEMS.register("mk1_optics", () -> new OpticsItem(cyberwareProps("mk1_optics"))),
//        MK2_OPTICS = ITEMS.register("mk2_optics", () -> new OpticsItem(cyberwareProps("mk2_optics"), true)),
//        MK3_OPTICS = ITEMS.register("mk3_optics", () -> new OpticsItem(cyberwareProps("mk3_optics"), true)),
        NIGHT_VISION_EYES = ITEMS.register("night_vision_eyes", () -> new SimpleAbilityCyberwareItem<>(cyberwareProps("night_vision_eyes"), CybAbilities.NIGHT_VISION)),

        //UPPER ORGANS
//        EMERGENCY_DEFIBRILLATOR = ITEMS.register("emergency_defibrillator", () -> new SimpleAbilityCyberwareItem(cyberwareProps("emergency_defibrillator"), CybAbilities.EMERGENCY_DEFIBRILLATOR)),
        OXYGEN_RECYCLER = ITEMS.register("oxygen_recycler", () -> new CyberwareItem(cyberwareProps("oxygen_recycler"))),
        HEALTH_BOOST_ORGANS = ITEMS.register("health_boost_organs", () -> new AttributeCyberwareItem(cyberwareProps("health_boost_organs"), Pair.of(Attributes.MAX_HEALTH, new AttributeModifier(modLoc("health_boost_organs"), 5, AttributeModifier.Operation.ADD_VALUE)))),

        //LOWER ORGANS
        STOMACH_FILTER = ITEMS.register("stomach_filter", () -> new CyberwareItem(cyberwareProps("stomach_filter"))),
//        SYNAPTIC_DISABLER = ITEMS.register("synaptic_disabler", () -> new SimpleAbilityCyberwareItem(cyberwareProps("synaptic_disabler"), CybAbilities.SYNAPTIC_DISABLER)),
//        MK1_DOUBLE_JUMP_ADDER = ITEMS.register("mk1_double_jump_adder", () -> new AttributeCyberwareItem(cyberwareProps("mk1_double_jump_adder"), Pair.of(CybAttributes.DOUBLE_JUMPS.get(), new AttributeModifier(UUID.fromString("053bf72d-a9e1-4e3f-8373-e2491155f9f5"), "Lower Organs Double Jump Boost", 1.0, AttributeModifier.Operation.ADD_VALUE)))),
//        MK2_DOUBLE_JUMP_ADDER = ITEMS.register("mk2_double_jump_adder", () -> new AttributeCyberwareItem(cyberwareProps("mk2_double_jump_adder"), Pair.of(CybAttributes.DOUBLE_JUMPS.get(), new AttributeModifier(UUID.fromString("a819279b-894b-4c33-bb05-74d5751859f6"), "Lower Organs Double Jump Boost", 2.0, AttributeModifier.Operation.ADD_VALUE)))),

        //SKELETON
        REINFORCED_SKELETON = ITEMS.register("reinforced_skeleton", () -> new AttributeCyberwareItem(cyberwareProps("reinforced_skeleton"), Pair.of(Attributes.ARMOR, new AttributeModifier(modLoc("reinforced_skeleton"), 2.0, AttributeModifier.Operation.ADD_VALUE)))),
        TITANIUM_SKELETON = ITEMS.register("titanium_skeleton", () -> new AttributeCyberwareItem(cyberwareProps("titanium_skeleton"),
                        Pair.of(Attributes.ARMOR, new AttributeModifier(modLoc("titanium_skeleton"), 5.0, AttributeModifier.Operation.ADD_VALUE)),
                Pair.of(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(modLoc("titanium_skeleton"), 2.0, AttributeModifier.Operation.ADD_VALUE))
                )),
        HEALTH_BOOST_SKELETON = ITEMS.register("health_boost_skeleton", () -> new AttributeCyberwareItem(cyberwareProps("health_boost_skeleton"), Pair.of(Attributes.MAX_HEALTH, new AttributeModifier(modLoc("health_boost_skeleton"), 5, AttributeModifier.Operation.ADD_VALUE)))),
//        KINETIC_DISCHARGER = ITEMS.register("kinetic_discharger", () -> new KineticDischargerItem(cyberwareProps("kinetic_discharger"))),

        //SKIN
        FIRE_RESISTANCE_SKIN = ITEMS.register("fire_resistance_skin", () -> new MobEffectCyberwareItem(cyberwareProps("fire_resistance_skin"), Triple.of(MobEffects.FIRE_RESISTANCE, -1, 0))),
        PROJECTILE_DEFLECTOR = ITEMS.register("projectile_deflector", () -> new CyberwareItem(cyberwareProps("projectile_deflector"))),

        //ARMS
        RANGE_EXTENDER = ITEMS.register("range_extender", () -> new AttributeCyberwareItem(cyberwareProps("range_extender"), Pair.of(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(modLoc("range_extender"), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))),


        //HANDS
        STRENGTH_HANDS = ITEMS.register("strength_hands", () -> new AttributeCyberwareItem(cyberwareProps("strength_hands"), Pair.of(Attributes.ATTACK_DAMAGE, new AttributeModifier(modLoc("strength_hands"), 3.0, AttributeModifier.Operation.ADD_VALUE)))),
        STONE_MINING_FISTS = ITEMS.register("stone_mining_fists", () -> new CyberwareItem(cyberwareProps("stone_mining_fists"))),


        //LEGS
//        REINFORCED_TENDONS = ITEMS.register("reinforced_tendons", () -> new ReinforcedTendonsItem(cyberwareProps("reinforced_tendons"))),
        SPEED_LEGS = ITEMS.register("speed_legs", () -> new MobEffectCyberwareItem(cyberwareProps("speed_legs"), Triple.of(MobEffects.MOVEMENT_SPEED, -1, 1))),

        //FEET
        BIONIC_FEET = ITEMS.register("bionic_feet", () -> new AttributeCyberwareItem(cyberwareProps("bionic_feet"), Pair.of(Attributes.ARMOR, new AttributeModifier(modLoc("bionic_feet"), 2.0, AttributeModifier.Operation.ADD_VALUE)))),
        SOUND_ABSORBENT_FEET = ITEMS.register("sound_absorbent_feet", () -> new CyberwareItem(cyberwareProps("sound_absorbent_feet"))),
        JUMP_BOOST_FEET = ITEMS.register("jump_boost_feet", () -> new MobEffectCyberwareItem(cyberwareProps("jump_boost_feet"), Triple.of(MobEffects.JUMP, -1, 2))),
        FULL_SPEED_FEET = ITEMS.register("full_speed_feet", () -> new CyberwareItem(cyberwareProps("full_speed_feet")))
    ;


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static Item.Properties cyberwareProps(String name) {
        return new Item.Properties().stacksTo(1).component(CybDataComponents.CYBERWARE_PROPERTIES, propMap.get(name).get().build());
    }

    private static void addToMap(String name, Supplier<Builder> builder) {
        propMap.put(name, builder);
    }


    static {
        addToMap("overcapacity_head", () -> new Builder().setCapacity(3));
        addToMap("carbon_fiber_skull", () -> new Builder().setCapacity(3));
        addToMap("mk1_berserk", () -> new Builder().setCapacity(9).addIncompatibilities(CybTags.BERSERKS).showIncompatibilities(false));
        addToMap("mk2_berserk", () -> new Builder().setCapacity(9).addIncompatibilities(CybTags.BERSERKS).showIncompatibilities(false));
        addToMap("mk3_berserk", () -> new Builder().setCapacity(9).addIncompatibilities(CybTags.BERSERKS).showIncompatibilities(false));
        addToMap("mk1_dash", () -> new Builder().setCapacity(6).addIncompatibilities(CybTags.DASH_ITEMS).showIncompatibilities(false));
        addToMap("mk2_dash", () -> new Builder().setCapacity(6).addIncompatibilities(CybTags.DASH_ITEMS).showIncompatibilities(false));


        addToMap("mk1_optics", () -> new Builder().setCapacity(5).addIncompatibilities(CybTags.OPTICS).showIncompatibilities(false));
        addToMap("mk2_optics", () -> new Builder().setCapacity(5).addIncompatibilities(CybTags.OPTICS).showIncompatibilities(false));
        addToMap("mk3_optics", () -> new Builder().setCapacity(5).addIncompatibilities(CybTags.OPTICS).showIncompatibilities(false));
        addToMap("night_vision_eyes", () -> new Builder().setCapacity(3).addRequirements(CybTags.OPTICS));


        addToMap("emergency_defibrillator", () -> new Builder().setCapacity(11));
        addToMap("oxygen_recycler", () -> new Builder().setCapacity(5));


        addToMap("health_boost_organs", () -> new Builder().setCapacity(3));
        addToMap("stomach_filter", () -> new Builder().setCapacity(5));
        addToMap("synaptic_disabler", () -> new Builder().setCapacity(7));
//        addToMap("mk1_double_jump_adder", () -> new Builder().setCapacity(5));
//        addToMap("mk2_double_jump_adder", () -> new Builder().setCapacity(5));


        addToMap("reinforced_skeleton", () -> new Builder().setCapacity(7).addIncompatibilities(CybTags.SKELETONS).showIncompatibilities(false));
        addToMap("titanium_skeleton", () -> new Builder().setCapacity(7).addIncompatibilities(CybTags.SKELETONS).showIncompatibilities(false));
        addToMap("health_boost_skeleton", () -> new Builder().setCapacity(6));
        addToMap("kinetic_discharger", () -> new Builder().setCapacity(8));


        addToMap("fire_resistance_skin", () -> new Builder().setCapacity(5));
        addToMap("projectile_deflector", () -> new Builder().setCapacity(4));


        addToMap("range_extender", () -> new Builder().setCapacity(4));


        addToMap("strength_hands", () -> new Builder().setCapacity(4));
        addToMap("stone_mining_fists", () -> new Builder().setCapacity(3));


        addToMap("reinforced_tendons", () -> new Builder().setCapacity(7));
        addToMap("speed_legs", () -> new Builder().setCapacity(4));


        addToMap("bionic_feet", () -> new Builder().setCapacity(5));
        addToMap("sound_absorbent_feet", () -> new Builder().setCapacity(2).addRequirements(CybItems.BIONIC_FEET.get()));
        addToMap("jump_boost_feet", () -> new Builder().setCapacity(4).addRequirements(CybItems.BIONIC_FEET.get()).addIncompatibilities(CybItems.SOUND_ABSORBENT_FEET.get()));
        addToMap("full_speed_feet", () -> new Builder().setCapacity(2).addRequirements(CybItems.BIONIC_FEET.get()).addIncompatibilities(CybItems.JUMP_BOOST_FEET.get()));
    }

}
