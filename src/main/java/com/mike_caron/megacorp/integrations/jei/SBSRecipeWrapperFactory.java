package com.mike_caron.megacorp.integrations.jei;

import com.mike_caron.megacorp.recipes.SBSRecipe;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

public class SBSRecipeWrapperFactory
    implements IRecipeWrapperFactory<SBSRecipe>
{
    @Override
    public IRecipeWrapper getRecipeWrapper(SBSRecipe sbsRecipe)
    {
        return new SBSRecipeCategory.SBSRecipeWrapper(sbsRecipe);
    }
}
