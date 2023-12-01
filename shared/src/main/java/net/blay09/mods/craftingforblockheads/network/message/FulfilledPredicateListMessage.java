package net.blay09.mods.craftingforblockheads.network.message;

import net.blay09.mods.craftingforblockheads.menu.WorkshopMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.HashSet;
import java.util.Set;

public class FulfilledPredicateListMessage {

    private final Set<String> fulfilledPredicates;

    public FulfilledPredicateListMessage(Set<String> fulfilledPredicates) {
        this.fulfilledPredicates = fulfilledPredicates;
    }

    public static FulfilledPredicateListMessage decode(FriendlyByteBuf buf) {
        final var count = buf.readInt();
        final var fulfilledPredicates = new HashSet<String>(count);
        for (int i = 0; i < count; i++) {
            fulfilledPredicates.add(buf.readUtf());
        }
        return new FulfilledPredicateListMessage(fulfilledPredicates);
    }

    public static void encode(FulfilledPredicateListMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.fulfilledPredicates.size());
        for (final var fulfilledPredicate : message.fulfilledPredicates) {
            buf.writeUtf(fulfilledPredicate);
        }
    }

    public static void handle(Player player, FulfilledPredicateListMessage message) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof WorkshopMenu) {
            ((WorkshopMenu) container).setFulfilledPredicates(message.fulfilledPredicates);
        }
    }

}
