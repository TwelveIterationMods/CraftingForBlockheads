package net.blay09.mods.craftingforblockheads.menu;

import net.blay09.mods.craftingforblockheads.crafting.RecipeWithStatus;

import java.util.Comparator;

class RecipeWithStatusComparator implements Comparator<RecipeWithStatus> {
    @Override
    public int compare(RecipeWithStatus o1, RecipeWithStatus o2) {
        final var missingDiff = o1.missingIngredients().size() - o2.missingIngredients().size();
        if (missingDiff != 0) {
            return missingDiff;
        }

        final var lockedCount1 = o1.lockedInputs().stream()
                .filter(stack -> !stack.isEmpty())
                .count();
        final var lockedCount2 = o2.lockedInputs().stream()
                .filter(stack -> !stack.isEmpty())
                .count();

        return Long.compare(lockedCount2, lockedCount1);
    }
}
