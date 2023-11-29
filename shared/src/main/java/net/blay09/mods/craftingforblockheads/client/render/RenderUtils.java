package net.blay09.mods.craftingforblockheads.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.craftingforblockheads.block.BlockKitchen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RenderUtils {

    public static void applyBlockAngle(PoseStack poseStack, BlockState state) {
        applyBlockAngle(poseStack, state, 180f);
    }

    public static void applyBlockAngle(PoseStack poseStack, BlockState state, float angleOffset) {
        float angle = state.getValue(BlockKitchen.FACING).toYRot();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(angleOffset - angle));
    }

    public static void renderItem(ItemStack itemStack, int combinedLight, PoseStack poseStack, MultiBufferSource buffer, Level level) {
        Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED.FIXED, combinedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, level, 0);
    }

}
