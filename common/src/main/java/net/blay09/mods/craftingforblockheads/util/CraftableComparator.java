package net.blay09.mods.craftingforblockheads.util;

import net.blay09.mods.craftingforblockheads.crafting.RecipeWithStatus;
import net.minecraft.world.item.*;

import java.util.Comparator;

public class CraftableComparator implements Comparator<RecipeWithStatus> {

    @Override
    public int compare(RecipeWithStatus o1, RecipeWithStatus o2) {
        return Comparator
                .comparing((RecipeWithStatus r) -> !r.missingIngredients().isEmpty() || !r.missingPredicates().isEmpty())
                .thenComparing(r -> !r.missingPredicates().isEmpty())
                .thenComparing(r -> getTierLevel(r.resultItem()))
                .thenComparing(r -> r.resultItem().getDisplayName().getString(), String.CASE_INSENSITIVE_ORDER)
                .compare(o1, o2);
    }

    private int getTierLevel(ItemStack itemStack) {
        if (itemStack.getItem() instanceof TieredItem tieredItem) {
            final var tier = tieredItem.getTier();
            return (tier instanceof Tiers tiers) ? tiers.ordinal() : tier.getLevel();
        } else if (itemStack.getItem() instanceof ArmorItem armorItem) {
            return armorItem.getMaterial() instanceof ArmorMaterials armorMaterials ? getArmorTier(armorMaterials) : 0;
        }
        return 0;
    }

    private int getArmorTier(ArmorMaterials armorMaterials) {
        return switch (armorMaterials) {
            case LEATHER -> Tiers.STONE.ordinal();
            case GOLD -> Tiers.GOLD.ordinal();
            case CHAIN, IRON -> Tiers.IRON.ordinal();
            case DIAMOND -> Tiers.DIAMOND.ordinal();
            case NETHERITE -> Tiers.NETHERITE.ordinal();
            default -> 0;
        };
    }

}
