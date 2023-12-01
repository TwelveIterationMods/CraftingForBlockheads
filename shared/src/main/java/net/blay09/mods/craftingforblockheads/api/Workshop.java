package net.blay09.mods.craftingforblockheads.api;

import net.blay09.mods.craftingforblockheads.api.capability.WorkshopItemProvider;
import net.blay09.mods.craftingforblockheads.menu.WorkshopFilterWithStatus;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Workshop {
    ItemStack getActivatingItemStack();

    BlockState getActivatingBlockState();

    Set<Block> getProvidedBlocks();

    Set<TagKey<Block>> getProvidedBlockTags();

    List<WorkshopItemProvider> getItemProviders(@Nullable Player player);

    Set<String> getFulfilledPredicates(@Nullable Player player);

    Map<String, WorkshopFilterWithStatus> getAvailableFilters(@Nullable Player player);
}
