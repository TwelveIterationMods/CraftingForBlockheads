package net.blay09.mods.craftingforblockheads.client;

import net.blay09.mods.balm.api.client.screen.BalmScreens;
import net.blay09.mods.craftingforblockheads.client.gui.screen.*;
import net.blay09.mods.craftingforblockheads.menu.ModMenus;

public class ModScreens {
    public static void initialize(BalmScreens screens) {
        screens.registerScreen(ModMenus.workbench::get, WorkshopScreen::new);
    }
}
