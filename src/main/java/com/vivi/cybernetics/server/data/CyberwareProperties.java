package com.vivi.cybernetics.server.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vivi.cybernetics.common.item.CyberwareItem;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CyberwareProperties {

    private final List<Ingredient> requirements;
    private final List<Ingredient> incompatibilities;
    private final boolean showRequirements;
    private final boolean showIncompatibilities;
    private final boolean showDescription;
    private final int capacity;


    public CyberwareProperties(List<Ingredient> requirements, List<Ingredient> incompatibilities, boolean showRequirements, boolean showIncompatibilities, boolean showDescription, int capacity) {
        this.requirements = requirements;
        this.incompatibilities = incompatibilities;
        this.showRequirements = showRequirements;
        this.showIncompatibilities = showIncompatibilities;
        this.showDescription = showDescription;
        this.capacity = capacity;
    }

    @Deprecated(forRemoval = true)
    public static CyberwareProperties fromStringList(List<String> requirements, List<String> incompatibilities, boolean showRequirements, boolean showIncompatibilities, boolean showDescription, int capacity) {
        return new CyberwareProperties(toIngredientList(requirements), toIngredientList(incompatibilities), showRequirements, showIncompatibilities, showDescription, capacity);
    }

    @Deprecated(forRemoval = true)
    public static List<Ingredient> toIngredientList(List<String> stringList) {
        List<Ingredient> output = new ArrayList<>();
        stringList.forEach(str -> {
            if(str.startsWith("#")) {
                ResourceLocation rLoc = ResourceLocation.tryParse(str.substring(1));
                if(rLoc == null) {
                    throw new ResourceLocationException("Tag " + str + "is not a valid resource location!");
                }
                output.add(Ingredient.of(ItemTags.create(rLoc)));
            }
            else {
                ResourceLocation rLoc = ResourceLocation.tryParse(str);
                if(rLoc == null) {
                    throw new ResourceLocationException("Tag " + str + "is not a valid resource location!");
                }
                output.add(Ingredient.of(ForgeRegistries.ITEMS.getValue(rLoc)));
            }
        });
        return output;
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeCollection(requirements, (buf1, ingredient) -> {
            ingredient.toNetwork(buf1);
        });
        buf.writeCollection(incompatibilities, (buf1, ingredient) -> {
            ingredient.toNetwork(buf1);
        });
        buf.writeBoolean(showRequirements);
        buf.writeBoolean(showIncompatibilities);
        buf.writeBoolean(showDescription);
        buf.writeVarInt(capacity);
    }

    public static CyberwareProperties fromNetwork(FriendlyByteBuf buf) {
        List<Ingredient> req = buf.readList(Ingredient::fromNetwork);
        List<Ingredient> inc = buf.readList(Ingredient::fromNetwork);
        boolean showReq = buf.readBoolean();
        boolean showInc = buf.readBoolean();
        boolean showDesc = buf.readBoolean();
        int cap = buf.readVarInt();
        return new CyberwareProperties(req, inc, showReq, showInc, showDesc, cap);
    }

    public JsonObject serialize() {
        JsonObject output = new JsonObject();
        output.addProperty("capacity", capacity);
        output.addProperty("show_requirements", showRequirements);
        output.addProperty("show_incompatibilities", showIncompatibilities);
        output.addProperty("show_description", showDescription);
        JsonArray jsonRequirements = new JsonArray();
        requirements.forEach(req -> {
            jsonRequirements.add(req.toJson());
        });
        output.add("requirements", jsonRequirements);
        JsonArray jsonIncompatibilities = new JsonArray();
        incompatibilities.forEach(inc -> {
            jsonIncompatibilities.add(inc.toJson());
        });
        output.add("incompatibilities", jsonIncompatibilities);

        return output;
    }

    public static CyberwareProperties deserialize(JsonElement json) {
        JsonObject object = json.getAsJsonObject();
        List<Ingredient> req = new ArrayList<>();
        if(object.has("requirements")) {
            JsonArray requirements = object.getAsJsonArray("requirements");
            requirements.forEach(str -> {
                req.add(Ingredient.fromJson(str));
            });
        }
        List<Ingredient> inc = new ArrayList<>();
        if(object.has("incompatibilities")) {
            JsonArray incompatibilities = object.getAsJsonArray("incompatibilities");
            incompatibilities.forEach(str -> {
                inc.add(Ingredient.fromJson(str));
            });
        }
        boolean showRequirements = true;
        if(object.has("show_requirements")) {
            showRequirements = object.get("show_requirements").getAsBoolean();
        }
        boolean showIncompatibilities = true;
        if(object.has("show_incompatibilities")) {
            showIncompatibilities = object.get("show_incompatibilities").getAsBoolean();
        }
        boolean showDescription = true;
        if(object.has("show_description")) {
            showDescription = object.get("show_description").getAsBoolean();
        }
        int capacity = CyberwareItem.DEFAULT_CAPACITY;
        if(object.has("capacity")) {
            capacity = object.get("capacity").getAsInt();
        }
        return new CyberwareProperties(req, inc, showRequirements, showIncompatibilities, showDescription, capacity);
    }

    @Override
    public String toString() {
        return "Requirements: {" + listToString(requirements) + "}, Incompatibilities: {" + listToString(incompatibilities) + "}";
    }

    private String listToString(List<Ingredient> ingredients) {
        StringBuilder output = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            output.append(Arrays.toString(ingredient.getItems()));
        }
        return output.toString();
    }

    public List<Ingredient> getRequirements() {
        return requirements;
    }

    public List<Ingredient> getIncompatibilities() {
        return incompatibilities;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean showRequirements() {
        return showRequirements;
    }

    public boolean showIncompatibilities() {
        return showIncompatibilities;
    }

    public boolean showDescription() {
        return showDescription;
    }
}
