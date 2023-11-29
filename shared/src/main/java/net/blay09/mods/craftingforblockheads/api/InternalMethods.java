package net.blay09.mods.craftingforblockheads.api;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public interface InternalMethods {
    Workshop createWorkshop(ItemStack itemStack);

    Workshop createWorkshop(Level level, BlockPos pos);

    void registerProvider(CraftingForBlockheadsProvider provider);

    void unregisterProvider(CraftingForBlockheadsProvider provider);

    <C extends Container, T extends Recipe<C>> void registerRecipeWorkshopHandler(Class<T> recipeClass, RecipeWorkshopHandler<T> recipeWorkshopHandler);

    void registerWorkshopPredicateDeserializer(String name, Function<JsonObject, WorkshopPredicate> deserializer);
}
