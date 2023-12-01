package net.blay09.mods.craftingforblockheads.api;

import net.minecraft.world.item.ItemStack;

public interface ItemFilter {
    boolean test(ItemStack itemStack);
    ItemStack[] getItems();
}
