package com.vertexcubed.cybernetics.common.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.ListCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public record CyberwareProperties(int capacity, List<Ingredient> requirements, List<Ingredient> incompatibilities, Description description) {

    public static final Codec<CyberwareProperties> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("capacity").forGetter(CyberwareProperties::capacity),
                    Ingredient.LIST_CODEC.fieldOf("requirements").forGetter(CyberwareProperties::requirements),
                    Ingredient.LIST_CODEC.fieldOf("incompatibilities").forGetter(CyberwareProperties::incompatibilities),
                    Description.CODEC.fieldOf("description").forGetter(CyberwareProperties::description)
                    ).apply(instance, CyberwareProperties::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CyberwareProperties> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CyberwareProperties::capacity,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), CyberwareProperties::requirements,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), CyberwareProperties::incompatibilities,
            Description.STREAM_CODEC, CyberwareProperties::description,
            CyberwareProperties::new
    );




    public record Description(boolean showDescription, boolean showRequirements, boolean showIncompatibilities) {
        public static final Codec<Description> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.BOOL.fieldOf("show_description").forGetter(Description::showDescription),
                        Codec.BOOL.fieldOf("show_requirements").forGetter(Description::showRequirements),
                        Codec.BOOL.fieldOf("show_incompatibilities").forGetter(Description::showIncompatibilities)
                ).apply(instance, Description::new));

        public static final StreamCodec<ByteBuf, Description> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Description::showDescription,
                ByteBufCodecs.BOOL, Description::showRequirements,
                ByteBufCodecs.BOOL, Description::showIncompatibilities,
                Description::new
        );
    }



    public static class Builder {

        private final List<Ingredient> requirements = new ArrayList<>();
        private final List<Ingredient> incompatibilities = new ArrayList<>();
        private boolean showRequirements = true;
        private boolean showIncompatibilities = true;
        private boolean showDescription = true;
        private int capacity = 2;
        public Builder() {

        }

        public Builder addRequirements(Ingredient... requirements) {
            this.requirements.addAll(List.of(requirements));
            return this;
        }
        public Builder addRequirements(Object... requirements) {
            for(Object o : requirements) {
                Ingredient ingredient;
                if(o instanceof Item) {
                    ingredient = Ingredient.of((Item) o);
                }
                else if(o instanceof TagKey<?>) {
                    TagKey<?> tag = (TagKey<?>) o;
                    if(!tag.isFor(BuiltInRegistries.ITEM.key())) {
                        throw new IllegalArgumentException("Tag " + tag + " is not item tag!");
                    }
                    ingredient = Ingredient.of((TagKey<Item>) tag);
                }
                else {
                    throw new IllegalArgumentException("Must pass in a tag or an item!");
                }
                this.requirements.add(ingredient);
            }
            return this;
        }

        public Builder addIncompatibilities(Ingredient... incompatibilities) {
            this.incompatibilities.addAll(List.of(incompatibilities));
            return this;
        }
        public Builder addIncompatibilities(Object... incompatibilities) {
            for(Object o : incompatibilities) {
                Ingredient ingredient;
                if(o instanceof Item) {
                    ingredient = Ingredient.of((Item) o);
                }
                else if(o instanceof TagKey<?>) {
                    TagKey<?> tag = (TagKey<?>) o;
                    if(!tag.isFor(BuiltInRegistries.ITEM.key())) {
                        throw new IllegalArgumentException("Tag " + tag + " is not item tag!");
                    }
                    ingredient = Ingredient.of((TagKey<Item>) tag);
                }
                else {
                    throw new IllegalArgumentException("Must pass in a tag or an item!");
                }
                this.incompatibilities.add(ingredient);
            }
            return this;
        }

        public Builder setShowRequirements(boolean showRequirements) {
            this.showRequirements = showRequirements;
            return this;
        }

        public Builder setShowIncompatibilities(boolean showIncompatibilities) {
            this.showIncompatibilities = showIncompatibilities;
            return this;
        }

        public Builder setShowDescription(boolean showDescription) {
            this.showDescription = showDescription;
            return this;
        }

        public Builder setCapacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public CyberwareProperties build() {
            return new CyberwareProperties(capacity, requirements, incompatibilities, new Description(showDescription, showRequirements, showIncompatibilities));
        }
    }
}
