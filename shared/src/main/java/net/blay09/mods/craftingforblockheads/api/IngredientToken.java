package net.blay09.mods.craftingforblockheads.api;

import net.minecraft.world.item.ItemStack;

public interface IngredientToken {
    ItemStack peek();
    ItemStack consume();
    ItemStack restore(ItemStack itemStack);
}
