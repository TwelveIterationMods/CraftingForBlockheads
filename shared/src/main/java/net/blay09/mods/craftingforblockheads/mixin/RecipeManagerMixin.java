package net.blay09.mods.craftingforblockheads.mixin;

import net.blay09.mods.craftingforblockheads.tag.ModItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Inject(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true)
    public void getRecipeFor(RecipeType<?> recipeType, Container container, Level level, CallbackInfoReturnable<Optional<Recipe<?>>> callbackInfo) {
        final var result = callbackInfo.getReturnValue();
        if (result.isPresent()) {
            final var recipe = result.get();
            final var resultItem = recipe.getResultItem(level.registryAccess());
            if (resultItem.is(ModItemTags.IS_WORKSHOP_EXCLUSIVE)) {
                callbackInfo.setReturnValue(Optional.empty());
            }
        }
    }

    @Inject(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true)
    public void getRecipeFor(RecipeType<?> recipeType, Container container, Level level, @Nullable ResourceLocation resourceLocation, CallbackInfoReturnable<Optional<Recipe<?>>> callbackInfo) {
        final var result = callbackInfo.getReturnValue();
        if (result.isPresent()) {
            final var recipe = result.get();
            final var resultItem = recipe.getResultItem(level.registryAccess());
            if (resultItem.is(ModItemTags.IS_WORKSHOP_EXCLUSIVE)) {
                callbackInfo.setReturnValue(Optional.empty());
            }
        }
    }

    @Inject(method = "getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    public void getRecipesFor(RecipeType<?> recipeType, Container container, Level level, CallbackInfoReturnable<List<Recipe<?>>> callbackInfo) {
        final var result = callbackInfo.getReturnValue();
        Set<Recipe<?>> removedRecipes = null;
        for (Recipe<?> recipe : result) {
            final var resultItem = recipe.getResultItem(level.registryAccess());
            if (resultItem.is(ModItemTags.IS_WORKSHOP_EXCLUSIVE)) {
                if (removedRecipes == null) {
                    removedRecipes = new HashSet<>();
                }
                removedRecipes.add(recipe);
            }
        }
        if (removedRecipes != null) {
            final var changedResult = new ArrayList<>(result);
            changedResult.removeAll(removedRecipes);
            callbackInfo.setReturnValue(changedResult);
        }
    }

}
