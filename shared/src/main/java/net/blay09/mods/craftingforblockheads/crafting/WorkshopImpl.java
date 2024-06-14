package net.blay09.mods.craftingforblockheads.crafting;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.api.provider.ProviderUtils;
import net.blay09.mods.craftingforblockheads.api.Workshop;
import net.blay09.mods.craftingforblockheads.api.WorkshopFilter;
import net.blay09.mods.craftingforblockheads.api.WorkshopItemProvider;
import net.blay09.mods.craftingforblockheads.api.WorkshopPredicate;
import net.blay09.mods.craftingforblockheads.menu.WorkshopFilterWithStatus;
import net.blay09.mods.craftingforblockheads.registry.CraftingForBlockheadsRegistry;
import net.blay09.mods.craftingforblockheads.tag.ModBlockTags;
import net.blay09.mods.craftingforblockheads.workshop.ContainerWorkshopItemProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class WorkshopImpl implements Workshop {

    private final ItemStack activatingItemStack;
    private final BlockState activatingBlockState;
    private final Set<BlockPos> checkedPos = new HashSet<>();
    private final List<WorkshopItemProvider> itemProviderList = new ArrayList<>();
    private final Set<Block> providedBlocks = new HashSet<>();
    private final Set<TagKey<Block>> providedBlockTags = new HashSet<>();

    public WorkshopImpl(ItemStack itemStack) {
        activatingItemStack = itemStack;
        activatingBlockState = Blocks.AIR.defaultBlockState();
    }

    public WorkshopImpl(Level level, BlockPos pos) {
        activatingBlockState = level.getBlockState(pos);
        activatingItemStack = ItemStack.EMPTY;
        providedBlocks.add(activatingBlockState.getBlock());
        activatingBlockState.getTags().forEach(providedBlockTags::add);
        findNeighbourCraftingBlocks(level, pos, true);
    }

    private void findNeighbourCraftingBlocks(Level level, BlockPos pos, boolean extendedUpSearch) {
        for (Direction direction : Direction.values()) {
            int upSearch = (extendedUpSearch && direction == Direction.UP) ? 2 : 1;
            for (int n = 1; n <= upSearch; n++) {
                BlockPos position = pos.relative(direction, n);
                if (!checkedPos.contains(position)) {
                    checkedPos.add(position);

                    BlockState state = level.getBlockState(position);
                    BlockEntity blockEntity = level.getBlockEntity(position);
                    if (blockEntity != null) {
                        WorkshopItemProvider itemProvider = ProviderUtils.getProvider(blockEntity, WorkshopItemProvider.class);
                        if (itemProvider != null) {
                            itemProviderList.add(itemProvider);

                            providedBlocks.add(state.getBlock());
                            state.getTags().forEach(providedBlockTags::add);

                            findNeighbourCraftingBlocks(level, position, true);
                            continue;
                        }
                    }
                    if (state.is(ModBlockTags.WORKSHOP_CONNECTORS) || state.is(ModBlockTags.IS_WORKSHOP_CORE)) {
                        providedBlocks.add(state.getBlock());
                        state.getTags().forEach(providedBlockTags::add);

                        findNeighbourCraftingBlocks(level, position, false);
                    }
                }
            }
        }
    }

    @Override
    public ItemStack getActivatingItemStack() {
        return activatingItemStack;
    }

    @Override
    public BlockState getActivatingBlockState() {
        return activatingBlockState;
    }

    @Override
    public Set<Block> getProvidedBlocks() {
        return providedBlocks;
    }

    @Override
    public Set<TagKey<Block>> getProvidedBlockTags() {
        return providedBlockTags;
    }

    @Override
    public List<WorkshopItemProvider> getItemProviders(@Nullable Player player) {
        List<WorkshopItemProvider> sourceInventories = Lists.newArrayList();
        sourceInventories.addAll(itemProviderList);
        if (player != null) {
            sourceInventories.add(new ContainerWorkshopItemProvider(player.getInventory()));
        }
        return sourceInventories;
    }

    @Override
    public Set<String> getFulfilledPredicates(@Nullable Player player) {
        final var fulfilledRequirements = new HashSet<String>();
        for (Map.Entry<String, WorkshopPredicate> predicate : CraftingForBlockheadsRegistry.getWorkshopPredicates().entrySet()) {
            if (predicate.getValue().isSatisfied(this, player)) {
                fulfilledRequirements.add(predicate.getKey());
            }
        }
        return fulfilledRequirements;
    }

    @Override
    public Map<String, WorkshopFilterWithStatus> getAvailableFilters(Set<String> fulfilledPredicates) {
        final var result = new HashMap<String, WorkshopFilterWithStatus>();
        for (Map.Entry<String, WorkshopFilter> entry : CraftingForBlockheadsRegistry.getWorkshopFilters().entrySet()) {
            if (fulfilledPredicates.containsAll(entry.getValue().getHardRequirements())) {
                final var missingPredicates = entry.getValue()
                        .getSoftRequirements()
                        .stream()
                        .filter(predicate -> !fulfilledPredicates.contains(predicate))
                        .collect(Collectors.toSet());
                final var filterWithStatus = new WorkshopFilterWithStatus(entry.getValue(), missingPredicates);
                result.put(entry.getKey(), filterWithStatus);
            }
        }
        return result;
    }
}
