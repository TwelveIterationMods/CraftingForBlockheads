package net.blay09.mods.craftingforblockheads.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import net.blay09.mods.craftingforblockheads.client.gui.screen.WorkshopScreen;

import java.util.ArrayList;

public class CraftingForBlockheadsREIClientPlugin implements REIClientPlugin {
    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(WorkshopScreen.class, screen -> {
            final var list = new ArrayList<Rectangle>();
            for (final var button : screen.getFilterButtons()) {
                list.add(new Rectangle(button.getX(), button.getY(), button.getWidth(), button.getHeight()));
            }

            return list;
        });
    }
}
