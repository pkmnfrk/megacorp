package com.mike_caron.megacorp.recipes;

import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class PCRecipe
{
    @Nonnull
    public final String name;
    @Nonnull
    public final FluidStack input;
    @Nonnull
    public final FluidStack output;
    public final int ticks;

    public PCRecipe(@Nonnull String name, @Nonnull FluidStack output, @Nonnull FluidStack input, int ticks)
    {
        this.name = name;
        this.input = input;
        this.output = output;
        this.ticks = ticks;
    }

    public boolean isMatch(PCRecipe recipe)
    {
        return isMatch(recipe.input);
    }

    public boolean isMatch(FluidStack input)
    {
        return input.isFluidEqual(this.input);
    }
}
