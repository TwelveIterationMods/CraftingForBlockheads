package net.blay09.mods.craftingforblockheads.registry.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.Set;

import static net.blay09.mods.craftingforblockheads.registry.json.JsonCompatLoader.itemsFromJson;
import static net.blay09.mods.craftingforblockheads.registry.json.JsonCompatLoader.stringSetFromJson;

public record JsonProviderFilterData(String identifier, String name, ItemStack icon, NonNullList<Ingredient> includes,
                                     NonNullList<Ingredient> excludes, Set<String> hardRequirements, Set<String> softRequirements, int priority) {

    public static JsonProviderFilterData fromJson(String identifier, JsonObject jsonObject) {
        final var name = GsonHelper.getAsString(jsonObject, "name");
        final var includes = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "includes"));
        final var excludes = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "excludes", new JsonArray()));
        final var icon = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "icon"));
        final var hardRequirements = stringSetFromJson(GsonHelper.getAsJsonArray(jsonObject, "hard_requirements", new JsonArray()));
        final var softRequirements = stringSetFromJson(GsonHelper.getAsJsonArray(jsonObject, "soft_requirements", new JsonArray()));
        final var priority = GsonHelper.getAsInt(jsonObject, "priority", 0);
        return new JsonProviderFilterData(identifier, name, icon, includes, excludes, hardRequirements, softRequirements, priority);
    }
}
