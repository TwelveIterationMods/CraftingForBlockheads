package net.blay09.mods.craftingforblockheads.menu;

import net.blay09.mods.craftingforblockheads.crafting.RecipeWithStatus;

import java.util.Comparator;

class RecipeWithStatusComparator implements Comparator<RecipeWithStatus> {
    @Override
    public int compare(RecipeWithStatus o1, RecipeWithStatus o2) {
        return o1.missingIngredients().size() - o2.missingIngredients().size();
    }
}
