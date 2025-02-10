package net.blay09.mods.craftingforblockheads.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.craftingforblockheads.block.entity.WorkbenchBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class WorkbenchRenderer implements BlockEntityRenderer<WorkbenchBlockEntity> {

    public WorkbenchRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(WorkbenchBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
    }

}
