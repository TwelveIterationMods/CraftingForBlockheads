package net.blay09.mods.craftingforblockheads;

import com.google.gson.JsonObject;
import net.blay09.mods.craftingforblockheads.api.*;
import net.blay09.mods.craftingforblockheads.crafting.WorkshopImpl;
import net.blay09.mods.craftingforblockheads.registry.CraftingForBlockheadsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public Workshop createWorkshop(ItemStack itemStack) {
        return new WorkshopImpl(itemStack);
    }

    @Override
    public Workshop createWorkshop(Level level, BlockPos pos) {
        return new WorkshopImpl(level, pos);
    }

    @Override
    public void registerProvider(CraftingForBlockheadsProvider provider) {
        CraftingForBlockheadsRegistry.registerProvider(provider);
    }

    @Override
    public void unregisterProvider(CraftingForBlockheadsProvider provider) {
        CraftingForBlockheadsRegistry.unregisterProvider(provider);
    }

    @Override
    public <C extends Container, T extends Recipe<C>> void registerRecipeWorkshopHandler(Class<T> recipeClass, RecipeWorkshopHandler<T> recipeWorkshopHandler) {
        CraftingForBlockheadsRegistry.registerRecipeWorkshopHandler(recipeClass, recipeWorkshopHandler);
    }

    @Override
    public void registerWorkshopPredicateDeserializer(String name, Function<JsonObject, WorkshopPredicate> deserializer) {
        CraftingForBlockheadsRegistry.registerWorkshopPredicateDeserializer(name, deserializer);
    }
}
