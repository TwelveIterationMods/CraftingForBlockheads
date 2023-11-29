package net.blay09.mods.craftingforblockheads.util;

import net.blay09.mods.craftingforblockheads.crafting.RecipeWithStatus;

import java.util.Comparator;

public class CraftableComparator implements Comparator<RecipeWithStatus> {

    @Override
    public int compare(RecipeWithStatus o1, RecipeWithStatus o2) {
        // Checking for missing ingredients and predicates for both objects
        boolean o1HasMissingIngredients = !o1.missingIngredients().isEmpty();
        boolean o1HasMissingPredicates = !o1.missingPredicates().isEmpty();
        boolean o2HasMissingIngredients = !o2.missingIngredients().isEmpty();
        boolean o2HasMissingPredicates = !o2.missingPredicates().isEmpty();

        // Compare based on missing predicates and ingredients
        if (!o1HasMissingIngredients && !o1HasMissingPredicates && (o2HasMissingIngredients || o2HasMissingPredicates)) {
            // o1 has neither missing ingredients nor predicates but o2 does
            return -1;
        } else if (!o2HasMissingIngredients && !o2HasMissingPredicates && (o1HasMissingIngredients || o1HasMissingPredicates)) {
            // o2 has neither missing ingredients nor predicates but o1 does
            return 1;
        } else if (!o1HasMissingPredicates && o2HasMissingPredicates) {
            // o1 only has missing ingredients, but o2 has missing predicates or both
            return -1;
        } else if (!o2HasMissingPredicates && o1HasMissingPredicates) {
            // o2 only has missing ingredients, but o1 has missing predicates or both
            return 1;
        }

        // If both are in the same category, compare based on display names
        String s1 = o1.resultItem().getDisplayName().getString();
        String s2 = o2.resultItem().getDisplayName().getString();
        return s1.compareToIgnoreCase(s2);
    }


}
