package net.blay09.mods.craftingforblockheads.api;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public interface RecipeWorkshopHandler<T extends Recipe<?>> {
    int mapToMatrixSlot(T recipe, int ingredientIndex);

    ItemStack assemble(T recipe, CraftingContainer craftingContainer, RegistryAccess registryAccess);
}
