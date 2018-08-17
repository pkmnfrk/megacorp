package com.mike_caron.megacorp.integrations.jei;

import com.mike_caron.megacorp.recipes.PCRecipe;
import com.mike_caron.megacorp.recipes.SBSRecipe;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

public class PCRecipeWrapperFactory
    implements IRecipeWrapperFactory<PCRecipe>
{
    @Override
    public IRecipeWrapper getRecipeWrapper(PCRecipe sbsRecipe)
    {
        return new PCRecipeCategory.PCRecipeWrapper(sbsRecipe);
    }
}
