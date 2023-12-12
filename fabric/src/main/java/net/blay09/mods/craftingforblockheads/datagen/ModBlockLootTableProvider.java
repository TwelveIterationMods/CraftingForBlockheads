package net.blay09.mods.craftingforblockheads.datagen;

import net.blay09.mods.craftingforblockheads.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class ModBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected ModBlockLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        add(ModBlocks.workbench, createSingleItemTable(ModBlocks.workbench));
        add(ModBlocks.carpentersWorkbench, createSingleItemTable(ModBlocks.carpentersWorkbench));
        add(ModBlocks.tailorsWorkbench, createSingleItemTable(ModBlocks.tailorsWorkbench));
        add(ModBlocks.masonsWorkbench, createSingleItemTable(ModBlocks.masonsWorkbench));
        add(ModBlocks.armorersWorkbench, createSingleItemTable(ModBlocks.armorersWorkbench));
        add(ModBlocks.fletchersWorkbench, createSingleItemTable(ModBlocks.fletchersWorkbench));
        add(ModBlocks.alchemistsWorkbench, createSingleItemTable(ModBlocks.alchemistsWorkbench));
        add(ModBlocks.engineersWorkbench, createSingleItemTable(ModBlocks.engineersWorkbench));
        add(ModBlocks.tinkerersWorkbench, createSingleItemTable(ModBlocks.tinkerersWorkbench));
    }
}
