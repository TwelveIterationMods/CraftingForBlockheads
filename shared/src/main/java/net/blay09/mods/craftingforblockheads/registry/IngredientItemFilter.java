package net.blay09.mods.craftingforblockheads.registry;

import net.blay09.mods.craftingforblockheads.api.ItemFilter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientItemFilter implements ItemFilter {

    private final Ingredient ingredient;

    public IngredientItemFilter(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return ingredient.test(itemStack);
    }

    @Override
    public ItemStack[] getItems() {
        return ingredient.getItems();
    }

    public Ingredient getIngredient() {
        return ingredient;
    }
}
