package net.blay09.mods.craftingforblockheads.block.entity;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.block.BalmBlockEntities;
import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.blay09.mods.craftingforblockheads.block.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    public static DeferredObject<BlockEntityType<WorkbenchBlockEntity>> workbench;

    public static void initialize(BalmBlockEntities blockEntities) {
        workbench = blockEntities.registerBlockEntity(id("workbench"),
                WorkbenchBlockEntity::new,
                () -> new Block[]{
                        ModBlocks.workbench,
                        ModBlocks.carpentersWorkbench,
                        ModBlocks.tailorsWorkbench,
                        ModBlocks.masonsWorkbench,
                        ModBlocks.armorersWorkbench,
                        ModBlocks.fletchersWorkbench,
                        ModBlocks.alchemistsWorkbench,
                        ModBlocks.engineersWorkbench,
                        ModBlocks.tinkerersWorkbench
                });
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(CraftingForBlockheads.MOD_ID, name);
    }

}
