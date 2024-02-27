package net.blay09.mods.craftingforblockheads.menu;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.blay09.mods.craftingforblockheads.api.Workshop;
import net.blay09.mods.craftingforblockheads.crafting.CraftingContext;
import net.blay09.mods.craftingforblockheads.crafting.WorkshopImpl;
import net.blay09.mods.craftingforblockheads.api.WorkshopFilter;
import net.blay09.mods.craftingforblockheads.network.message.*;
import net.blay09.mods.craftingforblockheads.registry.WorkshopPredicateLevel;
import net.blay09.mods.craftingforblockheads.util.CraftableComparator;
import net.blay09.mods.craftingforblockheads.menu.slot.CraftMatrixFakeSlot;
import net.blay09.mods.craftingforblockheads.menu.slot.CraftableFakeSlot;
import net.blay09.mods.craftingforblockheads.registry.CraftingForBlockheadsRegistry;
import net.blay09.mods.craftingforblockheads.crafting.RecipeWithStatus;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WorkshopMenu extends AbstractContainerMenu {

    private final Player player;
    private final WorkshopImpl workshop;

    private final List<CraftableFakeSlot> craftableSlots = new ArrayList<>();
    private final List<CraftMatrixFakeSlot> matrixSlots = new ArrayList<>();

    private final NonNullList<ItemStack> lockedInputs = NonNullList.withSize(9, ItemStack.EMPTY);

    private final List<RecipeWithStatus> filteredItems = new ArrayList<>();

    private String currentSearch;
    private WorkshopFilter currentFilter;
    private Comparator<RecipeWithStatus> currentSorting = new CraftableComparator();

    private List<RecipeWithStatus> craftables = new ArrayList<>();
    private Set<String> fulfilledPredicates = new HashSet<>();
    private Map<String, WorkshopFilterWithStatus> availableFilters = new HashMap<>();

    private boolean filtersDirty = true;
    private boolean craftablesDirty = true;
    private boolean recipesDirty = true;
    private boolean isDirtyClient;
    private int scrollOffset;
    private RecipeWithStatus selectedCraftable;
    private List<RecipeWithStatus> recipesForSelection;
    private int recipesForSelectionIndex;

    public WorkshopMenu(MenuType<WorkshopMenu> containerType, int windowId, Player player, WorkshopImpl workshop) {
        super(containerType, windowId);

        this.player = player;
        this.workshop = workshop;

        final var fulfilledPredicates = workshop.getFulfilledPredicates(player);
        currentFilter = workshop.getAvailableFilters(fulfilledPredicates).values()
                .stream()
                .filter(WorkshopFilterWithStatus::available)
                .map(WorkshopFilterWithStatus::filter)
                .max(Comparator.comparing(WorkshopFilter::getPriority))
                .orElse(null);

        Container fakeInventory = new DefaultContainer(5 * 4 + 3 * 3);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                CraftableFakeSlot slot = new CraftableFakeSlot(fakeInventory, j + i * 5, 10 + j * 18, 10 + i * 18);
                craftableSlots.add(slot);
                addSlot(slot);
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                CraftMatrixFakeSlot slot = new CraftMatrixFakeSlot(fakeInventory, j + i * 3, 115 + j * 18, 11 + i * 18);
                matrixSlots.add(slot);
                addSlot(slot);
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(player.getInventory(), j + i * 9 + 9, 8 + j * 18, 92 + i * 18) {
                    @Override
                    public void setChanged() {
                        craftablesDirty = true;
                        recipesDirty = true;
                        filtersDirty = true;
                    }
                });
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(player.getInventory(), i, 8 + i * 18, 150) {
                @Override
                public void setChanged() {
                    craftablesDirty = true;
                    recipesDirty = true;
                    filtersDirty = true;
                }
            });
        }
    }

    @Override
    public void clicked(int slotNumber, int dragType, ClickType clickType, Player player) {
        var handled = false;
        if (slotNumber >= 0 && slotNumber < slots.size()) {
            Slot slot = slots.get(slotNumber);
            if (slot instanceof CraftableFakeSlot craftableSlot) {
                if (player.level().isClientSide) {
                    if (isSelectedSlot(craftableSlot)) {
                        if (clickType == ClickType.PICKUP || clickType == ClickType.PICKUP_ALL || clickType == ClickType.QUICK_MOVE || clickType == ClickType.CLONE) {
                            requestCraft(clickType == ClickType.QUICK_MOVE, clickType == ClickType.CLONE);
                            handled = true;
                        }
                    } else {
                        selectCraftable(craftableSlot.getCraftable());
                        handled = true;
                    }
                }
            }
        }

        if (!handled) {
            super.clicked(slotNumber, dragType, clickType, player);
        }
    }


    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (filtersDirty) {
            broadcastFulfilledPredicates();
            filtersDirty = false;
        }

        if (craftablesDirty) {
            broadcastCraftables(currentFilter != null ? currentFilter.getId() : null);
            craftablesDirty = false;
        }

        if (recipesDirty) {
            if (selectedCraftable != null) {
                broadcastRecipesForResultItem(selectedCraftable.resultItem());
            }
            recipesDirty = false;
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void slotsChanged(Container inventory) {
        // NOP, we don't want detectAndSendChanges called here, otherwise it will spam on crafting a stack of items
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();
            if (slotIndex >= 56 && slotIndex < 65) {
                if (!moveItemStackTo(slotStack, 29, 56, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= 29 && slotIndex < 56) {
                if (!moveItemStackTo(slotStack, 56, 65, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }

    public void selectCraftable(@Nullable RecipeWithStatus craftable) {
        selectedCraftable = craftable;
        resetSelectedRecipe();
        updateCraftableSlots();

        if (craftable != null) {
            if (player.level().isClientSide) {
                lockedInputs.clear();
                requestRecipes(craftable);
            }
        } else {
            resetSelectedRecipe();
            updateMatrixSlots();
        }
    }

    public void resetSelectedRecipe() {
        recipesForSelection = null;
        recipesForSelectionIndex = 0;
        updateMatrixSlots();
    }

    public void requestCraftables(WorkshopFilter filter) {
        Balm.getNetworking().sendToServer(new RequestCraftablesMessage(filter.getId()));
    }

    public void handleRequestCraftables(String filterId) {
        final var filter = availableFilters.get(filterId);
        if (filter != null) {
            setCurrentFilter(filter.filter());
        }

        craftablesDirty = true;
    }

    public void requestRecipes(RecipeWithStatus craftable) {
        Balm.getNetworking().sendToServer(new RequestRecipesMessage(craftable.resultItem(), lockedInputs));
    }

    public void handleRequestRecipes(ItemStack resultItem, NonNullList<ItemStack> lockedInputs) {
        selectedCraftable = findRecipeForResultItem(resultItem);
        this.lockedInputs.clear();
        for (int i = 0; i < lockedInputs.size(); i++) {
            this.lockedInputs.set(i, lockedInputs.get(i));
        }

        recipesDirty = true;
    }

    private void requestCraft(boolean craftFullStack, boolean addToInventory) {
        final var selectedRecipe = getSelectedRecipe();
        if (selectedRecipe != null) {
            final var recipe = selectedRecipe.recipe(player);
            Balm.getNetworking().sendToServer(new CraftRecipeMessage(recipe.getId(), lockedInputs, craftFullStack, addToInventory));
        }
    }

    public List<RecipeWithStatus> getAvailableCraftables(@Nullable WorkshopFilterWithStatus filter) {
        if (filter == null || !filter.available()) {
            return Collections.emptyList();
        } else {
            final var result = new HashMap<ResourceLocation, RecipeWithStatus>();
            final var context = new CraftingContext(workshop, player);
            final var recipesByItemId = CraftingForBlockheadsRegistry.getRecipesByItemId();
            for (ResourceLocation itemId : recipesByItemId.keySet()) {
                for (Recipe<?> recipe : recipesByItemId.get(itemId)) {
                    final var resultItem = recipe.getResultItem(player.level().registryAccess());
                    if (!filterMatches(recipe, resultItem)) {
                        continue;
                    }

                    if (isGroupItem(resultItem)) {
                        continue;
                    }

                    final var operation = context.createOperation(recipe).prepare();
                    final var itemRequirements = CraftingForBlockheadsRegistry.getItemRequirements(resultItem);
                    final var fulfilledPredicates = workshop.getFulfilledPredicates(player);
                    final var missingPredicates = new HashSet<String>();
                    var hasMissingHardPredicates = false;
                    for (final var entry : itemRequirements.entrySet()) {
                        final var predicate = entry.getKey();
                        final var level = entry.getValue();
                        if (!fulfilledPredicates.contains(predicate)) {
                            if (level == WorkshopPredicateLevel.HARD) {
                                hasMissingHardPredicates = true;
                                break;
                            } else {
                                missingPredicates.add(predicate);
                            }
                        }
                    }

                    if (hasMissingHardPredicates) {
                        continue;
                    }

                    final var recipeWithStatus = new RecipeWithStatus(recipe.getId(),
                            resultItem,
                            missingPredicates,
                            operation.getMissingIngredients(),
                            operation.getMissingIngredientsMask(),
                            operation.getLockedInputs());
                    result.compute(itemId, (k, v) -> RecipeWithStatus.best(v, recipeWithStatus));
                }
            }
            return result.values().stream().toList();
        }
    }

    private boolean isGroupItem(ItemStack resultItem) {
        final var itemId = Balm.getRegistries().getKey(resultItem.getItem());
        for (final var group : CraftingForBlockheadsRegistry.getGroups()) {
            final var groupItemId = Balm.getRegistries().getKey(group.getParentItem());
            if (groupItemId.equals(itemId)) {
                continue;
            }

            for (final var ingredient : group.getChildren()) {
                if (ingredient.test(resultItem)) {
                    return true;
                }
            }
        }

        return false;
    }

    private Collection<Recipe<?>> getRecipesFor(ItemStack resultItem) {
        final var recipes = new ArrayList<>(CraftingForBlockheadsRegistry.getRecipesFor(resultItem));
        recipes.addAll(CraftingForBlockheadsRegistry.getRecipesInGroup(resultItem));
        return recipes;
    }

    public void broadcastCraftables(@Nullable String filterId) {
        final var filter = availableFilters.get(filterId);
        craftables = getAvailableCraftables(filter);
        Balm.getNetworking().sendTo(player, new CraftablesListMessage(craftables));
    }

    public void broadcastRecipesForResultItem(ItemStack resultItem) {
        final List<RecipeWithStatus> result = new ArrayList<>();

        final var context = new CraftingContext(workshop, player);
        final var recipesForResult = getRecipesFor(resultItem);
        for (Recipe<?> recipe : recipesForResult) {
            final var recipeResultItem = recipe.getResultItem(player.level().registryAccess());
            if (!filterMatches(recipe, recipeResultItem)) {
                continue;
            }

            final var operation = context.createOperation(recipe).withLockedInputs(lockedInputs).prepare();
            final var itemRequirements = CraftingForBlockheadsRegistry.getItemRequirements(resultItem);
            final var fulfilledPredicates = workshop.getFulfilledPredicates(player);
            final var missingPredicates = new HashSet<String>();
            var hasMissingHardPredicates = false;
            for (final var entry : itemRequirements.entrySet()) {
                final var predicate = entry.getKey();
                final var level = entry.getValue();
                if (!fulfilledPredicates.contains(predicate)) {
                    if (level == WorkshopPredicateLevel.HARD) {
                        hasMissingHardPredicates = true;
                        break;
                    } else {
                        missingPredicates.add(predicate);
                    }
                }
            }

            if (hasMissingHardPredicates) {
                continue;
            }

            result.add(new RecipeWithStatus(recipe.getId(),
                    recipeResultItem,
                    missingPredicates,
                    operation.getMissingIngredients(),
                    operation.getMissingIngredientsMask(),
                    operation.getLockedInputs()));
        }

        result.sort(currentSorting);
        this.recipesForSelection = result;
        Balm.getNetworking().sendTo(player, new RecipesListMessage(result));
    }

    public void craft(ResourceLocation recipeId, NonNullList<ItemStack> lockedInputs, boolean craftFullStack, boolean addToInventory) {
        final var level = player.level();
        final var recipe = level.getRecipeManager().byKey(recipeId).orElse(null);
        if (recipe == null) {
            CraftingForBlockheads.logger.error("Received invalid recipe from client: {}", recipeId);
            return;
        }

        var craftable = this.craftables.stream().filter(it -> it.recipe(player) == recipe).findAny().orElse(null);
        if (craftable == null) {
            craftable = this.recipesForSelection.stream().filter(it -> it.recipe(player) == recipe).findAny().orElse(null);
            if (craftable == null) {
                CraftingForBlockheads.logger.error("Received invalid craft request, unknown recipe {}", recipeId);
                return;
            }
        }

        if (!craftable.missingPredicates().isEmpty()) {
            CraftingForBlockheads.logger.error("Received invalid craft request for {}, missing predicates {}", recipeId, craftable.missingPredicates());
            return;
        }

        final var context = new CraftingContext(workshop, player);
        final var operation = context.createOperation(recipe).withLockedInputs(lockedInputs);
        final var resultItem = recipe.getResultItem(level.registryAccess());
        final var repeats = craftFullStack ? resultItem.getMaxStackSize() / resultItem.getCount() : 1;
        for (int i = 0; i < repeats; i++) {
            operation.prepare();
            if (operation.canCraft()) {
                final var carried = getCarried();
                if (!carried.isEmpty() && (!ItemStack.isSameItemSameTags(carried, resultItem) || carried.getCount() >= carried.getMaxStackSize())) {
                    if (craftFullStack || addToInventory) {
                        addToInventory = true;
                    } else {
                        break;
                    }
                }
                final var itemStack = operation.craft(this, player.level().registryAccess());
                if (!itemStack.isEmpty()) {
                    if (addToInventory) {
                        if (!player.getInventory().add(itemStack)) {
                            player.drop(itemStack, false);
                        }
                    } else {

                        if (carried.isEmpty()) {
                            setCarried(itemStack);
                        } else if (ItemStack.isSameItemSameTags(carried, itemStack) && carried.getCount() < carried.getMaxStackSize()) {
                            carried.grow(itemStack.getCount());
                        } else {
                            if (!player.getInventory().add(itemStack)) {
                                player.drop(itemStack, false);
                            }
                        }
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        craftablesDirty = true;
        recipesDirty = true;
    }

    public void setCraftables(List<RecipeWithStatus> craftables) {
        int previousSelectionIndex = selectedCraftable != null ? filteredItems.indexOf(selectedCraftable) : -1;

        this.craftables = craftables;
        updateFilteredItems();

        // Make sure the previously selected recipe stays in the same slot, even if others moved
        if (previousSelectionIndex != -1) {
            Iterator<RecipeWithStatus> it = filteredItems.iterator();
            RecipeWithStatus found = null;
            while (it.hasNext()) {
                RecipeWithStatus recipe = it.next();
                if (ItemStack.isSameItem(recipe.resultItem(), selectedCraftable.resultItem())) {
                    found = recipe;
                    it.remove();
                    break;
                }
            }
            while (previousSelectionIndex > filteredItems.size()) {
                filteredItems.add(null);
            }
            filteredItems.add(previousSelectionIndex, found);
            selectedCraftable = found;
        }

        // Updates the items inside the recipe slots
        updateCraftableSlots();

        setDirty(true);
    }

    public void updateCraftableSlots() {
        int i = scrollOffset * 5;
        for (final var slot : craftableSlots) {
            if (i < filteredItems.size()) {
                final var craftable = filteredItems.get(i);
                if (selectedCraftable != null && ItemStack.isSameItemSameTags(selectedCraftable.resultItem(), craftable.resultItem())) {
                    final var selectedRecipe = getSelectedRecipe();
                    slot.setCraftable(selectedRecipe != null ? selectedRecipe : craftable);
                } else {
                    slot.setCraftable(craftable);
                }
                i++;
            } else {
                slot.setCraftable(null);
            }
        }
    }

    private void updateMatrixSlots() {
        final var selectedRecipe = getSelectedRecipe();
        if (selectedRecipe != null) {
            final var recipe = selectedRecipe.recipe(player);
            updateMatrixSlots(recipe, selectedRecipe);
        } else {
            for (int i = 0; i < matrixSlots.size(); i++) {
                CraftMatrixFakeSlot matrixSlot = matrixSlots.get(i);
                matrixSlot.setIngredient(i, Ingredient.EMPTY, ItemStack.EMPTY);
                matrixSlot.setMissing(true);
            }
        }
    }

    private <C extends Container, T extends Recipe<C>> void updateMatrixSlots(T recipe, RecipeWithStatus status) {
        final var ingredients = recipe.getIngredients();
        final var matrix = NonNullList.withSize(9, Ingredient.EMPTY);
        final var missingMatrix = new boolean[9];
        final var ingredientIndexMatrix = new int[9];
        final var recipeTypeHandler = CraftingForBlockheadsRegistry.getRecipeWorkshopHandler(recipe);
        if (recipeTypeHandler != null) {
            for (int i = 0; i < ingredients.size(); i++) {
                final var ingredient = ingredients.get(i);
                final var matrixSlot = recipeTypeHandler.mapToMatrixSlot(recipe, i);
                matrix.set(matrixSlot, ingredient);
                missingMatrix[matrixSlot] = (status.missingIngredientsMask() & (1 << i)) == (1 << i);
                ingredientIndexMatrix[matrixSlot] = i;
            }
        }

        for (int i = 0; i < matrixSlots.size(); i++) {
            final var matrixSlot = matrixSlots.get(i);
            final var lockedInputs = status.lockedInputs();
            final int ingredientIndex = ingredientIndexMatrix[i];
            final var lockedInput = lockedInputs.get(ingredientIndex);
            matrixSlot.setIngredient(ingredientIndex, matrix.get(i), lockedInput);
            matrixSlot.setMissing(missingMatrix[i]);
        }
    }

    public void setSortComparator(Comparator<RecipeWithStatus> comparator) {
        this.currentSorting = comparator;
        // When re-sorting, make sure to remove all null slots that were added to preserve layout
        filteredItems.removeIf(Objects::isNull);
        filteredItems.sort(comparator);
        updateCraftableSlots();
    }

    public int getItemListCount() {
        return filteredItems.size();
    }

    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
        updateCraftableSlots();
    }

    public void search(@Nullable String term) {
        this.currentSearch = term;
        updateFilteredItems();
        setScrollOffset(0);
    }

    private void updateFilteredItems() {
        filteredItems.clear();
        for (RecipeWithStatus craftable : craftables) {
            if (searchMatches(craftable)) {
                filteredItems.add(craftable);
            }
        }
        filteredItems.sort(currentSorting);
    }

    private boolean searchMatches(RecipeWithStatus craftable) {
        if (currentSearch == null || currentSearch.trim().isEmpty()) {
            return true;
        }

        final var resultItem = craftable.resultItem();
        final var lowerCaseSearch = currentSearch.toLowerCase();
        if (resultItem.getDisplayName().getString().toLowerCase(Locale.ENGLISH).contains(lowerCaseSearch)) {
            return true;
        } else {
            List<Component> tooltips = resultItem.getTooltipLines(player, TooltipFlag.Default.NORMAL);
            for (Component tooltip : tooltips) {
                if (tooltip.getString().toLowerCase(Locale.ENGLISH).contains(lowerCaseSearch)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean filterMatches(Recipe<?> recipe, ItemStack resultItem) {
        if (currentFilter == null) {
            return true;
        }

        return currentFilter.getIncludes().stream().anyMatch(it -> it.test(recipe, resultItem)) && currentFilter.getExcludes()
                .stream()
                .noneMatch(it -> it.test(recipe, resultItem));
    }

    @Nullable
    public RecipeWithStatus getSelectedRecipe() {
        return recipesForSelection != null ? recipesForSelection.get(recipesForSelectionIndex) : null;
    }

    public boolean isSelectedSlot(CraftableFakeSlot slot) {
        final var selectedRecipe = getSelectedRecipe();
        final var craftable = selectedRecipe != null ? selectedRecipe : selectedCraftable;
        return craftable != null && slot.getCraftable() != null && ItemStack.isSameItemSameTags(slot.getCraftable().resultItem(),
                craftable.resultItem());
    }

    @Deprecated
    public boolean isDirty() {
        return isDirtyClient;
    }

    @Deprecated
    public void setDirty(boolean dirty) {
        isDirtyClient = dirty;
    }

    public void setRecipesForSelection(List<RecipeWithStatus> recipes) {
        recipesForSelection = recipes.size() > 0 ? recipes : null;
        recipesForSelectionIndex = recipesForSelection != null ? Math.max(0, Math.min(recipesForSelection.size() - 1, recipesForSelectionIndex)) : 0;

        updateMatrixSlots();
    }

    public void nextRecipe(int dir) {
        if (recipesForSelection != null) {
            recipesForSelectionIndex = Math.max(0, Math.min(recipesForSelection.size() - 1, recipesForSelectionIndex + dir));
            updateCraftableSlots();
        }

        updateMatrixSlots();
    }

    public boolean selectionHasRecipeVariants() {
        return recipesForSelection != null && recipesForSelection.size() > 1;
    }

    public boolean selectionHasPreviousRecipe() {
        return recipesForSelectionIndex > 0;
    }

    public boolean selectionHasNextRecipe() {
        return recipesForSelection != null && recipesForSelectionIndex < recipesForSelection.size() - 1;
    }

    public List<CraftMatrixFakeSlot> getMatrixSlots() {
        return matrixSlots;
    }

    @Nullable
    public RecipeWithStatus findRecipeForResultItem(ItemStack resultItem) {
        return craftables.stream().filter(it -> ItemStack.isSameItemSameTags(it.resultItem(), resultItem)).findAny().orElse(null);
    }

    @Deprecated
    public int getRecipesForSelectionIndex() {
        return filteredItems.indexOf(selectedCraftable);
    }

    public void setCurrentFilter(WorkshopFilter filter) {
        if (filter != currentFilter) {
            selectCraftable(null);
            currentFilter = filter;
            requestCraftables(filter);
        }
    }

    public WorkshopFilter getCurrentFilter() {
        return currentFilter;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setLockedInput(int i, ItemStack lockedInput) {
        lockedInputs.set(i, lockedInput);
        if (selectedCraftable != null) {
            requestRecipes(selectedCraftable);
        }
    }

    public void broadcastFulfilledPredicates() {
        fulfilledPredicates = workshop.getFulfilledPredicates(player);
        availableFilters = workshop.getAvailableFilters(fulfilledPredicates);

        Balm.getNetworking().sendTo(player, new FulfilledPredicateListMessage(fulfilledPredicates));
    }

    public void setFulfilledPredicates(Set<String> fulfilledPredicates) {
        this.fulfilledPredicates = fulfilledPredicates;
        this.availableFilters = workshop.getAvailableFilters(fulfilledPredicates);
    }

    public Map<String, WorkshopFilterWithStatus> getAvailableFilters() {
        return availableFilters;
    }
}
