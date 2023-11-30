package net.blay09.mods.craftingforblockheads;

import net.blay09.mods.balm.api.config.BalmConfigData;
import net.blay09.mods.balm.api.config.Comment;
import net.blay09.mods.balm.api.config.Config;
import net.blay09.mods.balm.api.config.ExpectedType;

import java.util.List;

@Config(CraftingForBlockheads.MOD_ID)
public class CraftingForBlockheadsConfigData implements BalmConfigData {

    @ExpectedType(String.class)
    @Comment("The presets to use for the workbench filters and progression.")
    public List<String> presets = List.of("demo");

}
