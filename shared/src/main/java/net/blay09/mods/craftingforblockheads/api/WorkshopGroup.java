package net.blay09.mods.craftingforblockheads.api;

import net.minecraft.world.item.Item;

import java.util.List;

public interface WorkshopGroup {
    Item getParentItem();
    List<ItemFilter> getChildren();
}
