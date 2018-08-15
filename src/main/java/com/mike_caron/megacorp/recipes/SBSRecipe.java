package com.mike_caron.megacorp.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class SBSRecipe
{
    public final String name;
    public final FluidStack output;
    public final ItemStack input1;
    public final ItemStack input2;
    public final int ticks;

    public SBSRecipe(String name, FluidStack output, ItemStack input1, ItemStack input2, int ticks)
    {
        this.name = name;
        this.output = output;
        this.input1 = input1;
        this.input2 = input2;
        this.ticks = ticks;
    }

    public boolean isMatch(SBSRecipe recipe)
    {
        return isMatch(recipe.input1, recipe.input2);
    }

    public boolean isMatch(ItemStack input1, ItemStack input2)
    {
        return (this.input1.isItemEqual(input1) && this.input2.isItemEqual(input2))
            || (this.input2.isItemEqual(input1) && this.input1.isItemEqual(input2));
    }
}
