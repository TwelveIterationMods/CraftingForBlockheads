package net.blay09.mods.craftingforblockheads.compat;

import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.forge.REIPluginClient;
import net.blay09.mods.craftingforblockheads.compat.rei.CraftingForBlockheadsREIClientPlugin;

@REIPluginClient
public class CraftingForBlockheadsForgeREIClientPlugin extends CraftingForBlockheadsREIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new WorkshopREIDisplayCategory());
    }
}
