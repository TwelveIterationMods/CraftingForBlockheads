package net.blay09.mods.craftingforblockheads.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Axis;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.client.rendering.BalmModels;
import net.blay09.mods.craftingforblockheads.CraftingForBlockheads;
import net.blay09.mods.craftingforblockheads.block.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Supplier;

public class ModModels {

    public static void initialize(BalmModels models) {
        registerColoredKitchenBlock(BalmClient.getModels(), () -> ModBlocks.workbench, "block/workbench");
    }

    private static DeferredObject<BakedModel> createLowerableFacingModel(String modelPath) {
        return createLowerableFacingModel(modelPath, Collections.emptyList());
    }

    private static DeferredObject<BakedModel> createLowerableFacingModel(String modelPath, List<RenderType> renderTypes) {
        return BalmClient.getModels().loadDynamicModel(id(modelPath), null, null, ModModels::lowerableFacingTransforms, renderTypes);
    }

    private static void registerColoredKitchenBlock(BalmModels models, Supplier<Block> blockSupplier, String modelPath) {
        models.overrideModel(blockSupplier, models.loadDynamicModel(id(modelPath), null, it -> {
            if (it.getValue(BlockKitchen.HAS_COLOR)) {
                return replaceTexture(getColoredTerracottaTexture(it.getValue(BlockKitchen.COLOR)));
            }

            return Collections.emptyMap();
        }, ModModels::lowerableFacingTransforms)::get);
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(CraftingForBlockheads.MOD_ID, path);
    }

    private static ImmutableMap<String, String> replaceTexture(String texturePath) {
        return ImmutableMap.<String, String>builder().put("texture", texturePath).put("particle", texturePath).build();
    }

    private static String getColoredTerracottaTexture(DyeColor color) {
        return "minecraft:block/" + color.name().toLowerCase(Locale.ENGLISH) + "_terracotta";
    }

    private static void lowerableFacingTransforms(BlockState state, Matrix4f transform) {
        if (state.hasProperty(BlockKitchen.LOWERED) && state.getValue(BlockKitchen.LOWERED)) {
            transform.translate(new Vector3f(0, -0.05f, 0f));
        }

        if (state.hasProperty(BlockKitchen.FACING)) {
            float angle = state.getValue(BlockKitchen.FACING).toYRot();
            transform.rotate(Axis.YP.rotationDegrees(180 - angle));
        }
    }
}
