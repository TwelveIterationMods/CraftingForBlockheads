package net.blay09.mods.craftingforblockheads.compat;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Collections;

public class WorkshopREIDisplay extends BasicDisplay {

    public WorkshopREIDisplay(Recipe<?> recipe) {
        super(EntryIngredients.ofIngredients(recipe.getIngredients()), Collections.singletonList(EntryIngredients.of(recipe.getResultItem(registryAccess()))));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CraftingForBlockheadsREI.WORKSHOP;
    }
}
