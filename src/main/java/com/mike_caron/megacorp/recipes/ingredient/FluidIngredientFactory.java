package com.mike_caron.megacorp.recipes.ingredient;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mike_caron.megacorp.item.Bottle;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.IngredientNBT;
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
        int amount = 1000;
        if(JsonUtils.hasField(json, "amount"))
        {
            amount = JsonUtils.getInt(json, "amount");
        }
        final Fluid fluid = FluidRegistry.getFluid(fluidName);

        if (fluid == null) {
            throw new JsonSyntaxException("Unknown fluid '" + fluidName + "'");
        }

        ItemStack filledBottle = Bottle.with(new FluidStack(fluid, amount));

        if (filledBottle.isEmpty()) {
            throw new JsonSyntaxException("No bucket registered for fluid '" + fluidName + "'");
        }

        //MegaCorpMod.logger.debug("Returning ingredient for fluid '" + fluidName + "'");

        if(amount == 1000)
        {
            ItemStack filledBucket = FluidUtil.getFilledBucket(new FluidStack(fluid, 0));
            return IngredientNBT.fromStacks(filledBottle, filledBucket);
        }
        else
        {
            return IngredientNBT.fromStacks(filledBottle);
        }

    }
}
