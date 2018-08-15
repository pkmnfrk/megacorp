package com.mike_caron.megacorp.integrations.jei;

import com.mike_caron.megacorp.block.ModBlocks;
import com.mike_caron.megacorp.block.sbs.ContainerSBS;
import com.mike_caron.megacorp.fluid.ModFluids;
import com.mike_caron.megacorp.gui.GuiSBS;
import com.mike_caron.megacorp.gui.control.GuiContainerBase;
import com.mike_caron.megacorp.item.ModItems;
import com.mike_caron.megacorp.recipes.SBSRecipe;
import com.mike_caron.megacorp.recipes.SBSRecipeManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

@mezz.jei.api.JEIPlugin
public class JEIPlugin
    implements IModPlugin
{

    public static final String SBS_CATEGORY = "megacorp:sbs";

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
        registry.addRecipes(SBSRecipeManager.recipes, SBS_CATEGORY);
        registry.handleRecipes(SBSRecipe.class, SBSRecipeCategory.SBSRecipeWrapper::new, SBS_CATEGORY);

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.small_business_simulator), SBS_CATEGORY);
        registry.addRecipeClickArea(GuiSBS.class, 53, 35, 38, 26, SBS_CATEGORY);
        registry.addAdvancedGuiHandlers(new GuiHandler(GuiContainerBase.class));

        IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();

        transferRegistry.addRecipeTransferHandler(ContainerSBS.class, SBS_CATEGORY, 0, 2, 2, 36);

        ModBlocks.getAllBlocks().forEach(block -> registry.addIngredientInfo(new ItemStack(block), VanillaTypes.ITEM,  block.getUnlocalizedName() + ".description"));
        ModItems.getAllItems().forEach(item -> registry.addIngredientInfo(new ItemStack(item), VanillaTypes.ITEM, item.getUnlocalizedName() + ".description"));
        ModFluids.getAllFluids().forEach(fluid -> registry.addIngredientInfo(new FluidStack(fluid, 1000), VanillaTypes.FLUID, fluid.getUnlocalizedName() + ".description"));
    }
}
