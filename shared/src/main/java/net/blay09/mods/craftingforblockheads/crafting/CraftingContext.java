package net.blay09.mods.craftingforblockheads.crafting;

import net.blay09.mods.craftingforblockheads.api.Workshop;
import net.blay09.mods.craftingforblockheads.api.capability.IWorkshopItemProvider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CraftingContext {

    private final List<IWorkshopItemProvider> itemProviders;

    public CraftingContext(final Workshop workshop, final @Nullable Player player) {
        itemProviders = workshop.getItemProviders(player);
    }

    public CraftingOperation createOperation(Recipe<?> recipe) {
        return new CraftingOperation(this, recipe);
    }

    public List<IWorkshopItemProvider> getItemProviders() {
        return itemProviders;
    }
}
