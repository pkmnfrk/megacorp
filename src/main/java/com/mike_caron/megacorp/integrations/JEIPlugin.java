package com.mike_caron.megacorp.integrations;

import com.mike_caron.megacorp.block.ModBlocks;
import com.mike_caron.megacorp.gui.control.GuiContainerBase;
import com.mike_caron.megacorp.integrations.jei.GuiHandler;
import com.mike_caron.megacorp.integrations.jei.SBSRecipeCategory;
import com.mike_caron.megacorp.recipes.SBSRecipe;
import com.mike_caron.megacorp.recipes.SBSRecipeManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

@mezz.jei.api.JEIPlugin
public class JEIPlugin
    implements IModPlugin
{
    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        IJeiHelpers helpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = helpers.getGuiHelper();
        registry.addRecipeCategories(
            new SBSRecipeCategory(guiHelper)
        );
    }

    @Override
    public void register(IModRegistry registry)
    {
        registry.addRecipes(SBSRecipeManager.recipes, "megacorp:sbs");
        registry.handleRecipes(SBSRecipe.class, SBSRecipeCategory.SBSRecipeWrapper::new, "megacorp:sbs");

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.small_business_simulator), "megacorp:sbs");

        /*
        registry.addAdvancedGuiHandlers(new GuiHandler(GuiSBS.class));
        registry.addAdvancedGuiHandlers(new GuiHandler(GuiUplink.class));
        registry.addAdvancedGuiHandlers(new GuiHandler(GuiProfitMaterializer.class));
        */
        registry.addAdvancedGuiHandlers(new GuiHandler(GuiContainerBase.class));
    }
}
