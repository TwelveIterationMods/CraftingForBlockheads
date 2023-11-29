package net.blay09.mods.craftingforblockheads.tag;

import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> WORKSHOP_CORE = TagKey.create(Registries.ITEM, new ResourceLocation(CraftingForBlockheads.MOD_ID, "workshop_core"));
    public static final TagKey<Item> SECRET = TagKey.create(Registries.ITEM, new ResourceLocation(CraftingForBlockheads.MOD_ID, "secret"));
    public static final TagKey<Item> EXCLUSIVE = TagKey.create(Registries.ITEM, new ResourceLocation(CraftingForBlockheads.MOD_ID, "exclusive"));
}
