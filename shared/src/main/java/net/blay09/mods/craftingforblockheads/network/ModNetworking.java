package net.blay09.mods.craftingforblockheads.network;

import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.blay09.mods.craftingforblockheads.network.message.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.registerServerboundPacket(id("request_craftables"), RequestCraftablesMessage.class, RequestCraftablesMessage::encode, RequestCraftablesMessage::decode, RequestCraftablesMessage::handle);
        networking.registerServerboundPacket(id("request_recipes"), RequestRecipesMessage.class, RequestRecipesMessage::encode, RequestRecipesMessage::decode, RequestRecipesMessage::handle);
        networking.registerServerboundPacket(id("craft_recipe"), CraftRecipeMessage.class, CraftRecipeMessage::encode, CraftRecipeMessage::decode, CraftRecipeMessage::handle);

        networking.registerClientboundPacket(id("filters"), WorkshopFiltersMessage.class, WorkshopFiltersMessage::encode, WorkshopFiltersMessage::decode, WorkshopFiltersMessage::handle);
        networking.registerClientboundPacket(id("craftables"), CraftablesListMessage.class, CraftablesListMessage::encode, CraftablesListMessage::decode, CraftablesListMessage::handle);
        networking.registerClientboundPacket(id("recipes"), RecipesListMessage.class, RecipesListMessage::encode, RecipesListMessage::decode, RecipesListMessage::handle);
    }

    @NotNull
    private static ResourceLocation id(String name) {
        return new ResourceLocation(CraftingForBlockheads.MOD_ID, name);
    }

}
