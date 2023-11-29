package net.blay09.mods.craftingforblockheads.registry;

import net.blay09.mods.craftingforblockheads.api.CraftingForBlockheadsProvider;
import net.blay09.mods.craftingforblockheads.api.WorkshopFilter;
import net.blay09.mods.craftingforblockheads.api.WorkshopPredicate;
import net.blay09.mods.craftingforblockheads.registry.json.JsonProviderData;
import net.blay09.mods.craftingforblockheads.registry.json.JsonProviderFilterData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class DataDrivenProviderFactory {
    public static CraftingForBlockheadsProvider createProvider(JsonProviderData data) {
        final var senderModId = data.modId();

        final var filters = new HashMap<String, WorkshopFilter>();
        for (JsonProviderFilterData filter : data.filters()) {
            filters.put(filter.identifier(), new WorkshopFilter() {
                @Override
                public String getId() {
                    return filter.identifier();
                }

                @Override
                public Component getName() {
                    return Component.translatable(filter.name());
                }

                @Override
                public ItemStack getIcon() {
                    return filter.icon();
                }

                @Override
                public Component getTooltip() {
                    return Component.translatable(filter.name());
                }

                @Override
                public List<Ingredient> getIncludes() {
                    return filter.includes();
                }

                @Override
                public List<Ingredient> getExcludes() {
                    return filter.excludes();
                }

                @Override
                public Set<String> getHardRequirements() {
                    return filter.hardRequirements();
                }

                @Override
                public Set<String> getSoftRequirements() {
                    return filter.softRequirements();
                }

                @Override
                public int getPriority() {
                    return filter.priority();
                }
            });
        }

        return new CraftingForBlockheadsProvider() {
            @Override
            public String getModId() {
                return senderModId;
            }

            @Override
            public Map<String, WorkshopFilter> getFilters() {
                return filters;
            }

            @Override
            public Collection<Ingredient> getCraftables() {
                return data.craftables();
            }

            @Override
            public Map<String, WorkshopPredicate> getPredicates() {
                return data.predicates();
            }
        };
    }

}