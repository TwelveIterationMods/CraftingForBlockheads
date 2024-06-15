package net.blay09.mods.craftingforblockheads.block.entity;

import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.blay09.mods.craftingforblockheads.api.ItemFilter;
import net.blay09.mods.craftingforblockheads.api.WorkshopFilter;
import net.blay09.mods.craftingforblockheads.crafting.WorkshopImpl;
import net.blay09.mods.craftingforblockheads.menu.ModMenus;
import net.blay09.mods.craftingforblockheads.menu.WorkshopMenu;
import net.blay09.mods.craftingforblockheads.network.WorkshopFilterSerialization;
import net.blay09.mods.craftingforblockheads.registry.IngredientItemFilter;
import net.blay09.mods.craftingforblockheads.registry.NbtIngredientItemFilter;
import net.blay09.mods.craftingforblockheads.registry.RecipeFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;


public class WorkbenchBlockEntity extends BalmBlockEntity implements BalmMenuProvider {

    public WorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.workbench.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.craftingforblockheads.workbench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        final var workshop = new WorkshopImpl(level, worldPosition);
        final var fulfilledPredicates = workshop.getFulfilledPredicates(player);
        final var availableFilters = workshop.getAvailableFilters(fulfilledPredicates);
        return new WorkshopMenu(ModMenus.workbench.get(), i, player, availableFilters, workshop);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        final var workshop = new WorkshopImpl(level, worldPosition);
        final var fulfilledPredicates = workshop.getFulfilledPredicates(player);
        final var availableFilters = workshop.getAvailableFilters(fulfilledPredicates);
        buf.writeBlockPos(worldPosition);
        WorkshopFilterSerialization.writeAvailableFilters(buf, availableFilters);
    }

}
