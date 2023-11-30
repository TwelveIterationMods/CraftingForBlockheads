package net.blay09.mods.craftingforblockheads.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.blay09.mods.craftingforblockheads.api.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.*;
import java.util.function.Function;

public class CraftingForBlockheadsRegistry {

    private static final List<CraftingForBlockheadsProvider> providers = new ArrayList<>();
    private static final Multimap<ResourceLocation, Recipe<?>> recipesByItemId = ArrayListMultimap.create();
    private static final Multimap<ResourceLocation, Recipe<?>> recipesByGroup = ArrayListMultimap.create();
    private static final Map<String, Function<JsonObject, WorkshopPredicate>> workshopPredicateDeserializers = new HashMap<>();
    private static final Map<Class<? extends Recipe<?>>, RecipeWorkshopHandler<? extends Recipe<?>>> recipeWorkshopHandlers = new HashMap<>();

    public static <C extends Container, T extends Recipe<C>> void registerRecipeWorkshopHandler(Class<T> recipeType, RecipeWorkshopHandler<T> handler) {
        recipeWorkshopHandlers.put(recipeType, handler);
    }

    public static void registerWorkshopPredicateDeserializer(String name, Function<JsonObject, WorkshopPredicate> deserializer) {
        workshopPredicateDeserializers.put(name, deserializer);
    }

    public static Function<JsonObject, WorkshopPredicate> getWorkshopPredicateDeserializer(String name) {
        return workshopPredicateDeserializers.get(name);
    }

    public static void registerProvider(CraftingForBlockheadsProvider provider) {
        providers.add(provider);
    }

    public static void unregisterProvider(CraftingForBlockheadsProvider provider) {
        providers.remove(provider);
    }

    public static List<Ingredient> getCraftables() {
        final var craftables = new ArrayList<Ingredient>();
        for (final var provider : providers) {
            craftables.addAll(provider.getCraftables());
        }
        return Collections.unmodifiableList(craftables);
    }

    public static Map<String, WorkshopFilter> getWorkshopFilters() {
        final var filters = new HashMap<String, WorkshopFilter>();
        for (final var provider : providers) {
            filters.putAll(provider.getFilters());
        }
        return Collections.unmodifiableMap(filters);
    }

    public static Map<String, WorkshopPredicate> getWorkshopPredicates() {
        final var predicates = new HashMap<String, WorkshopPredicate>();
        for (final var provider : providers) {
            predicates.putAll(provider.getPredicates());
        }

        return Collections.unmodifiableMap(predicates);
    }

    public static List<WorkshopGroup> getGroups() {
        final var groups = new ArrayList<WorkshopGroup>();
        for (final var provider : providers) {
            groups.addAll(provider.getGroups());
        }
        return Collections.unmodifiableList(groups);
    }

    public static Collection<Recipe<?>> getRecipesFor(ItemStack resultItem) {
        final var itemId = Balm.getRegistries().getKey(resultItem.getItem());
        return recipesByItemId.get(itemId);
    }

    public static Collection<? extends Recipe<?>> getRecipesInGroup(ItemStack resultItem) {
        final var itemId = Balm.getRegistries().getKey(resultItem.getItem());
        return recipesByGroup.get(itemId);
    }

    public static Multimap<ResourceLocation, Recipe<?>> getRecipesByItemId() {
        return recipesByItemId;
    }

    private static boolean isEligibleResultItem(ItemStack itemStack) {
        for (final var craftablePredicate : getCraftables()) {
            if (craftablePredicate.test(itemStack)) {
                return true;
            }
        }
        return false;
    }

    private static <T extends Container> void loadRecipesByType(RecipeManager recipeManager, RegistryAccess registryAccess, RecipeType<? extends Recipe<T>> recipeType) {
        for (final var recipe : recipeManager.getAllRecipesFor(recipeType)) {
            final var resultItem = recipe.getResultItem(registryAccess);
            if (isEligibleResultItem(resultItem)) {
                final var itemId = Balm.getRegistries().getKey(resultItem.getItem());
                recipesByItemId.put(itemId, recipe);

                final var groups = getGroups();
                for (final var group : groups) {
                    for (final var ingredient : group.getChildren()) {
                        if (ingredient.test(resultItem)) {
                            final var groupItemId = Balm.getRegistries().getKey(group.getParentItem());
                            recipesByGroup.put(groupItemId, recipe);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void reload(RecipeManager recipeManager, RegistryAccess registryAccess) {
        recipesByItemId.clear();
        loadRecipesByType(recipeManager, registryAccess, RecipeType.CRAFTING);
    }

    public static Map<String, WorkshopPredicateLevel> getItemRequirements(ItemStack itemStack) {
        final var predicates = getWorkshopPredicates();
        final var result = new HashMap<String, WorkshopPredicateLevel>();
        itemStack.getTags().map(TagKey::location).forEach(location -> {
            if (location.getNamespace().equals(CraftingForBlockheads.MOD_ID)) {
                if (location.getPath().startsWith("requires_")) {
                    final var predicateKey = location.getPath().substring("requires_".length());
                    final var predicate = predicates.get(predicateKey);
                    if (predicate != null) {
                        result.put(predicateKey, WorkshopPredicateLevel.HARD);
                    }
                } else if (location.getPath().startsWith("soft_requires_")) {
                    final var predicateKey = location.getPath().substring("soft_requires_".length());
                    final var predicate = predicates.get(predicateKey);
                    if (predicate != null) {
                        result.put(predicateKey, WorkshopPredicateLevel.SOFT);
                    }
                }
            }
        });
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>, V extends RecipeWorkshopHandler<T>> V getRecipeWorkshopHandler(T recipe) {
        for (Class<? extends Recipe<?>> handlerClass : recipeWorkshopHandlers.keySet()) {
            if (handlerClass.isAssignableFrom(recipe.getClass())) {
                return (V) recipeWorkshopHandlers.get(handlerClass);
            }
        }

        return (V) recipeWorkshopHandlers.get(recipe.getClass());
    }
}
