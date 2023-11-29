package net.blay09.mods.craftingforblockheads.block;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.blay09.mods.craftingforblockheads.block.entity.WorkbenchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class WorkbenchBlock extends BlockDyeableKitchen {

    public static final String name = "workbench";
    public static final ResourceLocation registryName = new ResourceLocation(CraftingForBlockheads.MOD_ID, name);

    public WorkbenchBlock() {
        super(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(2.5f), registryName);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, COLOR, HAS_COLOR);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        WorkbenchBlockEntity blockEntity = (WorkbenchBlockEntity) level.getBlockEntity(pos);
        if (!heldItem.isEmpty()) {
            if (blockEntity != null) {
                if (tryRecolorBlock(state, heldItem, level, pos, player, rayTraceResult)) {
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (!level.isClientSide) {
            Balm.getNetworking().openGui(player, blockEntity);
        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WorkbenchBlockEntity(pos, state);
    }

}
