package net.blay09.mods.craftingforblockheads.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class BlockDyeableKitchen extends BlockKitchen {

    protected BlockDyeableKitchen(BlockBehaviour.Properties properties, ResourceLocation registryName) {
        super(properties, registryName);
    }

    @Override
    protected boolean isDyeable() {
        return true;
    }
}
