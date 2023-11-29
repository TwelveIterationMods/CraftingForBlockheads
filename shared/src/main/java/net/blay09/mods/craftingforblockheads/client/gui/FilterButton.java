package net.blay09.mods.craftingforblockheads.client.gui;

import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.blay09.mods.craftingforblockheads.menu.WorkshopFilterWithStatus;
import net.blay09.mods.craftingforblockheads.menu.WorkshopMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class FilterButton extends Button {

    private static final ResourceLocation ICONS = new ResourceLocation(CraftingForBlockheads.MOD_ID, "textures/gui/gui.png");

    private final WorkshopFilterWithStatus filter;
    private final WorkshopMenu menu;

    public FilterButton(int x, int y, WorkshopFilterWithStatus filter, WorkshopMenu menu) {
        super(x, y, 20, 20, Component.empty(), it -> {
            if (filter.missingPredicates().isEmpty()) {
                menu.setCurrentFilter(filter.filter());
                menu.updateCraftableSlots();
            }
        }, Button.DEFAULT_NARRATION);
        this.filter = filter;
        this.menu = menu;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.setColor(1f, 1f, 1f, 1f);
        final var texX = 176;
        final int texY;
        if (isHovered && filter.available()) {
            texY = 20;
        } else if (menu.getCurrentFilter() == filter.filter()) {
            texY = 0;
        } else {
            texY = 40;
        }
        guiGraphics.blit(ICONS, getX(), getY(), texX, texY, width, height);
        guiGraphics.renderItem(filter.filter().getIcon(), getX() + 2, getY() + 2);

        if (!filter.available()) {
            final var pose = guiGraphics.pose();
            pose.pushPose();
            pose.translate(0, 0, 300);
            guiGraphics.blit(ICONS, getX() + 2, getY() + 2, 192, 60, 16, 16);
            pose.popPose();
        }
    }

    public List<Component> getTooltipLines() {
        List<Component> tooltipLines = new ArrayList<>();
        tooltipLines.add(this.filter.filter().getTooltip());
        for (String missingPredicate : filter.missingPredicates()) {
            tooltipLines.add(Component.translatable("tooltip.craftingforblockheads.missing_" + missingPredicate).withStyle(ChatFormatting.RED));
        }
        return tooltipLines;
    }
}
