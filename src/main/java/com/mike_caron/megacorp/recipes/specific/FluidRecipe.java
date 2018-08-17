package com.mike_caron.megacorp.recipes.specific;

import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidRecipe
    extends ShapedOreRecipe
{
    public FluidRecipe(ResourceLocation group, @Nonnull ItemStack result, CraftingHelper.ShapedPrimer primer)
    {
        super(group, result, primer);


    }

    @Nullable
    private FluidStack getFluidFromItem(ItemStack stack)
    {
        if(stack.isEmpty()) return null;

        if(!stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
            return null;

        IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

        for(IFluidTankProperties prop : fluidHandler.getTankProperties())
        {
            if(prop.canDrain())
                return prop.getContents();
        }

        return null;
    }

    private boolean fluidMatches(@Nonnull FluidStack fluid, @Nonnull ItemStack stack)
    {
        if(stack.isEmpty())
            return false;

        if(!stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
            return false;

        IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

        FluidStack drained = fluidHandler.drain(fluid, false);

        if(drained == null || drained.getFluid() != fluid.getFluid() || drained.amount != fluid.amount)
            return false;

        return true;
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world)
    {
        NonNullList<Ingredient> ingredients = getIngredients();

        if(inventoryCrafting.getSizeInventory() != ingredients.size())
            return false;

        for(int i = 0; i < ingredients.size(); i++)
        {
            Ingredient ing = ingredients.get(i);
            ItemStack ingredientStack = ing.getMatchingStacks()[0];
            FluidStack desiredFluid = getFluidFromItem(ingredientStack);

            if(desiredFluid != null)
            {
                if(!fluidMatches(desiredFluid, inventoryCrafting.getStackInSlot(i)))
                    return false;
            }
            else
            {
                if(!ing.apply(inventoryCrafting.getStackInSlot(i)))
                    return false;
            }
        }

        return true;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        NonNullList<Ingredient> ingredients = getIngredients();
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); i++)
        {
            ItemStack stack = inv.getStackInSlot(i);
            Ingredient ing = ingredients.get(i);
            ItemStack ingredientStack = ing.getMatchingStacks()[0];
            FluidStack desiredFluid = getFluidFromItem(ingredientStack);

            if(desiredFluid != null)
            {
                if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
                {
                    IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                    FluidStack drained = fluidHandler.drain(desiredFluid, true);

                    nonnulllist.set(i, fluidHandler.getContainer().copy());
                }
            }
            else
            {
                nonnulllist.set(i, ForgeHooks.getContainerItem(stack));
            }
        }

        return nonnulllist;
    }

    public static class Factory
        implements IRecipeFactory
    {
        @Override
        public IRecipe parse(JsonContext jsonContext, JsonObject json)
        {
            final String group = JsonUtils.getString(json, "group", "");

            final CraftingHelper.ShapedPrimer primer = RecipeUtil.parseShaped(jsonContext, json);
            final ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), jsonContext);

            return new FluidRecipe(group.isEmpty() ? null : new ResourceLocation(group), result, primer);
        }
    }

}
