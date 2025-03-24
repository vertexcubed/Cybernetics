package com.vertexcubed.cybernetics.common.registry;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.menu.CyberwareMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CybMenus {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, Cybernetics.MOD_ID);

    public static final Supplier<MenuType<CyberwareMenu>> CYBERWARE_MENU = MENUS.register("cyberware", () -> new MenuType<>((id, inv) -> new CyberwareMenu(id, inv, inv.player.getData(CybAttachments.CYBERWARE_INVENTORY)), FeatureFlags.DEFAULT_FLAGS));

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
