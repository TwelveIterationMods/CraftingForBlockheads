package net.blay09.mods.craftingforblockheads.network.message;

import net.blay09.mods.craftingforblockheads.menu.WorkshopMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class RequestCraftablesMessage {

    private final String filterId;

    public RequestCraftablesMessage(String filterId) {
        this.filterId = filterId;
    }

    public static RequestCraftablesMessage decode(FriendlyByteBuf buf) {
        final var filterId = buf.readUtf();
        return new RequestCraftablesMessage(filterId);
    }

    public static void encode(RequestCraftablesMessage message, FriendlyByteBuf buf) {
        buf.writeUtf(message.filterId);
    }

    public static void handle(ServerPlayer player, RequestCraftablesMessage message) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof WorkshopMenu) {
            ((WorkshopMenu) container).handleRequestCraftables(message.filterId);
        }
    }
}
