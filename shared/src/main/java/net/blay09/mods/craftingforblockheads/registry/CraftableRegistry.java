//package net.blay09.mods.craftingforblockheads.registry;
//
//import com.google.common.collect.ArrayListMultimap;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Multimap;
//import net.blay09.mods.balm.api.Balm;
//import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
//import net.blay09.mods.craftingforblockheads.api.*;
//import net.blay09.mods.craftingforblockheads.api.capability.IWorkshopItemProvider;
//import net.blay09.mods.craftingforblockheads.menu.inventory.InventoryCraftBook;
//import net.blay09.mods.craftingforblockheads.registry.recipe.GeneralCraftable;
//import net.minecraft.core.RegistryAccess;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.Container;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.*;
//import net.minecraft.world.level.Level;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.*;
//
//public class CraftableRegistry {
//
//    private static final List<Recipe<Container>> recipeList = Lists.newArrayList();
//    private static final ArrayListMultimap<ResourceLocation, Craftable> craftables = ArrayListMultimap.create();
//
//    public static void initCraftableRegistry(RecipeManager recipeManager, RegistryAccess registryAccess) {
//        recipeList.clear();
//        craftables.clear();
//
//        // Crafting Recipes of Food Items
//        for (Recipe<?> recipe : recipeManager.getRecipes()) {
//            // Restrict the search to crafting to prevent duplicates from smoking and campfire cooking, as well as issues with other mod custom recipes
//            if (!isValidRecipeType(recipe)) {
//                continue;
//            }
//
//            ItemStack output = recipe.getResultItem(registryAccess);
//
//            //noinspection ConstantConditions
//            if (output == null) {
//                CraftingForBlockheads.logger.warn("Recipe " + recipe.getId() + " returned a null ItemStack in getRecipeOutput - this is bad! The developer of " + recipe.getId()
//                        .getNamespace() + " should return an empty ItemStack instead to avoid problems.");
//                continue;
//            }
//
//            if (isAllowedOutputItem(output)) {
//                addRecipe(recipe, registryAccess);
//            }
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    public static void addRecipe(Recipe<? extends Container> recipe, RegistryAccess registryAccess) {
//        ItemStack output = recipe.getResultItem(registryAccess);
//        if (!output.isEmpty() && !recipe.getIngredients().isEmpty()) {
//            Craftable craftable;
//            if (recipe instanceof net.minecraft.world.item.crafting.CraftingRecipe) {
//                craftable = new GeneralCraftable(recipe, recipe.getResultItem(registryAccess));
//            } else {
//                return;
//            }
//
//            recipeList.add((Recipe<Container>) recipe);
//            craftables.put(Balm.getRegistries().getKey(output.getItem()), craftable);
//        }
//    }
//
//    @Nullable
//    @SuppressWarnings("unchecked")
//    public static <T extends Recipe<?>> T findFoodRecipe(InventoryCraftBook craftMatrix, Level level, RecipeType<T> recipeType, Item expectedItem) {
//        for (Recipe<Container> recipe : recipeList) {
//            if (recipe.getType() == recipeType && recipe.matches(craftMatrix, level) && recipe.getResultItem(level.registryAccess())
//                    .getItem() == expectedItem) {
//                return (T) recipe;
//            }
//        }
//
//        return null;
//    }
//}
