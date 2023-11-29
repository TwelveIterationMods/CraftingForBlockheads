package net.blay09.mods.craftingforblockheads.client;

import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.fabricmc.api.ClientModInitializer;

public class FabricCraftingForBlockheadsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BalmClient.initialize(CraftingForBlockheads.MOD_ID, CraftingForBlockheadsClient::initialize);
    }
}
