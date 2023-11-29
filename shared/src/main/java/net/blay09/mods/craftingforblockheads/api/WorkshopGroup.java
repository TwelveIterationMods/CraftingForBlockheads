package net.blay09.mods.craftingforblockheads.api;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public interface WorkshopGroup {
    Item getParentItem();
    List<Ingredient> getChildren();
}
