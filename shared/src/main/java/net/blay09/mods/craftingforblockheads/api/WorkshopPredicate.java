package net.blay09.mods.craftingforblockheads.api;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface WorkshopPredicate {
    boolean isSatisfied(Workshop workshop, @Nullable Player player);

    default WorkshopPredicate and(WorkshopPredicate other) {
        return (workshop, player) -> isSatisfied(workshop, player) && other.isSatisfied(workshop, player);
    }
}
