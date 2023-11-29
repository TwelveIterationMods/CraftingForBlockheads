package net.blay09.mods.craftingforblockheads.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.blay09.mods.craftingforblockheads.client.gui.FilterButton;
import net.blay09.mods.craftingforblockheads.menu.WorkshopFilterWithStatus;
import net.blay09.mods.craftingforblockheads.menu.WorkshopMenu;
import net.blay09.mods.craftingforblockheads.menu.slot.CraftMatrixFakeSlot;
import net.blay09.mods.craftingforblockheads.crafting.RecipeWithStatus;
import net.blay09.mods.craftingforblockheads.menu.slot.CraftableFakeSlot;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class WorkshopScreen extends AbstractContainerScreen<WorkshopMenu> {

    private static final int SCROLLBAR_COLOR = 0xFFAAAAAA;
    private static final int SCROLLBAR_Y = 8;
    private static final int SCROLLBAR_WIDTH = 7;
    private static final int SCROLLBAR_HEIGHT = 74;

    private static final ResourceLocation guiTexture = new ResourceLocation(CraftingForBlockheads.MOD_ID, "textures/gui/gui.png");
    private static final int VISIBLE_ROWS = 4;
    private static final int VISIBLE_COLS = 5;

    private int scrollBarScaledHeight;
    private int scrollBarXPos;
    private int scrollBarYPos;
    private int currentOffset;

    private double mouseClickY = -1;
    private int indexWhenClicked;
    private int lastNumberOfMoves;

    private Button btnNextRecipe;
    private Button btnPrevRecipe;

    private EditBox searchBar;

    private final List<FilterButton> filterButtons = new ArrayList<>();

    private final String[] noCraftables;
    private final String[] noSelection;

    public WorkshopScreen(WorkshopMenu container, Inventory playerInventory, Component displayName) {
        super(container, playerInventory, displayName);

        noCraftables = I18n.get("gui.craftingforblockheads.no_craftables").split("\\\\n");
        noSelection = I18n.get("gui.craftingforblockheads.no_selection").split("\\\\n");
    }

    @Override
    protected void init() {
        imageHeight = 174;
        super.init();

        btnPrevRecipe = Button.builder(Component.literal("<"), it -> menu.nextRecipe(-1))
                .pos(leftPos + imageWidth - 36 - 14, topPos + 68).size(13, 15).build();
        btnPrevRecipe.visible = false;
        addRenderableWidget(btnPrevRecipe);

        btnNextRecipe = Button.builder(Component.literal(">"), it -> menu.nextRecipe(1))
                .pos(leftPos + imageWidth - 18 - 14, topPos + 68).size(13, 15).build();
        btnNextRecipe.visible = false;
        addRenderableWidget(btnNextRecipe);

        searchBar = new EditBox(minecraft.font, leftPos + 8, topPos - 5, 70, 10, searchBar, Component.empty());
        setInitialFocus(searchBar);

        int yOffset = -80;

        filterButtons.clear();
        final var availableFilters = menu.getWorkshop().getAvailableFilters(minecraft.player)
                .values()
                .stream()
                .sorted(Comparator.comparingInt(WorkshopFilterWithStatus::priority).reversed()).toList();
        for (WorkshopFilterWithStatus filter : availableFilters) {
            FilterButton filterButton = new FilterButton(leftPos - 20, height / 2 + yOffset, filter, menu);
            addRenderableWidget(filterButton);
            filterButtons.add(filterButton);

            yOffset += 20;
        }

        recalculateScrollBar();
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta == 0) {
            return false;
        }

        if (menu.getSelectedRecipe() != null && mouseX >= leftPos + 114 && mouseY >= topPos + 10 && mouseX < leftPos + 168 && mouseY < topPos + 64) {
            Slot slot = ((AbstractContainerScreenAccessor) this).getHoveredSlot();
            if (slot instanceof CraftMatrixFakeSlot fakeSlot && fakeSlot.getVisibleStacks().size() > 1) {
                final var lockedInput = fakeSlot.scrollDisplayListAndLock(delta > 0 ? -1 : 1);
                menu.setLockedInput(slot.getContainerSlot(), lockedInput);
            }
        } else {
            setCurrentOffset(delta > 0 ? currentOffset - 1 : currentOffset + 1);
        }

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        boolean result = super.mouseReleased(mouseX, mouseY, state);

        if (state != -1 && mouseClickY != -1) {
            mouseClickY = -1;
            indexWhenClicked = 0;
            lastNumberOfMoves = 0;
        }

        return result;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (button == 1 && mouseX >= searchBar.getX() && mouseX < searchBar.getX() + searchBar.getWidth() && mouseY >= searchBar.getY() && mouseY < searchBar.getY() + searchBar.getHeight()) {
            searchBar.setValue("");
            menu.search(null);
            menu.updateCraftableSlots();
            setCurrentOffset(currentOffset);
            return true;
        } else {
            if (searchBar.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        if (mouseX >= scrollBarXPos && mouseX <= scrollBarXPos + SCROLLBAR_WIDTH && mouseY >= scrollBarYPos && mouseY <= scrollBarYPos + scrollBarScaledHeight) {
            mouseClickY = mouseY;
            indexWhenClicked = currentOffset;
        }

        Slot mouseSlot = ((AbstractContainerScreenAccessor) this).getHoveredSlot();
        if (mouseSlot instanceof CraftMatrixFakeSlot fakeSlot) {
            if (button == 0) {
                ItemStack itemStack = mouseSlot.getItem();
                RecipeWithStatus recipe = menu.findRecipeForResultItem(itemStack);
                if (recipe != null) {
                    menu.selectCraftable(recipe);
                    setCurrentOffset(menu.getRecipesForSelectionIndex());
                }
            } else if (button == 1) {
                final var lockedInput = fakeSlot.toggleLock();
                menu.setLockedInput(mouseSlot.getContainerSlot(), lockedInput);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean charTyped(char c, int keyCode) {
        boolean result = super.charTyped(c, keyCode);

        menu.search(searchBar.getValue());
        menu.updateCraftableSlots();
        setCurrentOffset(currentOffset);

        return result;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.player.closeContainer();
            return true;
        }

        if (searchBar.keyPressed(keyCode, scanCode, modifiers) || searchBar.isFocused()) {
            menu.search(searchBar.getValue());
            menu.updateCraftableSlots();
            setCurrentOffset(currentOffset);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        if (menu.isDirty()) {
            setCurrentOffset(currentOffset);
            menu.setDirty(false);
        }

        guiGraphics.setColor(1f, 1f, 1f, 1f);
        guiGraphics.blit(guiTexture, leftPos, topPos - 10, 0, 0, imageWidth, imageHeight + 10);

        if (mouseClickY != -1) {
            float pixelsPerFilter = (SCROLLBAR_HEIGHT - scrollBarScaledHeight) / (float) Math.max(1,
                    (int) Math.ceil(menu.getItemListCount() / (float) VISIBLE_COLS) - VISIBLE_ROWS);
            if (pixelsPerFilter != 0) {
                int numberOfFiltersMoved = (int) ((mouseY - mouseClickY) / pixelsPerFilter);
                if (numberOfFiltersMoved != lastNumberOfMoves) {
                    setCurrentOffset(indexWhenClicked + numberOfFiltersMoved);
                    lastNumberOfMoves = numberOfFiltersMoved;
                }
            }
        }

        btnPrevRecipe.visible = menu.selectionHasRecipeVariants();
        btnPrevRecipe.active = menu.selectionHasPreviousRecipe();
        btnNextRecipe.visible = menu.selectionHasRecipeVariants();
        btnNextRecipe.active = menu.selectionHasNextRecipe();

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        Font font = minecraft.font;
        RecipeWithStatus selection = menu.getSelectedRecipe();
        if (selection == null) {
            int curY = topPos + 68 / 2 - noSelection.length / 2 * font.lineHeight;
            for (String s : noSelection) {
                guiGraphics.drawString(font, s, leftPos + 140 - font.width(s) / 2, curY, 0xFFFFFFFF, true);
                curY += font.lineHeight + 5;
            }
        } else {
            guiGraphics.blit(guiTexture, leftPos + 114, topPos + 10, 0, 184, 54, 54);
        }

        if (selection != null) {
            for (CraftMatrixFakeSlot slot : menu.getMatrixSlots()) {
                if (slot.isLocked() && slot.getVisibleStacks().size() > 1) {
                    guiGraphics.blit(guiTexture, leftPos + slot.x, topPos + slot.y, 176, 60, 16, 16);
                }
            }
        }

        guiGraphics.fill(scrollBarXPos, scrollBarYPos, scrollBarXPos + SCROLLBAR_WIDTH, scrollBarYPos + scrollBarScaledHeight, SCROLLBAR_COLOR);

        if (menu.getItemListCount() == 0) {
            guiGraphics.fill(leftPos + 8, topPos + 8, leftPos + 100, topPos + 82, 0xAA222222);
            int curY = topPos + 71 / 2 - noCraftables.length / 2 * font.lineHeight;
            for (String s : noCraftables) {
                guiGraphics.drawString(font, s, leftPos + 55 - font.width(s) / 2, curY, 0xFFFFFFFF, true);
                curY += font.lineHeight + 5;
            }
        }


        searchBar.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        var poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, 300);
        for (Slot slot : menu.slots) {
            if (slot instanceof CraftMatrixFakeSlot fakeSlot) {
                if (fakeSlot.isMissing() && !slot.getItem().isEmpty()) {
                    guiGraphics.fillGradient(leftPos + slot.x, topPos + slot.y, leftPos + slot.x + 16, topPos + slot.y + 16, 0x77FF4444, 0x77FF5555);
                }
            } else if (slot instanceof CraftableFakeSlot fakeSlot && fakeSlot.getCraftable() != null) {
                if (menu.isSelectedSlot(fakeSlot)) {
                    poseStack.translate(0, 0, -300);
                    guiGraphics.blit(guiTexture, leftPos + slot.x, topPos + slot.y, 176, 60, 16, 16);
                    poseStack.translate(0, 0, 300);
                }

                var slotRecipe = fakeSlot.getCraftable();
                final var selectedRecipe = menu.getSelectedRecipe();
                if (selectedRecipe != null && ItemStack.isSameItemSameTags(selectedRecipe.resultItem(), slotRecipe.resultItem())) {
                    slotRecipe = menu.getSelectedRecipe();
                }
                if (!slotRecipe.missingPredicates().isEmpty()) {
                    guiGraphics.fillGradient(leftPos + slot.x, topPos + slot.y, leftPos + slot.x + 16, topPos + slot.y + 16, 0x77222222, 0x77333333);
                    guiGraphics.blit(guiTexture, leftPos + slot.x, topPos + slot.y, 192, 76, 16, 16);
                } else if (!slotRecipe.missingIngredients().isEmpty()) {
                    guiGraphics.fillGradient(leftPos + slot.x, topPos + slot.y, leftPos + slot.x + 16, topPos + slot.y + 16, 0x77222222, 0x77333333);
                    guiGraphics.blit(guiTexture, leftPos + slot.x + 1, topPos + slot.y + 1, 208, 60, 16, 16);
                }
            }
        }
        poseStack.popPose();

        for (CraftMatrixFakeSlot matrixSlot : menu.getMatrixSlots()) {
            matrixSlot.updateSlot(partialTicks);
        }

        for (FilterButton sortButton : this.filterButtons) {
            if (sortButton.isMouseOver(mouseX, mouseY) && sortButton.active) {
                guiGraphics.renderTooltip(font, sortButton.getTooltipLines(), Optional.empty(), mouseX, mouseY);
            }
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void recalculateScrollBar() {
        int scrollBarTotalHeight = SCROLLBAR_HEIGHT - 1;
        this.scrollBarScaledHeight = (int) (scrollBarTotalHeight * Math.min(1f,
                ((float) VISIBLE_ROWS / (Math.ceil(menu.getItemListCount() / (float) VISIBLE_COLS)))));
        this.scrollBarXPos = leftPos + imageWidth - SCROLLBAR_WIDTH - 9 - 58;
        this.scrollBarYPos = topPos + SCROLLBAR_Y + ((scrollBarTotalHeight - scrollBarScaledHeight) * currentOffset / Math.max(1,
                (int) Math.ceil((menu.getItemListCount() / (float) VISIBLE_COLS)) - VISIBLE_ROWS));
    }

    private void setCurrentOffset(int currentOffset) {
        this.currentOffset = Math.max(0, Math.min(currentOffset, (int) Math.ceil(menu.getItemListCount() / (float) VISIBLE_COLS) - VISIBLE_ROWS));

        menu.setScrollOffset(this.currentOffset);

        recalculateScrollBar();
    }

    public List<FilterButton> getFilterButtons() {
        return filterButtons;
    }

}
