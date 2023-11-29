package net.blay09.mods.craftingforblockheads;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.forge.provider.ForgeBalmProviders;
import net.blay09.mods.craftingforblockheads.api.capability.IWorkshopItemProvider;
import net.blay09.mods.craftingforblockheads.client.CraftingForBlockheadsClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CraftingForBlockheads.MOD_ID)
public class ForgeCraftingForBlockheads {

    public static Capability<IWorkshopItemProvider> WORKSHOP_ITEM_PROVIDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public ForgeCraftingForBlockheads() {
        Balm.initialize(CraftingForBlockheads.MOD_ID, CraftingForBlockheads::initialize);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> BalmClient.initialize(CraftingForBlockheads.MOD_ID, CraftingForBlockheadsClient::initialize));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerCapabilities);

        ForgeBalmProviders providers = (ForgeBalmProviders) Balm.getProviders();
        providers.register(IWorkshopItemProvider.class, new CapabilityToken<>() {
        });
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IWorkshopItemProvider.class);
    }
}
