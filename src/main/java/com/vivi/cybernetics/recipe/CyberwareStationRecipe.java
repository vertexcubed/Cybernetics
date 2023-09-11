package com.vivi.cybernetics.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vivi.cybernetics.Cybernetics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.util.GsonHelper.getAsJsonArray;
import static net.minecraft.util.GsonHelper.getAsJsonObject;

public class CyberwareStationRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> ingredients;
    private final Ingredient input;
    protected final List<Boolean> itemMatchesSlot = new ArrayList<>();


    public CyberwareStationRecipe(ResourceLocation id, Ingredient input, NonNullList<Ingredient> ingredients, ItemStack output) {
        this.id = id;
        this.input = input;
        this.ingredients = ingredients;
        this.output = output;
        ingredients.forEach(ingredient -> itemMatchesSlot.add(false));
    }


    public Ingredient getInput() {
        return input;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if(level.isClientSide) return false;

        //main input MUST match
        if(!input.test(container.getItem(0))) return false;


        for(int i = 0; i < 6; i++) {
            itemMatchesSlot.set(i, false);
        }


        boolean b = false;
        //match 6 slots
        for(Ingredient ingredient : ingredients) {
            for(int i = 0; i < 6; i++) {
                if(ingredient.test(container.getItem(i + 1)) && !itemMatchesSlot.get(i)) {
                    itemMatchesSlot.set(i, true);
                    b = true;
                    break;
                }
            }
            if(!b) {
                return false;
            }
            b = false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(SimpleContainer pContainer) {
        return Recipe.super.getRemainingItems(pContainer);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<CyberwareStationRecipe> {
        private Type() {

        }
        public static final Type INSTANCE = new Type();
        public static final String ID = "cyberware_crafting";
    }

    public static class Serializer implements RecipeSerializer<CyberwareStationRecipe> {
        private Serializer() {

        }
        public static final Serializer INSTANCE = new Serializer();
//        public static final ResourceLocation ID = new ResourceLocation(Cybernetics.MOD_ID, "cyberware_crafting");

        @Override
        public CyberwareStationRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(getAsJsonObject(json, "output"));

            JsonArray ingredientJson = getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.withSize(6, Ingredient.EMPTY);
            for(int i = 0; i < ingredientJson.size(); i++) {
                ingredients.set(i, Ingredient.fromJson(ingredientJson.get(i)));
            }

            Ingredient input = Ingredient.fromJson(getAsJsonObject(json, "input"));

            return new CyberwareStationRecipe(id, input, ingredients, output);
        }

        @Override
        public @Nullable CyberwareStationRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            //THE ORDER MATTERS!!! IT MUST MATCH toNetwork()
            Ingredient input = Ingredient.fromNetwork(buf);
            NonNullList<Ingredient> ingredients = NonNullList.withSize(buf.readVarInt(), Ingredient.EMPTY);
            for(int i = 0; i < ingredients.size(); i++) {
                ingredients.set(i, Ingredient.fromNetwork(buf));
            }
            ItemStack output = buf.readItem();
            return new CyberwareStationRecipe(id, input, ingredients, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CyberwareStationRecipe recipe) {
            //THE ORDER MATTERS!!! IT MUST MATCH fromNetwork()
            recipe.getInput().toNetwork(buf);
            buf.writeVarInt(recipe.getIngredients().size());
            recipe.getIngredients().forEach(ingredient -> ingredient.toNetwork(buf));
            buf.writeItem(recipe.getResultItem());
        }
    }
}
