package com.mike_caron.megacorp.recipes.ingredient;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;

public class FluidIngredientFactory
    implements IIngredientFactory
{
    @Nonnull
    @Override
    public Ingredient parse(final JsonContext context, final JsonObject json) {
        final String fluidName = JsonUtils.getString(json, "fluid");
        final Fluid fluid = FluidRegistry.getFluid(fluidName);

        if (fluid == null) {
            throw new JsonSyntaxException("Unknown fluid '" + fluidName + "'");
        }

        final ItemStack filledBucket = FluidUtil.getFilledBucket(new FluidStack(fluid, 0));

        if (filledBucket.isEmpty()) {
            throw new JsonSyntaxException("No bucket registered for fluid '" + fluidName + "'");
        }

        return new FluidIngredient(filledBucket);
    }
}
