package net.blay09.mods.craftingforblockheads.api.capability;

import net.blay09.mods.craftingforblockheads.crafting.IngredientToken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Collection;

public interface IWorkshopItemProvider {
    IngredientToken findIngredient(Ingredient ingredient, Collection<IngredientToken> ingredientTokens);

    IngredientToken findIngredient(ItemStack itemStack, Collection<IngredientToken> ingredientTokens);
}
