package net.blay09.mods.craftingforblockheads.network.message;

import net.blay09.mods.craftingforblockheads.menu.WorkshopMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class RequestRecipesMessage {

    private final ItemStack outputItem;
    private final NonNullList<ItemStack> lockedInputs;

    public RequestRecipesMessage(ItemStack outputItem, NonNullList<ItemStack> lockedInputs) {
        this.outputItem = outputItem;
        this.lockedInputs = lockedInputs;
    }

    public static RequestRecipesMessage decode(FriendlyByteBuf buf) {
        ItemStack outputItem = buf.readItem();
        final var lockedInputsCount = buf.readByte();
        NonNullList<ItemStack> lockedInputs = NonNullList.createWithCapacity(lockedInputsCount);
        for (int i = 0; i < lockedInputsCount; i++) {
            lockedInputs.add(buf.readItem());
        }
        return new RequestRecipesMessage(outputItem, lockedInputs);
    }

    public static void encode(RequestRecipesMessage message, FriendlyByteBuf buf) {
        buf.writeItem(message.outputItem);
        buf.writeByte(message.lockedInputs.size());
        for (ItemStack itemstack : message.lockedInputs) {
            buf.writeItem(itemstack);
        }
    }

    public static void handle(ServerPlayer player, RequestRecipesMessage message) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof WorkshopMenu) {
            ((WorkshopMenu) container).handleRequestRecipes(message.outputItem, message.lockedInputs);
        }
    }
}
