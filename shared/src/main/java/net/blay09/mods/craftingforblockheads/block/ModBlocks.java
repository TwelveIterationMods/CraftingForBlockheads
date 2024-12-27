package net.blay09.mods.craftingforblockheads.block;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.block.BalmBlocks;
import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static Block workbench;
    public static Block carpentersWorkbench;
    public static Block tailorsWorkbench;
    public static Block masonsWorkbench;
    public static Block armorersWorkbench;
    public static Block fletchersWorkbench;
    public static Block alchemistsWorkbench;
    public static Block engineersWorkbench;
    public static Block tinkerersWorkbench;

    public static void initialize(BalmBlocks blocks) {
        blocks.register(() -> workbench = new WorkbenchBlock(defaultProperties()), () -> itemBlock(workbench), id("workbench"));
        blocks.register(() -> carpentersWorkbench = new WorkbenchBlock(defaultProperties()), () -> itemBlock(carpentersWorkbench), id("carpenters_workbench"));
        blocks.register(() -> tailorsWorkbench = new WorkbenchBlock(defaultProperties()), () -> itemBlock(tailorsWorkbench), id("tailors_workbench"));
        blocks.register(() -> masonsWorkbench = new WorkbenchBlock(defaultProperties()), () -> itemBlock(masonsWorkbench), id("masons_workbench"));
        blocks.register(() -> armorersWorkbench = new WorkbenchBlock(defaultProperties()), () -> itemBlock(armorersWorkbench), id("armorers_workbench"));
        blocks.register(() -> fletchersWorkbench = new WorkbenchBlock(defaultProperties()), () -> itemBlock(fletchersWorkbench), id("fletchers_workbench"));
        blocks.register(() -> alchemistsWorkbench = new WorkbenchBlock(defaultProperties()), () -> itemBlock(alchemistsWorkbench), id("alchemists_workbench"));
        blocks.register(() -> engineersWorkbench = new WorkbenchBlock(defaultProperties()), () -> itemBlock(engineersWorkbench), id("engineers_workbench"));
        blocks.register(() -> tinkerersWorkbench = new WorkbenchBlock(defaultProperties()), () -> itemBlock(tinkerersWorkbench), id("tinkerers_workbench"));
    }

    private static BlockBehaviour.Properties defaultProperties() {
        return BlockBehaviour.Properties.of();
    }

    private static BlockItem itemBlock(Block block) {
        return new BlockItem(block, Balm.getItems().itemProperties());
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(CraftingForBlockheads.MOD_ID, name);
    }

}
