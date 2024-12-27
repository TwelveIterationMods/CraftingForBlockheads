package net.blay09.mods.craftingforblockheads.datagen;

import net.blay09.mods.craftingforblockheads.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;

public class ModModelGenerator extends FabricModelProvider {
    public ModModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.workbench);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.carpentersWorkbench);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.tailorsWorkbench);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.masonsWorkbench);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.armorersWorkbench);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.fletchersWorkbench);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.alchemistsWorkbench);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.engineersWorkbench);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.tinkerersWorkbench);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
    }
}
