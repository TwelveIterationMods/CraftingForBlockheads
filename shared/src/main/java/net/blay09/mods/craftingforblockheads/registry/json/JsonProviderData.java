package net.blay09.mods.craftingforblockheads.registry.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.craftingforblockheads.api.ItemFilter;
import net.blay09.mods.craftingforblockheads.api.WorkshopGroup;
import net.blay09.mods.craftingforblockheads.api.WorkshopPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.blay09.mods.craftingforblockheads.registry.json.JsonCompatLoader.itemsFromJson;
import static net.blay09.mods.craftingforblockheads.registry.json.JsonCompatLoader.predicateFromJson;

public record JsonProviderData(String modId, String preset, List<ItemFilter> craftables, List<WorkshopGroup> groups, Map<String, WorkshopPredicate> predicates,
                               List<JsonProviderFilterData> filters) {

    public static JsonProviderData fromJson(JsonObject jsonObject) {
        final var modId = GsonHelper.getAsString(jsonObject, "modid");
        final var preset = GsonHelper.getAsString(jsonObject, "preset");
        final var craftables = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "craftables", new JsonArray()));
        final var groups = groupsFromJson(GsonHelper.getAsJsonObject(jsonObject, "groups", new JsonObject()));
        final var predicates = predicatesFromJson(GsonHelper.getAsJsonObject(jsonObject, "predicates", new JsonObject()));
        final var filters = filtersFromJson(GsonHelper.getAsJsonObject(jsonObject, "filters"));
        for (JsonProviderFilterData filter : filters) {
            craftables.addAll(filter.includes());
        }
        return new JsonProviderData(modId, preset, craftables, groups, predicates, filters);
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
            final var predicateJson = GsonHelper.getAsJsonObject(jsonObject, identifier);
            final var predicate = predicateFromJson(predicateJson);
            predicates.put(identifier, predicate);
        }

        return predicates;
    }

    private static List<WorkshopGroup> groupsFromJson(JsonObject jsonObject) {
        final var groups = new ArrayList<WorkshopGroup>();

        for (final var parentItemId : jsonObject.keySet()) {
            final var jsonArray = jsonObject.get(parentItemId).getAsJsonArray();
            final var children = itemsFromJson(jsonArray);
            final var parentItem = Balm.getRegistries().getItem(new ResourceLocation(parentItemId));
            if (parentItem == null) {
                throw new IllegalArgumentException("Unknown item: " + parentItemId);
            }

            groups.add(new WorkshopGroup() {
                @Override
                public Item getParentItem() {
                    return parentItem;
                }

                @Override
                public List<ItemFilter> getChildren() {
                    return children;
                }
            });
        }

        return groups;
    }
}
