package net.blay09.mods.craftingforblockheads.datagen;

import net.blay09.mods.craftingforblockheads.block.ModBlocks;
import net.blay09.mods.craftingforblockheads.tag.ModBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider<Block> {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BLOCK, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, new ResourceLocation("minecraft", "mineable/axe"))).add(
                ModBlocks.workbench,
                ModBlocks.carpentersWorkbench,
                ModBlocks.tailorsWorkbench,
                ModBlocks.masonsWorkbench,
                ModBlocks.armorersWorkbench,
                ModBlocks.fletchersWorkbench,
                ModBlocks.alchemistsWorkbench,
                ModBlocks.engineersWorkbench,
                ModBlocks.tinkerersWorkbench);

        getOrCreateTagBuilder(ModBlockTags.WORKSHOP_ITEM_PROVIDER).add(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.BARREL);
        getOrCreateTagBuilder(ModBlockTags.IS_WORKSHOP_CORE).add(
                        ModBlocks.workbench,
                        ModBlocks.carpentersWorkbench,
                        ModBlocks.tailorsWorkbench,
                        ModBlocks.masonsWorkbench,
                        ModBlocks.armorersWorkbench,
                        ModBlocks.fletchersWorkbench,
                        ModBlocks.alchemistsWorkbench,
                        ModBlocks.engineersWorkbench,
                        ModBlocks.tinkerersWorkbench);
    }

}
