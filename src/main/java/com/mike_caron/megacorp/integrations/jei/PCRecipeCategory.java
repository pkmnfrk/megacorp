package com.mike_caron.megacorp.integrations.jei;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.ModBlocks;
import com.mike_caron.megacorp.recipes.PCRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
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

public class PCRecipeCategory
    implements IRecipeCategory<PCRecipeCategory.PCRecipeWrapper>
{
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable arrow;
    private final IDrawable slotDrawable;

    private final String localizedName;
    private final String modName;

    public PCRecipeCategory(IGuiHelper guiHelper)
    {
        final ResourceLocation location = new ResourceLocation(MegaCorpMod.modId, "textures/gui/jei.png");

        background = guiHelper.drawableBuilder(location, 0, 55, 135, 54)
            .addPadding(1, 0, 0, 1)
            .build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.profit_condenser));
        localizedName = new TextComponentTranslation("tile.megacorp:profit_condenser.name").getUnformattedText();
        modName = new TextComponentTranslation("itemGroup.megacorp").getUnformattedText();
        arrow = guiHelper.drawableBuilder(location, 136, 55, 36, 46)
            .buildAnimated(400, IDrawableAnimated.StartDirection.LEFT, false);

        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    @Nonnull
    public String getUid()
    {
        return JEIPlugin.PC_CATEGORY;
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
        arrow.draw(minecraft, 50, 5);
    }

    @Nullable
    @Override
    public IDrawable getIcon()
    {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, PCRecipeWrapper PCRecipeWrapper, IIngredients iIngredients)
    {
        IGuiFluidStackGroup fluidStacks = iRecipeLayout.getFluidStacks();

        fluidStacks.init(0, true, 1, 2, 43, 52, 10000, true, null);
        fluidStacks.init(1, false, 91, 2, 43, 52, 10000, true, null);

        fluidStacks.set(iIngredients);

    }

    public static class PCRecipeWrapper
        implements IRecipeWrapper
    {
        PCRecipe recipe;

        public PCRecipeWrapper(PCRecipe recipe)
        {
            this.recipe = recipe;

        }

        @Override
        public void getIngredients(@Nonnull IIngredients iIngredients)
        {
            iIngredients.setInput(VanillaTypes.FLUID, recipe.input);
            iIngredients.setOutput(VanillaTypes.FLUID, recipe.output);
        }
    }
}
