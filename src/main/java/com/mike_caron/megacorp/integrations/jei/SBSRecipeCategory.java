package com.mike_caron.megacorp.integrations.jei;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.ModBlocks;
import com.mike_caron.megacorp.recipes.SBSRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class SBSRecipeCategory
    implements IRecipeCategory<SBSRecipeCategory.SBSRecipeWrapper>
{
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable arrow;
    private final IDrawable slotDrawable;

    private final String localizedName;
    private final String modName;

    public SBSRecipeCategory(IGuiHelper guiHelper)
    {
        final ResourceLocation location = new ResourceLocation(MegaCorpMod.modId, "textures/gui/jei.png");

        background = guiHelper.drawableBuilder(location, 0, 0, 108, 54)
            .addPadding(1, 0, 0, 1)
            .build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.small_business_simulator));
        localizedName = new TextComponentTranslation("tile.megacorp:small_business_simulator.name").getUnformattedText();
        modName = new TextComponentTranslation("itemGroup.megacorp").getUnformattedText();
        arrow = guiHelper.drawableBuilder(location, 109, 0, 38, 26)
            .buildAnimated(400, IDrawableAnimated.StartDirection.LEFT, false);

        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    @Nonnull
    public String getUid()
    {
        return "megacorp:sbs";
    }

    @Override
    @Nonnull
    public String getTitle()
    {
        return localizedName;
    }

    @Nonnull
    @Override
    public String getModName()
    {
        return modName;
    }

    @Override
    @Nonnull
    public IDrawable getBackground()
    {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        arrow.draw(minecraft, 20, 15);
    }

    @Nullable
    @Override
    public IDrawable getIcon()
    {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, SBSRecipeWrapper sbsRecipeWrapper, IIngredients iIngredients)
    {
        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        itemStacks.init(0, true, 0, 8);
        itemStacks.init(1, true, 0, 31);


        IGuiFluidStackGroup fluidStacks = iRecipeLayout.getFluidStacks();
        fluidStacks.init(2, false, 64, 2, 43, 52, 10000, true, null);

        itemStacks.set(iIngredients);
        fluidStacks.set(iIngredients);

    }

    public static class SBSRecipeWrapper
        implements IRecipeWrapper
    {
        SBSRecipe recipe;

        public SBSRecipeWrapper(SBSRecipe recipe)
        {
            this.recipe = recipe;

        }

        @Override
        public void getIngredients(@Nonnull IIngredients iIngredients)
        {
            iIngredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(Arrays.asList(recipe.input1, recipe.input2), Arrays.asList(recipe.input2, recipe.input1)));
            iIngredients.setOutput(VanillaTypes.FLUID, recipe.output);
        }
    }
}
