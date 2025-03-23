package com.vertexcubed.cybernetics.common.registry;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.item.CyberwareItem;
import com.vertexcubed.cybernetics.common.item.CyberwareProperties;
import com.vertexcubed.cybernetics.common.item.CyberwareProperties.Builder;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CybItems {


    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Cybernetics.MOD_ID);

    public static final Map<String, CyberwareProperties> propMap = new HashMap<>();

    public static final DeferredHolder<Item, CyberwareItem>
            OXYGEN_RECYCLER = ITEMS.register("oxygen_recycler", () -> new CyberwareItem(cyberwareProps("oxygen_recycler")))

    ;


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static Item.Properties cyberwareProps(String name) {
        return new Item.Properties().stacksTo(1).component(CybDataComponents.CYBERWARE_PROPERTIES, propMap.get(name));
    }

    private static void addToMap(String name, Builder builder) {
        propMap.put(name, builder.build());
    }


    static {
        addToMap("oxygen_recycler", new Builder().setCapacity(5));
    }

}
