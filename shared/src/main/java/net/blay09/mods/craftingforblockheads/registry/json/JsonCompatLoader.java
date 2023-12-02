package net.blay09.mods.craftingforblockheads.registry.json;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.craftingforblockheads.CraftingForBlockheadsConfig;
import net.blay09.mods.craftingforblockheads.api.CraftingForBlockheadsAPI;
import net.blay09.mods.craftingforblockheads.api.CraftingForBlockheadsProvider;
import net.blay09.mods.craftingforblockheads.api.ItemFilter;
import net.blay09.mods.craftingforblockheads.api.WorkshopPredicate;
import net.blay09.mods.craftingforblockheads.registry.CraftingForBlockheadsRegistry;
import net.blay09.mods.craftingforblockheads.registry.DataDrivenProviderFactory;
import net.blay09.mods.craftingforblockheads.registry.IngredientItemFilter;
import net.blay09.mods.craftingforblockheads.registry.NbtIngredientItemFilter;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class JsonCompatLoader implements ResourceManagerReloadListener {

    private static final Logger logger = LoggerFactory.getLogger(JsonCompatLoader.class);
    private static final Gson gson = new Gson();
    private static final FileToIdConverter COMPAT_JSONS = FileToIdConverter.json("craftingforblockheads");

    private final List<CraftingForBlockheadsProvider> providersFromDataPacks = new ArrayList<>();

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        for (final var providersFromDataPack : providersFromDataPacks) {
            CraftingForBlockheadsAPI.unregisterProvider(providersFromDataPack);
        }
        providersFromDataPacks.clear();

        final var simpleFile = new File(Balm.getConfig().getConfigDir(), "CraftingForBlockheadsRegistry.json");
        if (simpleFile.exists()) {
            try (final var reader = new FileReader(simpleFile)) {
                final var gridProviders = load(gson.fromJson(reader, JsonElement.class));
                providersFromDataPacks.addAll(gridProviders);
            } catch (Exception e) {
                logger.error("Parsing error loading Crafting for Blockheads data file at {}", simpleFile, e);
            }
        } else {
            try {
                Files.writeString(simpleFile.toPath(), "[]");
            } catch (IOException ignored) {
            }
        }

        for (final var entry : COMPAT_JSONS.listMatchingResources(resourceManager).entrySet()) {
            try (final var reader = entry.getValue().openAsReader()) {
                final var gridProviders = load(gson.fromJson(reader, JsonElement.class));
                providersFromDataPacks.addAll(gridProviders);
            } catch (Exception e) {
                logger.error("Parsing error loading Crafting for Blockheads data file at {}", entry.getKey(), e);
            }
        }
    }

    private static List<CraftingForBlockheadsProvider> load(@Nullable JsonElement jsonElement) {
        if (jsonElement == null) {
            return Collections.emptyList();
        }

        if (jsonElement.isJsonObject()) {
            if (jsonElement.getAsJsonObject().keySet().isEmpty()) {
                return Collections.emptyList();
            }

            return Collections.singletonList(load(JsonProviderData.fromJson(jsonElement.getAsJsonObject())));
        } else if (jsonElement.isJsonArray()) {
            if (jsonElement.getAsJsonArray().size() == 0) {
                return Collections.emptyList();
            }

            final var providers = new ArrayList<CraftingForBlockheadsProvider>();
            for (JsonElement element : jsonElement.getAsJsonArray()) {
                providers.addAll(load(element));
            }
            return providers;
        } else {
            throw new IllegalArgumentException("Invalid Crafting for Blockheads data, expected json object or array");
        }
    }

    private static CraftingForBlockheadsProvider load(JsonProviderData data) {
        final var presets = CraftingForBlockheadsConfig.getActive().presets;
        final var modId = data.modId();
        boolean modLoaded = modId.equals("minecraft") || Balm.isModLoaded(modId);
        if (!modLoaded || !presets.contains(data.preset())) {
            return null;
        }

        CraftingForBlockheadsProvider provider = DataDrivenProviderFactory.createProvider(data);
        if (provider != null) {
            CraftingForBlockheadsAPI.registerProvider(provider);
            logger.info("{} has registered a provider for Crafting for Blockheads via data pack", data.modId());
        }
        return provider;
    }

    public static NonNullList<ItemFilter> itemsFromJson(JsonArray jsonArray) {
        NonNullList<ItemFilter> itemFilters = NonNullList.create();

        for (int i = 0; i < jsonArray.size(); i++) {
            final var jsonObject = jsonArray.get(i).getAsJsonObject();
            final var ingredient = Ingredient.fromJson(jsonObject, false);
            if (ingredient.isEmpty()) {
                continue;
            }

            final var nbtJson = jsonObject.get("nbt");
            if (nbtJson != null) {
                final var nbt = readNbt(nbtJson);
                final var strict = GsonHelper.getAsBoolean(jsonObject, "strict", false);
                itemFilters.add(new NbtIngredientItemFilter(ingredient, nbt, strict));
            } else {
                itemFilters.add(new IngredientItemFilter(ingredient));
            }
        }

        return itemFilters;
    }

    public static Set<String> stringSetFromJson(JsonArray jsonArray) {
        Set<String> strings = new HashSet<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            strings.add(jsonArray.get(i).getAsString());
        }

        return strings;
    }

    private static CompoundTag readNbt(JsonElement json) {
        try {
            if (json.isJsonObject()) {
                return TagParser.parseTag(json.toString());
            } else {
                return TagParser.parseTag(GsonHelper.convertToString(json, "nbt"));
            }
        } catch (CommandSyntaxException commandSyntaxException) {
            throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException.getMessage());
        }
    }

    public static WorkshopPredicate predicateFromJson(JsonObject jsonObject) {
        final var predicateType = GsonHelper.getAsString(jsonObject, "type");
        final var deserializer = CraftingForBlockheadsRegistry.getWorkshopPredicateDeserializer(predicateType);
        if (deserializer != null) {
            return deserializer.apply(jsonObject);
        } else {
            throw new IllegalArgumentException("Unknown workshop predicate type: " + predicateType);
        }
    }
}
