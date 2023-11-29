package net.blay09.mods.craftingforblockheads.api;

import com.google.gson.JsonObject;
import net.blay09.mods.craftingforblockheads.InternalMethodsImpl;
import net.blay09.mods.craftingforblockheads.ShapedRecipeWorkshopHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class CraftingForBlockheadsAPI {

    private static final InternalMethodsImpl internalMethods = loadInternalMethods();

    private static InternalMethodsImpl loadInternalMethods() {
        try {
            return (InternalMethodsImpl) Class.forName("net.blay09.mods.craftingforblockheads.InternalMethodsImpl").getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException("Failed to load Crafting for Blockheads API", e);
        }
    }

    public static Workshop createWorkshop(Level level, BlockPos pos) {
        return internalMethods.createWorkshop(level, pos);
    }

    public static Workshop createWorkshop(ItemStack itemStack) {
        return internalMethods.createWorkshop(itemStack);
    }

    public static void registerProvider(CraftingForBlockheadsProvider provider) {
        internalMethods.registerProvider(provider);
    }

    public static void unregisterProvider(CraftingForBlockheadsProvider provider) {
        internalMethods.unregisterProvider(provider);
    }

    public static <C extends Container, T extends Recipe<C>> void registerRecipeWorkshopHandler(Class<T> recipeClass, RecipeWorkshopHandler<T> recipeWorkshopHandler) {
        internalMethods.registerRecipeWorkshopHandler(recipeClass, recipeWorkshopHandler);
    }

    public static void registerWorkshopPredicateDeserializer(String name, Function<JsonObject, WorkshopPredicate> deserializer) {
        internalMethods.registerWorkshopPredicateDeserializer(name, deserializer);
    }
}
