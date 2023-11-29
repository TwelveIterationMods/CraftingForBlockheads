package net.blay09.mods.craftingforblockheads.menu;

import net.blay09.mods.craftingforblockheads.api.WorkshopFilter;

import java.util.Set;

public record WorkshopFilterWithStatus(WorkshopFilter filter, Set<String> missingPredicates) {
    public boolean available() {
        return missingPredicates.isEmpty();
    }

    public int priority() {
        return filter.getPriority();
    }
}
