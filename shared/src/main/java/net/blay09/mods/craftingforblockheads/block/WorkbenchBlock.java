package net.blay09.mods.craftingforblockheads.block;

import net.blay09.mods.craftingforblockheads.block.entity.WorkbenchBlockEntity;
import net.blay09.mods.craftingforblockheads.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WorkbenchBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape BOUNDING_BOX_X = Block.box(0.5, 0, 0, 15.5, 15.0, 16);
    private static final VoxelShape BOUNDING_BOX_Z = Block.box(0, 0, 0.5, 16, 15.0, 15.5);

    protected WorkbenchBlock(BlockBehaviour.Properties properties) {
        super(properties.sound(SoundType.STONE).strength(2.5f));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WorkbenchBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(FACING).getAxis() == Direction.Axis.X) {
            return BOUNDING_BOX_X;
        } else {
            return BOUNDING_BOX_Z;
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(TextUtils.coloredTextComponent("tooltip.craftingforblockheads.multiblock_workshop", ChatFormatting.YELLOW));
        tooltip.add(Component.translatable("tooltip.craftingforblockheads.workbench").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

}
