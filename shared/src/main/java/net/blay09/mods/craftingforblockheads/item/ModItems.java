package net.blay09.mods.craftingforblockheads.item;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.item.BalmItems;
import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.blay09.mods.craftingforblockheads.block.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModItems {

    public static DeferredObject<CreativeModeTab> creativeModeTab;

    public static void initialize(BalmItems items) {
        creativeModeTab = items.registerCreativeModeTab(id("craftingforblockheads"), () -> new ItemStack(ModBlocks.workbench));
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(CraftingForBlockheads.MOD_ID, name);
    }
}
