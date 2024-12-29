package net.blay09.mods.craftingforblockheads.network.message;

import net.blay09.mods.craftingforblockheads.crafting.WorkshopImpl;
import net.blay09.mods.craftingforblockheads.menu.WorkshopFilterWithStatus;
import net.blay09.mods.craftingforblockheads.menu.WorkshopMenu;
import net.blay09.mods.craftingforblockheads.network.WorkshopFilterSerialization;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Map;

public class WorkshopFiltersMessage {

    private final Map<String, WorkshopFilterWithStatus> availableFilters;

    public WorkshopFiltersMessage(WorkshopImpl workshop, Player player) {
        final var fulfilledPredicates = workshop.getFulfilledPredicates(player);
        this.availableFilters = workshop.getAvailableFilters(fulfilledPredicates);
    }

    public WorkshopFiltersMessage(Map<String, WorkshopFilterWithStatus> availableFilters) {
        this.availableFilters = availableFilters;
    }

    public static WorkshopFiltersMessage decode(FriendlyByteBuf buf) {
        return new WorkshopFiltersMessage(WorkshopFilterSerialization.readAvailableFilters(buf));
    }

    public static void encode(WorkshopFiltersMessage msg, FriendlyByteBuf buf) {
        WorkshopFilterSerialization.writeAvailableFilters(buf, msg.availableFilters);
    }

    public static void handle(Player player, WorkshopFiltersMessage message) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof WorkshopMenu) {
            ((WorkshopMenu) container).setAvailableFilters(message.availableFilters);
        }
    }
}
