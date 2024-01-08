package net.blay09.mods.craftingforblockheads.crafting;

import net.blay09.mods.craftingforblockheads.api.Workshop;
import net.blay09.mods.craftingforblockheads.api.WorkshopItemProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CraftingContext {

    private final List<WorkshopItemProvider> itemProviders;

    public CraftingContext(final Workshop workshop, final @Nullable Player player) {
        itemProviders = workshop.getItemProviders(player);
    }

    public CraftingOperation createOperation(Recipe<?> recipe) {
        return new CraftingOperation(this, recipe);
    }

    public List<WorkshopItemProvider> getItemProviders() {
        return itemProviders;
    }
}
