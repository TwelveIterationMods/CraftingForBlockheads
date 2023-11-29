package net.blay09.mods.craftingforblockheads.registry.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.craftingforblockheads.CraftingForBlockheadsConfig;
import net.blay09.mods.craftingforblockheads.api.CraftingForBlockheadsAPI;
import net.blay09.mods.craftingforblockheads.api.CraftingForBlockheadsProvider;
import net.blay09.mods.craftingforblockheads.registry.DataDrivenProviderFactory;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.Ingredient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.*;

public class JsonCompatLoader implements ResourceManagerReloadListener {

    private static final Logger logger = LoggerFactory.getLogger(JsonCompatLoader.class);
    private static final Gson gson = new Gson();
    private static final FileToIdConverter COMPAT_JSONS = FileToIdConverter.json("craftingforblockheads");

    private final List<CraftingForBlockheadsProvider> providersFromDataPacks = new ArrayList<>();

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        for (CraftingForBlockheadsProvider providersFromDataPack : providersFromDataPacks) {
            CraftingForBlockheadsAPI.unregisterProvider(providersFromDataPack);
        }
        providersFromDataPacks.clear();

        for (Map.Entry<ResourceLocation, Resource> entry : COMPAT_JSONS.listMatchingResources(resourceManager).entrySet()) {
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                CraftingForBlockheadsProvider gridProvider = load(JsonProviderData.fromJson(gson.fromJson(reader, JsonObject.class)));
                if (gridProvider != null) {
                    providersFromDataPacks.add(gridProvider);
                }
            } catch (Exception e) {
                logger.error("Parsing error loading CraftingTweaks data file at {}", entry.getKey(), e);
            }
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

    public static NonNullList<Ingredient> itemsFromJson(JsonArray jsonArray) {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        for (int i = 0; i < jsonArray.size(); i++) {
            Ingredient ingredient = Ingredient.fromJson(jsonArray.get(i), false);
            if (!ingredient.isEmpty()) {
                ingredients.add(ingredient);
            }
        }

        return ingredients;
    }

    public static Set<String> stringSetFromJson(JsonArray jsonArray) {
        Set<String> strings = new HashSet<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            strings.add(jsonArray.get(i).getAsString());
        }

        return strings;
    }
}
