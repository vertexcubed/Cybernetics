package com.vertexcubed.cybernetics.datagen;


import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.registry.CybDPRegistries;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

public class DataGenerators {

    public static void gatherData(GatherDataEvent event) {



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
    }
}
