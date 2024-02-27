package net.blay09.mods.craftingforblockheads.registry;

import net.blay09.mods.craftingforblockheads.api.ItemFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeFilter implements ItemFilter {

    private final ResourceLocation recipeId;

    public RecipeFilter(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public boolean test(Recipe<?> recipe, ItemStack itemStack) {
        return recipe.getId().equals(recipeId);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return false;
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }
}
