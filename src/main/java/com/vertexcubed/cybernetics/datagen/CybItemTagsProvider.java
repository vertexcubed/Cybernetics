package com.vertexcubed.cybernetics.datagen;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.registry.CybItems;
import com.vertexcubed.cybernetics.common.registry.CybTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class CybItemTagsProvider extends ItemTagsProvider {
    public CybItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Cybernetics.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
                //section tags
        this.tag(CybTags.HEAD_SECTION).add(
                CybItems.OVERCAPACITY_HEAD.get(),
                CybItems.CARBON_FIBER_SKULL.get(),
//                CybItems.MK1_BERSERK.get(),
//                CybItems.MK2_BERSERK.get(),
//                CybItems.MK3_BERSERK.get(),
                CybItems.MK1_DASH.get(),
                CybItems.MK2_DASH.get()
        );
        this.tag(CybTags.EYES_SECTION).add(
                CybItems.MK1_OPTICS.get(),
                CybItems.MK2_OPTICS.get(),
                CybItems.MK3_OPTICS.get(),
                CybItems.NIGHT_VISION_EYES.get()
        );
        this.tag(CybTags.UPPER_ORGANS_SECTION).add(
                CybItems.EMERGENCY_DEFIBRILLATOR.get(),
                CybItems.OXYGEN_RECYCLER.get(),
                CybItems.HEALTH_BOOST_ORGANS.get()
        );
        this.tag(CybTags.LOWER_ORGANS_SECTION).add(
                CybItems.STOMACH_FILTER.get()
//                CybItems.SYNAPTIC_DISABLER.get(),
//                CybItems.MK1_DOUBLE_JUMP_ADDER.get(),
//                CybItems.MK2_DOUBLE_JUMP_ADDER.get()
        );
        this.tag(CybTags.SKELETON_SECTION).add(
                CybItems.REINFORCED_SKELETON.get(),
                CybItems.TITANIUM_SKELETON.get(),
                CybItems.HEALTH_BOOST_SKELETON.get(),
                CybItems.KINETIC_DISCHARGER.get()
        );
        this.tag(CybTags.SKIN_SECTION).add(
                CybItems.FIRE_RESISTANCE_SKIN.get(),
                CybItems.PROJECTILE_DEFLECTOR.get()
        );
        this.tag(CybTags.ARMS_SECTION).add(
                CybItems.RANGE_EXTENDER.get()
        );
        this.tag(CybTags.HANDS_SECTION).add(
                CybItems.STRENGTH_HANDS.get()
        );
        this.tag(CybTags.LEGS_SECTION).add(
                CybItems.REINFORCED_TENDONS.get(),
                CybItems.SPEED_LEGS.get()
        );
        this.tag(CybTags.FEET_SECTION).add(
                CybItems.BIONIC_FEET.get(),
                CybItems.SOUND_ABSORBENT_FEET.get(),
                CybItems.JUMP_BOOST_FEET.get(),
                CybItems.FULL_SPEED_FEET.get()
        );

//        this.tag(CybTags.BERSERKS).add(CybItems.MK1_BERSERK.get(), CybItems.MK2_BERSERK.get(), CybItems.MK3_BERSERK.get());
        this.tag(CybTags.OPTICS).add(CybItems.MK1_OPTICS.get(), CybItems.MK2_OPTICS.get(), CybItems.MK3_OPTICS.get());
        this.tag(CybTags.SKELETONS).add(CybItems.REINFORCED_SKELETON.get(), CybItems.TITANIUM_SKELETON.get());
//        this.tag(CybTags.DOUBLE_JUMP_ADDERS).add(CybItems.MK1_DOUBLE_JUMP_ADDER.get(), CybItems.MK2_DOUBLE_JUMP_ADDER.get());
        this.tag(CybTags.DASH_ITEMS).add(CybItems.MK1_DASH.get(), CybItems.MK2_DASH.get());
    }
}
