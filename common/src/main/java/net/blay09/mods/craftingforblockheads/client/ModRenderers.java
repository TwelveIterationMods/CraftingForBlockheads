package net.blay09.mods.craftingforblockheads.client;

import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.blay09.mods.craftingforblockheads.client.render.*;
import net.blay09.mods.craftingforblockheads.block.entity.ModBlockEntities;

public class ModRenderers {

    public static void initialize(BalmRenderers renderers) {
        renderers.registerBlockEntityRenderer(ModBlockEntities.workbench::get, WorkbenchRenderer::new);
    }

}
