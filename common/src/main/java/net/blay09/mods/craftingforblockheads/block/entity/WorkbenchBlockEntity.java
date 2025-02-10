package net.blay09.mods.craftingforblockheads.block.entity;

import net.blay09.mods.balm.common.BalmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class WorkbenchBlockEntity extends BalmBlockEntity {

    public WorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.workbench.get(), pos, state);
    }

}
