package com.vertexcubed.cybernetics.datagen;


import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.registry.CybDPRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
// TODO: create AbilityTagsProvider and CybAbilityTagsProvider
public class DataGenerators {

    public static void gatherData(GatherDataEvent event) {
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        CybBlockTagsProvider blockTags = new CybBlockTagsProvider(output, lookupProvider, existingFileHelper);

        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<DataProvider>) packOutput -> new DatapackBuiltinEntriesProvider(
                        packOutput,
                        event.getLookupProvider(),
                        new RegistrySetBuilder()
                                .add(CybDPRegistries.CYBERWARE_SECTION_KEY, CyberwareSectionProvider::register)

                        ,
                        Set.of(Cybernetics.MOD_ID)
                )
        );

        event.getGenerator().addProvider(
                event.includeServer(),
                blockTags
        );

        event.getGenerator().addProvider(
                event.includeServer(),
                new CybItemTagsProvider(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper)
        );
    }
}
