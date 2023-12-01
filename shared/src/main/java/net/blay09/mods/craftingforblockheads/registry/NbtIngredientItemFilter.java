package net.blay09.mods.craftingforblockheads.registry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Objects;

public class NbtIngredientItemFilter extends IngredientItemFilter {

    private final CompoundTag nbt;
    private final boolean strict;

    private ItemStack[] items;

    public NbtIngredientItemFilter(Ingredient ingredient, CompoundTag nbt, boolean strict) {
        super(ingredient);
        this.nbt = nbt;
        this.strict = strict;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        if (!super.test(itemStack)) {
            return false;
        }

        if (strict) {
            return Objects.equals(nbt, itemStack.getTag());
        } else {
            return NbtUtils.compareNbt(nbt, itemStack.getTag(), true);
        }
    }

    @Override
    public ItemStack[] getItems() {
        if (items == null) {
            final var baseItems = super.getItems();
            final var itemList = new ArrayList<ItemStack>();
            for (ItemStack baseItemStack : baseItems) {
                final var itemStack = baseItemStack.copy();
                itemStack.setTag(nbt.copy());
                if (test(itemStack)) {
                    itemList.add(itemStack);
                }
            }
            items = itemList.toArray(new ItemStack[0]);
        }

        return items;
    }
}
