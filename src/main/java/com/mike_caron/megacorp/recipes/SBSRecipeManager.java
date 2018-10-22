package com.mike_caron.megacorp.recipes;

import com.mike_caron.megacorp.fluid.ModFluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

public class SBSRecipeManager
{
    private SBSRecipeManager() {}
    public static final List<SBSRecipe> recipes = new ArrayList<>();

    public static void addRecipe(String name, FluidStack output, ItemStack item1, ItemStack item2, int ticks)
    {
        SBSRecipe recipe = new SBSRecipe(name, output, item1, item2, ticks);

        recipes.removeIf(sbsRecipe -> sbsRecipe.isMatch(recipe));

        recipes.add(recipe);
    }

    public static boolean hasRecipeWithIngredient(ItemStack ingredient)
    {
        for(SBSRecipe recipe : recipes)
        {
            if(recipe.input1.isItemEqual(ingredient) || recipe.input2.isItemEqual(ingredient))
                return true;
        }

        return false;
    }

    public static boolean hasRecipeWithIngredient(ItemStack input1, ItemStack input2)
    {
        if(input2.isEmpty())
            return hasRecipeWithIngredient(input1);

        for(SBSRecipe recipe : recipes)
        {
            if(recipe.isMatch(input1, input2))
                return true;
        }
        return false;
    }

    public static SBSRecipe getRecipeForIngredients(ItemStack ingredient1, ItemStack ingredient2)
    {
        for(SBSRecipe recipe : recipes)
        {
            if(recipe.isMatch(ingredient1, ingredient2))
                return recipe;
        }

        return null;
    }

    public static SBSRecipe getRecipeWithName(String name)
    {
        for(SBSRecipe recipe : recipes)
        {
            if(recipe.name.equals(name))
                return recipe;
        }

        return null;
    }

    @GameRegistry.ObjectHolder("minecraft:gold_block")
    public static Item gold_block;

    @GameRegistry.ObjectHolder("minecraft:gold_ingot")
    public static Item gold_ingot;

    @GameRegistry.ObjectHolder("minecraft:emerald")
    public static Item emerald;

    @GameRegistry.ObjectHolder("minecraft:emerald_block")
    public static Item emerald_block;

    public static void addDefaultRecipes()
    {
        addRecipe(
            "money",
            new FluidStack(ModFluids.MONEY, 20),
            new ItemStack(gold_block, 1),
            new ItemStack(emerald, 1),
            30
        );

        addRecipe(
            "money",
            new FluidStack(ModFluids.MONEY, 20),
            new ItemStack(gold_ingot, 9),
            new ItemStack(emerald, 1),
            25
        );

        addRecipe(
            "money2",
            new FluidStack(ModFluids.MONEY, 20 * 9),
            new ItemStack(gold_block, 9),
            new ItemStack(emerald_block, 1),
            150
        );
    }
}
