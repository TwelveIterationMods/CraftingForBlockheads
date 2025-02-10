package net.blay09.mods.craftingforblockheads.tag;

import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
    public static final TagKey<Block> IS_WORKSHOP_CORE = TagKey.create(Registries.BLOCK,
            new ResourceLocation(CraftingForBlockheads.MOD_ID, "is_workshop_core"));
    public static final TagKey<Block> WORKSHOP_ITEM_PROVIDER = TagKey.create(Registries.BLOCK,
            new ResourceLocation(CraftingForBlockheads.MOD_ID, "workshop_item_providers"));
    public static final TagKey<Block> WORKSHOP_CONNECTORS = TagKey.create(Registries.BLOCK,
            new ResourceLocation(CraftingForBlockheads.MOD_ID, "workshop_connectors"));
}
