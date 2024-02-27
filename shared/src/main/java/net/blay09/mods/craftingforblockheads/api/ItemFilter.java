package net.blay09.mods.craftingforblockheads.api;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public interface ItemFilter {
    default boolean test(Recipe<?> recipe, ItemStack itemStack) {
        return test(itemStack);
    }

    boolean test(ItemStack itemStack);

    ItemStack[] getItems();
}
