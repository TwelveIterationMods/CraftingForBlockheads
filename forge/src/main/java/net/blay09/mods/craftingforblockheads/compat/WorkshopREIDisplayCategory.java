package net.blay09.mods.craftingforblockheads.compat;

import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.blay09.mods.craftingforblockheads.block.ModBlocks;
import net.minecraft.network.chat.Component;

public class WorkshopREIDisplayCategory implements DisplayCategory<WorkshopREIDisplay> {
    @Override
    public CategoryIdentifier<? extends WorkshopREIDisplay> getCategoryIdentifier() {
        return CraftingForBlockheadsREI.WORKSHOP;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("rei.craftingforblockheads.workshop.title");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.workbench);
    }
}
