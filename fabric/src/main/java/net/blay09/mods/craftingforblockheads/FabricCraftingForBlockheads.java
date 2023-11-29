package net.blay09.mods.craftingforblockheads;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.balm.fabric.provider.FabricBalmProviders;
import net.blay09.mods.craftingforblockheads.api.capability.IWorkshopItemProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FabricCraftingForBlockheads implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initialize(CraftingForBlockheads.MOD_ID, CraftingForBlockheads::initialize);

        registerProvider(new ResourceLocation(CraftingForBlockheads.MOD_ID, "workshop_item_provider"), IWorkshopItemProvider.class);
    }

    private <T> void registerProvider(ResourceLocation identifier, Class<T> clazz) {
        var providers = ((FabricBalmProviders) Balm.getProviders());
        providers.registerProvider(identifier, clazz);
    }

    private <T> void registerLookup(ResourceLocation identifier, Class<T> clazz, BlockEntityType<?>... blockEntities) {
        var lookup = BlockApiLookup.get(identifier, clazz, Void.class);
        lookup.registerForBlockEntities((blockEntity, context) -> ((BalmBlockEntity) blockEntity).getProvider(clazz), blockEntities);
    }
}
