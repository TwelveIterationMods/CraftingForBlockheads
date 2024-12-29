package net.blay09.mods.craftingforblockheads.tag;

import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> IS_WORKSHOP_CORE = TagKey.create(Registries.ITEM, new ResourceLocation(CraftingForBlockheads.MOD_ID, "is_workshop_core"));
    public static final TagKey<Item> IS_WORKSHOP_EXCLUSIVE = TagKey.create(Registries.ITEM,
            new ResourceLocation(CraftingForBlockheads.MOD_ID, "is_workshop_exclusive"));
    public static final TagKey<Item> CRAFTABLE_GENERAL = TagKey.create(Registries.ITEM,
            new ResourceLocation(CraftingForBlockheads.MOD_ID, "craftable/general"));
    public static final TagKey<Item> CRAFTABLE_CARPENTRY = TagKey.create(Registries.ITEM,
            new ResourceLocation(CraftingForBlockheads.MOD_ID, "craftable/carpentry"));
    public static final TagKey<Item> CRAFTABLE_MASONRY = TagKey.create(Registries.ITEM,
            new ResourceLocation(CraftingForBlockheads.MOD_ID, "craftable/masonry"));
    public static final TagKey<Item> CRAFTABLE_TINKERING = TagKey.create(Registries.ITEM,
            new ResourceLocation(CraftingForBlockheads.MOD_ID, "craftable/tinkering"));
    public static final TagKey<Item> CRAFTABLE_FLETCHING = TagKey.create(Registries.ITEM,
            new ResourceLocation(CraftingForBlockheads.MOD_ID, "craftable/fletching"));
    public static final TagKey<Item> CRAFTABLE_BLACKSMITHING = TagKey.create(Registries.ITEM, new ResourceLocation(CraftingForBlockheads.MOD_ID, "craftable/blacksmithing"));
    public static final TagKey<Item> CRAFTABLE_PRECIOUS_SMITHING = TagKey.create(Registries.ITEM, new ResourceLocation(CraftingForBlockheads.MOD_ID, "craftable/precious_smithing"));
    public static final TagKey<Item> CRAFTABLE_ENGINEERING = TagKey.create(Registries.ITEM,
            new ResourceLocation(CraftingForBlockheads.MOD_ID, "craftable/engineering"));
    public static final TagKey<Item> CRAFTABLE_TAILORING = TagKey.create(Registries.ITEM,
            new ResourceLocation(CraftingForBlockheads.MOD_ID, "craftable/tailoring"));

}
