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
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.workbench);
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.carpentersWorkbench);
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.tailorsWorkbench);
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.masonsWorkbench);
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.armorersWorkbench);
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.fletchersWorkbench);
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.alchemistsWorkbench);
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.engineersWorkbench);
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.tinkerersWorkbench);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
    }
}
