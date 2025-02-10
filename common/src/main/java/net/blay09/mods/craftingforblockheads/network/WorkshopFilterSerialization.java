package net.blay09.mods.craftingforblockheads.network;

import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.blay09.mods.craftingforblockheads.api.ItemFilter;
import net.blay09.mods.craftingforblockheads.api.WorkshopFilter;
import net.blay09.mods.craftingforblockheads.menu.WorkshopFilterWithStatus;
import net.blay09.mods.craftingforblockheads.registry.IngredientItemFilter;
import net.blay09.mods.craftingforblockheads.registry.NbtIngredientItemFilter;
import net.blay09.mods.craftingforblockheads.registry.RecipeFilter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class WorkshopFilterSerialization {

    public static void writeAvailableFilters(FriendlyByteBuf buf, Map<String, WorkshopFilterWithStatus> availableFilters) {
        buf.writeVarInt(availableFilters.size());
        for (final var entry : availableFilters.entrySet()) {
            buf.writeUtf(entry.getKey());
            final var filterWithStatus = entry.getValue();
            buf.writeVarInt(filterWithStatus.missingPredicates().size());
            for (final var missingPredicate : filterWithStatus.missingPredicates()) {
                buf.writeUtf(missingPredicate);
            }
            write(buf, filterWithStatus.filter());
        }
    }

    public static void write(FriendlyByteBuf buf, WorkshopFilter filter) {
        buf.writeUtf(filter.getId());
        buf.writeComponent(filter.getName());
        buf.writeItem(filter.getIcon());
        buf.writeComponent(filter.getTooltip());
        buf.writeInt(filter.getPriority());
        buf.writeVarInt(filter.getIncludes().size());
        for (final var include : filter.getIncludes()) {
            write(buf, include);
        }
        buf.writeVarInt(filter.getExcludes().size());
        for (final var exclude : filter.getExcludes()) {
            write(buf, exclude);
        }
        buf.writeVarInt(filter.getHardRequirements().size());
        for (final var hardRequirement : filter.getHardRequirements()) {
            buf.writeUtf(hardRequirement);
        }
        buf.writeVarInt(filter.getSoftRequirements().size());
        for (final var softRequirement : filter.getSoftRequirements()) {
            buf.writeUtf(softRequirement);
        }
    }

    public static void write(FriendlyByteBuf buf, ItemFilter itemFilter) {
        if (itemFilter instanceof NbtIngredientItemFilter nbtIngredientItemFilter) {
            buf.writeResourceLocation(new ResourceLocation(CraftingForBlockheads.MOD_ID, "nbt_ingredient"));
            nbtIngredientItemFilter.getIngredient().toNetwork(buf);
            buf.writeNbt(nbtIngredientItemFilter.getNbt());
            buf.writeBoolean(nbtIngredientItemFilter.isStrict());
        } else if (itemFilter instanceof RecipeFilter recipeFilter) {
            buf.writeResourceLocation(new ResourceLocation(CraftingForBlockheads.MOD_ID, "recipe"));
            buf.writeResourceLocation(recipeFilter.getRecipeId());
        } else if (itemFilter instanceof IngredientItemFilter ingredientItemFilter) {
            buf.writeResourceLocation(new ResourceLocation(CraftingForBlockheads.MOD_ID, "ingredient"));
            ingredientItemFilter.getIngredient().toNetwork(buf);
        } else {
            throw new IllegalArgumentException("Unknown ItemFilter type: " + itemFilter.getClass().getName());
        }
    }

    public static Map<String, WorkshopFilterWithStatus> readAvailableFilters(FriendlyByteBuf buf) {
        final var availableFilters = new HashMap<String, WorkshopFilterWithStatus>();
        final var filterCount = buf.readVarInt();
        for (int i = 0; i < filterCount; i++) {
            final var key = buf.readUtf();
            final var missingPredicateCount = buf.readVarInt();
            final var missingPredicates = new HashSet<String>();
            for (int j = 0; j < missingPredicateCount; j++) {
                missingPredicates.add(buf.readUtf());
            }
            final var filter = readWorkshopFilter(buf);
            final var filterWithStatus = new WorkshopFilterWithStatus(filter, missingPredicates);
            availableFilters.put(key, filterWithStatus);
        }
        return availableFilters;
    }

    public static WorkshopFilter readWorkshopFilter(FriendlyByteBuf buf) {
        final var id = buf.readUtf();
        final var name = buf.readComponent();
        final var icon = buf.readItem();
        final var tooltip = buf.readComponent();
        final var priority = buf.readInt();
        final var includes = new ArrayList<ItemFilter>();
        final var includeCount = buf.readVarInt();
        for (int i = 0; i < includeCount; i++) {
            includes.add(readItemFilter(buf));
        }
        final var excludes = new ArrayList<ItemFilter>();
        final var excludeCount = buf.readVarInt();
        for (int i = 0; i < excludeCount; i++) {
            excludes.add(readItemFilter(buf));
        }
        final var hardRequirements = new HashSet<String>();
        final var hardRequirementCount = buf.readVarInt();
        for (int i = 0; i < hardRequirementCount; i++) {
            hardRequirements.add(buf.readUtf());
        }
        final var softRequirements = new HashSet<String>();
        final var softRequirementCount = buf.readVarInt();
        for (int i = 0; i < softRequirementCount; i++) {
            softRequirements.add(buf.readUtf());
        }
        return new WorkshopFilter() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public Component getName() {
                return name;
            }

            @Override
            public ItemStack getIcon() {
                return icon;
            }

            @Override
            public Component getTooltip() {
                return tooltip;
            }

            @Override
            public int getPriority() {
                return priority;
            }

            @Override
            public List<ItemFilter> getIncludes() {
                return includes;
            }

            @Override
            public List<ItemFilter> getExcludes() {
                return excludes;
            }

            @Override
            public Set<String> getHardRequirements() {
                return hardRequirements;
            }

            @Override
            public Set<String> getSoftRequirements() {
                return softRequirements;
            }
        };
    }

    public static ItemFilter readItemFilter(FriendlyByteBuf buf) {
        final var type = buf.readResourceLocation();
        if (type.equals(new ResourceLocation(CraftingForBlockheads.MOD_ID, "nbt_ingredient"))) {
            final var ingredient = Ingredient.fromNetwork(buf);
            final var nbt = buf.readNbt();
            final var strict = buf.readBoolean();
            return new NbtIngredientItemFilter(ingredient, nbt, strict);
        } else if (type.equals(new ResourceLocation(CraftingForBlockheads.MOD_ID, "recipe"))) {
            final var recipeId = buf.readResourceLocation();
            return new RecipeFilter(recipeId);
        } else if (type.equals(new ResourceLocation(CraftingForBlockheads.MOD_ID, "ingredient"))) {
            final var ingredient = Ingredient.fromNetwork(buf);
            return new IngredientItemFilter(ingredient);
        } else {
            throw new IllegalArgumentException("Unknown ItemFilter type: " + type);
        }
    }
}
