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
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.engineersWorkbench);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
    }
}
