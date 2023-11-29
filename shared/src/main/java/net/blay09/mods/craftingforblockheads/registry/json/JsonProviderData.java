package net.blay09.mods.craftingforblockheads.registry.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.craftingforblockheads.api.WorkshopPredicate;
import net.blay09.mods.craftingforblockheads.registry.CraftingForBlockheadsRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.blay09.mods.craftingforblockheads.registry.json.JsonCompatLoader.itemsFromJson;

public record JsonProviderData(String modId, String preset, List<Ingredient> craftables, Map<String, WorkshopPredicate> predicates,
                               List<JsonProviderFilterData> filters) {

    public static JsonProviderData fromJson(JsonObject jsonObject) {
        final var modId = GsonHelper.getAsString(jsonObject, "modid");
        final var preset = GsonHelper.getAsString(jsonObject, "preset");
        final var craftables = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "craftables", new JsonArray()));
        final var predicates = predicatesFromJson(GsonHelper.getAsJsonObject(jsonObject, "predicates", new JsonObject()));
        final var filters = filtersFromJson(GsonHelper.getAsJsonObject(jsonObject, "filters"));
        for (JsonProviderFilterData filter : filters) {
            craftables.addAll(filter.includes());
        }
        return new JsonProviderData(modId, preset, craftables, predicates, filters);
    }

    private static List<JsonProviderFilterData> filtersFromJson(JsonObject jsonObject) {
        List<JsonProviderFilterData> filters = new ArrayList<>();

        for (String identifier : jsonObject.keySet()) {
            filters.add(JsonProviderFilterData.fromJson(identifier, jsonObject.getAsJsonObject(identifier)));
        }

        return filters;
    }

    private static Map<String, WorkshopPredicate> predicatesFromJson(JsonObject jsonObject) {
        final var predicates = new HashMap<String, WorkshopPredicate>();

        for (final var identifier : jsonObject.keySet()) {
            final var predicateJson = jsonObject.get(identifier);
            if (predicateJson.isJsonArray()) {
                final var predicateJsonArray = predicateJson.getAsJsonArray();
                WorkshopPredicate combinedPredicate = null;
                for (int i = 0; i < predicateJsonArray.size(); i++) {
                    final var predicate = predicateFromJson(predicateJsonArray.get(i).getAsJsonObject());
                    if (combinedPredicate == null) {
                        combinedPredicate = predicate;
                    } else {
                        combinedPredicate = combinedPredicate.and(predicate);
                    }
                }
                predicates.put(identifier, combinedPredicate);
            } else if (predicateJson.isJsonObject()) {
                final var predicateJsonObject = predicateJson.getAsJsonObject();
                final var predicate = predicateFromJson(predicateJsonObject);
                predicates.put(identifier, predicate);
            }
        }

        return predicates;
    }

    private static WorkshopPredicate predicateFromJson(JsonObject jsonObject) {
        final var predicateType = GsonHelper.getAsString(jsonObject, "type");
        final var deserializer = CraftingForBlockheadsRegistry.getWorkshopPredicateDeserializer(predicateType);
        if (deserializer != null) {
            return deserializer.apply(jsonObject);
        } else {
            throw new IllegalArgumentException("Unknown workshop predicate type: " + predicateType);
        }
    }

}
