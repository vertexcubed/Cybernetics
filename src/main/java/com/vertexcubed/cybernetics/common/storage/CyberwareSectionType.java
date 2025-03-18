package com.vertexcubed.cybernetics.common.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CyberwareSectionType {

    public static final Codec<CyberwareSectionType> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    MenuProperties.CODEC.fieldOf("menu_properties").forGetter(CyberwareSectionType::menuProperties),
                    InventoryProperties.CODEC.fieldOf("inventory_properties").forGetter(CyberwareSectionType::invProperties)
            )
            .apply(instance, CyberwareSectionType::new)
    );

    private final MenuProperties menuProp;
    private final InventoryProperties invProp;

    public static CyberwareSectionType fromRaw(ResourceLocation texture, int x, int y, int playerX, int playerY, TagKey<Item> tag, int size) {
        return new CyberwareSectionType(new MenuProperties(texture, x, y, playerX, playerY), new InventoryProperties(tag, size));
    }

    public CyberwareSectionType(MenuProperties menuProp, InventoryProperties invProp) {
        this.menuProp = menuProp;
        this.invProp = invProp;
    }

    private MenuProperties menuProperties() {
        return menuProp;
    }

    private InventoryProperties invProperties() {
        return invProp;
    }

    public int x() {
        return menuProp.x();
    }

    public int y() {
        return menuProp.y();
    }

    public int playerX() {
        return menuProp.playerX;
    }

    public int playerY() {
        return menuProp.playerY();
    }

    public ResourceLocation texture() {
        return menuProp.texture();
    }

    public TagKey<Item> tag() {
        return invProp.tag();
    }

    public int size() {
        return invProp.size();
    }




    public record MenuProperties(ResourceLocation texture, int x, int y, int playerX, int playerY) {
        public static final Codec<MenuProperties> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        ResourceLocation.CODEC.fieldOf("texture").forGetter(MenuProperties::texture),
                        Codec.INT.fieldOf("x").forGetter(MenuProperties::x),
                        Codec.INT.fieldOf("y").forGetter(MenuProperties::y),
                        Codec.INT.fieldOf("player_x").forGetter(MenuProperties::playerX),
                        Codec.INT.fieldOf("player_y").forGetter(MenuProperties::playerY)
                ).apply(instance, MenuProperties::new)
        );
    }

    public record InventoryProperties(TagKey<Item> tag, int size) {
        public static final Codec<InventoryProperties> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        TagKey.codec(BuiltInRegistries.ITEM.key()).fieldOf("tag").forGetter(InventoryProperties::tag),
                        Codec.INT.fieldOf("size").forGetter(InventoryProperties::size)
                ).apply(instance, InventoryProperties::new)
        );
    }

}
