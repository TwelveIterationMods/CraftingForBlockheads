package net.blay09.mods.craftingforblockheads.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.blay09.mods.craftingforblockheads.client.gui.screen.WorkshopScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIAddon implements IModPlugin {

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(WorkshopScreen.class, new IGuiContainerHandler<>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(WorkshopScreen screen) {
                final var list = new ArrayList<Rect2i>();
                for (final var button : screen.getFilterButtons()) {
                    list.add(new Rect2i(button.getX(), button.getY(), button.getWidth(), button.getHeight()));
                }

                return list;
            }
        });
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation("craftingforblockheads", "jei");
    }

}
