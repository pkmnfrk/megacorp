package com.mike_caron.megacorp.recipes;

import com.mike_caron.megacorp.fluid.ModFluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class PCRecipeManager
{
    private PCRecipeManager() {}
    public static final List<PCRecipe> recipes = new ArrayList<>();

    public static void addRecipe(String name, FluidStack output, FluidStack input, int ticks)
    {
        PCRecipe recipe = new PCRecipe(name, output, input, ticks);

        recipes.removeIf(sbsRecipe -> sbsRecipe.isMatch(recipe));

        recipes.add(recipe);
    }

    public static boolean hasRecipeWithIngredient(FluidStack ingredient)
    {
        for(PCRecipe recipe : recipes)
        {
            if(recipe.input.isFluidEqual(ingredient))
                return true;
        }

        return false;
    }

    public static PCRecipe getRecipeForIngredients(FluidStack ingredient)
    {
        for(PCRecipe recipe : recipes)
        {
            if(recipe.isMatch(ingredient))
                return recipe;
        }

        return null;
    }

    public static PCRecipe getRecipeWithName(String name)
    {
        for(PCRecipe recipe : recipes)
        {
            if(recipe.name.equals(name))
                return recipe;
        }

        return null;
    }

    public static void addDefaultRecipes()
    {
        addRecipe(
            "dense_money",
            new FluidStack(ModFluids.MONEY, 1000),
            new FluidStack(ModFluids.DENSE_MONEY, 1),
            20 * 6
        );
    }
}
