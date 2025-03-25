package com.vertexcubed.cybernetics.common.registry;

import com.vertexcubed.cybernetics.Cybernetics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CybCreativeTabs{

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Cybernetics.MOD_ID);

    public static final Supplier<CreativeModeTab> TAB = CREATIVE_TABS.register("cybernetics",
            () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.cybernetics")).icon(() ->
                            new ItemStack(Items.BARRIER))
                    .displayItems((parameters, output) -> {
                                CybItems.ITEMS.getEntries().forEach((item) -> output.accept(item.get()));
//                                CybBlocks.BLOCKS.getEntries().forEach(block -> {
//                                    output.accept(block.get().asItem());
//                                });
                            }
                    ).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
