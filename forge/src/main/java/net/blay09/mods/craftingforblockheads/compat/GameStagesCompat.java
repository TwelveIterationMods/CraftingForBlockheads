package net.blay09.mods.craftingforblockheads.compat;

import net.blay09.mods.craftingforblockheads.api.CraftingForBlockheadsAPI;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.util.GsonHelper;

public class GameStagesCompat {
    public GameStagesCompat() {
        CraftingForBlockheadsAPI.registerWorkshopPredicateDeserializer("has_gamestage", (jsonObject -> {
            String gameStage = GsonHelper.getAsString(jsonObject, "stage");
            return (workshop, player) -> player != null && GameStageHelper.hasStage(player, gameStage);
        }));
    }
}
