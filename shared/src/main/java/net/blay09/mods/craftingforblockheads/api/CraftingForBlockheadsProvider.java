package net.blay09.mods.craftingforblockheads.api;

import java.util.Collection;
import java.util.Map;

public interface CraftingForBlockheadsProvider {
    String getModId();

    Map<String, WorkshopFilter> getFilters();

    Map<String, WorkshopPredicate> getPredicates();

    Collection<ItemFilter> getCraftables();

    Collection<WorkshopGroup> getGroups();
}
