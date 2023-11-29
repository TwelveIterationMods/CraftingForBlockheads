package net.blay09.mods.craftingforblockheads;

import net.blay09.mods.balm.api.Balm;

public class CraftingForBlockheadsConfig {
    public static CraftingForBlockheadsConfigData getActive() {
        return Balm.getConfig().getActive(CraftingForBlockheadsConfigData.class);
    }

    public static void initialize() {
        Balm.getConfig().registerConfig(CraftingForBlockheadsConfigData.class, null);
    }
}
