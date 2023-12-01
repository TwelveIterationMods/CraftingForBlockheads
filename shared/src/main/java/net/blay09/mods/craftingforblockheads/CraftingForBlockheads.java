package net.blay09.mods.craftingforblockheads;

import com.google.gson.JsonElement;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.UseBlockEvent;
import net.blay09.mods.balm.api.event.UseItemEvent;
import net.blay09.mods.balm.api.event.server.ServerReloadFinishedEvent;
import net.blay09.mods.balm.api.event.server.ServerStartedEvent;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.craftingforblockheads.api.CraftingForBlockheadsAPI;
import net.blay09.mods.craftingforblockheads.api.WorkshopPredicate;
import net.blay09.mods.craftingforblockheads.block.ModBlocks;
import net.blay09.mods.craftingforblockheads.crafting.WorkshopImpl;
import net.blay09.mods.craftingforblockheads.menu.ModMenus;
import net.blay09.mods.craftingforblockheads.item.ModItems;
import net.blay09.mods.craftingforblockheads.menu.WorkshopMenu;
import net.blay09.mods.craftingforblockheads.network.ModNetworking;
import net.blay09.mods.craftingforblockheads.block.entity.ModBlockEntities;
import net.blay09.mods.craftingforblockheads.registry.CraftingForBlockheadsRegistry;
import net.blay09.mods.craftingforblockheads.registry.json.JsonCompatLoader;
import net.blay09.mods.craftingforblockheads.tag.ModBlockTags;
import net.blay09.mods.craftingforblockheads.tag.ModItemTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.blay09.mods.craftingforblockheads.registry.json.JsonCompatLoader.predicateFromJson;

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

        CraftingForBlockheadsAPI.registerWorkshopPredicateDeserializer("all_of", jsonObject -> {
            final var conditions = GsonHelper.getAsJsonArray(jsonObject, "conditions");
            if (conditions.isEmpty()) {
                throw new IllegalArgumentException("all_of predicate requires at least one condition");
            }

            WorkshopPredicate combinedPredicate = null;
            for (JsonElement condition : conditions) {
                final var predicate = predicateFromJson(condition.getAsJsonObject());
                if (combinedPredicate == null) {
                    combinedPredicate = predicate;
                } else {
                    combinedPredicate = combinedPredicate.and(predicate);
                }
            }
            return combinedPredicate;
        });

        CraftingForBlockheadsAPI.registerWorkshopPredicateDeserializer("any_of", jsonObject -> {
            final var conditions = GsonHelper.getAsJsonArray(jsonObject, "conditions");
            if (conditions.isEmpty()) {
                throw new IllegalArgumentException("any_of predicate requires at least one condition");
            }

            WorkshopPredicate combinedPredicate = null;
            for (JsonElement condition : conditions) {
                final var predicate = predicateFromJson(condition.getAsJsonObject());
                if (combinedPredicate == null) {
                    combinedPredicate = predicate;
                } else {
                    combinedPredicate = combinedPredicate.or(predicate);
                }
            }
            return combinedPredicate;
        });

        CraftingForBlockheadsAPI.registerWorkshopPredicateDeserializer("none_of", jsonObject -> {
            final var conditions = GsonHelper.getAsJsonArray(jsonObject, "conditions");
            if (conditions.isEmpty()) {
                throw new IllegalArgumentException("none_of predicate requires at least one condition");
            }

            WorkshopPredicate combinedPredicate = null;
            for (JsonElement condition : conditions) {
                final var predicate = predicateFromJson(condition.getAsJsonObject());
                if (combinedPredicate == null) {
                    combinedPredicate = predicate;
                } else {
                    combinedPredicate = combinedPredicate.and(predicate);
                }
            }
            return combinedPredicate != null ? combinedPredicate.negate() : (workshop, player) -> true;
        });

        CraftingForBlockheadsAPI.registerWorkshopPredicateDeserializer("not", jsonObject -> {
            final var condition = GsonHelper.getAsJsonObject(jsonObject, "condition");
            final var predicate = predicateFromJson(condition);
            return predicate.negate();
        });

        CraftingForBlockheadsAPI.registerWorkshopPredicateDeserializer("meets_predicate", jsonObject -> {
            final var predicateId = GsonHelper.getAsString(jsonObject, "predicate");
            return (workshop, player) -> {
                final var predicate = CraftingForBlockheadsRegistry.getWorkshopPredicates().get(predicateId);
                return predicate.isSatisfied(workshop, player);
            };
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

        Balm.getEvents().onEvent(UseBlockEvent.class, event -> {
            final var player = event.getPlayer();
            final var level = player.level();
            final var pos = event.getHitResult().getBlockPos();
            final var state = level.getBlockState(pos);
            if (state.is(ModBlockTags.WORKSHOP_CORE)) {
                Balm.getNetworking().openGui(player, new BalmMenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("container.craftingforblockheads.workbench");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                        return new WorkshopMenu(ModMenus.workbench.get(), i, player, new WorkshopImpl(level, pos));
                    }

                    @Override
                    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                        buf.writeBlockPos(pos);
                    }
                });
                event.setResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        });

        Balm.getEvents().onEvent(UseItemEvent.class, event -> {
            final var player = event.getPlayer();
            final var itemStack = player.getItemInHand(event.getHand());
            if (itemStack.is(ModItemTags.WORKSHOP_CORE)) {
                Balm.getNetworking().openGui(player, new BalmMenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("container.craftingforblockheads.workbench");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                        return new WorkshopMenu(ModMenus.workbenchItem.get(), i, player, new WorkshopImpl(itemStack));
                    }

                    @Override
                    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                        buf.writeItem(itemStack);
                    }
                });
                event.setResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        });
    }

}
