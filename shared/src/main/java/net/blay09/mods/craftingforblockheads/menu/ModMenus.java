package net.blay09.mods.craftingforblockheads.menu;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.blay09.mods.craftingforblockheads.crafting.WorkshopImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ModMenus {

    public static DeferredObject<MenuType<WorkshopMenu>> workbench;
    public static DeferredObject<MenuType<WorkshopMenu>> workbenchItem;

    public static void initialize(BalmMenus menus) {
        workbench = menus.registerMenu(id("workbench"), (windowId, inv, data) -> {
            final var level = inv.player.level();
            final var pos = data.readBlockPos();
            final var workshop = new WorkshopImpl(level, pos);
            return new WorkshopMenu(workbench.get(), windowId, inv.player, new HashMap<>(), workshop);
        });

        workbenchItem = menus.registerMenu(id("workbench_item"), (windowId, inv, data) -> {
            final var itemStack = data.readItem();
            final var workshop = new WorkshopImpl(itemStack);
            return new WorkshopMenu(workbenchItem.get(), windowId, inv.player, new HashMap<>(), workshop);
        });
    }

    @NotNull
    private static ResourceLocation id(String name) {
        return new ResourceLocation(CraftingForBlockheads.MOD_ID, name);
    }

}
