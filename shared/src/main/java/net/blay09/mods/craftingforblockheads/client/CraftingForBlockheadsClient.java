package net.blay09.mods.craftingforblockheads.client;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.event.client.ItemTooltipEvent;
import net.blay09.mods.balm.api.event.client.RecipesUpdatedEvent;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.craftingforblockheads.client.gui.screen.WorkshopScreen;
import net.blay09.mods.craftingforblockheads.menu.WorkshopMenu;
import net.blay09.mods.craftingforblockheads.menu.slot.CraftMatrixFakeSlot;
import net.blay09.mods.craftingforblockheads.menu.slot.CraftableFakeSlot;
import net.blay09.mods.craftingforblockheads.crafting.RecipeWithStatus;
import net.blay09.mods.craftingforblockheads.registry.CraftingForBlockheadsRegistry;
import net.blay09.mods.craftingforblockheads.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.Slot;

public class CraftingForBlockheadsClient {
    public static void initialize() {
        ModRenderers.initialize(BalmClient.getRenderers());
        ModScreens.initialize(BalmClient.getScreens());
        ModTextures.initialize(BalmClient.getTextures());
        ModModels.initialize(BalmClient.getModels());

        Balm.getEvents().onEvent(RecipesUpdatedEvent.class, event -> CraftingForBlockheadsRegistry.reload(event.getRecipeManager(), event.getRegistryAccess()));

        Balm.getEvents().onEvent(ItemTooltipEvent.class, event -> {
            if (!(Minecraft.getInstance().screen instanceof WorkshopScreen screen)) {
                return;
            }

            WorkshopMenu menu = screen.getMenu();
            Slot hoverSlot = ((AbstractContainerScreenAccessor) screen).getHoveredSlot();
            if (hoverSlot instanceof CraftableFakeSlot recipeSlot && event.getItemStack() == hoverSlot.getItem()) {
                final var craftable = recipeSlot.getCraftable();
                if (menu.isSelectedSlot(recipeSlot)) {
                    RecipeWithStatus recipeWithStatus = menu.getSelectedRecipe();
                    if (recipeWithStatus == null) {
                        return;
                    }

                    if (!recipeWithStatus.missingPredicates().isEmpty()) {
                        for (String missingPredicate : recipeWithStatus.missingPredicates()) {
                            event.getToolTip()
                                    .add(TextUtils.coloredTextComponent("tooltip.craftingforblockheads.missing_" + missingPredicate, ChatFormatting.RED));
                        }
                    } else if (!recipeWithStatus.missingIngredients().isEmpty()) {
                        event.getToolTip().add(TextUtils.coloredTextComponent("tooltip.craftingforblockheads.missing_ingredients", ChatFormatting.RED));
                    } else if (Screen.hasShiftDown()) {
                        event.getToolTip().add(TextUtils.coloredTextComponent("tooltip.craftingforblockheads.click_to_craft_stack", ChatFormatting.GREEN));
                    } else {
                        event.getToolTip().add(TextUtils.coloredTextComponent("tooltip.craftingforblockheads.click_to_craft_one", ChatFormatting.GREEN));
                    }
                } else if (craftable != null && !craftable.missingPredicates().isEmpty()) {
                    for (String missingPredicate : craftable.missingPredicates()) {
                        event.getToolTip()
                                .add(TextUtils.coloredTextComponent("tooltip.craftingforblockheads.missing_" + missingPredicate, ChatFormatting.RED));
                    }
                } else if (craftable != null && !craftable.missingIngredients().isEmpty()) {
                    event.getToolTip().add(TextUtils.coloredTextComponent("tooltip.craftingforblockheads.missing_ingredients", ChatFormatting.RED));
                } else {
                    event.getToolTip().add(TextUtils.coloredTextComponent("tooltip.craftingforblockheads.click_to_see_recipe", ChatFormatting.YELLOW));
                }
            } else if (hoverSlot instanceof CraftMatrixFakeSlot fakeSlot && event.getItemStack() == hoverSlot.getItem()) {
                if (fakeSlot.getVisibleStacks().size() > 1) {
                    if (fakeSlot.isMissing()) {
                        if (fakeSlot.isLocked()) {
                            event.getToolTip().add(TextUtils.coloredTextComponent("tooltip.craftingforblockheads.click_to_unlock", ChatFormatting.GREEN));
                        } else {
                            event.getToolTip().add(TextUtils.coloredTextComponent("tooltip.craftingforblockheads.click_to_lock", ChatFormatting.GREEN));
                        }
                    }
                    event.getToolTip().add(TextUtils.coloredTextComponent("tooltip.craftingforblockheads.scroll_to_switch", ChatFormatting.YELLOW));
                }
            }
        });

    }
}
