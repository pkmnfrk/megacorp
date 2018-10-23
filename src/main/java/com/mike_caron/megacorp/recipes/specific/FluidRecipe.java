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
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidRecipe
    extends ShapedOreRecipe
{
    public FluidRecipe(ResourceLocation group, @Nonnull ItemStack result, CraftingHelper.ShapedPrimer primer)
    {
        super(group, result, primer);

        //MegaCorpMod.logger.debug("Initializing fluid-based recipe to create " + result);
    }

    @Nullable
    private FluidStack getFluidFromItem(ItemStack stack)
    {
        if(stack.isEmpty()) return null;

        if(!stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
            return null;

        IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

        FluidStack ret = null;

        if(fluidHandler instanceof FluidHandlerItemStack)
        {
            ret = ((FluidHandlerItemStack) fluidHandler).getFluid();
        }
        if(fluidHandler instanceof FluidHandlerItemStackSimple)
        {
            ret = ((FluidHandlerItemStackSimple) fluidHandler).getFluid();
        }

        if(ret == null)
        {
            for (IFluidTankProperties prop : fluidHandler.getTankProperties())
            {
                if (prop.canDrain())
                {
                    ret = prop.getContents();
                }
            }
        }

        if(ret != null)
            ret = ret.copy();

        return ret;
    }

    private boolean fluidMatches(@Nonnull FluidStack fluid, @Nonnull ItemStack stack)
    {
        //MegaCorpMod.logger.debug("Does this fluid match? '" + fluid.getFluid().getName() + "x" + fluid.amount + "mB' vs '" + stack + "'");

        if(stack.isEmpty())
        {
            //MegaCorpMod.logger.debug("Nope, stack is empty");
            return false;
        }

        if(!stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
        {
            //MegaCorpMod.logger.debug("Nope, stack doesn't have capability");
            return false;
        }

        IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

        FluidStack drained = fluidHandler.drain(fluid, false);

        //NBTTagCompound nbt = stack.getTagCompound();
        //MegaCorpMod.logger.debug("Table: {}", DataUtils.toJson(nbt).toString());

        if(drained == null || drained.getFluid() != fluid.getFluid() || drained.amount != fluid.amount)
        {
            //String dbg = "";
            //if(drained == null)
            //{
            //    dbg = "null";
            //}
            //else
            //{
            //    dbg += drained.getFluid().getName() + "x" + drained.amount + "mB";
            //}
            //MegaCorpMod.logger.debug("Nope, stack couldn't drain fluid: {}", dbg);
            return false;
        }

        //MegaCorpMod.logger.debug("Yep");
        return true;
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world)
    {
        NonNullList<Ingredient> ingredients = getIngredients();

        if(inventoryCrafting.getSizeInventory() != ingredients.size())
            return false;

        //MegaCorpMod.logger.debug("Checking matching for recipe for " + this.getRecipeOutput());

        for(int i = 0; i < ingredients.size(); i++)
        {
            Ingredient ing = ingredients.get(i);
            ItemStack ingredientStack = ing.getMatchingStacks()[0];
            FluidStack desiredFluid = getFluidFromItem(ingredientStack);
            ItemStack stack = inventoryCrafting.getStackInSlot(i);

            if(desiredFluid != null)
            {

                //NBTTagCompound nbt = ingredientStack.getTagCompound();
                //MegaCorpMod.logger.debug("Ingredient: {}", DataUtils.toJson(nbt).toString());

                if(!fluidMatches(desiredFluid, stack))
                {
                    //MegaCorpMod.logger.debug("Recipe doesn't match because fluid doesn't match");
                    return false;
                }
            }
            else
            {
                if(!ing.apply(stack))
                {
                    //MegaCorpMod.logger.debug("Recipe doesn't match because {} doesn't match {}", stack, toString(ing));
                    return false;
                }
            }
        }

        //MegaCorpMod.logger.debug("Recipe matches!");

        return true;
    }

    private String toString(Ingredient ing)
    {
        StringBuilder ret = new StringBuilder();

        boolean first = true;
        for(ItemStack s : ing.getMatchingStacks())
        {
            if(first)
                first = false;
            else
                ret.append(",");

            ret.append(s);
        }

        return ret.toString();
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
