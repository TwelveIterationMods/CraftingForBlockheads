package net.blay09.mods.craftingforblockheads;

import net.blay09.mods.craftingforblockheads.api.RecipeWorkshopHandler;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class ShapedRecipeWorkshopHandler implements RecipeWorkshopHandler<ShapedRecipe> {
    @Override
    public int mapToMatrixSlot(ShapedRecipe recipe, int ingredientIndex) {
        final int recipeWidth = recipe.getWidth();
        final int origX = ingredientIndex % recipeWidth;
        final int origY = ingredientIndex / recipeWidth;

        // Offset to center the recipe if its width is 1
        final int offsetX = recipeWidth == 1 ? 1 : 0;

        return origY * 3 + origX + offsetX;
    }

    @Override
    public ItemStack assemble(ShapedRecipe recipe, CraftingContainer craftingContainer, RegistryAccess registryAccess) {
        return recipe.assemble(craftingContainer, registryAccess);
    }
}
