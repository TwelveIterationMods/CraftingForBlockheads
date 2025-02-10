package net.blay09.mods.craftingforblockheads.network.message;

import net.blay09.mods.craftingforblockheads.menu.WorkshopMenu;
import net.blay09.mods.craftingforblockheads.crafting.RecipeWithStatus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.*;

public class CraftablesListMessage {

    private final List<RecipeWithStatus> recipes;

    public CraftablesListMessage(List<RecipeWithStatus> recipes) {
        this.recipes = recipes;
    }

    public static CraftablesListMessage decode(FriendlyByteBuf buf) {
        final var recipeCount = buf.readInt();
        final var recipes = new ArrayList<RecipeWithStatus>(recipeCount);
        for (int i = 0; i < recipeCount; i++) {
            recipes.add(RecipeWithStatus.fromNetwork(buf));
        }
        return new CraftablesListMessage(recipes);
    }

    public static void encode(CraftablesListMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.recipes.size());
        for (RecipeWithStatus recipe : message.recipes) {
            recipe.toNetwork(buf);
        }
    }

    public static void handle(Player player, CraftablesListMessage message) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof WorkshopMenu) {
            ((WorkshopMenu) container).setCraftables(message.recipes);
        }
    }

}
