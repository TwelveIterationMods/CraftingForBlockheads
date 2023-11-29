package net.blay09.mods.craftingforblockheads.api;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Set;

public interface WorkshopFilter {
    String getId();
    Component getName();

    ItemStack getIcon();

    Component getTooltip();

    List<Ingredient> getIncludes();

    List<Ingredient> getExcludes();

    Set<String> getHardRequirements();

    Set<String> getSoftRequirements();

    int getPriority();
}
