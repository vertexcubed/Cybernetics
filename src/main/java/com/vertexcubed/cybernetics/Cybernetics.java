package com.vertexcubed.cybernetics;

import com.mojang.logging.LogUtils;
import com.vertexcubed.cybernetics.common.registry.*;
import com.vertexcubed.cybernetics.datagen.DataGenerators;
import com.vertexcubed.cybernetics.server.network.CybPayloads;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

@Mod(Cybernetics.MOD_ID)
public class Cybernetics
{

    public static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }


    public static final String MOD_ID = "cybernetics";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    // Creates a creative tab with the id "cybernetics:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cybernetics")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> Items.BARRIER.getDefaultInstance())
            .displayItems((parameters, output) -> {

            }).build());

    public Cybernetics(IEventBus modEventBus, ModContainer modContainer)
    {
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);



        CybAttachments.register(modEventBus);
        CybMenus.register(modEventBus);
        CybDataComponents.register(modEventBus);
        CybItems.register(modEventBus);
        CybCreativeTabs.register(modEventBus);
        CybAbilities.register(modEventBus);

    }

    @SubscribeEvent
    public void registerRegistries(NewRegistryEvent event) {
        event.register(CybAbilities.ABILITY_TYPE_REGISTRY);
    }

    @SubscribeEvent
    public void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        CybPayloads.regsiter(event);
    }

    @SubscribeEvent
    public void registerScreens(RegisterMenuScreensEvent event) {
        CybScreens.register(event);
    }

    @SubscribeEvent
    public void registerDPRegistries(DataPackRegistryEvent.NewRegistry event) {
        CybDPRegistries.register(event);
    }

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerators.gatherData(event);
    }



    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }

        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            CybKeyMappings.register(event);
        }
    }
}
