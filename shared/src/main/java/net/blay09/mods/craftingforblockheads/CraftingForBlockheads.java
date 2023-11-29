package net.blay09.mods.craftingforblockheads;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.server.ServerReloadFinishedEvent;
import net.blay09.mods.balm.api.event.server.ServerStartedEvent;
import net.blay09.mods.craftingforblockheads.api.CraftingForBlockheadsAPI;
import net.blay09.mods.craftingforblockheads.block.ModBlocks;
import net.blay09.mods.craftingforblockheads.menu.ModMenus;
import net.blay09.mods.craftingforblockheads.item.ModItems;
import net.blay09.mods.craftingforblockheads.network.ModNetworking;
import net.blay09.mods.craftingforblockheads.block.entity.ModBlockEntities;
import net.blay09.mods.craftingforblockheads.registry.CraftingForBlockheadsRegistry;
import net.blay09.mods.craftingforblockheads.registry.json.JsonCompatLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CraftingForBlockheads {

    public static final String MOD_ID = "craftingforblockheads";
    public static final Logger logger = LogManager.getLogger(MOD_ID);

    public static void initialize() {
        CraftingForBlockheadsConfig.initialize();
        ModNetworking.initialize(Balm.getNetworking());
        ModBlocks.initialize(Balm.getBlocks());
        ModBlockEntities.initialize(Balm.getBlockEntities());
        ModItems.initialize(Balm.getItems());
        ModMenus.initialize(Balm.getMenus());

        CraftingForBlockheadsAPI.registerRecipeWorkshopHandler(ShapedRecipe.class, new ShapedRecipeWorkshopHandler());
        CraftingForBlockheadsAPI.registerRecipeWorkshopHandler(ShapelessRecipe.class, new ShapelessRecipeWorkshopHandler());

        CraftingForBlockheadsAPI.registerWorkshopPredicateDeserializer("workshop_core", jsonObject -> {
            final var blockId = GsonHelper.getAsString(jsonObject, "block", null);
            final var itemId = GsonHelper.getAsString(jsonObject, "item", null);
            final var tagId = GsonHelper.getAsString(jsonObject, "tag", null);

            if (blockId != null) {
                final var block = Balm.getRegistries().getBlock(new ResourceLocation(blockId));
                return (workshop, player) -> workshop.getActivatingBlockState().is(block);
            } else if (itemId != null) {
                final var item = Balm.getRegistries().getItem(new ResourceLocation(itemId));
                return (workshop, player) -> workshop.getActivatingItemStack().getItem() == item;
            } else if (tagId != null) {
                ResourceLocation tagResourceLocation = new ResourceLocation(tagId);
                final var itemTag = Balm.getRegistries().getItemTag(tagResourceLocation);
                final var blockTag = TagKey.create(Registries.BLOCK, tagResourceLocation);
                return (workshop, player) -> workshop.getActivatingBlockState().is(blockTag) || workshop.getActivatingItemStack().is(itemTag);
            }

            throw new IllegalArgumentException("workshop_core predicate requires either block, item or tag");
        });

        CraftingForBlockheadsAPI.registerWorkshopPredicateDeserializer("workshop_has", jsonObject -> {
            final var blockId = GsonHelper.getAsString(jsonObject, "block", null);
            final var tagId = GsonHelper.getAsString(jsonObject, "tag", null);

            if (blockId != null) {
                final var block = Balm.getRegistries().getBlock(new ResourceLocation(blockId));
                return (workshop, player) -> workshop.getProvidedBlocks().contains(block);
            } else if (tagId != null) {
                final var blockTag = TagKey.create(Registries.BLOCK, new ResourceLocation(tagId));
                return (workshop, player) -> workshop.getProvidedBlockTags().contains(blockTag);
            }

            throw new IllegalArgumentException("workshop_has predicate requires either block or tag");
        });

        Balm.addServerReloadListener(new ResourceLocation(MOD_ID, "json_registry"), new JsonCompatLoader());

        Balm.getEvents()
                .onEvent(ServerReloadFinishedEvent.class,
                        (ServerReloadFinishedEvent event) -> CraftingForBlockheadsRegistry.reload(event.getServer().getRecipeManager(),
                                event.getServer().registryAccess()));

        Balm.getEvents().onEvent(ServerStartedEvent.class, event -> {
            RecipeManager recipeManager = event.getServer().getRecipeManager();
            CraftingForBlockheadsRegistry.reload(recipeManager, event.getServer().registryAccess());
        });
    }

}
